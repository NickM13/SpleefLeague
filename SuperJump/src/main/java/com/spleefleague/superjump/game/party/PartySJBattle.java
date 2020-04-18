/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.superjump.game.party;

import com.spleefleague.core.database.variable.DBPlayer;
import com.spleefleague.superjump.game.SJBattle;
import java.util.List;

/**
 * @author NickM13
 */
public class PartySJBattle extends SJBattle<PartySJArena> {

    public PartySJBattle(List<DBPlayer> players, PartySJArena arena) {
        super(players, arena);
    }

}
