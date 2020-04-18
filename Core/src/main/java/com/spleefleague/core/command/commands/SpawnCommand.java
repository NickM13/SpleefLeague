/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.command.commands;

import com.spleefleague.core.command.CommandTemplate;
import com.spleefleague.core.command.annotation.CommandAnnotation;
import com.spleefleague.core.command.error.CoreError;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.Rank;
import com.spleefleague.core.plugin.CorePlugin;

/**
 * @author NickM13
 */
public class SpawnCommand extends CommandTemplate {

    public SpawnCommand() {
        super(SpawnCommand.class, "spawn", Rank.DEFAULT);
        setDescription("Teleport to spawn");
    }
    
    @CommandAnnotation
    public void spawn(CorePlayer sender) {
        if (!sender.isInBattle()) {
            sender.gotoSpawn();
        } else {
            error(sender, CoreError.INGAME);
        }
    }

}
