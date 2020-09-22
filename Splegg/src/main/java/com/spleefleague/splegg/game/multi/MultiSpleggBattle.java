package com.spleefleague.splegg.game.multi;

import com.comphenix.protocol.wrappers.BlockPosition;
import com.spleefleague.core.game.Arena;
import com.spleefleague.core.game.battle.dynamic.DynamicBattle;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.world.FakeUtils;
import com.spleefleague.core.world.build.BuildStructure;
import com.spleefleague.coreapi.chat.ChatColor;
import com.spleefleague.splegg.Splegg;
import com.spleefleague.splegg.game.SpleggMode;
import com.spleefleague.splegg.util.SpleggUtils;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

/**
 * @author NickM13
 * @since 9/12/2020
 */
public class MultiSpleggBattle extends DynamicBattle<MultiSpleggPlayer> {

    private static float DECAY_TIME = 10;

    private final List<List<BlockPosition>> layers = new ArrayList<>();

    private int currentDecayLayer = 0;

    public MultiSpleggBattle(List<UUID> players, Arena arena) {
        super(Splegg.getInstance(), players, arena, MultiSpleggPlayer.class, SpleggMode.MULTI.getBattleMode());
    }

    /**
     * Initialize base battle settings such as GameWorld tools and Scoreboard values
     */
    @Override
    protected void setupBaseSettings() {
        SpleggUtils.setupBaseSettings(this);
        BlockPosition origin = getArena().getOrigin().toBlockPosition();
        for (BuildStructure structure : getArena().getStructures()) {
            gameWorld.setBaseBlocks(
                    FakeUtils.translateBlocks(
                            FakeUtils.rotateBlocks(structure.getFakeBlocks(), (int) getArena().getOrigin().getYaw()),
                            origin));
        }
    }

    private float getDecay() {
        return (float) getRoundTime() / DECAY_TIME;
    }

    @Override
    public void updateExperience() {
        if (getDecay() >= layers.size()) {
            chatGroup.setExperience(1, layers.size());
        } else {
            chatGroup.setExperience(getDecay() % 1, (int) Math.floor(getDecay()));
        }
    }

    @Override
    public void fillField() {
        SpleggUtils.fillFieldFast(this);
        for (MultiSpleggPlayer msp : battlers.values()) {
            msp.respawn();
        }

        BlockPosition origin = getArena().getOrigin().toBlockPosition();
        for (BuildStructure structure : getArena().getStructures()) {
            for (BlockPosition pos : structure.getFakeBlocks().keySet()) {
                int layer = Math.max(0, Math.round(-pos.getY() / 10f));
                while (layers.size() < layer + 1) {
                    layers.add(new ArrayList<>());
                }
                layers.get(layer).add(pos.add(origin));
            }
        }
    }

    @Override
    public void reset() {
        fillField();
    }

    @Override
    public void startRound() {
        super.startRound();
        for (MultiSpleggPlayer msp : battlers.values()) {
            msp.resetAbilities();
        }
    }

    private final static Random random = new Random();
    private final static BlockData INDICATOR = Material.RED_CONCRETE.createBlockData();
    private final static BlockData AIR = Material.AIR.createBlockData();

    @Override
    public void updateField() {
        for (MultiSpleggPlayer msp : battlers.values()) {
            msp.updateAbilities();
        }
        //gameWorld.performBaseBreakRegen();
        if (currentDecayLayer < getDecay() - 1) {
            Vector center = new Vector(arena.getOrigin().getX(), arena.getOrigin().getY() - currentDecayLayer * 10, arena.getOrigin().getZ());
            if (currentDecayLayer < layers.size()) {
                for (BlockPosition pos : layers.get(currentDecayLayer)) {
                    long time = (long) ((20 - pos.toVector().distance(center)) * 20);
                    gameWorld.setBlockDelayed(pos, INDICATOR, time);
                    gameWorld.addBlockDelayed(pos, AIR, time + 20);
                }
            }
            currentDecayLayer++;
        }
        /*
        for (int i = 0; i < getDecay() - 1 && i < layers.size(); i++) {
            if (!layers.get(i).isEmpty()) {
                gameWorld.setBlock(layers.get(i).remove(random.nextInt(layers.get(i).size())), AIR);
            }
        }
        */
    }

    @Override
    protected void sendEndMessage(MultiSpleggPlayer msp) {
        chatGroup.sendTitle(msp.getCorePlayer().getDisplayName() + ChatColor.GRAY + " won the game",
                ChatColor.GRAY + "Using " + ChatColor.RED + msp.getGun1().getName() + ChatColor.GRAY + " and " + ChatColor.BLUE + msp.getGun2().getName(), 20, 160, 20);
    }

    @Override
    protected void failBattler(CorePlayer cp) {
        super.failBattler(cp);
        gameWorld.doFailBlast(cp);
    }

}
