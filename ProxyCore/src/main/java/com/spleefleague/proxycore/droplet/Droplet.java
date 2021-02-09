package com.spleefleague.proxycore.droplet;

import net.md_5.bungee.api.config.ServerInfo;

import java.util.Objects;

/**
 * @author NickM13
 * @since 2/5/2021
 */
public class Droplet {

    String name;
    String host;
    int port;
    DropletType type;
    ServerInfo info;

    public Droplet(String name, String host, int port, DropletType type, ServerInfo info) {
        this.name = name;
        this.host = host;
        this.port = port;
        this.type = type;
        this.info = info;
    }

    public String getName() {
        return name;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public DropletType getType() {
        return type;
    }

    public ServerInfo getInfo() {
        return info;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Droplet droplet = (Droplet) o;
        return port == droplet.port && host.equals(droplet.host);
    }

    @Override
    public int hashCode() {
        return Objects.hash(host, port);
    }
}
