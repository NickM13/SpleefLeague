/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.superjump.game.practice;

import com.spleefleague.core.util.database.DBPlayer;
import com.spleefleague.superjump.game.SJBattle;
import java.util.List;

/**
 * @author NickM13
 */
public class PracticeSJBattle extends SJBattle<PracticeSJArena> {

    public PracticeSJBattle(List<DBPlayer> players, PracticeSJArena arena) {
        super(players, arena);
    }

}
