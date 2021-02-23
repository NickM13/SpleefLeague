package com.spleefleague.proxycore.listener.spigot.battle;

import com.spleefleague.coreapi.utils.packet.bungee.battle.PacketBungeeBattleSpectate;
import com.spleefleague.coreapi.utils.packet.spigot.battle.PacketSpigotBattleSpectate;
import com.spleefleague.proxycore.ProxyCore;
import com.spleefleague.proxycore.game.session.BattleSession;
import com.spleefleague.proxycore.game.session.BattleSessionManager;
import com.spleefleague.proxycore.listener.spigot.SpigotListener;
import com.spleefleague.proxycore.player.ProxyCorePlayer;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.Connection;

import java.util.concurrent.TimeUnit;

/**
 * @author NickM13
 * @since 9/18/2020
 */
public class SpigotListenerBattleSpectate extends SpigotListener<PacketSpigotBattleSpectate> {

    @Override
    protected void receive(Connection sender, PacketSpigotBattleSpectate packet) {
        ProxyCorePlayer spectator = ProxyCore.getInstance().getPlayers().get(packet.spectator);
        ProxyCorePlayer target = ProxyCore.getInstance().getPlayers().get(packet.target);
        if (target.isBattling()) {
            BattleSession session = BattleSessionManager.getSession(target.getCurrentBattle());
            if (session.isSpectatable()) {
                spectator.connect(session.getDroplet());
                ProxyCore.getInstance().getPacketManager().sendPacket(target.getCurrentServer(), new PacketBungeeBattleSpectate(packet));
                spectator.setSpectating(true);
                return;
            }
        }
        spectator.getPlayer().sendMessage(new TextComponent(ChatColor.YELLOW + target.getName() + ChatColor.RED + "'s game cannot be spectated"));
    }

}
