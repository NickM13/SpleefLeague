package com.spleefleague.proxycore.game.queue;

import com.spleefleague.proxycore.party.ProxyParty;

import java.util.Objects;
import java.util.UUID;

/**
 * @author NickM13
 * @since 2/7/2021
 */
public class QueueParty extends QueueEntity {

    ProxyParty party;
    int size;
    UUID uuid;

    public QueueParty(ProxyParty party, String query, int rating) {
        super(query, rating);
        this.party = party;
        this.size = party.getPlayerCount();
        this.uuid = UUID.randomUUID();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        QueueParty that = (QueueParty) o;
        return uuid.equals(that.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid);
    }

}
