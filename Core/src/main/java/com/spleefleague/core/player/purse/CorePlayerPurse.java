package com.spleefleague.core.player.purse;

import com.spleefleague.core.Core;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.coreapi.player.purse.PlayerPurse;
import com.spleefleague.coreapi.utils.packet.shared.NumAction;
import com.spleefleague.coreapi.utils.packet.spigot.player.PacketSpigotPlayerCurrency;

/**
 * @author NickM13
 * @since 2/2/2021
 */
public class CorePlayerPurse extends PlayerPurse {

    private CorePlayer owner;

    public CorePlayerPurse(CorePlayer owner) {
        this.owner = owner;
    }

    public void addCurrency(CoreCurrency currency, int amount) {
        super.addCurrency(currency.name(), amount);
        PacketSpigotPlayerCurrency packet = new PacketSpigotPlayerCurrency(owner.getUniqueId(), NumAction.CHANGE, currency.packetType, amount);
        Core.getInstance().sendPacket(packet);
    }

    public int getCurrency(CoreCurrency currency) {
        return super.getCurrency(currency.name());
    }

}
