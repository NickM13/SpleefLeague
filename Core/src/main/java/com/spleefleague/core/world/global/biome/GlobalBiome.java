package com.spleefleague.core.world.global.biome;

import org.bukkit.WeatherType;
import org.bukkit.World;
import org.bukkit.block.Biome;

import java.util.HashMap;
import java.util.Map;

/**
 * @author NickM13
 * @since 4/21/2020
 */
public class GlobalBiome {
    
    private static final Map<Biome, GlobalBiome> globalBiomeMap = new HashMap<>();
    
    public static void init() {
        //globalBiomeMap.put(Biome.FOREST, new GlobalBiome(200L, 0L, World.Environment.NETHER));
        
        /*
        Core.getProtocolManager().addPacketListener(new PacketAdapter(Core.getInstance(), PacketType.Play.Server.RESPAWN) {
            @Override
            public void onPacketSending(PacketEvent event) {
                CorePlayer cp = Core.getInstance().getPlayers().get(event.getPlayer());
                System.out.println("Respawn packet!");
                try {
                    PacketContainer packetContainer = event.getPacket();
                    PacketPlayOutRespawn packetPlayOutRespawn = (PacketPlayOutRespawn) packetContainer.getHandle();
                    Field dimensionField = null;
                    dimensionField = PacketPlayOutRespawn.class.getDeclaredField("a");
                    dimensionField.setAccessible(true);
                    World.Environment environment;
                    if (cp.getGlobalBiome() == null) {
                        environment = World.Environment.NORMAL;
                    } else {
                        environment = cp.getGlobalBiome().getEnvironment();
                    }
                    switch (environment) {
                        case NETHER:    dimensionField.set(packetPlayOutRespawn, DimensionManager.NETHER); break;
                        case THE_END:   dimensionField.set(packetPlayOutRespawn, DimensionManager.THE_END); break;
                        default:        dimensionField.set(packetPlayOutRespawn, DimensionManager.OVERWORLD);
                    }
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        });
        */
    }
    
    public static GlobalBiome get(Biome biome) {
        return globalBiomeMap.get(biome);
    }
    
    private final long startRain, stopRain;
    private final World.Environment environment;
    
    public GlobalBiome(long startRain, long stopRain, World.Environment environment) {
        this.startRain = startRain;
        this.stopRain = stopRain;
        this.environment = environment;
    }
    
    public WeatherType getWeatherType(long time) {
        if (startRain == -1L) {
            return WeatherType.CLEAR;
        } else if (startRain < stopRain) {
            return startRain < time && time < stopRain ? WeatherType.DOWNFALL : WeatherType.CLEAR;
        } else {
            return stopRain < time && time < startRain ? WeatherType.CLEAR : WeatherType.DOWNFALL;
        }
    }
    
    public World.Environment getEnvironment() {
        return environment;
    }
    
}
