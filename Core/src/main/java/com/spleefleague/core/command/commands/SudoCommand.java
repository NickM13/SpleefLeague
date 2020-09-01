/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.command.commands;

import com.spleefleague.core.command.CoreCommand;
import com.spleefleague.core.command.annotation.CommandAnnotation;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.rank.Rank;
import java.util.List;
import org.bukkit.Bukkit;

/**
 * @author NickM13
 */
public class SudoCommand extends CoreCommand {
    
    public SudoCommand() {
        super("sudo", Rank.DEVELOPER);
    }
    
    @CommandAnnotation
    public void sudo(CorePlayer sender, CorePlayer receiver, String command) {
        Bukkit.dispatchCommand(receiver.getPlayer(), command);
        success(sender, "Command run for " + receiver.getDisplayName() + ": /" + command);
    }
    
    @CommandAnnotation
    public void sudo(CorePlayer sender, List<CorePlayer> receivers, String command) {
        for (CorePlayer cp : receivers) {
            Bukkit.dispatchCommand(cp.getPlayer(), command);
        }
        success(sender, "Sudoed all : /" + command);
    }
    
}
