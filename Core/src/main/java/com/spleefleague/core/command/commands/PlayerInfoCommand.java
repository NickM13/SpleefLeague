/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.command.commands;

import com.google.common.collect.Lists;
import com.mongodb.client.MongoCursor;
import com.spleefleague.core.Core;
import com.spleefleague.core.chat.ChatUtils;
import com.spleefleague.core.command.annotation.CommandAnnotation;
import com.spleefleague.core.chat.Chat;
import com.spleefleague.core.command.CoreCommand;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.rank.CoreRank;
import com.spleefleague.core.util.TimeUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.spleefleague.coreapi.database.variable.DBPlayer;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bson.Document;
import org.bukkit.OfflinePlayer;

/**
 * @author NickM13
 */
public class PlayerInfoCommand extends CoreCommand {

    public PlayerInfoCommand() {
        super("playerinfo", CoreRank.TEMP_MOD);
        addAlias("pi");
        setUsage("/playerinfo [player]");
        setDescription("Get player's server statistics");
    }

    @CommandAnnotation
    public void playerinfo(CorePlayer sender) {
        playerinfo(sender, sender.getPlayer());
    }

    @CommandAnnotation
    public void playerinfo(CorePlayer sender, OfflinePlayer op) {
        CorePlayer cp = Core.getInstance().getPlayers().getOffline(op.getUniqueId());

        if (cp == null) {
            error(sender, op.getName() + " has never logged in!");
            return;
        }

        List<TextComponent> textComponents = new ArrayList<>();
        TextComponent component;
        textComponents.add(ChatUtils.centerChat(Chat.TAG_BRACE + "[ " + cp.getDisplayNamePossessive() + " data" + Chat.TAG_BRACE + " ]" + "\n"));
        textComponents.add(new TextComponent(Chat.TAG_BRACE + "Name: " +
                Chat.DEFAULT + op.getName() + "\n"));

        component = new TextComponent(Chat.TAG_BRACE + "UUID: ");
        component.addExtra(new TextComponent(new ComponentBuilder().append(Chat.DEFAULT + op.getUniqueId().toString() + "\n")
                .event(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, op.getUniqueId().toString()))
                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder().append("Click to copy to clipboard").create())).create()));
        textComponents.add(component);

        textComponents.add(new TextComponent(Chat.TAG_BRACE + "Rank: " +
                Chat.DEFAULT + cp.getRank().getColor() + cp.getRank().getDisplayNameUnformatted() + "\n"));
        textComponents.add(new TextComponent(Chat.TAG_BRACE + "State: " +
                Chat.DEFAULT + getState(cp) + "\n"));
        textComponents.add(new TextComponent(Chat.TAG_BRACE + "Muted: " +
                Chat.DEFAULT + getMuted(cp) + "\n"));
        if (cp.getOnlineState() == DBPlayer.OnlineState.OFFLINE)
            textComponents.add(new TextComponent(Chat.TAG_BRACE + "Last seen: " +
                    Chat.DEFAULT + getLastSeen(cp) + "\n"));
        /*
        textComponents.add(new TextComponent(Chat.TAG_BRACE + "IP: " +
                Chat.DEFAULT + getIp(cp) + "\n"));
        textComponents.add(new TextComponent(Chat.TAG_BRACE + "Shared accounts: " +
                Chat.DEFAULT + getSharedAccounts(cp) + "\n"));
        */
        textComponents.add(new TextComponent(Chat.TAG_BRACE + "Total online time: " +
                Chat.DEFAULT + getOnlineTime(cp) + "\n"));
        textComponents.add(new TextComponent(Chat.TAG_BRACE + "Total active time: " +
                Chat.DEFAULT + getActiveTime(cp) + "\n"));

        sender.sendMessage(textComponents.toArray(new TextComponent[0]));
    }

    private String getMuted(CorePlayer cp) {
        return "Maybe?";
    }

    private String getState(CorePlayer cp) {
        String state;

        if (cp.getOnlineState() == DBPlayer.OnlineState.OFFLINE) {
            /*
            Infraction infraction = Infraction.getMostRecent(cp.getUniqueId(), Lists.newArrayList(Infraction.Type.BAN, Infraction.Type.TEMPBAN, Infraction.Type.UNBAN));
            if (infraction == null) {
                state = "Offline";
            } else {
                switch (infraction.getType()) {
                    case BAN:
                        state = "Permanent Ban";
                        break;
                    case TEMPBAN:
                        state = "Banned for " + infraction.getRemainingTimeString();
                        break;
                    default:
                        state = "Offline";
                        break;
                }
            }
             */
            state = "Offline";
        } else {
            if (cp.isAfk()) {
                state = "Away";
            } else {
                state = "Online";
            }
        }

        return state;
    }

    /*
    
    private String getIp(CorePlayer cp) {
        Document doc = Core.getInstance().getPluginDB().getCollection("PlayerConnections").find(new Document("uuid", cp.getUniqueId().toString()).append("type", "JOIN")).sort(new Document("date", -1)).first();
        if (doc == null) return "None Found";
        return doc.get("ip", String.class);
    }
    
    private String getSharedAccounts(CorePlayer cp) {
        MongoCursor<Document> cursor = Core.getInstance().getPluginDB().getCollection("PlayerConnections").find().iterator();
        
        String sharedAccounts = "";
        Set<String> uuids = new HashSet<>();
        Set<String> ips = new HashSet<>();
        
        while (cursor.hasNext()) {
            Document doc = cursor.next();
            if (doc.get("type", String.class).equalsIgnoreCase("JOIN")) {
                if (cp.getUniqueId().toString().equals(doc.get("uuid", String.class))) {
                    ips.add(doc.get("ip", String.class));
                }
            }
        }
        
        for (String ip : ips) {
            cursor = Core.getInstance().getPluginDB().getCollection("PlayerConnections").find(new Document("ip", ip)).iterator();
            while (cursor.hasNext()) {
                Document doc = cursor.next();
                uuids.add(doc.get("uuid", String.class));
            }
        }
        
        for (String uuid : uuids) {
            Document doc = Core.getInstance().getPluginDB().getCollection("Players").find(new Document("uuid", uuid)).first();
            if (doc != null && doc.containsKey("username")) {
                if (sharedAccounts.length() > 0) sharedAccounts += ", ";
                    sharedAccounts += doc.get("username", String.class);
            }
        }
        
        return sharedAccounts;
    }

    */

    private String getOnlineTime(CorePlayer cp) {
        String onlineTime = "";
        long onlineTimeTotal = 0;
        long lastJoin = -1;

        MongoCursor<Document> cursor = Core.getInstance().getPluginDB().getCollection("PlayerConnections").find(new Document("uuid", cp.getUniqueId().toString())).sort(new Document("date", 1)).iterator();

        while (cursor.hasNext()) {
            Document doc = cursor.next();
            long time = doc.get("date", Date.class).getTime();
            switch (doc.get("type", String.class)) {
                case "JOIN":
                    lastJoin = time;
                    break;
                case "LEAVE":
                    if (lastJoin != -1) {
                        onlineTimeTotal += time - lastJoin;
                    }
                    break;
                default:
                    break;
            }
        }

        onlineTime = TimeUtils.timeToString(onlineTimeTotal);

        return onlineTime;
    }

    private String getActiveTime(CorePlayer cp) {
        return TimeUtils.timeToString(cp.getStatistics().get("general", "playTime"));
    }

    private String getLastSeen(CorePlayer cp) {
        String lastSeen = "";
        long lastConnection = -1;

        MongoCursor<Document> cursor = Core.getInstance().getPluginDB().getCollection("PlayerConnections").find(new Document("uuid", cp.getUniqueId().toString())).sort(new Document("date", 1)).iterator();

        while (cursor.hasNext()) {
            Document doc = cursor.next();
            long time = doc.get("date", Date.class).getTime();
            lastConnection = time;
        }

        if (lastConnection == -1) {
            lastSeen = "Never";
        } else {
            lastSeen = TimeUtils.timeToString(System.currentTimeMillis() - lastConnection);
        }

        return lastSeen;
    }

}
