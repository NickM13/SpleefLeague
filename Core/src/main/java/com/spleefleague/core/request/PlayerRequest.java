/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.request;

import com.spleefleague.core.Core;
import com.spleefleague.core.player.CorePlayer;
import java.util.function.BiConsumer;

/**
 * @author NickM13
 */
public class PlayerRequest extends Request {
    
    protected BiConsumer<CorePlayer, CorePlayer> action;
    
    public PlayerRequest(BiConsumer<CorePlayer, CorePlayer> action) {
        super();
        this.action = action;
    }
    
    @Override
    public void accept(CorePlayer receiver, String target) {
        if (isTimedout()) {
            timeout(receiver, target);
        } else {
            action.accept(receiver, Core.getInstance().getPlayers().get(target));
        }
    }
    
    @Override
    public void decline(CorePlayer receiver, String target) {
        CorePlayer sender = Core.getInstance().getPlayers().get(target);
        receiver.sendMessage(tag + "You have declined " + sender.getDisplayNamePossessive()+ " request");
        sender.sendMessage(tag + receiver.getDisplayName() + " declined your reqeust");
    }
    
    @Override
    public void timeout(CorePlayer receiver, String target) {
        CorePlayer sender = Core.getInstance().getPlayers().get(target);
        receiver.sendMessage(tag + "Request from " + sender.getDisplayName()+ " has timed out");
        sender.sendMessage(tag + "Request to " + receiver.getDisplayName() + " has timed out");
    }
    
}
