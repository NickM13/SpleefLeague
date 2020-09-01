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
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

/**
 * @author NickM13
 */
public class RequestManager {
    
    // Receiver, <Sender, Request>
    protected static Map<String, Map<String, Request>> requests = new HashMap<>();
    
    protected static void validatePlayer(CorePlayer receiver) {
        if (!requests.containsKey(receiver.getName())) {
            requests.put(receiver.getName(), new HashMap<>());
        }
    }
    
    public static void checkTimeouts() {
        CorePlayer receiver;
        for (Map.Entry<String, Map<String, Request>> r : requests.entrySet()) {
            receiver = Core.getInstance().getPlayers().get(r.getKey());
            Iterator<Map.Entry<String, Request>> sit = r.getValue().entrySet().iterator();
            while (sit.hasNext()) {
                Map.Entry<String, Request> sn = sit.next();
                if (sn.getValue().isExpired()) {
                    sn.getValue().timeout(receiver, sn.getKey());
                    sit.remove();
                }
            }
        }
    }
    
    public static boolean acceptRequest(CorePlayer receiver, String target) {
        validatePlayer(receiver);
        if (requests.get(receiver.getName()).containsKey(target)) {
            requests.get(receiver.getName()).get(target).accept(receiver, target);
            requests.get(receiver.getName()).remove(target);
            return true;
        } else {
            //Core.sendMessageToPlayer(receiver, "Request no longer exists");
            return false;
        }
    }
    
    public static boolean declineRequest(CorePlayer receiver, String target) {
        validatePlayer(receiver);
        if (requests.get(receiver.getName()).containsKey(target)) {
            requests.get(receiver.getName()).get(target).decline(receiver, target);
            requests.get(receiver.getName()).remove(target);
            return true;
        } else {
            return false;
        }
    }
    
    public static void sendRequest(String tag, String msg, CorePlayer receiver, String target, Request request) {
        validatePlayer(receiver);
        request.setTag(tag);
        requests.get(receiver.getName()).put(target, request);
        
        TextComponent text = new TextComponent(tag);
        TextComponent accept = new TextComponent(Chat.TAG_BRACE + "[" + Chat.SUCCESS + "Accept" + Chat.TAG_BRACE + "]");
        accept.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to accept").create()));
        accept.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/request accept " + target));
        TextComponent decline = new TextComponent(Chat.TAG_BRACE + "[" + Chat.ERROR + "Decline" + Chat.TAG_BRACE + "]");
        decline.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to decline").create()));
        decline.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/request decline " + target));
        text.addExtra(accept);
        text.addExtra(" ");
        text.addExtra(decline);
        
        receiver.sendMessage(tag + msg);
        receiver.sendMessage(text);
    }
    
}
