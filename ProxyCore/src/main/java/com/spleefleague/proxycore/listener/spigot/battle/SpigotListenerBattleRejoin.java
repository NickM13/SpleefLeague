package com.spleefleague.proxycore.listener.spigot.battle;

import com.spleefleague.coreapi.utils.packet.bungee.battle.PacketBungeeBattleRejoin;
import com.spleefleague.coreapi.utils.packet.spigot.battle.PacketSpigotBattleRejoin;
import com.spleefleague.proxycore.ProxyCore;
import com.spleefleague.proxycore.game.session.BattleSession;
import com.spleefleague.proxycore.game.session.BattleSessionManager;
import com.spleefleague.proxycore.listener.spigot.SpigotListener;
import com.spleefleague.proxycore.player.ProxyCorePlayer;
import net.md_5.bungee.api.connection.Connection;

/**
 * @author NickM13
 * @since 9/18/2020
 */
public class SpigotListenerBattleRejoin extends SpigotListener<PacketSpigotBattleRejoin> {

    @Override
    protected void receive(Connection sender, PacketSpigotBattleRejoin packet) {
        ProxyCorePlayer pcp = ProxyCore.getInstance().getPlayers().get(packet.player);
        if (pcp != null && pcp.getCurrentBattle() != null && BattleSessionManager.isOngoing(pcp.getCurrentBattle())) {
            BattleSession session = BattleSessionManager.getSession(pcp.getCurrentBattle());
            pcp.connect(session.getDroplet());
            ProxyCore.getInstance().getPacketManager().sendPacket(session.getDroplet().getInfo(),
                    new PacketBungeeBattleRejoin(packet.player, session.getMode(), session.getBattleId()));
        }
    }

}
