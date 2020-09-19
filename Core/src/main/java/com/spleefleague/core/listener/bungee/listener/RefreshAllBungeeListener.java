package com.spleefleague.core.listener.bungee.listener;

import com.google.common.collect.Sets;
import com.spleefleague.core.Core;
import com.spleefleague.core.game.BattleMode;
import com.spleefleague.core.game.manager.BattleManager;
import com.spleefleague.core.listener.bungee.BungeeListener;
import com.spleefleague.core.plugin.CorePlugin;
import com.spleefleague.coreapi.utils.packet.QueueContainerInfo;
import com.spleefleague.coreapi.utils.packet.bungee.PacketRefreshAll;
import org.bukkit.entity.Player;

public class RefreshAllBungeeListener extends BungeeListener<PacketRefreshAll> {

    @Override
    protected void receive(Player sender, PacketRefreshAll packet) {
        for (CorePlugin<?> plugin : CorePlugin.getAllPlugins()) {
            plugin.getPlayers().refresh(Sets.newHashSet(packet.players));
        }
        for (QueueContainerInfo qci : packet.queueInfoList) {
            BattleManager manager = Core.getInstance().getBattleManager(BattleMode.get(qci.name));
            manager.setQueued(qci.queued);
            manager.setPlaying(qci.playing);
            manager.setSpectators(qci.spectators);
        }
    }

}
