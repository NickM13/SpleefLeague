/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.listener;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.google.common.collect.Lists;
import com.spleefleague.core.Core;
import com.spleefleague.core.infraction.Infraction;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.Rank;
import com.spleefleague.core.plugin.CorePlugin;
import com.spleefleague.core.util.database.DBPlayer;
import java.time.Instant;
import java.util.Date;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;

/**
 * @author NickM13
 */
public class ConnectionListener implements Listener {
    
    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent e) {
        Infraction infraction = Infraction.getMostRecent(e.getPlayer().getUniqueId(), Lists.newArrayList(Infraction.Type.BAN, Infraction.Type.TEMPBAN, Infraction.Type.UNBAN));
        Infraction reasoning = Infraction.getMostRecent(e.getPlayer().getUniqueId(), Lists.newArrayList(Infraction.Type.BAN, Infraction.Type.TEMPBAN, Infraction.Type.UNBAN, Infraction.Type.WARNING));
        
        if (infraction != null) {
            switch (infraction.getType()) {
                case BAN:
                    e.disallow(PlayerLoginEvent.Result.KICK_BANNED, "You are banned!" + "\n" + reasoning.getReason());
                    return;
                case TEMPBAN:
                    if (!infraction.isExpired()) {
                        e.disallow(PlayerLoginEvent.Result.KICK_BANNED, "You are temporarily banned! " + infraction.getRemainingTimeString() + "\n" + reasoning.getReason());
                    }
                    return;
            }
        }
        
        Document joinDoc = new Document("date", Date.from(Instant.now()));
        joinDoc.append("type", "JOIN");
        joinDoc.append("ip", e.getAddress().getHostAddress());
        joinDoc.append("uuid", e.getPlayer().getUniqueId().toString());

        Core.getInstance().getPluginDB().getCollection("PlayerConnections").insertOne(joinDoc);
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent e) {
        CorePlayer cp1 = Core.getInstance().getPlayers().get(e.getPlayer());
        if (cp1.isVanished()) {
            e.setJoinMessage("");
        }
        if (!cp1.getRank().hasPermission(Rank.MODERATOR)) {
            cp1.gotoSpawn();
        }
        Bukkit.getScheduler().runTaskLater(Core.getInstance(), () -> {
            for (CorePlayer cp2 : Core.getInstance().getPlayers().getAll()) {
                if (!cp2.getPlayer().equals(cp1.getPlayer()) && CorePlugin.isInBattleGlobal(cp2.getPlayer())) {
                    cp2.getPlayer().hidePlayer(Core.getInstance(), cp1.getPlayer());
                    cp1.getPlayer().hidePlayer(Core.getInstance(), cp2.getPlayer());
                }
            }
        }, 1L);
    }
    
    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerQuit(PlayerQuitEvent e) {
        CorePlayer cp = Core.getInstance().getPlayers().get(e.getPlayer());
        if (cp.isVanished()) {
            e.setQuitMessage("");
        }
        cp.close();
        PacketContainer packet = new PacketContainer(PacketType.Play.Server.PLAYER_INFO);
        packet.getPlayerInfoAction().write(0, EnumWrappers.PlayerInfoAction.REMOVE_PLAYER);
        packet.getPlayerInfoDataLists().write(0, Lists.newArrayList(new PlayerInfoData(
                WrappedGameProfile.fromPlayer(e.getPlayer()),
                1,
                EnumWrappers.NativeGameMode.fromBukkit(cp.getPlayer().getGameMode()),
                WrappedChatComponent.fromText(cp.getDisplayName()))));
        
        Core.sendPacketAll(packet);
        
        DBPlayer dbp = CorePlugin.getBattlePlayerGlobal(e.getPlayer());
        if (dbp != null) {
            dbp.getBattle().leavePlayer(dbp);
        }
    }
    
    @EventHandler(priority = EventPriority.LOW)
    public void onResourcePackStatus(PlayerResourcePackStatusEvent e) {
        switch (e.getStatus()) {
            case DECLINED:
                Core.sendMessageToPlayer(DBPlayer.convert(e.getPlayer()), "It's suggested that you use the resource pack provided by this server!");
                break;
            case FAILED_DOWNLOAD:
                Core.sendMessageToPlayer(DBPlayer.convert(e.getPlayer()), "Issue loading server resource pack, try relogging!");
                break;
        }
    }
    
}
