package com.spleefleague.proxycore.player.friends;

import com.spleefleague.coreapi.player.friends.FriendsManager;
import com.spleefleague.proxycore.ProxyCore;
import com.spleefleague.proxycore.player.ProxyCorePlayer;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.UUID;

/**
 * @author NickM13
 */
public class ProxyFriendsManager extends FriendsManager {

    public void init() {

    }

    public void close() {

    }

    public void onPlayerConnect(UUID uuid) {

    }

    public void onPlayerDisconnect() {

    }

    public void onPlayerAdd(ProxyCorePlayer sender, ProxyCorePlayer receiver) {
        if (friendListMap.get(sender.getUniqueId()).isFriend(receiver.getUniqueId())) {

        }
    }

    public void onPlayerRemove(UUID sender, UUID receiver) {

    }

    public void onPlayerDecline(UUID sender, UUID receiver) {

    }

}
