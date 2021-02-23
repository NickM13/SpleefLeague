package com.spleefleague.spleef.game.battle.power.ability.abilities.mobility;

import com.comphenix.protocol.wrappers.BlockPosition;
import com.google.common.collect.Sets;
import com.spleefleague.core.world.FakeBlock;
import com.spleefleague.core.world.FakeUtils;
import com.spleefleague.core.world.game.GameWorld;
import com.spleefleague.spleef.game.battle.power.ability.AbilityStats;
import com.spleefleague.spleef.game.battle.power.ability.abilities.AbilityMobility;
import org.bukkit.Material;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.BoundingBox;

import java.util.HashSet;
import java.util.Set;

/**
 * @author NickM13
 * @since 5/19/2020
 */
public class MobilityRollerSpades extends AbilityMobility {

    public static AbilityStats init() {
        return init(MobilityRollerSpades.class)
                .setCustomModelData(10)
                .setName("Roller Spades")
                .setDescription("Gain %XSpeed II% for %DURATION% seconds. Blocks you pass over turn to dust, exploding violently after %DURATION% seconds. May be reactivated to detonate early.")
                .setUsage(10);
    }

    private static final double DURATION = 7D;
    private static final double DELAY = 0.5D;

    private static final FakeBlock MARKER = new FakeBlock(Material.RED_CONCRETE.createBlockData());

    private double spadeTime = -1;
    private final Set<BlockPosition> marked = new HashSet<>();

    /**
     * Called every 0.1 seconds (2 ticks)
     */
    @Override
    public void update() {
        if (spadeTime >= 0) {
            if (spadeTime > getUser().getBattle().getRoundTime()) {
                if (FakeUtils.isOnGround(getUser().getCorePlayer())) {
                    BoundingBox bb = getPlayer().getBoundingBox();
                    Set<BlockPosition> toCheck = Sets.newHashSet(
                            new BlockPosition((int) Math.floor(bb.getMinX()), (int) Math.floor(bb.getMinY() - 1), (int) Math.floor(bb.getMinZ())),
                            new BlockPosition((int) Math.floor(bb.getMaxX()), (int) Math.floor(bb.getMinY() - 1), (int) Math.floor(bb.getMinZ())),
                            new BlockPosition((int) Math.floor(bb.getMaxX()), (int) Math.floor(bb.getMinY() - 1), (int) Math.floor(bb.getMaxZ())),
                            new BlockPosition((int) Math.floor(bb.getMinX()), (int) Math.floor(bb.getMinY() - 1), (int) Math.floor(bb.getMaxZ())));
                    for (BlockPosition pos : toCheck) {
                        FakeBlock fb = getUser().getBattle().getGameWorld().getFakeBlock(pos);
                        if (fb != null && getUser().getBattle().getGameWorld().getBreakables().contains(fb.getBlockData().getMaterial())) {
                            getUser().getBattle().getGameWorld().setBlockDelayed(pos, MARKER, (int) (DELAY * 20));
                            marked.add(pos);
                        }
                    }
                }
            } else {
                finish();
            }
        }
    }

    private void finish() {
        spadeTime = -1;
        GameWorld gameWorld = getUser().getBattle().getGameWorld();
        Set<BlockPosition> toBreak = new HashSet<>();
        Set<BlockPosition> toRepair = new HashSet<>();
        for (BlockPosition mark : marked) {
            FakeBlock fb = gameWorld.getFakeBlock(mark);
            if (fb != null) {
                if (fb.equals(MARKER)) {
                    toBreak.add(mark);
                    if (Math.random() > 0.5) toBreak.add(mark.add(new BlockPosition(-1, 0, 0)));
                    if (Math.random() > 0.5) toBreak.add(mark.add(new BlockPosition(1, 0, 0)));
                    if (Math.random() > 0.5) toBreak.add(mark.add(new BlockPosition(0, -1, 0)));
                    if (Math.random() > 0.5) toBreak.add(mark.add(new BlockPosition(0, 1, 0)));
                    if (Math.random() > 0.5) toBreak.add(mark.add(new BlockPosition(0, 0, -1)));
                    if (Math.random() > 0.5) toBreak.add(mark.add(new BlockPosition(0, 0, 1)));
                    continue;
                }
            }
            toRepair.add(mark);
        }
        for (BlockPosition pos : toBreak) {
            gameWorld.breakBlock(pos, getUser().getCorePlayer());
        }
        for (BlockPosition pos : toRepair) {
            gameWorld.clearBlockDelayed(pos);
        }
        marked.clear();
        applyCooldown();
    }

    @Override
    protected double getMissingPercent() {
        if (spadeTime >= 0) {
            return 1. - (spadeTime - getUser().getBattle().getRoundTime()) / DURATION;
        }
        return super.getMissingPercent();
    }

    /**
     * This is called when a player uses an ability that isn't on cooldown.
     */
    @Override
    public boolean onUse() {
        if (spadeTime >= 0) {
            finish();
        } else {
            getPlayer().addPotionEffect(PotionEffectType.SPEED.createEffect((int) (DURATION * 20), 1));
            spadeTime = getUser().getBattle().getRoundTime() + DURATION;
        }
        return false;
    }

    public void clear() {
        spadeTime = -1;
        GameWorld gameWorld = getUser().getBattle().getGameWorld();
        Set<BlockPosition> toBreak = new HashSet<>();
        Set<BlockPosition> toRepair = new HashSet<>();
        for (BlockPosition mark : marked) {
            FakeBlock fb = gameWorld.getFakeBlock(mark);
            if (fb != null) {
                if (fb.equals(MARKER)) {
                    toBreak.add(mark);
                    if (Math.random() > 0.5) toBreak.add(mark.add(new BlockPosition(-1, 0, 0)));
                    if (Math.random() > 0.5) toBreak.add(mark.add(new BlockPosition(1, 0, 0)));
                    if (Math.random() > 0.5) toBreak.add(mark.add(new BlockPosition(0, -1, 0)));
                    if (Math.random() > 0.5) toBreak.add(mark.add(new BlockPosition(0, 1, 0)));
                    if (Math.random() > 0.5) toBreak.add(mark.add(new BlockPosition(0, 0, -1)));
                    if (Math.random() > 0.5) toBreak.add(mark.add(new BlockPosition(0, 0, 1)));
                    continue;
                }
            }
            toRepair.add(mark);
        }
        for (BlockPosition pos : toBreak) {
            gameWorld.breakBlock(pos, getUser().getCorePlayer());
        }
        for (BlockPosition pos : toRepair) {
            gameWorld.clearBlockDelayed(pos);
        }
        marked.clear();
    }

    /**
     * Called at the start of a round
     */
    @Override
    public void reset() {
        getPlayer().removePotionEffect(PotionEffectType.SPEED);
        spadeTime = -1;
        clear();
    }

}
