package com.spleefleague.core.world.global;

import com.comphenix.protocol.wrappers.BlockPosition;
import com.spleefleague.core.Core;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.world.FakeUtils;
import com.spleefleague.core.world.game.projectile.ProjectileWorld;
import com.spleefleague.core.world.game.projectile.ProjectileWorldPlayer;
import com.spleefleague.core.world.global.lock.GlobalLock;
import com.spleefleague.core.world.global.vehicle.GlobalVehicle;
import net.minecraft.server.v1_15_R1.ItemStack;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_15_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

/**
 * Global Worlds are instances of Fake Worlds that all players are
 * added to on login and removed from on quit, which allows for things
 * such as spleef field showing out of matches
 *
 * @author NickM13
 * @since 4/21/2020
 */
public class GlobalWorld extends ProjectileWorld<GlobalWorldPlayer> {

    public static void init() {
        GlobalVehicle.init();
        GlobalLock.init();
    }

    // 32 * 128
    private static final double COMPRESS_MULTIPLY = 4096;

    public static class RotatingItemPlayer {

        public static class RotatingItem {

            private static double RADIUS = 1;

            int remainingTime;
            final int entityId = FakeUtils.getNextId();
            final ProjectileWorld<? extends ProjectileWorldPlayer> world;

            private double lastRotation = 0;
            double x, y, z;

            public RotatingItem(ProjectileWorld<? extends ProjectileWorldPlayer> world, double x, double y, double z, ItemStack item) {
                remainingTime = 40;
                world.getPlayerMap().values().forEach(pwp -> {
                    FakeUtils.sendArmorStandSpawn(
                            pwp.getPlayer(),
                            entityId,
                            x, y, z + RADIUS,
                            item);
                });
                this.x = x;
                this.y = y;
                this.z = z + RADIUS;
                this.world = world;
            }

            public boolean onTick() {
                if (--remainingTime > 0) {
                    return true;
                } else {
                    world.getPlayerMap().values().forEach(pwp -> {
                        FakeUtils.sendEntityDestroy(
                                pwp.getPlayer(),
                                entityId
                        );
                    });
                    return false;
                }
            }

            public void update(float rotation, short changeX, short changeY, short changeZ) {
                short realChangeX = (short) (changeX + (Math.sin(rotation) - Math.sin(lastRotation)) * COMPRESS_MULTIPLY * RADIUS);
                short realChangeY = changeY;
                short realChangeZ = (short) (changeZ + (Math.cos(rotation) - Math.cos(lastRotation)) * COMPRESS_MULTIPLY * RADIUS);
                this.x += realChangeX / COMPRESS_MULTIPLY;
                this.y += realChangeY / COMPRESS_MULTIPLY;
                this.z += realChangeZ / COMPRESS_MULTIPLY;
                world.getPlayerMap().values().forEach(pwp -> {
                    FakeUtils.sendEntityMoveLook(
                            pwp.getPlayer(),
                            entityId,
                            realChangeX,
                            realChangeY,
                            realChangeZ,
                            -rotation,
                            0);
                });
                world.spawnParticles(Particle.CLOUD, x, y, z, 0, 0, 0, 0, 1);
                lastRotation = rotation;
            }

        }

        private final List<RotatingItem> rotatingItems = new ArrayList<>();

        private final Player player;
        private final ProjectileWorld<? extends ProjectileWorldPlayer> world;
        private double x, y, z;
        private float rotation = 0;

        public RotatingItemPlayer(Player player, ProjectileWorld<? extends ProjectileWorldPlayer> world, ItemStack itemStack) {
            this.player = player;
            this.world = world;
            this.x = player.getLocation().getX();
            this.y = player.getLocation().getY();
            this.z = player.getLocation().getZ();
            this.rotatingItems.add(new RotatingItem(world, x, y + 1.5, z, itemStack));
        }

        public void addRotatingItem(ItemStack itemStack) {
            rotatingItems.add(new RotatingItem(world, x, y + 1.5, z, itemStack));
        }

        public boolean onTick() {
            rotatingItems.removeIf(item -> !item.onTick());
            if (rotatingItems.isEmpty()) {
                return false;
            }

            double lastX = x;
            double lastY = y;
            double lastZ = z;

            x = player.getLocation().getX();
            y = player.getLocation().getY();
            z = player.getLocation().getZ();

            short changeX = (short) ((x - lastX) * COMPRESS_MULTIPLY);
            short changeY = (short) ((y - lastY) * COMPRESS_MULTIPLY);
            short changeZ = (short) ((z - lastZ) * COMPRESS_MULTIPLY);

            float rotation = (float) (Math.PI * 2 / rotatingItems.size());
            int i = 0;
            for (RotatingItem item : rotatingItems) {
                item.update(this.rotation + rotation * i++, changeX, changeY, changeZ);
            }
            this.rotation -= 0.2;
            return true;
        }

    }

    private final Map<UUID, RotatingItemPlayer> rotatingItemMap = new HashMap<>();
    //private final List<RotatingItemChest>

    private final BukkitTask rotatingItemTask;

    public GlobalWorld(World world) {
        super(-1, world, GlobalWorldPlayer.class);
        rotatingItemTask = Bukkit.getScheduler().runTaskTimer(Core.getInstance(), () -> {
            rotatingItemMap.entrySet().removeIf(entry -> !entry.getValue().onTick());
        }, 3L, 3L);
    }

    public void addRotationItem(CorePlayer corePlayer, org.bukkit.inventory.ItemStack itemStack) {
        ItemStack nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
        if (!rotatingItemMap.containsKey(corePlayer.getUniqueId())) {
            rotatingItemMap.put(corePlayer.getUniqueId(), new RotatingItemPlayer(corePlayer.getPlayer(), this, nmsItemStack));
        } else {
            rotatingItemMap.get(corePlayer.getUniqueId()).addRotatingItem(nmsItemStack);
        }
    }

    @Override
    public void destroy() {
        super.destroy();
        rotatingItemTask.cancel();
    }

    @Override
    protected boolean onBlockPunch(CorePlayer cp, BlockPosition pos) {
        if (!fakeBlocks.containsKey(pos)) return false;
        updateBlock(pos);
        return true;
    }

    @Override
    protected boolean onItemUse(CorePlayer cp, BlockPosition pos, BlockPosition blockRelative) {
        if (!fakeBlocks.containsKey(pos)) return false;
        updateBlock(pos);
        return true;
    }

}
