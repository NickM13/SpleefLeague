package com.spleefleague.proxycore.listener;

import com.spleefleague.proxycore.ProxyCore;
import net.md_5.bungee.api.plugin.Listener;

/**
 * @author NickM13
 * @since 6/12/2020
 */
public class RefreshListener implements Listener {

    public RefreshListener() {
        ProxyCore.getInstance().getProxy().registerChannel("refresh:arenas");
    }

}
