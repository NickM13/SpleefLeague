package com.spleefleague.coreapi.utils.packet;

import com.spleefleague.coreapi.utils.packet.bungee.PacketBungee;
import com.spleefleague.coreapi.utils.packet.bungee.battle.PacketBungeeBattleSpectate;
import com.spleefleague.coreapi.utils.packet.bungee.battle.PacketBungeeBattleStart;
import com.spleefleague.coreapi.utils.packet.bungee.battle.PacketBungeeBattleChallenge;
import com.spleefleague.coreapi.utils.packet.bungee.chat.PacketBungeeChat;
import com.spleefleague.coreapi.utils.packet.bungee.chat.PacketBungeeChatTell;
import com.spleefleague.coreapi.utils.packet.bungee.connection.PacketBungeeConnection;
import com.spleefleague.coreapi.utils.packet.bungee.friend.PacketBungeeFriend;
import com.spleefleague.coreapi.utils.packet.bungee.party.PacketBungeeParty;
import com.spleefleague.coreapi.utils.packet.bungee.player.PacketBungeePlayerResync;
import com.spleefleague.coreapi.utils.packet.bungee.refresh.*;
import com.spleefleague.coreapi.utils.packet.spigot.PacketSpigot;
import com.spleefleague.coreapi.utils.packet.spigot.battle.*;
import com.spleefleague.coreapi.utils.packet.spigot.chat.PacketSpigotChat;
import com.spleefleague.coreapi.utils.packet.spigot.chat.PacketSpigotChatTell;
import com.spleefleague.coreapi.utils.packet.spigot.friend.PacketSpigotFriend;
import com.spleefleague.coreapi.utils.packet.spigot.party.PacketSpigotParty;
import com.spleefleague.coreapi.utils.packet.spigot.player.PacketSpigotPlayerCurrency;
import com.spleefleague.coreapi.utils.packet.spigot.player.PacketSpigotPlayerRating;
import com.spleefleague.coreapi.utils.packet.spigot.queue.PacketSpigotQueueRequeue;
import com.spleefleague.coreapi.utils.packet.spigot.queue.PacketSpigotQueueJoin;
import com.spleefleague.coreapi.utils.packet.spigot.queue.PacketSpigotQueueLeave;
import com.spleefleague.coreapi.utils.packet.spigot.server.PacketSpigotServerDirect;
import com.spleefleague.coreapi.utils.packet.spigot.server.PacketSpigotServerHub;

/**
 * @author NickM13
 * @since 9/18/2020
 */
public class PacketType {

    /**
     * Packets created on the Bungee server and sent to the Spigot server
     */
    public enum Bungee {

        BATTLE_CHALLENGE(PacketBungeeBattleChallenge.class),
        BATTLE_SPECTATE(PacketBungeeBattleSpectate.class),
        BATTLE_START(PacketBungeeBattleStart.class),
        CHAT(PacketBungeeChat.class),
        CHAT_TELL(PacketBungeeChatTell.class),
        CONNECTION(PacketBungeeConnection.class),
        FRIEND(PacketBungeeFriend.class),
        PARTY(PacketBungeeParty.class),
        PLAYER_RESYNC(PacketBungeePlayerResync.class),
        REFRESH_ALL(PacketBungeeRefreshAll.class),
        REFRESH_PARTY(PacketBungeeRefreshParty.class),
        REFRESH_QUEUE(PacketBungeeRefreshQueue.class),
        REFRESH_SCORE(PacketBungeeRefreshScore.class),
        REFRESH_SERVER_LIST(PacketBungeeRefreshServerList.class);

        private final Class<? extends PacketBungee> clazz;

        Bungee(Class<? extends PacketBungee> clazz) {
            this.clazz = clazz;
        }

        public Class<? extends PacketBungee> getClazz() {
            return clazz;
        }

    }

    /**
     * Packets created on the Spigot server and sent to the Bungee server
     */
    public enum Spigot {

        BATTLE_CHALLENGE(PacketSpigotBattleChallenge.class),
        BATTLE_END_RATED(PacketSpigotBattleEndRated.class),
        BATTLE_END_UNRATED(PacketSpigotBattleEndUnrated.class),
        BATTLE_FORCE_START(PacketSpigotBattleForceStart.class),
        BATTLE_SPECTATE(PacketSpigotBattleSpectate.class),
        CHAT(PacketSpigotChat.class),
        CHAT_TELL(PacketSpigotChatTell.class),
        FRIEND(PacketSpigotFriend.class),
        PARTY(PacketSpigotParty.class),
        PLAYER_CURRENCY(PacketSpigotPlayerCurrency.class),
        PLAYER_RATING(PacketSpigotPlayerRating.class),
        QUEUE_JOIN(PacketSpigotQueueJoin.class),
        QUEUE_LEAVE(PacketSpigotQueueLeave.class),
        QUEUE_REQUEUE(PacketSpigotQueueRequeue.class),
        SERVER_DIRECT(PacketSpigotServerDirect.class),
        SERVER_HUB(PacketSpigotServerHub.class);

        private final Class<? extends PacketSpigot> clazz;

        Spigot(Class<? extends PacketSpigot> clazz) {
            this.clazz = clazz;
        }

        public Class<? extends PacketSpigot> getClazz() {
            return clazz;
        }

    }

}
