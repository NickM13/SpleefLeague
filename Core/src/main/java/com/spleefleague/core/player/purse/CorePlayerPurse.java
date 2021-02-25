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

    private final CorePlayer owner;

    public CorePlayerPurse(CorePlayer owner) {
        this.owner = owner;
    }

    public void addCurrency(CoreCurrency currency, int amount) {
        this.addCurrency(currency, amount, false);
    }

    public void addCurrency(CoreCurrency currency, int amount, boolean sendMessage) {
        super.addCurrency(currency.name(), amount);
        PacketSpigotPlayerCurrency packet = new PacketSpigotPlayerCurrency(owner.getUniqueId(), NumAction.CHANGE, currency.packetType, amount);
        Core.getInstance().sendPacket(packet);
        if (sendMessage) {
            Core.getInstance().sendMessage(owner, "You've received " + currency.color + amount + " " + currency.displayName + (amount != 1 ? "s" : ""));
        }
    }

    public int getCurrency(CoreCurrency currency) {
        return super.getCurrency(currency.name());
    }

}
