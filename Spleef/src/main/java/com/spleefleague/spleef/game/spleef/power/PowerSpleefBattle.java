/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.spleef.game.spleef.power;

import com.spleefleague.core.player.BattleState;
import com.spleefleague.core.util.database.DBPlayer;
import com.spleefleague.spleef.game.SpleefBattle;
import com.spleefleague.spleef.player.SpleefPlayer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.event.player.PlayerMoveEvent;

/**
 * @author NickM13
 */
public class PowerSpleefBattle extends SpleefBattle {
    
    protected class PowerPlayer {
        
        class SimpleEffect {
            Long activeTime;
        }
        
        List<Power> powers = new ArrayList<>(4);
        
        SpleefPlayer player;
        
        PowerPlayer(SpleefPlayer player) {
            this.player = player;
            powers.add(0, new Power(player.getActivePower(0)));
            powers.add(1, new Power(player.getActivePower(1)));
            powers.add(2, new Power(player.getActivePower(2)));
            powers.add(3, new Power(player.getActivePower(3)));
        }
    }
    protected Map<SpleefPlayer, PowerPlayer> powerPlayers = new HashMap<>();
    
    public PowerSpleefBattle(List<DBPlayer> players, PowerSpleefArena arena) {
        super(players, arena);
    }
    
    @Override
    protected void startBattle() {
        super.startBattle();
        for (BattlePlayer bp : battlers.values()) {
            bp.player.getPlayer().getInventory().setItem(0, bp.player.getActivePower(0).getItem());
            bp.player.getPlayer().getInventory().setItem(1, bp.player.getActivePower(1).getItem());
            bp.player.getPlayer().getInventory().setItem(2, bp.player.getActivePower(2).getItem());
            bp.player.getPlayer().getInventory().setItem(3, bp.player.getActivePower(3).getItem());
            bp.player.getPlayer().getInventory().setItem(4, bp.player.getActiveShovel().getItem());
            bp.player.getPlayer().getInventory().setHeldItemSlot(4);
            powerPlayers.put(bp.player, new PowerPlayer(bp.player));
        }
    }
    
    @Override
    protected void startRound() {
        super.startRound();
        for (SpleefPlayer sp : powerPlayers.keySet()) {
            PowerPlayer pp = powerPlayers.get(sp);
            for (Power p : pp.powers) {
                p.reset(sp);
            }
        }
    }
    
    @Override
    public void updateExperience() {
        for (SpleefPlayer sp : powerPlayers.keySet()) {
            PowerPlayer pp = powerPlayers.get(sp);
            for (Power p : pp.powers) {
                p.updateEffects(sp);
            }
        }
    }
    
    @Override
    public void onMove(SpleefPlayer sp, PlayerMoveEvent e) {
        super.onMove(sp, e);
        if (sp.getBattleState().equals(BattleState.BATTLER) && isRoundStarted()) {
            sp.getActivePower(0).onMove(sp);
        }
    }
    
    @Override
    public void onBlockBreak(SpleefPlayer sp) {
        if (sp.getBattleState().equals(BattleState.BATTLER) && isRoundStarted()) {
            sp.getActivePower(0).onBlockBreak(sp);
        }
    }
    
    @Override
    public void onSlotChange(SpleefPlayer sp, int slot) {
        if (sp.getBattleState().equals(BattleState.BATTLER) &&
                slot != 4) {
            if (slot >= 1 && slot <= 3
                    && isRoundStarted()
                    && sp.getActivePower(slot).isReady(sp)) {
                powerPlayers.get(sp).powers.get(slot).activate(sp);
            }
            sp.getPlayer().getInventory().setHeldItemSlot(4);
        }
    }
    
    @Override
    public void onRightClick(SpleefPlayer sp) {
        if (sp.getBattleState().equals(BattleState.BATTLER) && isRoundStarted()
                && sp.getPlayer().getInventory().getItemInMainHand().getType().equals(SPLEEFER_TOOL)) {
            //sp.getActivePower(0).activate(sp);
        }
    }
    
}
