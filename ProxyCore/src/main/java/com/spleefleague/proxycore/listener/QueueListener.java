package com.spleefleague.proxycore.listener;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import com.spleefleague.proxycore.ProxyCore;
import com.spleefleague.proxycore.game.queue.QueueManager;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.UUID;

/**
 * @author NickM13
 * @since 6/23/2020
 */
public class QueueListener implements Listener {

    public QueueListener() {
        ProxyCore.getInstance().getProxy().registerChannel("queue:join");
        ProxyCore.getInstance().getProxy().registerChannel("queue:leave");
        ProxyCore.getInstance().getProxy().registerChannel("queue:leaveall");
        ProxyCore.getInstance().getProxy().registerChannel("queue:solo");
    }

    @EventHandler
    public void onChatPluginMessage(PluginMessageEvent event) {
        if (event.getTag().startsWith("queue:")) {
            ByteArrayDataInput input = ByteStreams.newDataInput(event.getData());
            UUID player = UUID.fromString(input.readUTF());
            switch (event.getTag()) {
                case "queue:join": {
                    String mode = input.readUTF();
                    String param = input.readUTF();
                    QueueManager.joinQueue(player, mode, param);
                    break;
                }
                case "queue:leave": {
                    String mode = input.readUTF();
                    QueueManager.leaveQueue(player, mode);
                    break;
                }
                case "queue:leaveall": {
                    QueueManager.leaveAllQueues(player);
                    break;
                }
                case "queue:solo": {
                    String mode = input.readUTF();
                    String param = input.readUTF();
                    QueueManager.joinSolo(player, mode, param);
                    break;
                }
            }
        }
    }

}
