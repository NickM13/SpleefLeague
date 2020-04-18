/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.superjump.game.versus;

import com.spleefleague.core.database.variable.DBPlayer;
import com.spleefleague.superjump.game.SJBattle;
import java.util.List;

/**
 * @author NickM13
 * @param <A>
 */
public class VersusSJBattle<A extends VersusSJArena> extends SJBattle<A> {

    public VersusSJBattle(List<DBPlayer> players, A arena) {
        super(players, arena);
    }

}
