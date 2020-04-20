/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.splegg.game;

import com.spleefleague.core.database.variable.DBPlayer;
import com.spleefleague.core.game.Battle;
import com.spleefleague.core.game.BattlePlayer;
import com.spleefleague.core.player.BattleState;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.world.FakeProjectile;
import com.spleefleague.splegg.Splegg;
import com.spleefleague.splegg.player.SpleggPlayer;
import org.bukkit.Material;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author NickM13
 */
public abstract class SpleggBattle extends Battle<SpleggArena> {

    private static Material SPLEGGER_TOOL = Material.DIAMOND_SHOVEL;
    
    private final Map<SpleggPlayer, SpleggPlayer> spleggPlayers = new HashMap<>();
    
    public SpleggBattle(List<CorePlayer> players, SpleggArena arena) {
        super(Splegg.getInstance(), players, arena, BattlePlayer.class);
    }

    @Override
    public void onRightClick(CorePlayer cp) {
        if (cp.getBattleState().equals(BattleState.BATTLER) && isRoundStarted() &&
                cp.getPlayer().getInventory().getItemInMainHand().getType().equals(SPLEGGER_TOOL) &&
                cp.getPlayer().getCooldown(SPLEGGER_TOOL) <= 0) {
            // TODO: Rewrite this
            FakeProjectile projectile = ((SpleggGun) cp.getCollectibles().getActiveOrDefault(SpleggGun.class, SpleggGun.getDefault())).getProjectile();
            cp.getPlayer().setCooldown(SPLEGGER_TOOL, 20 / projectile.fireRate);
            gameWorld.shootProjectile(cp, projectile);
        }
    }

    @Override
    public void updateField() {

    }

    @Override
    public void updateExperience() {

    }

}
