/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.party;

import com.spleefleague.core.Core;
import com.spleefleague.core.chat.ChatGroup;
import com.spleefleague.core.player.CorePlayer;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import net.md_5.bungee.api.chat.TextComponent;

/**
 * @author NickM13
 */
public class Party {
    
    private CorePlayer owner;
    private final Set<CorePlayer> players = new LinkedHashSet<>();
    private final ChatGroup chatGroup = new ChatGroup();
    private boolean disbanded = false;
    
    public static Party createParty(CorePlayer cp) {
        return new Party(cp);
    }
    
    public ChatGroup getChatGroup() {
        return chatGroup;
    }
     
    protected Party(CorePlayer owner) {
        this.owner = owner;
        Core.sendMessageToPlayer(owner, "You have created a party");
        players.add(owner);
        chatGroup.addPlayer(owner);
        owner.joinParty(this);
    }
    
    public boolean isOwner(CorePlayer cp) {
        return owner.equals(cp);
    }
    
    public CorePlayer getOwner() {
        return owner;
    }
    
    public Set<CorePlayer> getPlayers() {
        return players;
    }
    
    public boolean isDisbanded() {
        return disbanded;
    }
    
    public TextComponent getPlayersFormatted() {
        TextComponent message = new TextComponent("");
        
        Iterator<CorePlayer> cpit = players.iterator();
        while (cpit.hasNext()) {
            CorePlayer cp = cpit.next();
            message.addExtra(cp.getChatName());
            if (cpit.hasNext()) {
                message.addExtra(new TextComponent(", "));
            }
        }
        
        return message;
    }
    
    public void unqueue() {
        if (Core.getInstance().unqueuePartyGlobally(this)) {
            chatGroup.sendMessage("Your party has left all queues");
        }
    }
    
    public void transferOwnership(CorePlayer cp) {
        unqueue();
        if (players.contains(cp)) {
            owner = cp;
        }
    }
    
    public void add(CorePlayer cp) {
        if (cp.getParty() == null && !players.contains(cp) && !disbanded) {
            unqueue();
            chatGroup.sendMessage(cp.getDisplayName() + " has joined the party");
            Core.sendMessageToPlayer(cp, "You have joined a party");
            players.add(cp);
            chatGroup.addPlayer(cp);
            cp.joinParty(this);
        }
    }
    
    protected boolean remove(CorePlayer cp) {
        if (players.contains(cp)) {
            unqueue();
            players.remove(cp);
            chatGroup.removePlayer(cp);
            cp.leaveParty();
            if (players.isEmpty()) {
                disbanded = true;
            } else if (owner.equals(cp)) {
                owner = players.iterator().next();
            }
            return true;
        }
        return false;
    }
    
    public boolean kick(CorePlayer cp) {
        if (remove(cp)) {
            Core.sendMessageToPlayer(cp, "You were kicked from the party");
            chatGroup.sendMessage(cp.getDisplayName() + " was kicked from the party");
            return true;
        }
        return false;
    }
    
    public void leave(CorePlayer cp) {
        if (remove(cp)) {
            Core.sendMessageToPlayer(cp, "You have left the party");
            chatGroup.sendMessage(cp.getDisplayName() + " has left the party");
        }
    }
    
}
