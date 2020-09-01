package com.spleefleague.proxycore.listener;

import com.spleefleague.proxycore.ProxyCore;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

/**
 * @author NickM13
 * @since 6/22/2020
 */
public class PartyListener implements Listener {

    public PartyListener() {
        super();
        ProxyCore.getInstance().getProxy().registerChannel("party:create");
        ProxyCore.getInstance().getProxy().registerChannel("party:join");
        ProxyCore.getInstance().getProxy().registerChannel("party:leave");
    }

    @EventHandler
    public void onCoreMessage(PluginMessageEvent event) {
        switch (event.getTag()) {
            case "party:create":

                break;
            case "party:join":

                break;
            case "party:leave":

                break;
        }
    }

}
