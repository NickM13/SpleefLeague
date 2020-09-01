package com.spleefleague.splegg.game;

import com.spleefleague.core.game.battle.Battle;
import com.spleefleague.core.game.battle.BattlePlayer;
import com.spleefleague.core.player.CorePlayer;

/**
 * @author NickM13
 * @since 4/25/2020
 */
public class SpleggBattlePlayer extends BattlePlayer {
    
    private int knockouts;
    private int knockoutStreak;
    private SpleggGun spleggGun;

    public SpleggBattlePlayer(CorePlayer cp, Battle<?> battle) {
        super(cp, battle);
        this.knockouts = 0;
        this.knockoutStreak = 0;
        this.spleggGun = cp.getCollectibles().getActiveOrDefault(SpleggGun.class, SpleggGun.getDefault());
    }
    
    @Override
    public void respawn() {
        super.respawn();
        knockoutStreak = 0;
    }

    @Override
    public void onRightClick() {
        if (getPlayer().getCooldown(spleggGun.getMaterial()) <= 0
            && getBattle().isRoundStarted()) {
            getPlayer().setCooldown(spleggGun.getMaterial(), spleggGun.getProjectileStats().fireCooldown);
            getBattle().getGameWorld().shootProjectile(getCorePlayer(), spleggGun.getProjectileStats());
        }
    }
    
    public int getKnockouts() {
        return knockouts;
    }
    
    public void addKnockouts(int knockouts) {
        this.knockouts += knockouts;
        knockoutStreak += knockouts;
    }
    
    public int getKnockoutStreak() {
        return knockoutStreak;
    }
    
}
