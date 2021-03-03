package com.spleefleague.proxycore.command;

import com.spleefleague.proxycore.ProxyCore;
import com.spleefleague.proxycore.game.queue.QueueContainerDynamic;
import com.spleefleague.proxycore.player.ProxyCorePlayer;
import com.spleefleague.proxycore.season.SeasonManager;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

import java.util.UUID;

/**
 * @author NickM13
 * @since 2/2/2021
 */
public class DebugCommand extends Command {

    public DebugCommand() {
        super("debug", "proxycore.debug");
    }

    @Override
    public void execute(CommandSender commandSender, String[] strings) {
        if (strings[0].equalsIgnoreCase("removeLow")) {
            if (strings.length > 1) {
                String season = ProxyCore.getInstance().getSeasonManager().getCurrentSeason();
                for (UUID uuid : ProxyCore.getInstance().getPlayers().getAllOfflineUuids()) {
                    ProxyCorePlayer pcp = ProxyCore.getInstance().getPlayers().getOffline(uuid);
                    pcp.getRatings().removeLow(strings[1], season);
                }
            }
        }
    }

}
