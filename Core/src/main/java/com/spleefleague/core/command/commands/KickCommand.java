/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.command.commands;

import com.spleefleague.core.command.CoreCommand;
import com.spleefleague.core.command.annotation.CommandAnnotation;
import com.spleefleague.core.infraction.Infractions;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.rank.CoreRank;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

/**
 * @author NickM13
 */
public class KickCommand extends CoreCommand {
    
    public KickCommand() {
        super("kick", CoreRank.TEMP_MOD);
        setUsage("/kick <player> [reason]");
        setDescription("Kick a player from the server");
    }
    
    @CommandAnnotation
    public void kick(CorePlayer sender, OfflinePlayer op, String reason) {
        Infractions.kick(sender, op, reason);
    }
    @CommandAnnotation
    public void kick(CorePlayer sender, OfflinePlayer op) {
        Infractions.kick(sender, op, "");
    }
    @CommandAnnotation
    public void kick(CommandSender sender, OfflinePlayer op, String reason) {
        Infractions.kick(null, op, reason);
    }
    @CommandAnnotation
    public void kick(CommandSender sender, OfflinePlayer op) {
        Infractions.kick(null, op, "");
    }
    
}
