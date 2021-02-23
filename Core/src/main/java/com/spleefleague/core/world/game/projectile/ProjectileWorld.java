package com.spleefleague.core.world.game.projectile;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.spleefleague.core.Core;
import com.spleefleague.core.logger.CoreLogger;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.util.variable.BlockRaycastResult;
import com.spleefleague.core.world.FakeWorld;
import com.spleefleague.core.world.game.GameProjectile;
import net.minecraft.server.v1_15_R1.Entity;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_15_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftEntity;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * @author NickM13
 */
public class ProjectileWorld<PWP extends ProjectileWorldPlayer> extends FakeWorld<PWP> {

    protected static class FutureShot {

        ProjectileStats stats;
        Vector offset;
        double charge;
        int remaining;
        int nextTicks;

        FutureShot(ProjectileStats stats, Vector offset, double charge) {
            this.stats = stats;
            this.offset = offset;
            this.charge = charge;
            remaining = stats.repeat - 1;
            nextTicks = stats.repeatDelay;
        }

    }

    protected final BukkitTask projectileCollideTask;
    protected Map<UUID, GameProjectile> projectiles;

    protected BukkitTask futureShotsTask;
    protected final Map<UUID, Set<FutureShot>> futureShots;

    protected ProjectileWorld(int priority, World world, Class<PWP> fakePlayerClass) {
        super(priority, world, fakePlayerClass);

        projectiles = new HashMap<>();
        futureShots = new HashMap<>();

        projectileCollideTask = Bukkit.getScheduler()
                .runTaskTimer(Core.getInstance(),
                        this::updateProjectiles, 0L, 1L);

        futureShotsTask = Bukkit.getScheduler()
                .runTaskTimer(Core.getInstance(),
                        this::updateFutureShots, 0L, 1L);

        Core.addProtocolPacketAdapter(new PacketAdapter(Core.getInstance(), PacketType.Play.Server.SPAWN_ENTITY) {
            @Override
            public void onPacketSending(PacketEvent event) {
                PacketContainer packet = event.getPacket();
                org.bukkit.entity.Entity entity = packet.getEntityModifier(event).read(0);
                if (projectiles.containsKey(entity.getUniqueId()) &&
                        !getPlayerMap().containsKey(event.getPlayer().getUniqueId())) {
                    event.setCancelled(true);
                }
            }
        });
    }

    @Override
    public void destroy() {
        super.destroy();
        projectileCollideTask.cancel();
    }

    @Override
    protected boolean onBlockPunch(CorePlayer cp, BlockPosition pos) {
        return false;
    }

    @Override
    protected boolean onItemUse(CorePlayer cp, BlockPosition blockPosition, BlockPosition blockRelative) {
        return false;
    }

    public void clearProjectiles() {
        for (GameProjectile gp : projectiles.values()) {
            gp.getEntity().killEntity();
        }
        projectiles.clear();
    }

    protected void updateProjectiles() {
        projectiles.entrySet().removeIf(uuidGameProjectileEntry -> !uuidGameProjectileEntry.getValue().getEntity().isAlive());
    }

    protected net.minecraft.server.v1_15_R1.Entity shoot(List<Entity> entities,
                                                         CorePlayer shooter,
                                                         Location location,
                                                         ProjectileStats projectileStats,
                                                         double charge)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        net.minecraft.server.v1_15_R1.Entity entity = projectileStats.entityClass
                .getDeclaredConstructor(ProjectileWorld.class, CorePlayer.class, Location.class, ProjectileStats.class, Double.class)
                .newInstance(this, shooter, location, projectileStats, charge);
        projectiles.put(entity.getUniqueID(), new GameProjectile(entity, projectileStats));
        ((CraftWorld) getWorld()).getHandle().addEntity(entity);
        entities.add(entity);
        return entity;
    }

    protected List<net.minecraft.server.v1_15_R1.Entity> shoot(CorePlayer shooter,
                                                               Location location,
                                                               ProjectileStats projectileStats,
                                                               double charge) {
        List<net.minecraft.server.v1_15_R1.Entity> entities = new ArrayList<>();
        try {
            for (ProjectileWorldPlayer pwp : playerMap.values()) {
                pwp.getPlayer().playSound(location, projectileStats.soundEffect, projectileStats.soundVolume.floatValue(), projectileStats.soundPitch.floatValue());
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

    @SuppressWarnings("unused")
    public List<net.minecraft.server.v1_15_R1.Entity> shootProjectileCharged(CorePlayer shooter, ProjectileStats projectileStats, double charge) {
        return shootProjectileCharged(shooter, shooter.getPlayer().getEyeLocation().clone()
                        .add(shooter.getPlayer().getLocation().getDirection()
                                .crossProduct(new org.bukkit.util.Vector(0, 1, 0)).normalize()
                                .multiply(0.15).add(new org.bukkit.util.Vector(0, -0.15, 0))),
                projectileStats,
                charge);
    }

    public List<net.minecraft.server.v1_15_R1.Entity> shootProjectileCharged(CorePlayer shooter, Location location, ProjectileStats projectileStats, double charge) {
        if (projectileStats.repeat > 1) {
            if (!futureShots.containsKey(shooter.getUniqueId())) {
                futureShots.put(shooter.getUniqueId(), new HashSet<>());
            }
            futureShots.get(shooter.getUniqueId()).add(new FutureShot(projectileStats, location.clone().toVector().subtract(shooter.getLocation().clone().toVector()), charge));
        }
        return shoot(shooter, location, projectileStats, charge);
    }

    @SuppressWarnings("unused")
    public List<net.minecraft.server.v1_15_R1.Entity> shootProjectile(CorePlayer shooter, ProjectileStats projectileStats) {
        return shootProjectile(shooter, shooter.getPlayer().getEyeLocation().clone()
                .add(shooter.getPlayer().getLocation().getDirection()
                        .crossProduct(new org.bukkit.util.Vector(0, 1, 0)).normalize()
                        .multiply(0.15).add(new Vector(0, -0.15, 0))), projectileStats);
    }

    public List<net.minecraft.server.v1_15_R1.Entity> shootProjectile(CorePlayer shooter, Location location, ProjectileStats projectileStats) {
        if (projectileStats.repeat > 1) {
            if (!futureShots.containsKey(shooter.getUniqueId())) {
                futureShots.put(shooter.getUniqueId(), new HashSet<>());
            }
            futureShots.get(shooter.getUniqueId()).add(new FutureShot(projectileStats, location.clone().toVector().subtract(shooter.getLocation().clone().toVector()), 1));
        }
        return shoot(shooter, location, projectileStats, 1);
    }

    protected void updateFutureShots() {
        Iterator<Map.Entry<UUID, Set<FutureShot>>> it = futureShots.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<UUID, Set<FutureShot>> futureShots = it.next();
            Iterator<FutureShot> fsit = futureShots.getValue().iterator();
            while (fsit.hasNext()) {
                FutureShot futureShot = fsit.next();
                if (--futureShot.nextTicks <= 0) {
                    futureShot.nextTicks = futureShot.stats.repeatDelay;
                    futureShot.remaining--;
                    CorePlayer shooter = playerMap.get(futureShots.getKey()).getCorePlayer();
                    shoot(shooter, shooter.getLocation().add(futureShot.offset), futureShot.stats, futureShot.charge);
                    if (futureShot.remaining <= 0) {
                        fsit.remove();
                    }
                }
            }
            if (futureShots.getValue().isEmpty()) {
                it.remove();
            }
        }
    }

    public void stopFutureShots(CorePlayer shooter) {
        futureShots.remove(shooter.getUniqueId());
    }

    public boolean checkProjectileBlock(FakeEntity fakeEntity, CraftEntity craftEntity, BlockRaycastResult blockRaycastResult) {
        return false;
    }

    public void onProjectileBlockHit(CorePlayer shooter, BlockRaycastResult blockRaycastResult, ProjectileStats projectileStats) {

    }

}
