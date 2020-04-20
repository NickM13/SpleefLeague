/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.command.commands;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.spleefleague.core.Core;
import com.spleefleague.core.command.annotation.CommandAnnotation;
import com.spleefleague.core.command.CommandTemplate;
import com.spleefleague.core.player.infraction.Infraction;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.rank.Rank;
import com.spleefleague.core.util.TimeUtils;
import java.util.ArrayList;
import java.util.List;
import org.bson.Document;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;

/**
 * @author NickM13
 */
public class InfractionsCommand extends CommandTemplate {
    
    public InfractionsCommand() {
        super(InfractionsCommand.class, "infractions", Rank.MODERATOR);
        setUsage("/infractions <player> [page]");
        setDescription("View infractions of a player");
    }
    
    @CommandAnnotation
    public void infractions(CorePlayer sender, OfflinePlayer op, Integer page) {
        MongoCollection<Document> collection = Core.getInstance().getPluginDB().getCollection("Infractions");
        FindIterable<Document> iterable = collection.find(new Document("uuid", op.getUniqueId().toString())).sort(new Document("time", -1));
        List<Document> dbc = new ArrayList<>();
        
        for (Document d : iterable) {
            dbc.add(d);
        }
        
        if (dbc.isEmpty()) {
            error(sender, "This player does not have any infractions");
            return;
        }
        
        int maxPages = (dbc.size() - 1) / 10 + 1;
        if (page > maxPages || page < 1) page = 1;
        sender.sendMessage(ChatColor.DARK_GRAY + "[========== " + ChatColor.GRAY + op.getName() + ChatColor.GRAY + "'s infractions (" + ChatColor.RED + page + ChatColor.GRAY + "/" + ChatColor.RED + maxPages + ChatColor.GRAY + ") " + ChatColor.DARK_GRAY + "==========]");
        iterable.skip((page - 1) * 10);
        MongoCursor<Document> mc = iterable.iterator();
        for (int i = 1; i <= 10 && mc.hasNext(); i++) {
            Infraction inf = new Infraction(mc.next());
            sender.sendMessage(ChatColor.RED + String.valueOf((page - 1) * 10 + i) + ". " + ChatColor.DARK_GRAY + "| " + inf.getType().getColor() + inf.getType() + ChatColor.DARK_GRAY + " | " + ChatColor.RED + (inf.getPunisher() == null ? "CONSOLE" : inf.getPunisher()) + ChatColor.DARK_GRAY + " | " + ChatColor.GRAY + inf.getReason() + ChatColor.DARK_GRAY + " | " + ChatColor.GRAY + TimeUtils.gcdTimeToString(inf.getTime()) + " ago");
        }
    }
    
    @CommandAnnotation
    public void infractions(CorePlayer sender, OfflinePlayer op) {
        infractions(sender, op, 1);
    }

}
