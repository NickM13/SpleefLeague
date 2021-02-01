package com.spleefleague.core.command.commands;

import com.google.common.collect.Lists;
import com.spleefleague.core.Core;
import com.spleefleague.core.command.CoreCommand;
import com.spleefleague.core.command.annotation.CommandAnnotation;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.rank.CoreRank;
import com.spleefleague.coreapi.utils.packet.spigot.server.PacketSpigotServerHub;

public class HubCommand extends CoreCommand {

    public HubCommand() {
        super("hub", CoreRank.DEFAULT);
    }

    @CommandAnnotation
    public void hub(CorePlayer sender) {
        Core.getInstance().sendPacket(new PacketSpigotServerHub(Lists.newArrayList(sender)));
    }

}
