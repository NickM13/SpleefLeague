package com.spleefleague.spleef.game.spleef;

import com.spleefleague.core.game.Battle;
import com.spleefleague.core.game.BattlePlayer;
import com.spleefleague.core.player.CorePlayer;
import org.bukkit.Location;

/**
 * @author NickM
 * @since 4/14/2020
 */
public class SpleefBattlePlayer extends BattlePlayer {

    private Location spawn;
    private int points;
    private int knockouts;
    private int knockoutStreak;

    public SpleefBattlePlayer(CorePlayer cp, Battle<?> battle) {
        super(cp, battle);
        this.points = 0;
        this.knockouts = 0;
        this.knockoutStreak = 0;
    }

    public void setSpawn(Location spawn) {
        this.spawn = spawn;
    }

    public void respawn() {
        super.respawn();
        knockoutStreak = 0;
        if (spawn != null) {
            getPlayer().teleport(spawn);
        }
        System.out.println("respawn");
    }

    public int getPoints() {
        return points;
    }
    public void addPoints(int points) {
        this.points += points;
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
