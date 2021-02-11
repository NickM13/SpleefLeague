package com.spleefleague.proxycore.listener.spigot.battle;

import com.spleefleague.coreapi.utils.packet.spigot.battle.PacketSpigotBattleEnd;
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
public class SpigotListenerBattleEnd extends SpigotListener<PacketSpigotBattleEnd> {

    @Override
    protected void receive(Connection sender, PacketSpigotBattleEnd packet) {
        BattleSession battleSession = BattleSessionManager.destroyBattleSession(packet.battleId);
        if (battleSession != null) {
            for (UUID uuid : battleSession.getPlayers()) {
                ProxyCorePlayer pcp = ProxyCore.getInstance().getPlayers().get(uuid);
                pcp.setCurrrentBattle(null);
            }
        }
    }

}
