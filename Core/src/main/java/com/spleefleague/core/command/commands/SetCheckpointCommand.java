/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.command.commands;

import com.spleefleague.core.command.CoreCommand;
import com.spleefleague.core.command.annotation.CommandAnnotation;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.rank.CoreRank;

import java.util.List;

/**
 * @author NickM13
 */
public class SetCheckpointCommand extends CoreCommand {

    public SetCheckpointCommand() {
        super("setcheckpoint", CoreRank.DEVELOPER);
    }

}
