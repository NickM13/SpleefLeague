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

import java.util.List;
import java.util.UUID;

/**
 * @author NickM13
 * @since 9/12/2020
 */
public class MultiSpleggBattle extends DynamicBattle<MultiSpleggPlayer> {

    private static float DECAY_TIME = 60 * 6;

    public MultiSpleggBattle(UUID battleId, List<UUID> players, Arena arena) {
        super(Splegg.getInstance(), battleId, players, arena, MultiSpleggPlayer.class, SpleggMode.MULTI.getBattleMode());
    }

    @Override
    protected void setupBattlers() {
        super.setupBattlers();
        for (MultiSpleggPlayer battler : battlers.values()) {
            gameHistory.addPlayerAdditional(battler.getCorePlayer().getUniqueId(), "splegg:gun1", battler.getGun1().getIdentifier());
            gameHistory.addPlayerAdditional(battler.getCorePlayer().getUniqueId(), "splegg:gun2", battler.getGun2().getIdentifier());
        }
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

    @Override
    public void updateExperience() {
        if (gameWorld.isDecaying()) {
            float percent = 1 - gameWorld.getDecayPercent();
            if (percent > 0) {
                chatGroup.setExperience(percent, (int) gameWorld.getDecayRemainSeconds());
            } else {
                chatGroup.setExperience(0, 0);
            }
        } else {
            chatGroup.setExperience(1, (int) gameWorld.getDecayRemainSeconds());
        }
    }

    @Override
    public void fillField() {
        SpleggUtils.fillFieldFast(this);
        for (MultiSpleggPlayer msp : battlers.values()) {
            msp.respawn();
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
        gameWorld.enableDecay(200, (long) DECAY_TIME * 20);
    }

    @Override
    public void updateField() {
        for (MultiSpleggPlayer msp : battlers.values()) {
            msp.updateAbilities();
        }
        //gameWorld.performBaseBreakRegen();
    }

    @Override
    protected void sendEndMessage(MultiSpleggPlayer msp) {
        chatGroup.sendTitle(msp.getCorePlayer().getDisplayName() + ChatColor.GRAY + " won the game",
                ChatColor.GRAY + "Using " + ChatColor.RED + msp.getGun1().getDisplayName() + ChatColor.GRAY + " and " + ChatColor.BLUE + msp.getGun2().getName(), 20, 160, 20);
    }

    @Override
    protected void failBattler(CorePlayer cp) {
        super.failBattler(cp);
        gameWorld.doFailBlast(cp);
    }

}
