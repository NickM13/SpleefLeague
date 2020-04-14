/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.spleef.game;

import com.spleefleague.core.player.BattleState;
import com.spleefleague.core.util.database.DBPlayer;
import com.spleefleague.core.world.projectile.FakeProjectile;
import com.spleefleague.spleef.player.SpleefPlayer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.GameMode;

/**
 * @author NickM13
 */
public class SpleggBattle extends SpleefBattle {
    
    class SpleggPlayer {
        long fireCooldown = 0;
        
        public boolean fire(int fireRate) {
            if (fireCooldown < System.currentTimeMillis()) {
                fireCooldown = System.currentTimeMillis() + 1000 / fireRate;
                return true;
            }
            return false;
        }
    }
    
    private final Map<SpleefPlayer, SpleggPlayer> spleggPlayers = new HashMap<>();
    
    public SpleggBattle(List<DBPlayer> players, SpleggArena arena) {
        super(players, arena);
    }
    
    @Override
    protected void fillHotbar(SpleefPlayer sp) {
        sp.getPlayer().getInventory().setHeldItemSlot(0);
        sp.getPlayer().getInventory().clear();
        sp.getPlayer().getInventory().addItem(sp.getActiveSpleggGun().getItem());
    }
    
    @Override
    protected void startBattle() {
        super.startBattle();
        this.gameMode = GameMode.ADVENTURE;
        for (BattlePlayer bp : battlers.values()) {
            bp.player.getPlayer().setGameMode(GameMode.ADVENTURE);
            spleggPlayers.put(bp.player, new SpleggPlayer());
        }
    }
    
    @Override
    public void onRightClick(SpleefPlayer sp) {
        if (sp.getBattleState().equals(BattleState.BATTLER) && isRoundStarted() &&
                sp.getPlayer().getInventory().getItemInMainHand().getType().equals(SPLEEFER_TOOL) &&
                sp.getPlayer().getCooldown(SPLEEFER_TOOL) <= 0) {
            FakeProjectile projectile = sp.getActiveSpleggGun().getProjectile();
            sp.getPlayer().setCooldown(SPLEEFER_TOOL, 20 / projectile.fireRate);
            gameWorld.shootProjectile(sp.getPlayer(), projectile);
        }
    }
    
}
