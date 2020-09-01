package com.spleefleague.proxycore.player;

import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * @author NickM13
 * @since 6/22/2020
 */
public class ProxyParty {

    private final ProxyCorePlayer creator;
    private ProxyCorePlayer owner;
    private final SortedSet<ProxyCorePlayer> players = new TreeSet<>();

    private ProxyParty(ProxyCorePlayer creator) {
        this.creator = creator;
        this.owner = creator;
        join(creator);
    }

    public void setOwner(ProxyCorePlayer owner) {
        this.owner = owner;
    }

    public ProxyCorePlayer getOwner() {
        return owner;
    }

    public ProxyCorePlayer getCreator() {
        return creator;
    }

    public SortedSet<ProxyCorePlayer> getPlayers() {
        return players;
    }

    public void join(ProxyCorePlayer pcp) {
        if (pcp.getParty() == null) {
            players.add(pcp);
            pcp.setParty(this);
        }
    }

    public void leave(ProxyCorePlayer pcp) {
        if (players.remove(pcp)) {
            pcp.setParty(null);
        }
    }

    public static ProxyParty createParty(ProxyCorePlayer creator) {
        return new ProxyParty(creator);
    }

}
