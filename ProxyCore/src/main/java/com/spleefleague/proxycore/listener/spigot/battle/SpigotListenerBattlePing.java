package com.spleefleague.proxycore.listener.spigot.battle;

import com.spleefleague.coreapi.utils.packet.spigot.battle.PacketSpigotBattlePing;
import com.spleefleague.proxycore.game.session.BattleSession;
import com.spleefleague.proxycore.game.session.BattleSessionManager;
import com.spleefleague.proxycore.listener.spigot.SpigotListener;
import net.md_5.bungee.api.connection.Connection;

/**
 * @author NickM13
 * @since 9/18/2020
 */
public class SpigotListenerBattlePing extends SpigotListener<PacketSpigotBattlePing> {

    @Override
    protected void receive(Connection sender, PacketSpigotBattlePing packet) {
        BattleSessionManager.onPing(packet.battleId);
    }

}
