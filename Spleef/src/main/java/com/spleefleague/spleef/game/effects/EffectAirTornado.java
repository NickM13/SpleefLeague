package com.spleefleague.spleef.game.effects;

import com.spleefleague.core.world.global.GlobalWorld;
import org.bukkit.Location;

/**
 * @author NickM13
 * @since 3/1/2021
 */
public class EffectAirTornado {

    private final GlobalWorld globalWorld;
    private Location location;

    public EffectAirTornado(GlobalWorld globalWorld, Location location) {
        this.globalWorld = globalWorld;
        this.location = location;
    }

    public void tick() {
        
    }

}
