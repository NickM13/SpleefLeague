/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.request;

import com.spleefleague.core.player.CorePlayer;
import net.md_5.bungee.api.chat.BaseComponent;

/**
 * @author NickM13
 */
public abstract class Request {

    protected long timeout;
    protected CorePlayer receiver;
    protected BaseComponent tag;

    public Request(CorePlayer receiver, BaseComponent tag) {
        this.timeout = System.currentTimeMillis() + 120 * 1000;
        this.receiver = receiver;
        this.tag = tag;
    }

    public boolean isExpired() {
        return timeout < System.currentTimeMillis();
    }

    public abstract void accept();

    public abstract void decline();

    public abstract void timeout();

}
