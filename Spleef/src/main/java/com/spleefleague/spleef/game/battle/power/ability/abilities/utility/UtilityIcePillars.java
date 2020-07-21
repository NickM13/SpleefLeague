package com.spleefleague.spleef.game.battle.power.ability.abilities.utility;

import com.comphenix.protocol.wrappers.BlockPosition;
import com.spleefleague.core.chat.Chat;
import com.spleefleague.core.world.FakeBlock;
import com.spleefleague.core.world.FakeUtils;
import com.spleefleague.spleef.game.battle.power.PowerSpleefPlayer;
import com.spleefleague.spleef.game.battle.power.ability.abilities.AbilityUtility;
import org.bukkit.Material;
import org.bukkit.Particle;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * @author NickM13
 * @since 5/19/2020
 */
public class UtilityIcePillars extends AbilityUtility {

    private static final int COUNT = 10;
    private static final int HEIGHT = 3;
    private static final int RADIUS = 5;
    private static final double RISE = 1.5;
    private static final double STAY = 1.5;
    private static final double FALL = 1.5;

    public UtilityIcePillars() {
        super(2, 10);
    }

    @Override
    public String getDisplayName() {
        return "Ice Pillars";
    }

    @Override
    public String getDescription() {
        return Chat.DESCRIPTION + "Raises " +
                Chat.STAT + COUNT +
                Chat.DESCRIPTION + " ice pillars randomly in the field surrounding the caster, growing quickly over " +
                Chat.STAT + RISE +
                Chat.DESCRIPTION + " seconds and then melting back down over an additional " +
                Chat.STAT + STAY +
                Chat.DESCRIPTION + " seconds.";
    }

    /**
     * This is called when a player uses an ability that isn't on cooldown.
     *
     * @param psp Casting Player
     */
    @Override
    public boolean onUse(PowerSpleefPlayer psp) {
        Random rand = new Random();
        List<BlockPosition> solidGrounds = new ArrayList<>();
        BlockPosition groundBlock = FakeUtils.getHighestFakeBlockBelow(psp.getCorePlayer());
        Map<BlockPosition, FakeBlock> fakeBlocks = psp.getBattle().getGameWorld().getFakeBlocks();
        if (groundBlock.getY() >= 0) {
            for (int x = -RADIUS; x < RADIUS; x++) {
                for (int y = -RADIUS; y < RADIUS; y++) {
                    for (int z = -RADIUS; z < RADIUS; z++) {
                        if (x != 0 && z != 0) {
                            BlockPosition pos = groundBlock.add(new BlockPosition(x, 0, z));
                            if (Math.sqrt(x*x + z*z + y*y) < RADIUS &&
                                    fakeBlocks.containsKey(pos) &&
                                    !fakeBlocks.get(pos).getBlockData().getMaterial().isAir() &&
                                    (!fakeBlocks.containsKey(pos.add(new BlockPosition(0, 1, 0))) ||
                                    fakeBlocks.get(pos.add(new BlockPosition(0, 1, 0))).getBlockData().getMaterial().isAir())) {
                                solidGrounds.add(pos);
                            }
                        }
                    }
                }
            }
            if (solidGrounds.isEmpty()) {
                return false;
            }
            for (int i = 0; i < COUNT; i++) {
                int index = rand.nextInt(solidGrounds.size());
                BlockPosition pos = solidGrounds.get(index);
                for (int h = 0; h < HEIGHT; h++) {
                    psp.getBattle().getGameWorld().setBlockDelayed(pos.add(new BlockPosition(0, h + 1, 0)), Material.BLUE_ICE.createBlockData(), (int) (20 * RISE * ((double) h / HEIGHT)));
                    psp.getBattle().getGameWorld().addBlockDelayed(pos.add(new BlockPosition(0, h + 1, 0)), Material.AIR.createBlockData(), (int) (20 * (FALL * (STAY + (double) (HEIGHT - h) / HEIGHT))));
                }
                solidGrounds.remove(index);
                if (solidGrounds.isEmpty()) {
                    break;
                }
            }
            psp.getBattle().getGameWorld().spawnParticles(Particle.REDSTONE,
                    psp.getPlayer().getLocation().getX() - 0.35,
                    psp.getPlayer().getLocation().getY(),
                    psp.getPlayer().getLocation().getZ() - 0.35,
                    30, 0.7, 1.8, 0.7, 0D, getType().getDustBig());
        }
        return true;
    }

    /**
     * Called at the start of a round
     *
     * @param psp
     */
    @Override
    public void reset(PowerSpleefPlayer psp) {

    }
}
