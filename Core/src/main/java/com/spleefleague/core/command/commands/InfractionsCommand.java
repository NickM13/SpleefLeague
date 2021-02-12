/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.command.commands;

import com.mongodb.client.MongoCollection;
import com.spleefleague.core.Core;
import com.spleefleague.core.command.CoreCommand;
import com.spleefleague.core.command.annotation.CommandAnnotation;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.rank.CoreRank;
import com.spleefleague.core.util.TimeUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.spleefleague.coreapi.infraction.Infraction;
import org.bson.Document;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;

/**
 * @author NickM13
 */
public class InfractionsCommand extends CoreCommand {

    public InfractionsCommand() {
        super("infractions", CoreRank.TEMP_MOD);
        setUsage("/infractions <player> [page]");
        setDescription("View infractions of a player");
    }

    private static final String lb = ChatColor.DARK_GRAY + " | ";

    @CommandAnnotation
    public void infractions(CorePlayer sender, OfflinePlayer op, Integer page) {
        MongoCollection<Document> collection = Core.getInstance().getPluginDB().getCollection("Infractions");
        Document playerDoc = collection.find(new Document("identifier", op.getUniqueId().toString())).first();

        if (playerDoc == null) {
            error(sender, "This player does not have any infractions");
            return;
        }

        List<Document> infractions = playerDoc.getList("infractions", Document.class, new ArrayList<>());

        if (infractions.isEmpty()) {
            error(sender, "This player does not have any infractions");
            return;
        }

        Collections.reverse(infractions);

        int maxPages = (infractions.size() - 1) / 10 + 1;
        page = Math.min(maxPages, Math.max(1, page));
        sender.sendMessage(ChatColor.DARK_GRAY + "[========== " +
                ChatColor.GRAY + op.getName() + ChatColor.GRAY + "'s infractions (" +
                ChatColor.RED + page + ChatColor.GRAY + "/" + ChatColor.RED + maxPages + ChatColor.GRAY + ") " +
                ChatColor.DARK_GRAY + "==========]");
        Iterator<Document> it = infractions.iterator();
        for (int i = 0; i < (page - 1) * 10; i++) it.next();
        for (int i = 1; i <= 10 && it.hasNext(); i++) {
            Infraction infraction = new Infraction();
            Document doc = it.next();
            infraction.load(doc);
            sender.sendMessage(ChatColor.RED + "" + ((page - 1) * 10 + i) + "." + lb +
                    infraction.getType().getColor() + infraction.getType().toString() + lb +
                    ChatColor.RED + infraction.getPunisher() + lb +
                    ChatColor.GRAY + infraction.getReason() + lb +
                    ChatColor.GRAY + TimeUtils.gcdTimeToString(System.currentTimeMillis() - infraction.getTime()) + " ago");
        }
    }

    @CommandAnnotation
    public void infractions(CorePlayer sender, OfflinePlayer op) {
        infractions(sender, op, 1);
    }

}
