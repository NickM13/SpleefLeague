/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.command.commands;

import com.spleefleague.core.command.HoldableCommand;
import com.spleefleague.core.player.collectible.key.Key;
import com.spleefleague.core.player.rank.CoreRank;

/**
 * @author NickM13
 */
public class KeyCommand extends HoldableCommand {

    public KeyCommand() {
        super(Key.class, "key", CoreRank.DEVELOPER);
    }

}
