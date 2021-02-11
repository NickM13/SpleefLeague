package com.spleefleague.proxycore.droplet;

/**
 * @author NickM13
 * @since 2/5/2021
 */
public enum DropletType {

    LOBBY(false, 50, 70),
    MINIGAME(true, 20, 100);

    public boolean restricted;
    public int softCap;
    public double playersPerServer;

    DropletType(boolean restricted, int softCap, double playersPerServer) {
        this.restricted = restricted;
        this.softCap = softCap;
        this.playersPerServer = playersPerServer;
    }

    public String getName() {
        return name().toLowerCase();
    }

}
