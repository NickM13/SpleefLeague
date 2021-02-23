package com.spleefleague.proxycore.droplet;

/**
 * @author NickM13
 * @since 2/5/2021
 */
public enum DropletType {

    LOBBY(false, 50, 70, 1),
    MINIGAME(true, 20, 100, 1);

    public boolean restricted;
    public int softCap;
    public double playersPerServer;
    public int serverCap;

    DropletType(boolean restricted, int softCap, double playersPerServer, int serverCap) {
        this.restricted = restricted;
        this.softCap = softCap;
        this.playersPerServer = playersPerServer;
        this.serverCap = serverCap;
    }

    public String getName() {
        return name().toLowerCase();
    }

}
