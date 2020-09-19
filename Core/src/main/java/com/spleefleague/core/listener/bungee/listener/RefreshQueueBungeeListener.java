package com.spleefleague.core.listener.bungee.listener;

import com.spleefleague.core.Core;
import com.spleefleague.core.game.BattleMode;
import com.spleefleague.core.game.manager.BattleManager;
import com.spleefleague.core.listener.bungee.BungeeListener;
import com.spleefleague.coreapi.utils.packet.bungee.PacketRefreshQueue;
import org.bukkit.entity.Player;

public class RefreshQueueBungeeListener extends BungeeListener<PacketRefreshQueue> {

    @Override
    protected void receive(Player sender, PacketRefreshQueue packet) {
        BattleManager manager = Core.getInstance().getBattleManager(BattleMode.get(packet.queueInfo.name));
        manager.setQueued(packet.queueInfo.queued);
        manager.setPlaying(packet.queueInfo.playing);
        manager.setSpectators(packet.queueInfo.spectators);
    }

}
