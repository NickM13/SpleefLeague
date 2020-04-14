/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.chat;

import com.spleefleague.core.player.CorePlayer;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

/**
 * @author NickM13
 */
public class ChatRequest {
    
    private static long lastIndex = 0;
    private static final Map<Long, ChatRequest> requests = new HashMap<>();
    
    public static long createRequest(String chatMessage, String hoverMessage, String clickCommand, CorePlayer sender, CorePlayer receiver, BiConsumer<CorePlayer, CorePlayer> action, long seconds) {
        TextComponent text = new TextComponent(chatMessage);
        text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(hoverMessage).create()));
        text.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, clickCommand + lastIndex));
        text.setBold(true);
        text.setItalic(true);
        text.setColor(ChatColor.LIGHT_PURPLE);
        receiver.sendMessage(text);
        requests.put(lastIndex, new ChatRequest(sender, receiver, action, seconds * 1000));
        lastIndex++;
        return lastIndex - 1;
    }
    
    public static boolean acceptRequest(CorePlayer sender, long index) {
        ChatRequest request = requests.get(index);
        if (request != null && !request.isExpired()) {
            return request.accept(sender);
        }
        return false;
    }
    
    private long expireTime;
    private final CorePlayer sender;
    private final CorePlayer receiver;
    private final BiConsumer<CorePlayer, CorePlayer> action;
    
    public ChatRequest(CorePlayer sender, CorePlayer receiver, BiConsumer<CorePlayer, CorePlayer> action, long expireTime) {
        this.sender = sender;
        this.receiver = receiver;
        this.action = action;
        this.expireTime = expireTime == 0 ? 0 : expireTime + System.currentTimeMillis();
    }
    
    public boolean isExpired() {
        if (expireTime == 0) {
            return false;
        }
        return expireTime < System.currentTimeMillis();
    }
    
    public boolean accept(CorePlayer receiver) {
        if (this.receiver.equals(receiver)) {
            action.accept(sender, receiver);
            expireTime = System.currentTimeMillis();
            return true;
        }
        return false;
    }
    
}
