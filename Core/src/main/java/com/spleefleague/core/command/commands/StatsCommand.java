/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.command.commands;

import com.spleefleague.core.Core;
import com.spleefleague.core.command.annotation.CommandAnnotation;
import com.spleefleague.core.chat.Chat;
import com.spleefleague.core.command.CommandTemplate;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.Rank;
import com.spleefleague.core.plugin.CorePlugin;
import com.spleefleague.core.database.variable.DBPlayer;
import org.bukkit.OfflinePlayer;

/**
 * @author NickM13
 */
public class StatsCommand extends CommandTemplate {
    
    public StatsCommand() {
        super(StatsCommand.class, "stats", Rank.DEFAULT);
        setUsage("/stats");
        setDescription("Get ratings of a player");
    }
    
    @CommandAnnotation
    public void stats(CorePlayer sender, OfflinePlayer target) {
        if (!Core.getInstance().getPlayerManager().hasPlayedBefore(target.getUniqueId())) {
            error(sender, "Player has not logged in before.");
            return;
        }
        CorePlayer cp = Core.getInstance().getPlayers().get(target.getUniqueId());
        sender.sendMessage(Chat.fillTitle(Chat.BRACE + "[ " + cp.getDisplayName() + "'s Ranked Stats" + Chat.BRACE + " ]"));
        CorePlugin.getAllPlugins().forEach(plugin -> {
            DBPlayer dbp = plugin.getPlayers().get(target.getUniqueId());
            
            dbp.printStats(sender);
        });
    }
    
    @CommandAnnotation
    public void stats(CorePlayer sender) {
        stats(sender, sender.getPlayer());
    }

}
