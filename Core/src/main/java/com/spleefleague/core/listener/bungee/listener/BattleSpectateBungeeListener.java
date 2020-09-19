package com.spleefleague.core.listener.bungee.listener;

import com.spleefleague.core.Core;
import com.spleefleague.core.listener.bungee.BungeeListener;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.coreapi.utils.packet.bungee.PacketBattleSpectateBungee;
import org.bukkit.entity.Player;

public class BattleSpectateBungeeListener extends BungeeListener<PacketBattleSpectateBungee> {

    @Override
    protected void receive(Player sender, PacketBattleSpectateBungee packet) {
        Core.getInstance().getPlayers().addPlayerJoinAction(packet.spectator, spectator -> {
            CorePlayer target = Core.getInstance().getPlayers().get(packet.target);
            if (target.isInBattle()) {
                target.getBattle().addSpectator(spectator, target);
            }
        }, true);
    }

}
