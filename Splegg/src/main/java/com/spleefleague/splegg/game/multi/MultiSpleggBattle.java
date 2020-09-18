package com.spleefleague.splegg.game.multi;

import com.spleefleague.core.game.Arena;
import com.spleefleague.core.game.battle.dynamic.DynamicBattle;
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

    public MultiSpleggBattle(List<UUID> players, Arena arena) {
        super(Splegg.getInstance(), players, arena, MultiSpleggPlayer.class, SpleggMode.MULTI.getBattleMode());
    }

    /**
     * Initialize base battle settings such as GameWorld tools and Scoreboard values
     */
    @Override
    protected void setupBaseSettings() {
        SpleggUtils.setupBaseSettings(this);
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
    }

    @Override
    public void updateField() {
        for (MultiSpleggPlayer msp : battlers.values()) {
            msp.updateAbilities();
        }
        gameWorld.performBaseBreakRegen();
    }

    @Override
    protected void sendEndMessage(MultiSpleggPlayer msp) {
        chatGroup.sendTitle(msp.getCorePlayer().getDisplayName() + " won the game!",
                ChatColor.RED + msp.getGun1().getName() + "&7 and " + ChatColor.BLUE + msp.getGun2().getName(), 20, 160, 20);
    }

}
