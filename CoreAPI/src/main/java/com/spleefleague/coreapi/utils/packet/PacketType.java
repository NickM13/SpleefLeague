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
import com.spleefleague.coreapi.utils.packet.bungee.refresh.*;
import com.spleefleague.coreapi.utils.packet.spigot.PacketSpigot;
import com.spleefleague.coreapi.utils.packet.spigot.battle.*;
import com.spleefleague.coreapi.utils.packet.spigot.chat.PacketSpigotChat;
import com.spleefleague.coreapi.utils.packet.spigot.chat.PacketSpigotChatTell;
import com.spleefleague.coreapi.utils.packet.spigot.friend.PacketSpigotFriend;
import com.spleefleague.coreapi.utils.packet.spigot.party.PacketSpigotParty;
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
        BATTLE_SPECTATE(PacketBungeeBattleSpectate.class),
        BATTLE_START(PacketBungeeBattleStart.class),
        CHALLENGE(PacketBungeeBattleChallenge.class),
        CHAT(PacketBungeeChat.class),
        CONNECTION(PacketBungeeConnection.class),
        FRIEND(PacketBungeeFriend.class),
        PARTY(PacketBungeeParty.class),
        REFRESH_ALL(PacketBungeeRefreshAll.class),
        REFRESH_QUEUE(PacketBungeeRefreshQueue.class),
        REFRESH_PARTY(PacketBungeeRefreshParty.class),
        REFRESH_SCORE(PacketBungeeRefreshScore.class),
        SERVER_LIST(PacketBungeeRefreshServerList.class),
        TELL(PacketBungeeChatTell.class);

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
        SET_RATING(PacketSpigotPlayerRating.class),
        BATTLE_END_RATED(PacketSpigotBattleEndRated.class),
        BATTLE_END_UNRATED(PacketSpigotBattleEndUnrated.class),
        BATTLE_SPECTATE(PacketSpigotBattleSpectate.class),
        CHALLENGE(PacketSpigotBattleChallenge.class),
        CHAT(PacketSpigotChat.class),
        FORCE_START(PacketSpigotBattleForceStart.class),
        FRIEND(PacketSpigotFriend.class),
        HUB(PacketSpigotServerHub.class),
        PARTY(PacketSpigotParty.class),
        QUEUE_JOIN(PacketSpigotQueueJoin.class),
        QUEUE_LEAVE(PacketSpigotQueueLeave.class),
        SERVER_CONNECT(PacketSpigotServerDirect.class),
        REQUEUE(PacketSpigotQueueRequeue.class),
        TELL(PacketSpigotChatTell.class);

        private final Class<? extends PacketSpigot> clazz;

        Spigot(Class<? extends PacketSpigot> clazz) {
            this.clazz = clazz;
        }

        public Class<? extends PacketSpigot> getClazz() {
            return clazz;
        }

    }

}
