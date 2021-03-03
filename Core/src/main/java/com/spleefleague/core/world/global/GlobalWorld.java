package com.spleefleague.core.world.global;

import com.comphenix.protocol.wrappers.BlockPosition;
import com.spleefleague.core.Core;
import com.spleefleague.core.logger.CoreLogger;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.world.FakeUtils;
import com.spleefleague.core.world.game.projectile.ProjectileStats;
import com.spleefleague.core.world.game.projectile.ProjectileWorld;
import com.spleefleague.core.world.game.projectile.ProjectileWorldPlayer;
import com.spleefleague.core.world.global.lock.GlobalLock;
import com.spleefleague.core.world.global.vehicle.GlobalVehicle;
import net.minecraft.server.v1_15_R1.Entity;
import net.minecraft.server.v1_15_R1.ItemStack;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_15_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.lang.reflect.InvocationTargetException;
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

    private static final Map<String, GlobalWorld> GLOBAL_WORLDS = new HashMap<>();

    public static GlobalWorld getGlobalWorld(World world) {
        GlobalWorld globalWorld = GLOBAL_WORLDS.get(world.getName());
        if (globalWorld == null) {
            return createAndStore(world);
        }
        return GLOBAL_WORLDS.get(world.getName());
    }

    public static void init() {
        GlobalVehicle.init();
        GlobalLock.init();
        Bukkit.getWorlds().forEach(GlobalWorld::createAndStore);
    }

    public static GlobalWorld createAndStore(World world) {
        GlobalWorld globalWorld = new GlobalWorld(world);
        GLOBAL_WORLDS.put(world.getName(), globalWorld);
        return globalWorld;
    }

    // 32 * 128
    private static final double COMPRESS_MULTIPLY = 4096;

    public static class RotatingItemPlayer {

        public static class RotatingItem {

            private static final double RADIUS = 1.25;

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
                            (float) (-rotation + Math.PI),
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
            this.rotatingItems.add(new RotatingItem(world, x, y + 0.75, z, itemStack));
        }

        public RotatingItemPlayer(Player player, ProjectileWorld<? extends ProjectileWorldPlayer> world, ItemStack itemStack, double heightMod) {
            this.player = player;
            this.world = world;
            this.x = player.getLocation().getX();
            this.y = player.getLocation().getY();
            this.z = player.getLocation().getZ();
            this.rotatingItems.add(new RotatingItem(world, x, y + 0.75 + heightMod, z, itemStack));
        }

        public void addRotatingItem(ItemStack itemStack) {
            rotatingItems.add(new RotatingItem(world, x, y + 0.75, z, itemStack));
        }

        public void addRotatingItem(ItemStack itemStack, double heightMod) {
            rotatingItems.add(new RotatingItem(world, x, y + 0.75 + heightMod, z, itemStack));
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

    protected GlobalWorld(World world) {
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

    public void addRotationItem(CorePlayer corePlayer, org.bukkit.inventory.ItemStack itemStack, double heightMod) {
        ItemStack nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
        if (!rotatingItemMap.containsKey(corePlayer.getUniqueId())) {
            rotatingItemMap.put(corePlayer.getUniqueId(), new RotatingItemPlayer(corePlayer.getPlayer(), this, nmsItemStack, heightMod));
        } else {
            rotatingItemMap.get(corePlayer.getUniqueId()).addRotatingItem(nmsItemStack, heightMod);
        }
    }

    @Override
    public void destroy() {
        super.destroy();
        rotatingItemTask.cancel();
    }

    @Override
    protected boolean onBlockPunch(CorePlayer cp, BlockPosition pos) {
        if (getFakeBlock(pos) == null) return false;
        tryFix(cp, pos);
        return true;
    }

    @Override
    protected boolean onItemUse(CorePlayer cp, BlockPosition pos, BlockPosition blockRelative) {
        if (getFakeBlock(pos) == null) return false;
        tryFix(cp, pos);
        return true;
    }

    @Override
    protected List<net.minecraft.server.v1_15_R1.Entity> shoot(CorePlayer shooter,
                                                               Location location,
                                                               ProjectileStats projectileStats,
                                                               double charge) {
        List<net.minecraft.server.v1_15_R1.Entity> entities = new ArrayList<>();
        try {
            for (ProjectileWorldPlayer pwp : playerMap.values()) {
                if (pwp.getCorePlayer().getOptions().getBoolean("Sound:Gadget")) {
                    pwp.getPlayer().playSound(location, projectileStats.soundEffect, projectileStats.soundVolume.floatValue(), projectileStats.soundPitch.floatValue());
                }
            }
            switch (projectileStats.shape) {
                case PLUS:
                    shoot(entities, shooter, location.clone(),
                            projectileStats, charge);
                    for (int i = 1; i <= projectileStats.count; i++) {
                        shoot(entities, shooter, location.clone().add(
                                location.clone().getDirection().crossProduct(new org.bukkit.util.Vector(0, 1, 0)).normalize().multiply(i * (float) projectileStats.hSpread / 100 / projectileStats.count)),
                                projectileStats, charge);
                        shoot(entities, shooter, location.clone().add(
                                location.clone().getDirection().crossProduct(new org.bukkit.util.Vector(0, 1, 0)).normalize().multiply(-i * (float) projectileStats.hSpread / 100 / projectileStats.count)),
                                projectileStats, charge);
                    }
                    shooter.getStatistics().add("splegg", "eggsFired", (int) (projectileStats.count * charge * 2 + 1));
                    break;
                default:
                    for (int i = 0; i < projectileStats.count * charge; i++) {
                        shoot(entities, shooter, location, projectileStats, charge);
                    }
                    shooter.getStatistics().add("splegg", "eggsFired", (int) (projectileStats.count * charge));
                    break;
            }
            if (charge >= 0.2 && Math.abs(projectileStats.fireKnockback) > 0.001) {
                shooter.getPlayer().setVelocity(shooter.getPlayer().getLocation().getDirection().multiply(-projectileStats.fireKnockback * charge));
            }
        } catch (InstantiationException | NoSuchMethodException | InvocationTargetException | IllegalAccessException exception) {
            CoreLogger.logError(exception);
        }
        return entities;
    }

    @Override
    public void playSound(Location location, Sound sound, float volume, float pitch) {
        for (GlobalWorldPlayer globalWorldPlayer : playerMap.values()) {
            globalWorldPlayer.getPlayer().playSound(location, sound, volume, pitch);
        }
    }

    public void playSound(Location location, Sound sound, float volume, float pitch, String channel) {
        for (GlobalWorldPlayer globalWorldPlayer : playerMap.values()) {
            if (globalWorldPlayer.getCorePlayer().getOptions().getBoolean(channel)) {
                globalWorldPlayer.getPlayer().playSound(location, sound, volume, pitch);
            }
        }
    }

}
