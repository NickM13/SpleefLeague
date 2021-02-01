package com.spleefleague.proxycore.packet;

import com.spleefleague.coreapi.utils.packet.bungee.PacketBungee;
import com.spleefleague.coreapi.utils.packet.bungee.PacketBungeeBundleIn;
import com.spleefleague.coreapi.utils.packet.bungee.PacketBungeeBundleOut;
import com.spleefleague.proxycore.ProxyCore;
import com.spleefleague.proxycore.player.ProxyCorePlayer;
import com.spleefleague.proxycore.player.ProxyPlayerManager;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.scheduler.ScheduledTask;

import javax.annotation.Nonnull;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author NickM13
 * @since 1/31/2021
 */
public class PacketManager {

    private Map<String, List<byte[]>> serverPacketMap = new HashMap<>();
    private Map<String, ServerInfo> serverInfoMap = new HashMap<>();

    private ScheduledTask task;

    public void init() {
        task = ProxyCore.getInstance().getProxy().getScheduler().schedule(ProxyCore.getInstance(), this::run, 200L, 200L, TimeUnit.MILLISECONDS);
    }

    public void close() {
        task.cancel();
    }

    public void connect(ServerInfo serverInfo) {
        serverPacketMap.put(serverInfo.getName(), new ArrayList<>());
        serverInfoMap.put(serverInfo.getName(), serverInfo);
    }

    public void disconnect(String name) {
        serverPacketMap.remove(name);
        serverInfoMap.remove(name);
    }

    public void run() {
        Iterator<Map.Entry<String, List<byte[]>>> it = serverPacketMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, List<byte[]>> entry = it.next();
            if (entry.getValue().isEmpty()) continue;

            PacketBungeeBundleOut packetOut = new PacketBungeeBundleOut();

            Iterator<byte[]> it2 = entry.getValue().iterator();
            while (it2.hasNext()) {
                packetOut.addPacket(it2.next());
                it2.remove();
            }

            if (serverInfoMap.containsKey(entry.getKey())) {
                serverInfoMap.get(entry.getKey()).sendData("slcore:bungee", packetOut.toByteArray());
            } else {
                it.remove();
            }
        }
    }

    private void send(@Nonnull ServerInfo server, byte[] data) {
        if (serverPacketMap.containsKey(server.getName())) {
            serverPacketMap.get(server.getName()).add(data);
        }
    }

    /**
     * Send a packet to all servers with 1 or more players
     *
     * @param packet
     */
    public void sendPacket(PacketBungee packet) {
        byte[] data = packet.toByteArray();
        for (Map.Entry<String, ServerInfo> server : ProxyCore.getInstance().getProxy().getServersCopy().entrySet()) {
            if (!server.getValue().getPlayers().isEmpty()) {
                send(server.getValue(), data);
            }
        }
    }

    /**
     * Send a packet to all servers with 1 or more players
     *
     * @param packet
     */
    public void sendPacketExclude(ServerInfo exclude, PacketBungee packet) {
        Map<String, ServerInfo> serverCopy = ProxyCore.getInstance().getProxy().getServersCopy();
        serverCopy.remove(exclude.getName());
        for (Map.Entry<String, ServerInfo> server : serverCopy.entrySet()) {
            if (!server.getValue().getPlayers().isEmpty() && !exclude.equals(server.getValue())) {
                server.getValue().sendData("slcore:bungee", packet.toByteArray());
            }
        }
    }

    public void sendPacket(@Nonnull ServerInfo server, @Nonnull PacketBungee packet) {
        send(server, packet.toByteArray());
    }

    public void sendPacket(@Nonnull ProxyCorePlayer target, PacketBungee packet) {
        if (target.getCurrentServer() != null) {
            send(target.getCurrentServer(), packet.toByteArray());
        }
    }

    public void sendPacket(UUID target, PacketBungee packet) {
        ProxyCorePlayer pcp = ProxyCore.getInstance().getPlayers().get(target);
        if (pcp != null && pcp.getCurrentServer() != null) {
            send(pcp.getCurrentServer(), packet.toByteArray());
        }
    }

    public void sendPacket(PacketBungee packet, UUID... uuids) {
        Set<String> used = new HashSet<>();
        ProxyPlayerManager playerManager = ProxyCore.getInstance().getPlayers();
        byte[] data = packet.toByteArray();
        for (UUID uuid : uuids) {
            ProxyCorePlayer pcp = playerManager.get(uuid);
            if (pcp != null && pcp.getCurrentServer() != null && !used.contains(pcp.getCurrentServer().getName())) {
                send(pcp.getCurrentServer(), data);
                used.add(pcp.getCurrentServer().getName());
            }
        }
    }

}
