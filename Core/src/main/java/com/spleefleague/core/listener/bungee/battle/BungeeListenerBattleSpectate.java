package com.spleefleague.core.listener.bungee.battle;

import com.spleefleague.core.Core;
import com.spleefleague.core.listener.bungee.BungeeListener;
import com.spleefleague.core.player.CoreOfflinePlayer;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.coreapi.utils.packet.bungee.battle.PacketBungeeBattleSpectate;
import org.bukkit.entity.Player;

public class BungeeListenerBattleSpectate extends BungeeListener<PacketBungeeBattleSpectate> {

    @Override
    protected void receive(Player sender, PacketBungeeBattleSpectate packet) {
        Core.getInstance().getPlayers().addPlayerJoinAction(packet.spectator, spectator -> {
            CorePlayer target = Core.getInstance().getPlayers().get(packet.target);
            if (target.isInBattle()) {
                target.getBattle().addSpectator(spectator, target);
            }
        }, true);
    }

}
