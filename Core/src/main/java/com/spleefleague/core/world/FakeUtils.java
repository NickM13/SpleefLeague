package com.spleefleague.core.world;

import com.spleefleague.core.player.CorePlayer;
import net.minecraft.server.v1_15_R1.EntityPlayer;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;

/**
 * @author NickM13
 * @since 4/26/2020
 */
public class FakeUtils {

    public static boolean isInstantBreak(CorePlayer cp, Material material) {
        return true;
    }

}
