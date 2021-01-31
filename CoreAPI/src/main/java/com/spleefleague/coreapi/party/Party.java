package com.spleefleague.coreapi.party;

import java.util.*;

/**
 * @author NickM13
 */
public class Party {

    protected final UUID creator;
    protected UUID owner;
    protected final List<UUID> playerList = new ArrayList<>();

    public Party(UUID creator) {
        this.creator = creator;
        this.owner = creator;
    }

    public void setOwner(UUID owner) {
        playerList.removeIf(uuid -> uuid.equals(owner));
        playerList.add(0, owner);
        this.owner = owner;
    }

    public UUID getOwner() {
        return owner;
    }

    public UUID getCreator() {
        return creator;
    }

    public List<UUID> getPlayerList() {
        return playerList;
    }

    public void addPlayer(UUID uuid) {
        playerList.add(uuid);
    }

    public boolean removePlayer(UUID uuid) {
        return playerList.removeIf(uuid2 -> uuid2.equals(uuid));
    }

}
