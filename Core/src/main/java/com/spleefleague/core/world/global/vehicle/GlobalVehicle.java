package com.spleefleague.core.world.global.vehicle;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.spleefleague.core.Core;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

import java.util.HashMap;
import java.util.Map;

/**
 * @author NickM13
 * @since 5/3/2020
 */
public abstract class GlobalVehicle {

    protected static Map<Integer, LivingEntity> entityMap = new HashMap<>();

    public static void init() {
        Core.addProtocolPacketAdapter(new PacketAdapter(Core.getInstance(), PacketType.Play.Server.SPAWN_ENTITY) {
            @Override
            public void onPacketSending(PacketEvent event) {
                PacketContainer packet = event.getPacket();
                Entity entity = packet.getEntityModifier(event).read(0);
                if (entityMap.containsKey(entity.getEntityId()) &&
                        (!event.getPlayer().isInsideVehicle() || event.getPlayer().getVehicle() != entity)) {
                    //event.setCancelled(true);
                }
            }
        });
    }

    public static LivingEntity getPassenger(int entityId) {
        return entityMap.get(entityId);
    }

    public static LivingEntity remove(int entityId) {
        return entityMap.remove(entityId);
    }

}
