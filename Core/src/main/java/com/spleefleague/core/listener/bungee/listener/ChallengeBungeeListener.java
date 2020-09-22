package com.spleefleague.core.listener.bungee.listener;

import com.google.common.collect.Lists;
import com.spleefleague.core.Core;
import com.spleefleague.core.chat.Chat;
import com.spleefleague.core.game.BattleMode;
import com.spleefleague.core.game.arena.Arenas;
import com.spleefleague.core.listener.bungee.BungeeListener;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.coreapi.database.variable.DBPlayer;
import com.spleefleague.coreapi.queue.SubQuery;
import com.spleefleague.coreapi.utils.packet.bungee.PacketChallengeBungee;
import org.bukkit.entity.Player;

public class ChallengeBungeeListener extends BungeeListener<PacketChallengeBungee> {

    @Override
    protected void receive(Player sender, PacketChallengeBungee packet) {
        CorePlayer cpSender = Core.getInstance().getPlayers().get(packet.sender);
        CorePlayer cpReceiver = Core.getInstance().getPlayers().get(packet.receiver);
        BattleMode battleMode = BattleMode.get(packet.mode);
        if (cpSender != null && cpReceiver != null) {
            Chat.sendRequest(cpSender.getDisplayName() + " has challenged you to " + Chat.GAMEMODE + battleMode.getDisplayName(), cpReceiver, cpSender, (cp1, cp2) -> {
                if (cp1.getOnlineState() != DBPlayer.OnlineState.OFFLINE && cp2.getOnlineState() != DBPlayer.OnlineState.OFFLINE) {
                    System.out.println(packet.query);
                    SubQuery subQuery = SubQuery.getSubQuery(packet.query, "arena");
                    Core.getInstance().forceStart(
                            battleMode,
                            Lists.newArrayList(cp1, cp2),
                            subQuery == null || subQuery.hasStar ? Arenas.getRandom(battleMode) : Arenas.get(subQuery.values.get(0), battleMode));
                }
            });
        }
    }

}
