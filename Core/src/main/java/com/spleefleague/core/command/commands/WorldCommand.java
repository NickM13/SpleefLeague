package com.spleefleague.core.command.commands;

import com.spleefleague.core.command.CoreCommand;
import com.spleefleague.core.command.annotation.CommandAnnotation;
import com.spleefleague.core.command.annotation.OptionArg;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.rank.CoreRank;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author NickM13
 * @since 2/26/2021
 */
public class WorldCommand extends CoreCommand {

    public WorldCommand() {
        super("world", CoreRank.DEVELOPER);
        setOptions("worldNames", pi -> Bukkit.getWorlds().stream().map(World::getName).collect(Collectors.toSet()));
    }

    @CommandAnnotation
    public void world(CorePlayer sender,
                      @OptionArg(listName = "worldNames") String worldName) {
        sender.teleport(Objects.requireNonNull(Bukkit.getWorld(worldName)).getSpawnLocation());
        success(sender, "Teleported to world " + worldName);
    }

}
