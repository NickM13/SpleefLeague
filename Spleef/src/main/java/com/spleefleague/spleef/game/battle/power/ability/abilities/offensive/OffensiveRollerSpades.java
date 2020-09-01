package com.spleefleague.spleef.game.battle.power.ability.abilities.offensive;

import com.comphenix.protocol.wrappers.BlockPosition;
import com.google.common.collect.Sets;
import com.spleefleague.core.chat.Chat;
import com.spleefleague.core.world.FakeBlock;
import com.spleefleague.core.world.FakeUtils;
import com.spleefleague.core.world.game.GameWorld;
import com.spleefleague.spleef.game.battle.power.PowerSpleefPlayer;
import com.spleefleague.spleef.game.battle.power.ability.abilities.AbilityOffensive;
import org.bukkit.Material;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.BoundingBox;

import java.util.HashSet;
import java.util.Set;

/**
 * @author NickM13
 * @since 5/19/2020
 */
public class OffensiveRollerSpades extends AbilityOffensive {

    private static final double DURATION = 5D;
    private static final double DELAY = 0.5D;

    public OffensiveRollerSpades() {
        super(9, 7);
    }

    @Override
    public String getDisplayName() {
        return "Roller Spades";
    }

    @Override
    public String getDescription() {
        return Chat.DESCRIPTION + "Gain &cSpeed II &7for " +
                Chat.STAT + DURATION +
                Chat.DESCRIPTION + " seconds. Blocks you pass over turn to dust, exploding violently after " +
                Chat.STAT + DURATION +
                Chat.DESCRIPTION + " seconds. May be reactivated to detonate early.";
    }

    private static final Material MARKER = Material.RED_CONCRETE_POWDER;

    /**
     * Called every 0.1 seconds (2 ticks)
     *
     * @param psp
     */
    @Override
    public void update(PowerSpleefPlayer psp) {
        double time = (double) psp.getPowerValueMap().get("rollerspade");
        if (time >= 0) {
            if (time > psp.getBattle().getRoundTime()) {
                if (FakeUtils.isOnGround(psp.getCorePlayer())) {
                    BoundingBox bb = psp.getPlayer().getBoundingBox();
                    Set<BlockPosition> toCheck = Sets.newHashSet(
                            new BlockPosition((int) Math.floor(bb.getMinX()), (int) Math.floor(bb.getMinY() - 1), (int) Math.floor(bb.getMinZ())),
                            new BlockPosition((int) Math.floor(bb.getMaxX()), (int) Math.floor(bb.getMinY() - 1), (int) Math.floor(bb.getMinZ())),
                            new BlockPosition((int) Math.floor(bb.getMaxX()), (int) Math.floor(bb.getMinY() - 1), (int) Math.floor(bb.getMaxZ())),
                            new BlockPosition((int) Math.floor(bb.getMinX()), (int) Math.floor(bb.getMinY() - 1), (int) Math.floor(bb.getMaxZ())));
                    for (BlockPosition pos : toCheck) {
                        FakeBlock fb = psp.getBattle().getGameWorld().getFakeBlocks().get(pos);
                        if (fb != null && psp.getBattle().getGameWorld().getBreakables().contains(fb.getBlockData().getMaterial())) {
                            psp.getBattle().getGameWorld().setBlockDelayed(pos, MARKER.createBlockData(), (int) (DELAY * 20));
                            ((HashSet<BlockPosition>) psp.getPowerValueMap().get("rollermarks")).add(pos);
                        }
                    }
                }
            } else {
                finish(psp);
            }
        }
    }

    private void finish(PowerSpleefPlayer psp) {
        psp.getPowerValueMap().put("rollerspade", -1D);
        Set<BlockPosition> marks = (HashSet<BlockPosition>) psp.getPowerValueMap().get("rollermarks");
        GameWorld gameWorld = psp.getBattle().getGameWorld();
        Set<BlockPosition> toBreak = new HashSet<>();
        Set<BlockPosition> toRepair = new HashSet<>();
        for (BlockPosition mark : marks) {
            FakeBlock fb = gameWorld.getFakeBlocks().get(mark);
            if (fb != null) {
                if (fb.getBlockData().getMaterial().equals(MARKER)) {
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
            gameWorld.breakBlock(pos, psp.getCorePlayer());
        }
        for (BlockPosition pos : toRepair) {
            gameWorld.clearBlockDelayed(pos);
        }
        marks.clear();
        applyCooldown(psp);
    }

    /**
     * Returns a percent number of how much is remaining until the value is fully charged
     *
     * @param psp Casting Player
     * @return
     */
    @Override
    protected double getMissingPercent(PowerSpleefPlayer psp) {
        if ((double) psp.getPowerValueMap().get("rollerspade") >= 0) {
            return 1. - ((double) psp.getPowerValueMap().get("rollerspade") - psp.getBattle().getRoundTime()) / DURATION;
        }
        return super.getMissingPercent(psp);
    }

    /**
     * This is called when a player uses an ability that isn't on cooldown.
     *
     * @param psp Casting Player
     */
    @Override
    public boolean onUse(PowerSpleefPlayer psp) {
        if ((double) psp.getPowerValueMap().get("rollerspade") >= 0) {
            finish(psp);
        } else {
            psp.getPlayer().addPotionEffect(PotionEffectType.SPEED.createEffect((int) (DURATION * 20), 1));
            psp.getPowerValueMap().put("rollerspade", psp.getBattle().getRoundTime() + DURATION);
        }
        return false;
    }

    /**
     * Called at the start of a round
     *
     * @param psp
     */
    @Override
    public void reset(PowerSpleefPlayer psp) {
        psp.getPlayer().removePotionEffect(PotionEffectType.SPEED);
        psp.getPowerValueMap().put("rollerspade", -1D);
        psp.getPowerValueMap().put("rollermarks", new HashSet<>());
    }

}
