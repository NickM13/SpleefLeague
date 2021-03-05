package com.spleefleague.zone.gear.steampack;

import com.comphenix.protocol.wrappers.BlockPosition;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.util.CoreUtils;
import com.spleefleague.core.world.global.GlobalWorld;
import com.spleefleague.zone.gear.Gear;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.data.BlockData;
import org.bukkit.util.Vector;

/**
 * @author NickM13
 * @since 3/1/2021
 */
public class GearSteamPack extends Gear {

    public GearSteamPack() {
        super(GearType.STEAM_PACK);
    }

    public GearSteamPack(String identifier, String name) {
        super(GearType.STEAM_PACK, identifier, name);
    }

    @Override
    protected boolean onActivate(CorePlayer corePlayer) {
        GlobalWorld globalWorld = corePlayer.getGlobalWorld();
        
        return false;
    }

    @Override
    public void update() {

    }

}
