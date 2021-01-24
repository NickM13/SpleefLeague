/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.request;

import com.spleefleague.core.Core;
import com.spleefleague.core.player.CorePlayer;
import net.md_5.bungee.api.chat.BaseComponent;

import java.util.function.BiConsumer;

/**
 * @author NickM13
 */
public class PlayerRequest extends Request {
    
    protected BiConsumer<CorePlayer, CorePlayer> action;
    protected CorePlayer target;
    
    public PlayerRequest(BiConsumer<CorePlayer, CorePlayer> action, CorePlayer receiver, BaseComponent tag, CorePlayer target) {
        super(receiver, tag);
        this.target = target;
        this.action = action;
    }
    
    @Override
    public void accept() {
        if (isExpired()) {
            timeout();
        } else {
            action.accept(receiver, target);
        }
    }
    
    @Override
    public void decline() {
        receiver.sendMessage(tag + "You have declined " + target.getDisplayNamePossessive() + " request");
        target.sendMessage(tag + receiver.getDisplayName() + " declined your request");
    }
    
    @Override
    public void timeout() {
        CorePlayer sender = Core.getInstance().getPlayers().get(target);
        receiver.sendMessage(tag + "Request from " + sender.getDisplayName()+ " has timed out");
        sender.sendMessage(tag + "Request to " + receiver.getDisplayName() + " has timed out");
    }
    
}
