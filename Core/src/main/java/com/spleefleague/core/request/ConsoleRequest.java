/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.request;

import com.spleefleague.core.player.CorePlayer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.function.BiConsumer;

/**
 * @author NickM13
 */
public class ConsoleRequest extends Request {

    protected BiConsumer<CorePlayer, String> action;
    protected String name;

    public ConsoleRequest(BiConsumer<CorePlayer, String> action, CorePlayer receiver, BaseComponent tag, String name) {
        super(receiver, tag);
        this.name = name;
        this.action = action;
    }

    @Override
    public void accept() {
        if (isExpired()) {
            timeout();
        } else {
            action.accept(receiver, name);
        }
    }

    @Override
    public void decline() {
        receiver.sendMessage(tag, new TextComponent("You have declined " + name + " request"));
    }

    @Override
    public void timeout() {
        receiver.sendMessage(tag + "Request from " + name + " has timed out");
    }

}
