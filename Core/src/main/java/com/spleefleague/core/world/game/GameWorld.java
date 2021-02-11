/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.world.game;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.spleefleague.core.logger.CoreLogger;
import com.spleefleague.core.player.BattleState;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.util.variable.BlockRaycastResult;
import com.spleefleague.core.util.variable.Dimension;
import com.spleefleague.core.util.variable.Point;
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

import com.spleefleague.core.world.game.projectile.FakeEntity;
import com.spleefleague.core.world.game.projectile.FakeEntitySnowball;
import com.spleefleague.core.world.game.projectile.ProjectileStats;
import com.spleefleague.core.world.game.projectile.ProjectileWorld;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Snow;
import org.bukkit.craftbukkit.v1_15_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

/**
 * @author NickM13
 */
public class GameWorld extends ProjectileWorld<GameWorldPlayer> {

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

    protected BukkitTask futureBlockTask;
    protected final Map<BlockPosition, SortedSet<FutureBlock>> futureBlocks;

    protected BukkitTask burningTask;
    protected class BurningBlock {
        int life;
        int fuel;

        BurningBlock(int life, int fuel) {
            this.life = life;
            this.fuel = fuel;
        }

        public void subtract() {
            life--;
            fuel--;
        }
    }
    protected final Map<BlockPosition, BurningBlock> burningBlocks;

    protected final Map<BlockPosition, FakeBlock> baseBlocks;
    protected final Map<BlockPosition, Long> baseBreakTimes;
    private static final Long AIR_REGEN = 15 * 1000L;
    private static final Long CONNECT_REGEN = 30 * 1000L;
    private static final Long RAND_REGEN = 30 * 1000L;
    private double regenSpeed = 1;

    protected final BukkitTask playerBlastTask;
    protected final List<PlayerBlast> playerBlasts;

    protected final BukkitTask playerPortalTask;
    protected final Map<UUID, PortalPair> playerPortals;

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
        burningBlocks = new HashMap<>();
        baseBlocks = new HashMap<>();
        baseBreakTimes = new HashMap<>();

        showSpectators = true;

        playerBlasts = new ArrayList<>();
        playerBlastTask = Bukkit.getScheduler()
                .runTaskTimer(Core.getInstance(),
                        this::updatePlayerBlasts, 0L, 2L);

        playerPortals = new HashMap<>();
        playerPortalTask = Bukkit.getScheduler()
                .runTaskTimer(Core.getInstance(),
                        this::updatePlayerPortals, 0L, 2L);

        gameTasks = new ArrayList<>();

        futureBlockTask = Bukkit.getScheduler()
                .runTaskTimer(Core.getInstance(),
                        this::updateFutureBlocks, 0L, 2L);

        burningTask = Bukkit.getScheduler()
                .runTaskTimerAsynchronously(Core.getInstance(),
                        this::updateBurningBlocks, 0L, 5L);
    }

    @Override
    public void destroy() {
        super.destroy();
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

    private static final BlockData CORRODE_BLOCK = Material.MAGENTA_CONCRETE.createBlockData();

    /**
     * Corrodes blocks in a radius
     *
     * @param pos Origin
     * @param radius Radius
     * @return Number of Successes
     */
    public int corrodeBlocks(BlockPosition pos, double radius, double percent) {
        double dx, dy, dz;
        int corroded = 0;
        Random random = new Random();
        for (int x = -(int) Math.ceil(radius); x <= (int) Math.ceil(radius); x++) {
            dx = ((double)x) / radius;
            for (int y = -(int) Math.ceil(radius); y <= (int) Math.ceil(radius); y++) {
                dy = ((double)y) / radius;
                for (int z = -(int) Math.ceil(radius); z <= (int) Math.ceil(radius); z++) {
                    dz = ((double)z) / radius;
                    if (Math.sqrt(dx*dx + dy*dy + dz*dz) < 1
                            && random.nextDouble() <= percent) {
                        corroded += corrodeBlock(pos.add(new BlockPosition(x, y, z))) ? 1 : 0;
                    }
                }
            }
        }
        return corroded;
    }

    /**
     * Corrodes a block
     *
     * @param pos Block Position
     * @return Success
     */
    public boolean corrodeBlock(BlockPosition pos) {
        FakeBlock fb = fakeBlocks.get(pos);
        if (fb != null && !fb.getBlockData().getMaterial().equals(Material.AIR) && !fb.getBlockData().equals(CORRODE_BLOCK)) {
            getPlayerMap().values().forEach(fwp -> {
                fwp.getPlayer().spawnParticle(Particle.BLOCK_DUST, pos.toLocation(getWorld()).add(0.5, 0.5, 0.5), 20, 0.25, 0.25, 0.25, Material.PURPLE_CONCRETE_POWDER.createBlockData());
                fwp.getPlayer().playSound(pos.toLocation(getWorld()), Sound.ENTITY_SILVERFISH_STEP, 2, 0.75f);
            });
            setBlock(pos, CORRODE_BLOCK);
            setBlockDelayed(pos, Material.AIR.createBlockData(), 20);
            return true;
        } else {
            updateBlock(pos);
        }
        return false;
    }

    private static final BlockData WARMING_BLOCK = Material.ORANGE_CONCRETE_POWDER.createBlockData();
    private static final BlockData BURNING_BLOCK = Material.RED_CONCRETE_POWDER.createBlockData();
    private static final int BURN_TIME = 10;

    /**
     * Burns blocks in a radius
     *
     * @param pos Origin
     * @param radius Radius
     * @return Number of Successes
     */
    public int burnBlocks(BlockPosition pos, double radius, double percent) {
        double dx, dy, dz;
        int burnt = 0;
        Random random = new Random();
        for (int x = -(int) Math.ceil(radius); x <= (int) Math.ceil(radius); x++) {
            dx = ((double)x) / radius;
            for (int y = -(int) Math.ceil(radius); y <= (int) Math.ceil(radius); y++) {
                dy = ((double)y) / radius;
                for (int z = -(int) Math.ceil(radius); z <= (int) Math.ceil(radius); z++) {
                    dz = ((double)z) / radius;
                    if (Math.sqrt(dx*dx + dy*dy + dz*dz) < 1
                            && random.nextDouble() <= percent) {
                        burnt += burnBlock(pos.add(new BlockPosition(x, y, z)), BURN_TIME) ? 1 : 0;
                    }
                }
            }
        }
        return burnt;
    }

    /**
     * Burns a block
     *
     * @param pos Block Position
     * @return Success
     */
    public boolean burnBlock(BlockPosition pos, int fuel) {
        FakeBlock fb = fakeBlocks.get(pos);
        if (fb != null && !fb.getBlockData().getMaterial().equals(Material.AIR)) {
            if (burningBlocks.containsKey(pos)) {
                burningBlocks.get(pos).fuel = Math.max(burningBlocks.get(pos).fuel, fuel);
                return false;
            }
            getPlayerMap().values().forEach(fwp -> {
                fwp.getPlayer().spawnParticle(Particle.BLOCK_DUST, pos.toLocation(getWorld()).add(0.5, 0.5, 0.5), 20, 0.25, 0.25, 0.25, Material.RED_CONCRETE_POWDER.createBlockData());
                fwp.getPlayer().playSound(pos.toLocation(getWorld()), Sound.ENTITY_BLAZE_SHOOT, 0.25f, 0.5f);
            });
            burningBlocks.put(pos, new BurningBlock(BURN_TIME, fuel));
            setBlock(pos, WARMING_BLOCK);
            return true;
        } else {
            updateBlock(pos);
        }
        return false;
    }

    private static final BlockData ICED_BLOCK = Material.BLUE_ICE.createBlockData();

    /**
     * Burns blocks in a radius
     *
     * @param pos Origin
     * @param radius Radius
     * @return Number of Successes
     */
    public int iceBlocks(BlockPosition pos, double radius, double percent) {
        if (fakeBlocks.containsKey(pos) && fakeBlocks.get(pos).getBlockData().equals(ICED_BLOCK)) {
            Bukkit.getScheduler().runTaskAsynchronously(Core.getInstance(), () -> {
                Set<BlockPosition> whitelist = new HashSet<>();
                Set<BlockPosition> blacklist = new HashSet<>();
                Set<BlockPosition> checking = new HashSet<>();
                checking.add(pos);
                while (!checking.isEmpty()) {
                    BlockPosition check = checking.iterator().next();
                    if (fakeBlocks.containsKey(check) && fakeBlocks.get(check).getBlockData().equals(ICED_BLOCK)) {
                        whitelist.add(check);
                        attemptIceBlock(check.add(new BlockPosition(1, 0, 0)), blacklist, checking);
                        attemptIceBlock(check.add(new BlockPosition(0, 1, 0)), blacklist, checking);
                        attemptIceBlock(check.add(new BlockPosition(0, 0, 1)), blacklist, checking);
                        attemptIceBlock(check.add(new BlockPosition(-1, 0, 0)), blacklist, checking);
                        attemptIceBlock(check.add(new BlockPosition(0, -1, 0)), blacklist, checking);
                        attemptIceBlock(check.add(new BlockPosition(0, 0, -1)), blacklist, checking);
                    } else {
                        blacklist.add(check);
                    }
                    checking.remove(check);
                }
                Bukkit.getScheduler().runTask(Core.getInstance(), () -> {
                    for (BlockPosition pos2 : whitelist) {
                        breakBlock(pos2, null);
                    }
                });
            });
            return 1;
        } else {
            double dx, dy, dz;
            int iced = 0;
            Random random = new Random();
            for (int x = -(int) Math.ceil(radius); x <= (int) Math.ceil(radius); x++) {
                dx = ((double) x) / radius;
                for (int y = -(int) Math.ceil(radius); y <= (int) Math.ceil(radius); y++) {
                    dy = ((double) y) / radius;
                    for (int z = -(int) Math.ceil(radius); z <= (int) Math.ceil(radius); z++) {
                        dz = ((double) z) / radius;
                        if (Math.sqrt(dx * dx + dy * dy + dz * dz) < 1
                                && random.nextDouble() <= percent) {
                            iced += iceBlock(pos.add(new BlockPosition(x, y, z))) ? 1 : 0;
                        }
                    }
                }
            }
            return iced;
        }
    }

    private void attemptIceBlock(BlockPosition pos, Set<BlockPosition> blacklist, Set<BlockPosition> checking) {
        if (!blacklist.contains(pos)) {
            checking.add(pos);
            blacklist.add(pos);
        }
    }

    /**
     * Burns a block
     *
     * @param pos Block Position
     * @return Success
     */
    public boolean iceBlock(BlockPosition pos) {
        FakeBlock fb = fakeBlocks.get(pos);
        if (fb != null && !fb.getBlockData().getMaterial().equals(Material.AIR)) {
            getPlayerMap().values().forEach(fwp -> {
                fwp.getPlayer().spawnParticle(Particle.BLOCK_DUST, pos.toLocation(getWorld()).add(0.5, 0.5, 0.5), 20, 0.25, 0.25, 0.25, Material.LIGHT_BLUE_CONCRETE_POWDER.createBlockData());
                fwp.getPlayer().playSound(pos.toLocation(getWorld()), Sound.ENTITY_TURTLE_EGG_CRACK, 1, 1.25f);
            });
            setBlock(pos, ICED_BLOCK);
            return true;
        } else {
            updateBlock(pos);
        }
        return false;
    }

    /**
     * Burns blocks in a radius
     *
     * @param pos Origin
     * @param radius Radius
     * @return Number of Successes
     */
    public int breakRegenBlocks(BlockPosition pos, double radius, double percent) {
        double dx, dy, dz;
        int broken = 0;
        Random random = new Random();
        for (int x = -(int) Math.ceil(radius); x <= (int) Math.ceil(radius); x++) {
            dx = ((double)x) / radius;
            for (int y = -(int) Math.ceil(radius); y <= (int) Math.ceil(radius); y++) {
                dy = ((double)y) / radius;
                for (int z = -(int) Math.ceil(radius); z <= (int) Math.ceil(radius); z++) {
                    dz = ((double)z) / radius;
                    if (Math.sqrt(dx*dx + dy*dy + dz*dz) < 1
                            && random.nextDouble() <= percent) {
                        broken += breakRegenBlock(pos.add(new BlockPosition(x, y, z))) ? 1 : 0;
                    }
                }
            }
        }
        return broken;
    }

    /**
     * Burns a block
     *
     * @param pos Block Position
     * @return Success
     */
    public boolean breakRegenBlock(BlockPosition pos) {
        FakeBlock fb = fakeBlocks.get(pos);
        if (fb != null && !fb.getBlockData().getMaterial().equals(Material.AIR)) {
            getPlayerMap().values().forEach(fwp -> {
                fwp.getPlayer().spawnParticle(Particle.END_ROD, pos.toLocation(getWorld()).add(0.5, 0.5, 0.5), 10, 0.25, 0.25, 0.25);
                fwp.getPlayer().playSound(pos.toLocation(getWorld()), Sound.ENTITY_ENDER_EYE_DEATH, 1, 1.5f);
            });
            breakBlock(pos, null);
            if (baseBlocks.containsKey(pos)) {
                setBlockDelayed(pos, baseBlocks.get(pos).getBlockData(), 100);
            } else {

            }
            return true;
        } else {
            updateBlock(pos);
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

    protected void updatePlayerBlasts() {
        Iterator<PlayerBlast> pbit = playerBlasts.iterator();
        while (pbit.hasNext()) {
            PlayerBlast blast = pbit.next();
            playerMap.values().forEach(player -> player.getPlayer().spawnParticle(Particle.SWEEP_ATTACK, blast.loc, 10, 0.5, 4, 0.5));
            blast.time -= 2;
            if (blast.time < 0)
                pbit.remove();
            blast.loc.add(0, 4, 0);
        }
    }

    private static final List<Color> colors = Lists.newArrayList(
            Color.BLUE, Color.AQUA,
            Color.RED, Color.YELLOW,
            Color.GREEN, Color.TEAL,
            Color.BLACK, Color.WHITE);

    private int portalPhysicCheck = 0;

    protected void updatePlayerPortals() {
        for (Map.Entry<UUID, PortalPair> entry : playerPortals.entrySet()) {
            PortalPair portal = entry.getValue();
            int i = portal.colorIndex;
            if (portal.isLinked()) {
                GameWorldPlayer gwp = getPlayerMap().get(entry.getKey());
                if (gwp != null) {
                    Location toCompare = gwp.getPlayer().getLocation().clone().add(0, 1, 0);
                    if (portal.canTeleport1(gwp.getPlayer().getUniqueId()) && toCompare.distance(portal.getPortal1().getTpLoc()) < 1.5 && gwp.portal()) {
                        portal.teleportTo2(gwp.getPlayer());
                    } else if (portal.canTeleport2(gwp.getPlayer().getUniqueId()) && toCompare.distance(portal.getPortal2().getTpLoc()) < 1.5 && gwp.portal()) {
                        portal.teleportTo1(gwp.getPlayer());
                    }
                }
                GameUtils.spawnRingParticles(
                        this,
                        portal.getPortal1().getVisualPos(),
                        portal.getPortal1().getBlockFace().getDirection(),
                        new Particle.DustOptions(colors.get((i * 2) % colors.size()), 1.f),
                        0.5, 5);
                GameUtils.spawnRingParticles(
                        this,
                        portal.getPortal2().getVisualPos(),
                        portal.getPortal2().getBlockFace().getDirection(),
                        new Particle.DustOptions(colors.get((i * 2 + 1) % colors.size()), 1.f),
                        0.5, 5);
            } else {
                if (portal.getPortal1() != null) {
                    GameUtils.spawnRingParticles(
                            this,
                            portal.getPortal1().getVisualPos(),
                            portal.getPortal1().getBlockFace().getDirection(),
                            new Particle.DustOptions(colors.get((i * 2) % colors.size()), 1.5f),
                            0.25, 1);
                }
                if (portal.getPortal2() != null) {
                    GameUtils.spawnRingParticles(
                            this,
                            portal.getPortal2().getVisualPos(),
                            portal.getPortal2().getBlockFace().getDirection(),
                            new Particle.DustOptions(colors.get((i * 2 + 1) % colors.size()), 1.5f),
                            0.25, 1);
                }
            }
            i++;
        }
        portalPhysicCheck++;
        if (portalPhysicCheck > 5) {
            for (PortalPair portal : playerPortals.values()) {
                PortalPair.Portal portal1 = portal.getPortal1();
                PortalPair.Portal portal2 = portal.getPortal2();
                if (portal1 != null && (!isBlockSolid(portal1.getBlockPos()) || isBlockSolid(portal1.getRelativePos()))) {
                    portal.popPortal1();
                }
                if (portal2 != null && (!isBlockSolid(portal2.getBlockPos()) || isBlockSolid(portal2.getRelativePos()))) {
                    portal.popPortal2();
                }
            }
        }
    }

    protected void updateFutureBlocks() {
        for (Map.Entry<BlockPosition, SortedSet<FutureBlock>> futureList : futureBlocks.entrySet()) {
            Iterator<FutureBlock> fbit = futureList.getValue().iterator();
            while (fbit.hasNext()) {
                FutureBlock futureBlock = fbit.next();
                futureBlock.delay -= 2;
                if (futureBlock.delay <= 0) {
                    setBlock(futureList.getKey(), futureBlock.fakeBlock.getBlockData());
                    fbit.remove();
                } else if (futureBlock.delay < 7) {
                    Material futureMat = futureBlock.fakeBlock.getBlockData().getMaterial();
                    if (futureMat.equals(Material.SNOW_BLOCK)) {
                        Snow snow = (Snow) Material.SNOW.createBlockData();
                        snow.setLayers((int) (8 - futureBlock.delay));
                        setBlock(futureList.getKey(), snow);
                    } else {
                        if (fakeBlocks.containsKey(futureList.getKey())) {
                            Material fakeMat = fakeBlocks.get(futureList.getKey()).getBlockData().getMaterial();
                            if (futureMat.isAir() && (fakeMat.equals(Material.SNOW) || fakeMat.equals(Material.SNOW_BLOCK))) {
                                Snow snow = (Snow) Material.SNOW.createBlockData();
                                snow.setLayers((int) (futureBlock.delay));
                                setBlock(futureList.getKey(), snow);
                            }
                        }
                    }
                }
            }
        }
    }

    private void attemptBurn(BlockPosition pos, int fuel) {
        if (Math.random() < 0.20) {
            burnBlock(pos, fuel);
        }
    }

    private class AttemptBurnObj {
        BlockPosition pos;
        int fuel;
        public AttemptBurnObj(BlockPosition pos, int fuel) {
            this.pos = pos;
            this.fuel = fuel;
        }
    }

    public void updateBurningBlocks() {
        Iterator<Map.Entry<BlockPosition, BurningBlock>> it = burningBlocks.entrySet().iterator();
        List<AttemptBurnObj> toAttempt = new ArrayList<>();
        Set<BlockPosition> toBreak = new HashSet<>();
        Set<BlockPosition> toBurn = new HashSet<>();
        Set<BlockPosition> toParticle = new HashSet<>();
        while (it.hasNext()) {
            Map.Entry<BlockPosition, BurningBlock> burningBlock = it.next();
            FakeBlock fb = fakeBlocks.get(burningBlock.getKey());
            if (fb != null && (fb.getBlockData().equals(WARMING_BLOCK) || fb.getBlockData().equals(BURNING_BLOCK))) {
                burningBlock.getValue().subtract();
                if (burningBlock.getValue().fuel > 0) {
                    toAttempt.add(new AttemptBurnObj(burningBlock.getKey().add(new BlockPosition(1, 0, 0)), burningBlock.getValue().fuel));
                    toAttempt.add(new AttemptBurnObj(burningBlock.getKey().add(new BlockPosition(0, 1, 0)), burningBlock.getValue().fuel));
                    toAttempt.add(new AttemptBurnObj(burningBlock.getKey().add(new BlockPosition(0, 0, 1)), burningBlock.getValue().fuel));
                    toAttempt.add(new AttemptBurnObj(burningBlock.getKey().add(new BlockPosition(-1, 0, 0)), burningBlock.getValue().fuel));
                    toAttempt.add(new AttemptBurnObj(burningBlock.getKey().add(new BlockPosition(0, -1, 0)), burningBlock.getValue().fuel));
                    toAttempt.add(new AttemptBurnObj(burningBlock.getKey().add(new BlockPosition(0, 0, -1)), burningBlock.getValue().fuel));
                }
                if (burningBlock.getValue().life < 0) {
                    toBreak.add(burningBlock.getKey());
                    it.remove();
                } else {
                    if (burningBlock.getValue().life == 3) {
                        toBurn.add(burningBlock.getKey());
                    }
                    toParticle.add(burningBlock.getKey());
                }
            } else {
                it.remove();
            }
        }
        Bukkit.getScheduler().runTask(Core.getInstance(), () -> {
            for (AttemptBurnObj obj : toAttempt) {
                attemptBurn(obj.pos, obj.fuel);
            }
            for (BlockPosition pos : toBreak) {
                breakBlock(pos, null);
            }
            for (BlockPosition pos : toBurn) {
                setBlock(pos, BURNING_BLOCK);
            }
            for (BlockPosition pos : toParticle) {
                spawnParticles(Particle.SMOKE_NORMAL, pos.getX(), pos.getY() + 1, pos.getZ(), 10);
            }
        });
    }

    public void setShowSpectators(boolean state) {
        showSpectators = state;
    }

    public void clearProjectiles() {
        super.clearProjectiles();
        for (BukkitTask task : gameTasks) {
            task.cancel();
        }
        gameTasks.clear();
        futureBlocks.clear();
        futureShots.clear();
    }

    public void portalize(FakeEntity entity, Location location, Vector velocity) {
        try {
            FakeEntity newEntity = (FakeEntitySnowball) entity.getStats().entityClass
                    .getDeclaredConstructor(GameWorld.class, CorePlayer.class, Location.class, ProjectileStats.class, Double.class)
                    .newInstance(this, entity.getCpShooter(), location, entity.getStats(), 1D);
            projectiles.put(newEntity.getEntity().getUniqueID(), new GameProjectile(newEntity.getEntity(), entity.getStats()));
            newEntity.getEntity().setMot(velocity.getX(), velocity.getY(), velocity.getZ());
            newEntity.reducedStats(entity);
            ((CraftWorld) getWorld()).getHandle().addEntity(newEntity.getEntity());
            entity.getEntity().killEntity();
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException exception) {
            CoreLogger.logError(exception);
        }
    }

    public Map<UUID, PortalPair> getPlayerPortals() {
        return playerPortals;
    }

    public void createPortal(CorePlayer shooter, BlockPosition pos, BlockFace face) {
        if (!playerPortals.containsKey(shooter.getUniqueId())) {
            playerPortals.put(shooter.getUniqueId(), new PortalPair(playerPortals.size()));
        }
        playerPortals.get(shooter.getUniqueId()).pushPortal(getWorld(), pos, face);
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
        //spectator.getPlayer().setGameMode(GameMode.SPECTATOR);
        //spectator.getPlayer().setSpectatorTarget(target.getPlayer());
    }

    public Set<Material> getBreakables() {
        return breakables;
    }

    public void setTempBlock(BlockPosition blockPos, BlockData blockData, long ticks, boolean onlyAir) {
        if (futureBlocks.containsKey(blockPos)) return;
        if (onlyAir && fakeBlocks.containsKey(blockPos)) return;
        FakeBlock prev = fakeBlocks.get(blockPos);
        if (setBlock(blockPos, blockData)) {
            setBlockDelayed(blockPos, prev != null ? prev.getBlockData() : Material.AIR.createBlockData(), ticks);
        }
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

    public boolean hasBlockDelayed(BlockPosition blockPos) {
        return futureBlocks.containsKey(blockPos) && !futureBlocks.get(blockPos).isEmpty();
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

    private static final long INDICATOR_DELAY = 60;
    private long indicatorTick = 0;
    private long decayTick = 0;
    private long decayTotalTicks = 0;
    private long decayDelayStart = 0;
    private final Map<Long, Set<BlockPosition>> decayBlocks = new HashMap<>();

    /**
     * Times should be in ticks
     *
     * @param startDelay Ticks
     * @param duration Ticks
     */
    public void enableDecay(long startDelay, long duration) {
        decayDelayStart = Math.max(0, startDelay) / 2;
        // TODO: Rework to just take the transformed structure boundary boxes
        int lowX, lowY, lowZ, highX, highY, highZ;
        lowX = lowY = lowZ = Integer.MAX_VALUE;
        highX = highY = highZ = Integer.MIN_VALUE;
        decayTotalTicks = duration / 2;
        for (long i = 0; i < decayTotalTicks; i++) {
            decayBlocks.put(i, new HashSet<>());
        }
        for (BlockPosition pos : baseBlocks.keySet()) {
            lowX = Math.min(pos.getX(), lowX);
            lowY = Math.min(pos.getY(), lowY);
            lowZ = Math.min(pos.getZ(), lowZ);
            highX = Math.max(pos.getX(), highX);
            highY = Math.max(pos.getY(), highY);
            highZ = Math.max(pos.getZ(), highZ);
        }
        Dimension boundingBox = new Dimension(
                new Point(lowX - 0.5, lowY - 0.5, lowZ - 0.5),
                new Point(highX + 0.5, highY + 0.5, highZ + 0.5));
        Point lowCenter = boundingBox.getLowCenter();
        int layerHeight = 10;
        double furthestHoriz = lowCenter.distance(boundingBox.getLow());
        double furthestVerti = (boundingBox.getHigh().getY() - boundingBox.getLow().getY()) / layerHeight;
        double furthest = 1 + furthestVerti;
        for (BlockPosition pos : baseBlocks.keySet()) {
            double dx = pos.getX() - lowCenter.getX();
            double dy = pos.getY() - lowCenter.getY();
            double dz = pos.getZ() - lowCenter.getZ();

            double distH = Math.sqrt(dx * dx + dz * dz) / furthestHoriz;
            double distV = dy / layerHeight;

            double percentDist = 1 - (distH + distV) / furthest;

            long tick = (long) (percentDist * decayTotalTicks);
            if (tick < 0 || tick > decayTotalTicks) {
                continue;
            }
            decayBlocks.get(tick).add(pos);
        }

        decayTick = -decayDelayStart;
        indicatorTick = decayTick + INDICATOR_DELAY / 2;

        BukkitTask indicatorTask = Bukkit.getScheduler().runTaskTimer(Core.getInstance(), () -> {
            if (indicatorTick < 0) {
                indicatorTick++;
                return;
            }
            if (indicatorTick > decayTotalTicks) return;
            if (decayBlocks.containsKey(indicatorTick)) {
                replaceBlocks(decayBlocks.get(indicatorTick), INDICATOR);
            }
            indicatorTick++;
        }, 2, 2);
        gameTasks.add(indicatorTask);

        BukkitTask decayTask = Bukkit.getScheduler().runTaskTimer(Core.getInstance(), () -> {
            if (decayTick < 0) {
                decayTick++;
                return;
            }
            if (decayTick > decayTotalTicks) return;
            if (decayBlocks.containsKey(decayTick)) {
                replaceBlocks(decayBlocks.get(decayTick), AIR);
                for (BlockPosition pos : decayBlocks.get(decayTick)) {
                    baseBreakTimes.remove(pos);
                    futureBlocks.remove(pos);
                }
            }
            decayTick++;
        }, 2, 2);
        gameTasks.add(decayTask);
    }

    public long getDecayDelayStartRemainingSeconds() {
        return -decayTick / 10;
    }

    public boolean isDecaying() {
        return decayTick > 0;
    }

    public long getDecayRemainSeconds() {
        return (decayTotalTicks - decayTick) / 10;
    }

    public float getDecayPercent() {
        return ((float) decayTick / decayTotalTicks);
    }

    private static final BlockData AIR = Material.AIR.createBlockData();
    private static final BlockData INDICATOR = Material.RED_CONCRETE.createBlockData();

    public void decayBlock(BlockPosition pos) {
        setBlock(pos, AIR);
        baseBreakTimes.remove(pos);
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

    public void sendPacket(PacketContainer packet) {
        for (GameWorldPlayer gwp : playerMap.values()) {
            Core.sendPacket(gwp.getPlayer(), packet);
        }
    }

    @Override
    public boolean checkProjectileBlock(FakeEntity fakeEntity, CraftEntity craftEntity, BlockRaycastResult blockRaycastResult) {
        for (PortalPair pair : getPlayerPortals().values()) {
            if (pair.isLinked()) {
                if (pair.getPortal1().getBlockPos().equals(blockRaycastResult.getBlockPos()) && pair.getPortal1().getBlockFace().equals(blockRaycastResult.getFace())) {
                    portalize(fakeEntity, pair.getPortal2().getTpLoc(), pair.getNewVelocityTo2(craftEntity));
                    return true;
                } else if (pair.getPortal2().getBlockPos().equals(blockRaycastResult.getBlockPos()) && pair.getPortal2().getBlockFace().equals(blockRaycastResult.getFace())) {
                    portalize(fakeEntity, pair.getPortal1().getTpLoc(), pair.getNewVelocityTo1(craftEntity));
                    return true;
                }
            }
        }
        return false;
    }

    public void onProjectileBlockHit(CorePlayer shooter, BlockRaycastResult blockRaycastResult, ProjectileStats projectileStats) {
        switch (projectileStats.breakStyle) {
            case DEFAULT:
                shooter.getStatistics().add("splegg", "blocksBroken", breakBlocks(blockRaycastResult.getBlockPos(), projectileStats.breakRadius, projectileStats.breakPercent));
                break;
            case CORROSIVE:
                shooter.getStatistics().add("splegg", "blocksCorroded", corrodeBlocks(blockRaycastResult.getBlockPos(), projectileStats.breakRadius, projectileStats.breakPercent));
                break;
            case FIRE:
                shooter.getStatistics().add("splegg", "blocksBurnt", burnBlocks(blockRaycastResult.getBlockPos(), projectileStats.breakRadius, projectileStats.breakPercent));
                break;
            case ICE:
                shooter.getStatistics().add("splegg", "blocksIced", iceBlocks(blockRaycastResult.getBlockPos(), projectileStats.breakRadius, projectileStats.breakPercent));
                break;
            case REGENERATE:
                shooter.getStatistics().add("splegg", "blocksEndered", breakRegenBlocks(blockRaycastResult.getBlockPos(), projectileStats.breakRadius, projectileStats.breakPercent));
                break;
        }
    }

}
