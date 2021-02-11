package com.spleefleague.proxycore.listener.spigot.battle;

import com.spleefleague.coreapi.utils.packet.spigot.battle.PacketSpigotBattleEnd;
import com.spleefleague.coreapi.utils.packet.spigot.battle.PacketSpigotBattlePing;
import com.spleefleague.proxycore.ProxyCore;
import com.spleefleague.proxycore.game.BattleSession;
import com.spleefleague.proxycore.game.BattleSessionManager;
import com.spleefleague.proxycore.listener.spigot.SpigotListener;
import com.spleefleague.proxycore.player.ProxyCorePlayer;
import net.md_5.bungee.api.connection.Connection;

import java.util.UUID;

/**
 * @author NickM13
 * @since 9/18/2020
 */
public class SpigotListenerBattlePing extends SpigotListener<PacketSpigotBattlePing> {

    @Override
    protected void receive(Connection sender, PacketSpigotBattlePing packet) {
        BattleSession battleSession = BattleSessionManager.getSession(packet.battleId);
        if (battleSession != null) {
            battleSession.ping();
        }
    }

}
