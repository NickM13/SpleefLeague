package com.spleefleague.core.packet;

import com.google.common.collect.Iterables;
import com.spleefleague.core.Core;
import com.spleefleague.coreapi.utils.packet.spigot.PacketSpigot;
import com.spleefleague.coreapi.utils.packet.spigot.PacketSpigotBundleOut;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

/**
 * @author NickM13
 * @since 1/31/2021
 */
public class PacketManager {

    private List<byte[]> packetList = new ArrayList<>();

    private BukkitTask task;

    public void init() {
        task = Bukkit.getScheduler().runTaskTimer(Core.getInstance(), this::run, 2L, 2L);
    }

    public void close() {
        task.cancel();
    }

    public void flush() {
        run();
    }

    protected void run() {
        if (packetList.isEmpty()) return;

        PacketSpigotBundleOut packetOut = new PacketSpigotBundleOut();

        Iterator<byte[]> it2 = packetList.iterator();
        int total = 0;
        while (it2.hasNext()) {
            byte[] data = it2.next();
            packetOut.addPacket(data);
            it2.remove();
            total += data.length;
            if (total > 20000) {
                break;
            }
        }

        Player sender = Iterables.getFirst(Bukkit.getOnlinePlayers(), null);
        if (sender != null) {
            sender.sendPluginMessage(Core.getInstance(), "slcore:spigot", packetOut.toByteArray());
        }
    }

    /**
     * Send a packet to all servers with 1 or more players
     *
     * @param packet
     */
    public void sendPacket(PacketSpigot packet) {
        byte[] data = packet.toByteArray();
        packetList.add(data);
    }

}
