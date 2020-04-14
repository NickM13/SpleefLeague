/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.commands;

import com.comphenix.protocol.wrappers.BlockPosition;
import com.spleefleague.core.Core;
import com.spleefleague.core.command.CommandAnnotation;
import com.spleefleague.core.command.CommandTemplate;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.Rank;
import com.spleefleague.core.util.Point;
import com.spleefleague.core.util.RaycastResult;
import java.util.List;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Snowball;

/**
 * @author NickM13
 */
public class DebugCommand extends CommandTemplate {
    
    public DebugCommand() {
        super(DebugCommand.class, "debug", Rank.DEVELOPER);
        setUsage("/debug " + ChatColor.MAGIC + "[hope u no read]");
        setDescription("debu" + ChatColor.MAGIC + "g more read?");
    }
    
    @CommandAnnotation
    public void debug(CorePlayer sender) {
        Snowball snowball = sender.getPlayer().launchProjectile(Snowball.class);
        Point point = new Point(snowball.getLocation());
        List<RaycastResult> result = point.cast(sender.getPlayer().getLocation().getDirection(), 10);
        result.forEach(r -> {
            BlockPosition bp = r.blockPos;
            Material mat;
            switch (r.axis) {
                case 1:
                    mat = Material.RED_CONCRETE;
                    break;
                case 2:
                    mat = Material.GREEN_CONCRETE;
                    break;
                case 3:
                    mat = Material.BLUE_CONCRETE;
                    break;
                default: mat = Material.YELLOW_CONCRETE;
            }
            sender.getPlayer().getWorld().getBlockAt(new Location(sender.getLocation().getWorld(), (double)bp.getX(), (double)bp.getY(), (double)bp.getZ())).setBlockData(mat.createBlockData());
            System.out.println(r.toString());
        });
    }
    
}
