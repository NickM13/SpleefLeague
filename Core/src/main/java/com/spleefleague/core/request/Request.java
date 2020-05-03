/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.request;

import com.spleefleague.core.player.CorePlayer;

/**
 * @author NickM13
 */
public abstract class Request {
    
    protected String tag;
    protected long timeout;
    
    public Request() {
        this.timeout = System.currentTimeMillis() + 10 * 1000;
    }
    
    public void setTag(String tag) {
        this.tag = tag;
    }
    
    public boolean isTimedout() {
        return timeout < System.currentTimeMillis();
    }
    
    public abstract void accept(CorePlayer receiver, String sender);
    public abstract void decline(CorePlayer receiver, String sender);
    public abstract void timeout(CorePlayer receiver, String sender);
    
}
