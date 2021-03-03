package com.spleefleague.zone.command;

import com.spleefleague.core.command.CoreCommand;
import com.spleefleague.core.command.annotation.CommandAnnotation;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.rank.CoreRank;
import com.spleefleague.zone.CoreZones;
import org.bukkit.command.CommandSender;

import java.util.List;

/**
 * @author NickM13
 * @since 2/26/2021
 */
public class PlayerOptionsCommand extends CoreCommand {

    public PlayerOptionsCommand() {
        super("zoneplayeroption", CoreRank.DEVELOPER);
        setContainer("zones");
    }

    @CommandAnnotation
    public void setPlayerOption(CommandSender sender,
                                List<CorePlayer> targets,
                                String optionName,
                                Integer value) {
        for (CorePlayer target : targets) {
            CoreZones.getInstance().getPlayers().get(target).getOptions().setInteger(optionName, value);
            target.refreshHotbar();
        }
    }

}
