package com.spleefleague.proxycore.game.session;

import com.spleefleague.coreapi.database.annotation.DBField;
import com.spleefleague.coreapi.database.variable.DBEntity;
import com.spleefleague.proxycore.droplet.Droplet;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author NickM13
 * @since 2/10/2021
 */
public class BattleSession extends DBEntity {

    private final UUID battleId;
    @DBField private Boolean ongoing;
    @DBField private final String mode;
    @DBField private final String serverName;
    @DBField private final List<UUID> players = new ArrayList<>();

    private final Droplet droplet;

    private long lastPing;

    public BattleSession(UUID battleId, String mode, Droplet droplet, List<UUID> players) {
        this.battleId = battleId;
        this.identifier = battleId.toString();
        this.mode = mode;
        this.serverName = droplet.getName();
        this.players.addAll(players);

        this.droplet = droplet;
        lastPing = System.currentTimeMillis();
    }

    public void end() {
        this.ongoing = false;
    }

    public UUID getBattleId() {
        return battleId;
    }

    public String getMode() {
        return mode;
    }

    public List<UUID> getPlayers() {
        return players;
    }

    public Droplet getDroplet() {
        return droplet;
    }

    public void ping() {
        lastPing = System.currentTimeMillis();
    }

    /**
     * @return True if last ping was over 75 seconds ago
     */
    public boolean isHanged() {
        return (System.currentTimeMillis() - lastPing) > 75000;
    }

}
