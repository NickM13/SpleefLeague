/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.request;

import com.spleefleague.core.Core;
import com.spleefleague.core.chat.Chat;
import com.spleefleague.core.player.CorePlayer;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiConsumer;

import net.md_5.bungee.api.chat.*;
import org.bukkit.entity.Player;

/**
 * @author NickM13
 */
public class RequestManager {

    // Receiver, <Sender, Request>
    protected static Map<String, Map<UUID, Request>> requests = new HashMap<>();

    protected static void validatePlayer(CorePlayer receiver) {
        if (!requests.containsKey(receiver.getName())) {
            requests.put(receiver.getName(), new HashMap<>());
        }
    }

    public static void checkTimeouts() {
        CorePlayer receiver;
        for (Map.Entry<String, Map<UUID, Request>> r : requests.entrySet()) {
            receiver = Core.getInstance().getPlayers().get(r.getKey());
            Iterator<Map.Entry<UUID, Request>> sit = r.getValue().entrySet().iterator();
            while (sit.hasNext()) {
                Map.Entry<UUID, Request> sn = sit.next();
                if (sn.getValue().isExpired()) {
                    sn.getValue().timeout();
                    sit.remove();
                }
            }
        }
    }

    public static boolean acceptRequest(CorePlayer receiver, UUID uuid) {
        validatePlayer(receiver);
        if (requests.get(receiver.getName()).containsKey(uuid)) {
            requests.get(receiver.getName()).get(uuid).accept();
            requests.get(receiver.getName()).remove(uuid);
            return true;
        } else {
            //Core.sendMessageToPlayer(receiver, "Request no longer exists");
            return false;
        }
    }

    public static boolean declineRequest(CorePlayer receiver, UUID uuid) {
        validatePlayer(receiver);
        if (requests.get(receiver.getName()).containsKey(uuid)) {
            requests.get(receiver.getName()).get(uuid).decline();
            requests.get(receiver.getName()).remove(uuid);
            return true;
        } else {
            return false;
        }
    }

    public static void sendPlayerRequest(BaseComponent tag, CorePlayer receiver, CorePlayer target, BiConsumer<CorePlayer, CorePlayer> action, BaseComponent... messages) {
        PlayerRequest request = new PlayerRequest(action, receiver, tag, target);
        sendRequest(tag, receiver, target.getName(), request, messages);
    }

    public static void sendConsoleRequest(BaseComponent tag, CorePlayer receiver, String target, BiConsumer<CorePlayer, String> action, BaseComponent... messages) {
        ConsoleRequest request = new ConsoleRequest(action, receiver, tag, target);
        sendRequest(tag, receiver, target, request, messages);
    }

    public static void sendRequest(BaseComponent tag, CorePlayer receiver, String target, Request request, BaseComponent... message) {
        validatePlayer(receiver);
        UUID uuid = UUID.randomUUID();
        requests.get(receiver.getName()).put(uuid, request);

        TextComponent accept = new TextComponent(Chat.TAG_BRACE + "[" + Chat.SUCCESS + "Accept" + Chat.TAG_BRACE + "]");
        accept.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to accept").create()));
        accept.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/request accept " + uuid.toString()));
        TextComponent decline = new TextComponent(Chat.TAG_BRACE + "[" + Chat.ERROR + "Decline" + Chat.TAG_BRACE + "]");
        decline.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to decline").create()));
        decline.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/request decline " + uuid.toString()));

        receiver.sendMessage(new ComponentBuilder().append(tag).append(" ").append(message).create());
        receiver.sendMessage(tag, accept, new TextComponent(" "), decline);
    }

}
