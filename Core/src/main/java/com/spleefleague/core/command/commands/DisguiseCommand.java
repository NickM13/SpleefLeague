package com.spleefleague.core.command.commands;

import com.spleefleague.core.command.CoreCommand;
import com.spleefleague.core.command.annotation.CommandAnnotation;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.rank.CoreRank;
import org.bukkit.Bukkit;

import javax.annotation.Nullable;

public class DisguiseCommand extends CoreCommand {

    public DisguiseCommand() {
        super("disguise", CoreRank.DEVELOPER);
    }

    @CommandAnnotation
    public void disguise(CorePlayer sender, @Nullable String username) {
        if (username == null) {
            sender.setDisguise(null);
        } else {
            sender.setDisguise(Bukkit.getOfflinePlayer(username).getUniqueId());
        }
    }

}
