package com.spleefleague.coreapi.utils.packet;

import com.spleefleague.coreapi.utils.packet.bungee.*;
import com.spleefleague.coreapi.utils.packet.spigot.*;

/**
 * @author NickM13
 * @since 9/18/2020
 */
public class PacketType {

    /**
     * Packets created on the Bungee server and sent to the Spigot server
     */
    public enum Bungee {
        BATTLE_SPECTATE(PacketBattleSpectateBungee.class),
        BATTLE_START(PacketBattleStart.class),
        CHALLENGE(PacketChallengeBungee.class),
        CHAT(PacketChatBungee.class),
        CONNECTION(PacketConnection.class),
        REFRESH_ALL(PacketRefreshAll.class),
        REFRESH_QUEUE(PacketRefreshQueue.class),
        REFRESH_SCORE(PacketRefreshScore.class),
        TELL(PacketTellBungee.class);

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
        SET_RATING(PacketSetRating.class),
        BATTLE_END_RATED(PacketBattleEndRated.class),
        BATTLE_END_UNRATED(PacketBattleEndUnrated.class),
        BATTLE_SPECTATE(PacketBattleSpectateSpigot.class),
        CHALLENGE(PacketChallengeSpigot.class),
        CHAT(PacketChatSpigot.class),
        FORCE_START(PacketForceStart.class),
        HUB(PacketHub.class),
        PARTY_CREATE(PacketPartyCreate.class),
        PARTY_JOIN(PacketPartyJoin.class),
        PARTY_LEAVE(PacketPartyLeave.class),
        QUEUE_JOIN(PacketQueueJoin.class),
        QUEUE_LEAVE(PacketQueueLeave.class),
        REQUEUE(PacketRequeue.class),
        TELL(PacketTellSpigot.class);

        private final Class<? extends PacketSpigot> clazz;

        Spigot(Class<? extends PacketSpigot> clazz) {
            this.clazz = clazz;
        }

        public Class<? extends PacketSpigot> getClazz() {
            return clazz;
        }

    }

}
