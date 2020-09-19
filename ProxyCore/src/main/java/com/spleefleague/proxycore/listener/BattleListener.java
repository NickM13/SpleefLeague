package com.spleefleague.proxycore.listener;

import com.spleefleague.proxycore.game.BattleManager;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

/**
 * @author NickM13
 * @since 6/11/2020
 */
public class BattleListener implements Listener {

    @EventHandler
    public void onPreLogin(PreLoginEvent event) {
        String server = BattleManager.getInstance().getPlayerBattleServer(event.getConnection().getUniqueId());
        if (server != null) {
            // Connect player to that server, for rejoining teamspleef or something? Probably will just be a /rejoin command though
        }
    }

}
