/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.spleef.game.spleef.power;

import com.spleefleague.core.game.BattlePlayer;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.spleef.Spleef;
import com.spleefleague.spleef.game.SpleefBattle;
import com.spleefleague.spleef.player.SpleefPlayer;

import java.util.List;

/**
 * @author NickM13
 */
public class PowerSpleefBattle extends SpleefBattle {
    
    public PowerSpleefBattle(List<CorePlayer> players, PowerSpleefArena arena) {
        super(players, arena, PowerSpleefBattlePlayer.class);
    }

    @Override
    protected void setupBattlers() {
        super.setupBattlers();
        for (BattlePlayer bp : battlers.values()) {
            SpleefPlayer sp = Spleef.getInstance().getPlayers().get(bp.getCorePlayer());
            bp.getPlayer().getInventory().setItem(0, sp.getActivePower(0).getItem());
            bp.getPlayer().getInventory().setItem(1, sp.getActivePower(1).getItem());
            bp.getPlayer().getInventory().setItem(2, sp.getActivePower(2).getItem());
            bp.getPlayer().getInventory().setItem(3, sp.getActivePower(3).getItem());
            bp.getPlayer().getInventory().setItem(4, sp.getActiveShovel().getItem());
            bp.getPlayer().getInventory().setHeldItemSlot(4);
        }
    }
    
    @Override
    protected void startRound() {
        super.startRound();
        for (BattlePlayer bp : battlers.values()) {
            PowerSpleefBattlePlayer psbp = (PowerSpleefBattlePlayer) bp;
            for (Power p : psbp.powers) {
                SpleefPlayer sp = Spleef.getInstance().getPlayers().get(bp.getCorePlayer());
                p.reset(sp);
            }
        }
    }

    @Override
    public void updateScoreboard() {

    }

    @Override
    protected void joinBattler(CorePlayer cp) {

    }

    @Override
    public void updateField() {

    }

    /**
     * Applies elo change to all players in the battle
     * ELO change is 20 if players are the same rank
     * exponentially increasing/decreasing between (5, 40)
     *
     * @param winner Winner
     */
    @Override
    protected void applyEloChange(BattlePlayer winner) {

    }

    @Override
    protected void endRound(BattlePlayer winner) {

    }

    @Override
    protected void endBattle(BattlePlayer winner) {

    }

    @Override
    public void updateExperience() {
        for (BattlePlayer bp : battlers.values()) {
            for (Power p : ((PowerSpleefBattlePlayer) bp).powers) {
                p.updateEffects(Spleef.getInstance().getPlayers().get(bp.getCorePlayer()), this);
            }
        }
    }
    
    @Override
    public void onRightClick(CorePlayer cp) {
        /*
        if (dbp.getBattleState().equals(BattleState.BATTLER) && isRoundStarted()
                && dbp.getPlayer().getInventory().getItemInMainHand().getType().equals(SPLEEFER_TOOL)) {
            //sp.getActivePower(0).activate(sp);
        }
        */
    }

    @Override
    protected void resetPlayer(CorePlayer cp) {

    }

}
