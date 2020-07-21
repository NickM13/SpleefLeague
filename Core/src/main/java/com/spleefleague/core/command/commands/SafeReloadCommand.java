package com.spleefleague.core.command.commands;

import com.spleefleague.core.Core;
import com.spleefleague.core.command.CoreCommand;
import com.spleefleague.core.command.annotation.CommandAnnotation;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.rank.Rank;
import com.spleefleague.core.plugin.CorePlugin;
import org.bukkit.Bukkit;

public class SafeReloadCommand extends CoreCommand {

    public SafeReloadCommand() {
        super("safereload", Rank.DEVELOPER);
    }

    @CommandAnnotation
    public void safeReload(CorePlayer sender) {
        Core.getInstance().sendMessage("Reloading when no matches are being played!");
        Bukkit.getScheduler().runTaskTimer(Core.getInstance(), () -> {
            if (CorePlugin.getIngamePlayerNames().isEmpty()) {
                Bukkit.dispatchCommand(sender.getPlayer(), "reload confirm");
            }
        }, 0L, 5L);
    }

}
