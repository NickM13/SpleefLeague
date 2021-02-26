package com.spleefleague.core.command.commands;

import com.spleefleague.core.Core;
import com.spleefleague.core.command.CoreCommand;
import com.spleefleague.core.command.annotation.CommandAnnotation;
import com.spleefleague.core.command.annotation.OptionArg;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.rank.CoreRank;
import com.spleefleague.coreapi.utils.packet.spigot.server.PacketSpigotServerDirect;

public class ServersCommand extends CoreCommand {

    public ServersCommand() {
        super("servers", CoreRank.BUILDER);
        setOptions("serverNames", pi -> Core.getInstance().getServers());
    }

    @CommandAnnotation
    public void hub(CorePlayer sender,
                    @OptionArg(listName = "serverNames", force = false) String serverName) {
        Core.getInstance().sendPacket(new PacketSpigotServerDirect(sender.getUniqueId(), serverName));
    }

}
