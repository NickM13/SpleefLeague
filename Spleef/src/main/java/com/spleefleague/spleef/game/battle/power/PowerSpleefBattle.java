/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.spleef.game.battle.power;

import com.spleefleague.core.game.battle.versus.VersusBattle;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.spleef.Spleef;
import com.spleefleague.spleef.player.SpleefPlayer;

import java.util.List;

/**
 * @author NickM13
 */
public class PowerSpleefBattle extends VersusBattle<PowerSpleefArena, PowerSpleefPlayer> {
    
    public PowerSpleefBattle(List<CorePlayer> players, PowerSpleefArena arena) {
        super(Spleef.getInstance(), players, arena, PowerSpleefPlayer.class);
    }

    @Override
    protected void setupBaseSettings() {

    }

    @Override
    protected void setupBattlers() {
        super.setupBattlers();
        for (PowerSpleefPlayer bp : battlers.values()) {
            SpleefPlayer sp = Spleef.getInstance().getPlayers().get(bp.getCorePlayer());
            bp.getPlayer().getInventory().setItem(0, sp.getActivePower(0).getItem());
            bp.getPlayer().getInventory().setItem(1, sp.getActivePower(1).getItem());
            bp.getPlayer().getInventory().setItem(2, sp.getActivePower(2).getItem());
            bp.getPlayer().getInventory().setItem(3, sp.getActivePower(3).getItem());
            //bp.getPlayer().getInventory().setItem(4, sp.getActiveShovel().createItem());
            bp.getPlayer().getInventory().setHeldItemSlot(4);
        }
    }
    
    @Override
    protected void startRound() {
        super.startRound();
        for (PowerSpleefPlayer bp : battlers.values()) {
            for (Power p : bp.powers) {
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

    @Override
    protected void endRound(PowerSpleefPlayer winner) {
    
    }

    @Override
    protected void endBattle(PowerSpleefPlayer winner) {

    }

    @Override
    public void updateExperience() {
        for (PowerSpleefPlayer bp : battlers.values()) {
            for (Power p : bp.powers) {
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

}
