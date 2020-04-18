/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.superjump.game.versus.classic;

import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.superjump.game.SJBattle;
import java.util.List;

/**
 * @author NickM13
 */
public class ClassicSJBattle extends SJBattle<ClassicSJArena> {

    public ClassicSJBattle(List<CorePlayer> players, ClassicSJArena arena) {
        super(players, arena);
    }

}
