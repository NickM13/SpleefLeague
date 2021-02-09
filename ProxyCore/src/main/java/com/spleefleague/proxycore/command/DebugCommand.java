package com.spleefleague.proxycore.command;

import com.spleefleague.proxycore.ProxyCore;
import com.spleefleague.proxycore.droplet.DropletType;
import com.spleefleague.proxycore.player.ProxyCorePlayer;
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
        //ProxyCore.getInstance().getDropletManager().openNext(DropletType.LOBBY);
        //ProxyCore.getInstance().getDropletManager().sendHttpRequest();
    }

}
