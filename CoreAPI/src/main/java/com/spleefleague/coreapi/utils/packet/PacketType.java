package com.spleefleague.coreapi.utils.packet;

import com.spleefleague.coreapi.utils.packet.bungee.PacketBungee;
import com.spleefleague.coreapi.utils.packet.bungee.battle.PacketBungeeBattleRejoin;
import com.spleefleague.coreapi.utils.packet.bungee.battle.PacketBungeeBattleSpectate;
import com.spleefleague.coreapi.utils.packet.bungee.battle.PacketBungeeBattleStart;
import com.spleefleague.coreapi.utils.packet.bungee.connection.PacketBungeeConnection;
import com.spleefleague.coreapi.utils.packet.bungee.friend.PacketBungeeFriend;
import com.spleefleague.coreapi.utils.packet.bungee.party.PacketBungeeParty;
import com.spleefleague.coreapi.utils.packet.bungee.player.PacketBungeePlayerSound;
import com.spleefleague.coreapi.utils.packet.bungee.player.PacketBungeePlayerMute;
import com.spleefleague.coreapi.utils.packet.bungee.player.PacketBungeePlayerResync;
import com.spleefleague.coreapi.utils.packet.bungee.refresh.*;
import com.spleefleague.coreapi.utils.packet.bungee.server.PacketBungeeServerKill;
import com.spleefleague.coreapi.utils.packet.bungee.server.PacketBungeeServerPing;
import com.spleefleague.coreapi.utils.packet.spigot.PacketSpigot;
import com.spleefleague.coreapi.utils.packet.spigot.battle.*;
import com.spleefleague.coreapi.utils.packet.spigot.chat.*;
import com.spleefleague.coreapi.utils.packet.spigot.friend.PacketSpigotFriend;
import com.spleefleague.coreapi.utils.packet.spigot.party.PacketSpigotParty;
import com.spleefleague.coreapi.utils.packet.spigot.player.*;
import com.spleefleague.coreapi.utils.packet.spigot.queue.PacketSpigotQueueRequeue;
import com.spleefleague.coreapi.utils.packet.spigot.queue.PacketSpigotQueueJoin;
import com.spleefleague.coreapi.utils.packet.spigot.queue.PacketSpigotQueueLeave;
import com.spleefleague.coreapi.utils.packet.spigot.server.*;
import com.spleefleague.coreapi.utils.packet.spigot.ticket.PacketSpigotTicketClose;
import com.spleefleague.coreapi.utils.packet.spigot.ticket.PacketSpigotTicketOpen;
import com.spleefleague.coreapi.utils.packet.spigot.ticket.PacketSpigotTicketReply;

/**
 * @author NickM13
 * @since 9/18/2020
 */
public class PacketType {

    /**
     * Packets created on the Bungee server and sent to the Spigot server
     */
    public enum Bungee {

        BATTLE_REJOIN(PacketBungeeBattleRejoin.class),
        BATTLE_SPECTATE(PacketBungeeBattleSpectate.class),
        BATTLE_START(PacketBungeeBattleStart.class),
        CONNECTION(PacketBungeeConnection.class),
        FRIEND(PacketBungeeFriend.class),
        PARTY(PacketBungeeParty.class),
        PLAYER_MUTE(PacketBungeePlayerMute.class),
        PLAYER_RESYNC(PacketBungeePlayerResync.class),
        PLAYER_SOUND(PacketBungeePlayerSound.class),
        REFRESH_ALL(PacketBungeeRefreshAll.class),
        REFRESH_PARTY(PacketBungeeRefreshParty.class),
        REFRESH_QUEUE(PacketBungeeRefreshQueue.class),
        REFRESH_SCORE(PacketBungeeRefreshScore.class),
        REFRESH_SERVER_LIST(PacketBungeeRefreshServerList.class),
        SERVER_KILL(PacketBungeeServerKill.class),
        SERVER_PING(PacketBungeeServerPing.class);

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
        BATTLE_CHALLENGE_CONFIRM(PacketSpigotBattleChallengeConfirm.class),
        BATTLE_END(PacketSpigotBattleEnd.class),
        BATTLE_FORCE_START(PacketSpigotBattleForceStart.class),
        BATTLE_PING(PacketSpigotBattlePing.class),
        BATTLE_REJOIN(PacketSpigotBattleRejoin.class),
        BATTLE_SPECTATE(PacketSpigotBattleSpectate.class),
        CHAT_BROADCAST(PacketSpigotChatBroadcast.class),
        CHAT_CHANNEL_JOIN(PacketSpigotChatChannelJoin.class),
        CHAT_CONSOLE(PacketSpigotChatConsole.class),
        CHAT_FRIEND(PacketSpigotChatFriend.class),
        CHAT_GROUP(PacketSpigotChatGroup.class),
        CHAT_PLAYER(PacketSpigotChatPlayer.class),
        CHAT_TELL(PacketSpigotChatTell.class),
        FRIEND(PacketSpigotFriend.class),
        PARTY(PacketSpigotParty.class),
        PLAYER_COLLECTIBLE(PacketSpigotPlayerCollectible.class),
        PLAYER_COLLECTIBLE_SKIN(PacketSpigotPlayerCollectibleSkin.class),
        PLAYER_CRATE(PacketSpigotPlayerCrate.class),
        PLAYER_CURRENCY(PacketSpigotPlayerCurrency.class),
        PLAYER_INFRACTION(PacketSpigotPlayerInfraction.class),
        PLAYER_JOIN_SERVER(PacketSpigotPlayerJoinOther.class),
        PLAYER_OPTIONS(PacketSpigotPlayerOptions.class),
        PLAYER_RANK(PacketSpigotPlayerRank.class),
        PLAYER_RATING(PacketSpigotPlayerRating.class),
        PLAYER_STATISTICS(PacketSpigotPlayerStatistics.class),
        QUEUE_JOIN(PacketSpigotQueueJoin.class),
        QUEUE_LEAVE(PacketSpigotQueueLeave.class),
        QUEUE_REQUEUE(PacketSpigotQueueRequeue.class),
        SERVER_DIRECT(PacketSpigotServerDirect.class),
        SERVER_HUB(PacketSpigotServerHub.class),
        SERVER_PING(PacketSpigotServerPing.class),
        SERVER_RESTART(PacketSpigotServerRestart.class),
        SERVER_STOP(PacketSpigotServerStop.class),
        TICKET_CLOSE(PacketSpigotTicketClose.class),
        TICKET_OPEN(PacketSpigotTicketOpen.class),
        TICKET_REPLY(PacketSpigotTicketReply.class);

        private final Class<? extends PacketSpigot> clazz;

        Spigot(Class<? extends PacketSpigot> clazz) {
            this.clazz = clazz;
        }

        public Class<? extends PacketSpigot> getClazz() {
            return clazz;
        }

    }

}
