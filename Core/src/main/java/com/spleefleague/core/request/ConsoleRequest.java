/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.request;

import com.spleefleague.core.player.CorePlayer;
import java.util.function.BiConsumer;

/**
 * @author NickM13
 */
public class ConsoleRequest extends Request {
    
    protected BiConsumer<CorePlayer, String> action;
    
    public ConsoleRequest(BiConsumer<CorePlayer, String> action) {
        super();
        this.action = action;
    }
    
    @Override
    public void accept(CorePlayer receiver, String target) {
        if (isExpired()) {
            timeout(receiver, target);
        } else {
            action.accept(receiver, target);
        }
    }
    
    @Override
    public void decline(CorePlayer receiver, String target) {
        receiver.sendMessage(tag + "You have declined " + target + " request");
    }
    
    @Override
    public void timeout(CorePlayer receiver, String target) {
        receiver.sendMessage(tag + "Request from " + target + " has timed out");
    }
    
}
