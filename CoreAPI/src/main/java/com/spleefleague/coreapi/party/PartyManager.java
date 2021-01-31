package com.spleefleague.coreapi.party;

import java.util.*;

/**
 * @author NickM13
 */
public class PartyManager<P extends Party> {

    protected final Map<UUID, P> partyMap = new HashMap<>();

    protected boolean create(P party, UUID owner) {
        if (partyMap.containsKey(owner)) return false;
        party.addPlayer(owner);

        partyMap.put(owner, party);
        return true;
    }

    protected int addPlayer(UUID sender, UUID target) {
        if (partyMap.containsKey(target)) {
            return 1;
        }
        P party = partyMap.get(sender);

        if (party != null) {
            party.addPlayer(target);
            partyMap.put(target, party);
            return 0;
        } else {
            return 2;
        }
    }

    protected int removePlayer(UUID sender) {
        P party = partyMap.get(sender);

        if (party != null) {
            party.removePlayer(sender);
            partyMap.remove(sender);
            return 0;
        } else {
            return 1;
        }
    }

    protected boolean transfer(UUID sender, UUID target) {
        if (sender.equals(target)) return false;

        P senderParty = partyMap.get(sender);
        P targetParty = partyMap.get(sender);

        if (senderParty != null && senderParty == targetParty) {
            senderParty.setOwner(target);
            return true;
        }
        return false;
    }

}
