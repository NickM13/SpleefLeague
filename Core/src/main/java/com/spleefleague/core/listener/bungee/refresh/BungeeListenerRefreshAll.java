package com.spleefleague.core.listener.bungee.refresh;

import com.google.common.collect.Sets;
import com.spleefleague.core.Core;
import com.spleefleague.core.game.BattleMode;
import com.spleefleague.core.game.manager.BattleManager;
import com.spleefleague.core.listener.bungee.BungeeListener;
import com.spleefleague.core.player.scoreboard.PersonalScoreboard;
import com.spleefleague.core.plugin.CorePlugin;
import com.spleefleague.coreapi.utils.packet.bungee.refresh.PacketBungeeRefreshAll;
import com.spleefleague.coreapi.utils.packet.shared.QueueContainerInfo;
import org.bukkit.entity.Player;

public class BungeeListenerRefreshAll extends BungeeListener<PacketBungeeRefreshAll> {

    @Override
    protected void receive(Player sender, PacketBungeeRefreshAll packet) {
        //Core.getInstance().getLeaderboards().refresh();
        for (CorePlugin<?> plugin : CorePlugin.getAllPlugins()) {
            plugin.refreshPlayers(Sets.newHashSet(packet.players));
        }
        PersonalScoreboard.refreshPlayers();
    }

}
