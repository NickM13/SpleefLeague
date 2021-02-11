package com.spleefleague.proxycore.player.purse;

import com.google.common.collect.Lists;
import com.spleefleague.coreapi.player.purse.PlayerPurse;
import com.spleefleague.coreapi.utils.packet.bungee.player.PacketBungeePlayerResync;
import com.spleefleague.proxycore.ProxyCore;
import com.spleefleague.proxycore.player.ProxyCorePlayer;

/**
 * @author NickM13
 * @since 2/9/2021
 */
public class ProxyPlayerPurse extends PlayerPurse {

    ProxyCorePlayer owner;

    public ProxyPlayerPurse(ProxyCorePlayer owner) {
        this.owner = owner;
    }

    @Override
    public int setCurrency(String currency, int count) {
        super.setCurrency(currency, count);
        ProxyCore.getInstance().getPlayers().save(owner);
        ProxyCore.getInstance().getPacketManager().sendPacket(new PacketBungeePlayerResync(owner.getUniqueId(), Lists.newArrayList(PacketBungeePlayerResync.Field.PURSE)));
        return count;
    }

    @Override
    public int addCurrency(String currency, int amount) {
        super.addCurrency(currency, amount);
        ProxyCore.getInstance().getPlayers().save(owner);
        ProxyCore.getInstance().getPacketManager().sendPacket(new PacketBungeePlayerResync(owner.getUniqueId(), Lists.newArrayList(PacketBungeePlayerResync.Field.PURSE)));
        return getCurrency(currency);
    }

}
