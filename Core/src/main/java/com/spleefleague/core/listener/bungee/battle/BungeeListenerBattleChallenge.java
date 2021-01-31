package com.spleefleague.core.listener.bungee.battle;

import com.google.common.collect.Lists;
import com.spleefleague.core.Core;
import com.spleefleague.core.chat.Chat;
import com.spleefleague.core.game.BattleMode;
import com.spleefleague.core.game.arena.Arenas;
import com.spleefleague.core.listener.bungee.BungeeListener;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.coreapi.database.variable.DBPlayer;
import com.spleefleague.coreapi.queue.SubQuery;
import com.spleefleague.coreapi.utils.packet.bungee.battle.PacketBungeeBattleChallenge;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

import java.util.Objects;

public class BungeeListenerBattleChallenge extends BungeeListener<PacketBungeeBattleChallenge> {

    @Override
    protected void receive(Player sender, PacketBungeeBattleChallenge packet) {
        CorePlayer cpSender = Core.getInstance().getPlayers().get(packet.sender);
        CorePlayer cpReceiver = Core.getInstance().getPlayers().get(packet.receiver);
        BattleMode battleMode = BattleMode.get(packet.mode);
        if (cpSender != null && cpReceiver != null) {
            Chat.sendRequest(cpReceiver, cpSender, (cp1, cp2) -> {
                if (cp1.getOnlineState() != DBPlayer.OnlineState.OFFLINE && cp2.getOnlineState() != DBPlayer.OnlineState.OFFLINE) {
                    SubQuery subQuery = SubQuery.getSubQuery(packet.query, "arena");
                    Core.getInstance().forceStart(
                            battleMode,
                            Lists.newArrayList(cp1, cp2),
                            subQuery == null || subQuery.hasStar ? Objects.requireNonNull(Arenas.getRandom(battleMode)) : Arenas.get(subQuery.values.get(0), battleMode));
                }
            }, cpSender.getChatName(), new TextComponent(" has challenged you to " + Chat.GAMEMODE + battleMode.getDisplayName()));
        }
    }

}
