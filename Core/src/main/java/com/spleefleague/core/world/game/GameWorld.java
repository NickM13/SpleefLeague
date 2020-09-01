/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.world.game;

import com.google.common.collect.Sets;
import com.spleefleague.core.logger.CoreLogger;
import com.spleefleague.core.player.BattleState;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.util.variable.Position;
import com.spleefleague.core.world.*;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.spleefleague.core.Core;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

import com.spleefleague.core.world.game.projectile.ProjectileStats;
import net.minecraft.server.v1_16_R1.EntityPlayer;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Snow;
import org.bukkit.craftbukkit.v1_16_R1.CraftWorld;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

/**
 * @author NickM13
 */
public class GameWorld extends FakeWorld<GameWorldPlayer> {

    /**
     * Blocks that are added after a delay
     */
    protected static class FutureBlock {
        public long delay;
        public FakeBlock fakeBlock;

        public FutureBlock(long delay, FakeBlock fakeBlock) {
            this.delay = delay;
            this.fakeBlock = fakeBlock;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            FutureBlock that = (FutureBlock) o;

            if (delay != that.delay) return false;
            return Objects.equals(fakeBlock, that.fakeBlock);
        }

        @Override
        public int hashCode() {
            int result = (int) (delay ^ (delay >>> 32));
            result = 31 * result + (fakeBlock != null ? fakeBlock.hashCode() : 0);
            return result;
        }

    }

    /**
     * Player death effect
     */
    protected static class PlayerBlast {
        Location loc;
        int time;

        PlayerBlast(Location loc, int time) {
            this.loc = loc;
            this.time = time;
        }
    }

    protected final Set<Material> breakTools;
    protected final Set<Material> breakables;
    protected final Set<Material> unbreakables;
    protected boolean editable;

    protected final BukkitTask projectileCollideTask;
    protected Map<UUID, GameProjectile> projectiles;

    protected BukkitTask futureBlockTask;
    protected final Map<BlockPosition, SortedSet<FutureBlock>> futureBlocks;

    protected final Map<BlockPosition, FakeBlock> baseBlocks;
    protected final Map<BlockPosition, Long> baseBreakTimes;
    private static final Long AIR_REGEN = 15 * 1000L;
    private static final Long CONNECT_REGEN = 30 * 1000L;
    private static final Long RAND_REGEN = 30 * 1000L;
    private double regenSpeed = 1;

    protected final BukkitTask playerBlastTask;
    protected final List<PlayerBlast> playerBlasts;

    protected final List<BukkitTask> gameTasks;

    protected boolean showSpectators;
    protected final Map<UUID, CorePlayer> targetSpectatorMap = new HashMap<>();

    public GameWorld(World world) {
        super(1, world, GameWorldPlayer.class);
        breakTools = new HashSet<>();
        breakables = new HashSet<>();
        unbreakables = Sets.newHashSet(Material.CYAN_CONCRETE, Material.WHITE_STAINED_GLASS);
        editable = false;
        futureBlocks = new HashMap<>();
        baseBlocks = new HashMap<>();
        baseBreakTimes = new HashMap<>();
        projectiles = new HashMap<>();

        projectileCollideTask = Bukkit.getScheduler()
                .runTaskTimer(Core.getInstance(),
                        this::updateProjectiles, 0L, 1L);

        showSpectators = true;

        playerBlasts = new ArrayList<>();
        playerBlastTask = Bukkit.getScheduler()
                .runTaskTimer(Core.getInstance(),
                        this::updatePlayerBlasts, 0L, 2L);

        gameTasks = new ArrayList<>();

        futureBlockTask = Bukkit.getScheduler()
                .runTaskTimer(Core.getInstance(),
                        this::updateFutureBlocks, 0L, 2L);

        Core.getProtocolManager().addPacketListener(new PacketAdapter(Core.getInstance(), PacketType.Play.Server.SPAWN_ENTITY) {
            @Override
            public void onPacketSending(PacketEvent event) {
                PacketContainer packet = event.getPacket();
                Entity entity = packet.getEntityModifier(event).read(0);
                if (projectiles.containsKey(entity.getUniqueId()) &&
                        !getPlayerMap().containsKey(event.getPlayer().getUniqueId())) {
                    event.setCancelled(true);
                }
            }
        });

                /*
        Core.getProtocolManager().addPacketListener(new PacketAdapter(Core.getInstance(), PacketType.Play.Server.ENTITY_DESTROY) {
            @Override
            public void onPacketSending(PacketEvent event) {
                PacketContainer packet = event.getPacket();
                int[] entityIds = packet.getIntegerArrays().read(0);
                List<Integer> uncancelled = new ArrayList<>();
                for (int id : entityIds) {
                    net.minecraft.server.v1_16_R1.Entity entity = ((CraftWorld) event.getPlayer().getWorld()).getHandle().getEntity(id);
                    if (entity instanceof EntityPlayer) {

                    } else {
                        uncancelled.add(id);
                    }
                }
                int[] newIdArray = new int[uncancelled.size()];
                for (int i = 0; i < uncancelled.size(); i++) {
                    newIdArray[i] = uncancelled.get(i);
                }
                packet.getIntegerArrays().write(0, newIdArray);
            }
        });
                */
    }

    @Override
    public void destroy() {
        super.destroy();
        projectileCollideTask.cancel();
        futureBlockTask.cancel();
        playerBlastTask.cancel();
        clearProjectiles();
    }

    @Override
    public void clear() {
        super.clear();
        reset();
    }

    public void reset() {
        futureBlocks.clear();
        clearProjectiles();
    }

    public void runTask(BukkitTask task) {
        gameTasks.add(task);
    }

    /**
     * Attempt to break a block if the player is holding the right item
     * and the block is a breakable block, if fails send a fake packet
     * to the player to make sure the block doesn't disappear for them
     *
     * @param cp Core Player
     * @param pos Block Position
     */
    @Override
    protected boolean onBlockPunch(CorePlayer cp, BlockPosition pos) {
        if (!fakeBlocks.containsKey(pos) || fakeBlocks.get(pos).getBlockData().getMaterial().isAir()) return false;
        ItemStack heldItem = cp.getPlayer().getInventory().getItemInMainHand();
        if (editable
                && breakables.contains(fakeBlocks.get(pos).getBlockData().getMaterial())
                && breakTools.contains(heldItem.getType())) {
            for (FakeWorldPlayer fwp : playerMap.values()) {
                if (!fwp.getPlayer().equals(cp.getPlayer())) {
                    fwp.getPlayer().playSound(new Location(getWorld(), pos.getX(), pos.getY(), pos.getZ()), fakeBlocks.get(pos).getBreakSound(), 1, 1);
                }
            }
            breakBlock(pos, cp);
        } else {
            updateBlock(pos);
        }
        return true;
    }

    @Override
    public boolean breakBlock(BlockPosition pos, CorePlayer cp) {
        FakeBlock fb = fakeBlocks.get(pos);
        if (fb != null && !unbreakables.contains(fb.getBlockData().getMaterial()) && super.breakBlock(pos, cp)) {
            futureBlocks.remove(pos);
            if (cp != null && cp.getBattleState() == BattleState.BATTLER) {
                cp.getBattle().onBlockBreak(cp);
            }
            if (baseBlocks.containsKey(pos)) {
                baseBreakTimes.put(pos, System.currentTimeMillis() + (long) (Math.random() * RAND_REGEN));
            }
            return true;
        }
        return false;
    }

    /**
     * On player item use
     *
     * @param cp Core Player
     * @param blockPosition Click Block
     * @param blockRelative Placed Block
     * @return Cancel Event
     */
    @Override
    protected boolean onItemUse(CorePlayer cp, BlockPosition blockPosition, BlockPosition blockRelative) {
        return true;
    }

    protected void updateProjectiles() {
        projectiles.entrySet().removeIf(uuidGameProjectileEntry -> !uuidGameProjectileEntry.getValue().getEntity().isAlive());
    }

    protected void updatePlayerBlasts() {
        Iterator<PlayerBlast> pbit = playerBlasts.iterator();
        while (pbit.hasNext()) {
            PlayerBlast blast = pbit.next();
            playerMap.values().forEach(player -> {
                player.getPlayer().spawnParticle(Particle.SWEEP_ATTACK, blast.loc, 10, 0.5, 4, 0.5);
            });
            blast.time -= 2;
            if (blast.time < 0)
                pbit.remove();
            blast.loc.add(0, 4, 0);
        }
    }

    protected void updateFutureBlocks() {
        Iterator<Map.Entry<BlockPosition, SortedSet<FutureBlock>>> it = futureBlocks.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<BlockPosition, SortedSet<FutureBlock>> futureList = it.next();
            Iterator<FutureBlock> fbit = futureList.getValue().iterator();
            while (fbit.hasNext()) {
                FutureBlock futureBlock = fbit.next();
                futureBlock.delay -= 2;
                if (futureBlock.delay <= 0) {
                    setBlock(futureList.getKey(), futureBlock.fakeBlock.getBlockData());
                    updateBlock(futureList.getKey());
                    fbit.remove();
                } else if (futureBlock.delay < 7 && futureBlock.fakeBlock.getBlockData().getMaterial().equals(Material.SNOW_BLOCK)) {
                    Snow snow = (Snow) Material.SNOW.createBlockData();
                    snow.setLayers((int)(8 - futureBlock.delay));
                    setBlock(futureList.getKey(), snow);
                    updateBlock(futureList.getKey());
                }
            }
        }
    }

    public void setShowSpectators(boolean state) {
        showSpectators = state;
    }

    public void clearProjectiles() {
        for (GameProjectile gp : projectiles.values()) {
            gp.getEntity().killEntity();
        }
        projectiles.clear();
        for (BukkitTask task : gameTasks) {
            task.cancel();
        }
        gameTasks.clear();
        futureBlocks.clear();
    }

    public List<net.minecraft.server.v1_16_R1.Entity> shootProjectile(CorePlayer shooter, ProjectileStats fakeProjectile) {
        List<net.minecraft.server.v1_16_R1.Entity> entities = new ArrayList<>();
        try {
            for (int i = 0; i < fakeProjectile.count; i++) {
                net.minecraft.server.v1_16_R1.Entity entity = fakeProjectile.entityClass
                        .getDeclaredConstructor(GameWorld.class, CorePlayer.class, ProjectileStats.class)
                        .newInstance(this, shooter, fakeProjectile);
                projectiles.put(entity.getUniqueID(), new GameProjectile(entity, fakeProjectile));
                ((CraftWorld) getWorld()).getHandle().addEntity(entity);
                entities.add(entity);
            }
        } catch (InstantiationException | NoSuchMethodException | InvocationTargetException | IllegalAccessException exception) {
            CoreLogger.logError(exception);
        }
        return entities;
    }

    public List<net.minecraft.server.v1_16_R1.Entity> shootProjectile(Location location, ProjectileStats fakeProjectile) {
        List<net.minecraft.server.v1_16_R1.Entity> entities = new ArrayList<>();
        try {
            for (int i = 0; i < fakeProjectile.count; i++) {
                net.minecraft.server.v1_16_R1.Entity entity = fakeProjectile.entityClass
                        .getDeclaredConstructor(GameWorld.class, Location.class, ProjectileStats.class)
                        .newInstance(this, location, fakeProjectile);
                projectiles.put(entity.getUniqueID(), new GameProjectile(entity, fakeProjectile));
                ((CraftWorld) getWorld()).getHandle().addEntity(entity);
                entities.add(entity);
            }
        } catch (InstantiationException | NoSuchMethodException | InvocationTargetException | IllegalAccessException exception) {
            CoreLogger.logError(exception);
        }
        return entities;
    }
    
    public void doFailBlast(CorePlayer cp) {
        playerBlasts.add(new PlayerBlast(cp.getPlayer().getLocation(), 20));
        getPlayerMap().values().forEach(gwp ->
                gwp.getPlayer().playSound(gwp.getPlayer().getLocation(), Sound.ENTITY_DOLPHIN_DEATH, 15, 1));
    }
    
    public void addBreakTool(Material tool) {
        breakTools.add(tool);
    }
    
    public void addBreakableBlock(Material material) {
        breakables.add(material);
    }
    
    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    public boolean isEditable() {
        return editable;
    }

    public void setSpectator(CorePlayer spectator, CorePlayer target) {
        spectator.getPlayer().teleport(target.getPlayer().getLocation());
        spectator.getPlayer().setGameMode(GameMode.SPECTATOR);
        spectator.getPlayer().setSpectatorTarget(target.getPlayer());
    }

    public Set<Material> getBreakables() {
        return breakables;
    }

    /**
     * Sets a block to spawn after a delay based on its distance
     * from the positions
     *
     * @param blockPos Block Position
     * @param blockData Block Data
     * @param secondsPerBlock Seconds per block distance from locations
     * @param positions Positions
     */
    public void setBlockDelayed(BlockPosition blockPos, BlockData blockData, double secondsPerBlock, List<Position> positions) {
        if (futureBlocks.containsKey(blockPos)) return;
        Random random = new Random();
        double closest = -1;
        for (Position pos : positions) {
            double d = pos.distance(new Position(blockPos.getX(), (blockPos.getY() - pos.getY()) * 8 + pos.getY(), blockPos.getZ()));
            if (d < closest || closest < 0) {
                closest = d;
            }
        }
        closest -= 6;
        if (closest < 0) {
            setBlock(blockPos, blockData);
        } else {
            closest += random.nextInt(3);
            //futureBlocks.put(blockPos, new FutureBlock((long) ((closest) * secondsPerBlock * 20D), new FakeBlock(blockData)));
            setBlockDelayed(blockPos, blockData, (long) (closest * secondsPerBlock * 20D));
        }
    }

    /**
     * Sets a block to spawn after a delay
     *
     * @param blockPos Block Position
     * @param blockData Block Data
     * @param delayTicks Ticks to delay by
     */
    public void setBlockDelayed(BlockPosition blockPos, BlockData blockData, long delayTicks) {
        futureBlocks.put(blockPos, new TreeSet<>((l, r) -> (int) (l.delay - r.delay)));
        futureBlocks.get(blockPos).add(new FutureBlock(delayTicks, new FakeBlock(blockData)));
    }

    /**
     * Adds a block to the blockPos list spawn after a delay
     *
     * @param blockPos Block Position
     * @param blockData Block Data
     * @param delayTicks Ticks to delay by
     */
    public void addBlockDelayed(BlockPosition blockPos, BlockData blockData, long delayTicks) {
        if (!futureBlocks.containsKey(blockPos)) {
            futureBlocks.put(blockPos, new TreeSet<>((l, r) -> (int) (l.delay - r.delay)));
        }
        futureBlocks.get(blockPos).add(new FutureBlock(delayTicks, new FakeBlock(blockData)));
    }

    public void clearBlockDelayed(BlockPosition blockPos) {
        futureBlocks.put(blockPos, new TreeSet<>((l, r) -> (int) (l.delay - r.delay)));
    }

    public Map<BlockPosition, FakeBlock> getBaseBlocks() {
        return baseBlocks;
    }

    public void clearBaseBlocks() {
        baseBlocks.clear();
    }

    public void setBaseBlocks(Map<BlockPosition, FakeBlock> blocks) {
        baseBlocks.putAll(blocks);
    }

    public FakeBlock getBaseBlock(BlockPosition pos) {
        return baseBlocks.get(pos);
    }

    public void regenerateBlocks(BlockPosition pos, double radius) {
        double dx, dy, dz;
        for (int x = -(int) Math.ceil(radius); x <= (int) Math.ceil(radius); x++) {
            dx = ((double)x) / radius;
            for (int y = -(int) Math.ceil(radius); y <= (int) Math.ceil(radius); y++) {
                dy = ((double)y) / radius;
                for (int z = -(int) Math.ceil(radius); z <= (int) Math.ceil(radius); z++) {
                    dz = ((double)z) / radius;
                    double dist = Math.sqrt(dx*dx + dy*dy + dz*dz);
                    if (dist < 1) {
                        BlockPosition pos2 = pos.add(new BlockPosition(x, y, z));
                        FakeBlock fb = baseBlocks.get(pos2);
                        if (fb != null && (!fakeBlocks.containsKey(pos2) || fakeBlocks.get(pos2).getBlockData().getMaterial().isAir())) {
                            setBlock(pos2, fb.getBlockData());
                            baseBreakTimes.remove(pos);
                        }
                    }
                }
            }
        }
    }

    private static final Set<BlockPosition> relatives = Sets.newHashSet(
            new BlockPosition(-1, 0, 0),
            new BlockPosition(1, 0, 0),
            new BlockPosition(0, -1, 0),
            new BlockPosition(0, 1, 0),
            new BlockPosition(0, 0, -1),
            new BlockPosition(0, 0, 1)
    );

    private boolean isConnected(BlockPosition pos) {
        for (BlockPosition relative : relatives) {
            FakeBlock fb = fakeBlocks.get(pos.add(relative));
            if (fb != null && !fb.getBlockData().getMaterial().isAir()) {
                return true;
            }
        }
        return false;
    }

    public void setRegenSpeed(double regenSpeed) {
        this.regenSpeed = regenSpeed;
    }

    public void performBaseBreakRegen() {
        Iterator<Map.Entry<BlockPosition, Long>> it = baseBreakTimes.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<BlockPosition, Long> entry = it.next();
            if (System.currentTimeMillis() - entry.getValue() > AIR_REGEN / regenSpeed ||
                    (System.currentTimeMillis() - entry.getValue() > CONNECT_REGEN / regenSpeed && isConnected(entry.getKey()))) {
                setBlock(entry.getKey(), baseBlocks.get(entry.getKey()).getBlockData());
                it.remove();
            }
        }
    }

    public Map<BlockPosition, Long> getBaseBreakTimes() {
        return baseBreakTimes;
    }

    /**
     * Sets a fake block status in the world with the option to
     * instantly update and display or not
     *
     * @param pos          Block Position
     * @param blockData    Block Data
     * @param ignoreUpdate Should Send Packet
     */
    @Override
    public void setBlock(BlockPosition pos, BlockData blockData, boolean ignoreUpdate) {
        super.setBlock(pos, blockData, ignoreUpdate);
    }

    public void sendPacket(PacketContainer packet) {
        for (GameWorldPlayer gwp : playerMap.values()) {
            Core.sendPacket(gwp.getPlayer(), packet);
        }
    }

}
