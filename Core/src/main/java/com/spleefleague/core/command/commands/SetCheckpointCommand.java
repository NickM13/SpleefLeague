/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.command.commands;

import com.spleefleague.core.command.CoreCommand;
import com.spleefleague.core.command.annotation.CommandAnnotation;
import com.spleefleague.core.command.annotation.CorePlayerArg;
import com.spleefleague.core.command.annotation.HelperArg;
import com.spleefleague.core.command.annotation.LiteralArg;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.rank.CoreRank;
import com.spleefleague.core.util.variable.CoreLocation;
import com.spleefleague.core.util.variable.TpCoord;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;

import javax.annotation.Nullable;
import java.util.List;

/**
 * @author NickM13
 */
public class SetCheckpointCommand extends CoreCommand {

    public SetCheckpointCommand() {
        super("setcheckpoint", CoreRank.DEVELOPER);
    }

    @CommandAnnotation
    public void setcheckpointPlayer(CommandSender sender,
                            CorePlayer target,
                            @HelperArg(value = "x") TpCoord x,
                            @HelperArg(value = "y") TpCoord y,
                            @HelperArg(value = "z") TpCoord z,
                            @Nullable @HelperArg(value = "pitch") Long pitch,
                            @Nullable @HelperArg(value = "yaw") Long yaw) {
        if (sender instanceof BlockCommandSender) {
            Location loc = ((BlockCommandSender) sender).getBlock().getLocation().clone().add(0.5, 0.5, 0.5);
            TpCoord.apply(loc, x, y, z);
            target.setCheckpoint(new CoreLocation(loc.getWorld(), loc.getX(), loc.getY(), loc.getZ(), pitch, yaw));
            target.sendMessage(ChatColor.DARK_GRAY + "<" + ChatColor.GREEN + "" + ChatColor.BOLD + "Checkpoint" + ChatColor.DARK_GRAY + ">");
        }
    }

}
