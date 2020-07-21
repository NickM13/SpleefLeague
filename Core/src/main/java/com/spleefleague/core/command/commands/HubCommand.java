package com.spleefleague.core.command.commands;

import com.spleefleague.core.command.CoreCommand;
import com.spleefleague.core.command.annotation.CommandAnnotation;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.rank.Rank;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class HubCommand extends CoreCommand {

    public HubCommand() {
        super("hub", Rank.DEFAULT);
    }

    @CommandAnnotation
    public void hub(CorePlayer sender) {
        System.out.println(ProxyServer.getInstance());
        ProxiedPlayer pp = ProxyServer.getInstance().getPlayer(sender.getUniqueId());
        System.out.println(pp.getServer().getInfo().getName());
        //pp.getServer().getInfo().getName().equals("lobby");
    }

}
