package com.spleefleague.proxycore.player.collectibles;

import com.google.common.collect.Lists;
import com.spleefleague.coreapi.player.collectibles.CollectibleInfo;
import com.spleefleague.coreapi.player.collectibles.PlayerCollectibles;
import com.spleefleague.coreapi.utils.packet.bungee.player.PacketBungeePlayerResync;
import com.spleefleague.proxycore.ProxyCore;
import com.spleefleague.proxycore.player.ProxyCorePlayer;

import java.util.Collections;

/**
 * @author NickM13
 * @since 2/26/2021
 */
public class ProxyPlayerCollectibles extends PlayerCollectibles {

    private final ProxyCorePlayer owner;

    public ProxyPlayerCollectibles(ProxyCorePlayer owner) {
        this.owner = owner;
    }

    public boolean onDelete(String type, String identifier, String crate) {
        if (!collectibleMap.containsKey(type)) return false;
        CollectibleInfo info = collectibleMap.get(type).remove(identifier);
        if (info != null) {
            if (activeMap.getOrDefault(type, "").equals(identifier)) {
                activeMap.remove(type);
            }
            owner.getCrates().changeCrateCount(crate, info.getOwnedSkins().size() + 1);
            ProxyCore.getInstance().getPlayers().save(owner);
            if (owner.isOnline()) {
                ProxyCore.getInstance().getPacketManager().sendPacket(new PacketBungeePlayerResync(owner.getUniqueId(),
                        Lists.newArrayList(PacketBungeePlayerResync.Field.COLLECTIBLES, PacketBungeePlayerResync.Field.CRATES)));
            }
            return true;
        }
        return false;
    }

}
