package com.spleefleague.core.command.commands;

import com.google.common.collect.Lists;
import com.spleefleague.core.Core;
import com.spleefleague.core.command.CoreCommand;
import com.spleefleague.core.command.annotation.CommandAnnotation;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.rank.Rank;
import com.spleefleague.coreapi.utils.packet.spigot.PacketHub;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class HubCommand extends CoreCommand {

    public HubCommand() {
        super("hub", Rank.DEFAULT);
    }

    @CommandAnnotation
    public void hub(CorePlayer sender) {
        Core.getInstance().sendPacket(new PacketHub(Lists.newArrayList(sender)));
    }

}
