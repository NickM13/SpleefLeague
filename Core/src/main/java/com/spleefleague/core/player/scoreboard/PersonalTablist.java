package com.spleefleague.core.player.scoreboard;

import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;

public class PersonalTablist {

    private static final int SIZE = 10;

    private Objective objective;

    public PersonalTablist() {
        objective = null;
    }

    public void unregister() {
        if (objective != null) objective.unregister();
        objective = null;
    }

    public void register(Objective objective) {
        if (this.objective != null) objective.unregister();
        this.objective = objective;
        this.objective.setDisplaySlot(DisplaySlot.PLAYER_LIST);
    }

}
