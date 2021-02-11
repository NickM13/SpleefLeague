package com.spleefleague.proxycore.player.crates;

import com.google.common.collect.Lists;
import com.spleefleague.coreapi.player.crate.PlayerCrates;
import com.spleefleague.coreapi.utils.packet.bungee.player.PacketBungeePlayerResync;
import com.spleefleague.proxycore.ProxyCore;
import com.spleefleague.proxycore.player.ProxyCorePlayer;

/**
 * @author NickM13
 * @since 2/2/2021
 */
public class ProxyPlayerCrates extends PlayerCrates {

    ProxyCorePlayer owner;

    public ProxyPlayerCrates(ProxyCorePlayer owner) {
        this.owner = owner;
    }

    @Override
    public void setCrateCount(String crate, int count) {
        super.setCrateCount(crate, count);
        ProxyCore.getInstance().getPlayers().save(owner);
        ProxyCore.getInstance().getPacketManager().sendPacket(new PacketBungeePlayerResync(owner.getUniqueId(), Lists.newArrayList(PacketBungeePlayerResync.Field.CRATES)));
    }

    @Override
    public void changeCrateCount(String crate, int amount) {
        super.changeCrateCount(crate, amount);
        ProxyCore.getInstance().getPlayers().save(owner);
        ProxyCore.getInstance().getPacketManager().sendPacket(new PacketBungeePlayerResync(owner.getUniqueId(), Lists.newArrayList(PacketBungeePlayerResync.Field.CRATES)));
    }

    @Override
    public void addOpenedCrates(String crate, int amount) {
        super.addOpenedCrates(crate, amount);
        ProxyCore.getInstance().getPlayers().save(owner);
        ProxyCore.getInstance().getPacketManager().sendPacket(new PacketBungeePlayerResync(owner.getUniqueId(), Lists.newArrayList(PacketBungeePlayerResync.Field.CRATES)));
    }

}
