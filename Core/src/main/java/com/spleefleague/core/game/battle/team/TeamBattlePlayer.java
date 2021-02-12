package com.spleefleague.core.game.battle.team;

import com.spleefleague.core.game.battle.Battle;
import com.spleefleague.core.game.battle.BattlePlayer;
import com.spleefleague.core.player.CorePlayer;

/**
 * @author NickM13
 * @since 4/25/2020
 */
public class TeamBattlePlayer extends BattlePlayer {

    TeamBattleTeam<? extends TeamBattlePlayer> team;

    public TeamBattlePlayer(CorePlayer cp, Battle<?> battle) {
        super(cp, battle);
    }

    public void setTeam(TeamBattleTeam<? extends TeamBattlePlayer> team) {
        this.team = team;
    }

    public TeamBattleTeam<? extends TeamBattlePlayer> getTeam() {
        return team;
    }

}
