/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.player.party;

import com.spleefleague.core.Core;
import com.spleefleague.core.chat.ChatGroup;
import com.spleefleague.core.menu.InventoryMenuAPI;
import com.spleefleague.core.menu.InventoryMenuContainerChest;
import com.spleefleague.core.menu.InventoryMenuItemHotbar;
import com.spleefleague.core.player.CorePlayer;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Material;

/**
 * @author NickM13
 */
public class Party {
    
    private static final InventoryMenuItemHotbar partyListItem = (InventoryMenuItemHotbar) InventoryMenuAPI.createItemHotbar(3, "party")
            .setName("Party List")
            .setDescription("Open a list of all the current players in your party")
            .setDisplayItem(Material.JUNGLE_BOAT)
            .setAvailability(CorePlayer::isInParty)
            .setLinkedContainer(InventoryMenuAPI.createContainer()
                    .setTitle("Party List")
                    .setOpenAction((container, cp) -> {
                        container.clearUnsorted();
                        Party party = cp.getParty();
                        if (party != null) {
                            party.getPlayers().forEach(cp2 -> container.addMenuItem(InventoryMenuAPI.createItemDynamic()
                                    .setName(cp2.getDisplayName())
                                    .setDescription(party.getOwner().equals(cp2) ? "Owner" : "")
                                    .setDisplayItem(cp2.getSkull())));
                        }
                    }));
    
    public static Party createParty(CorePlayer cp) {
        return new Party(cp);
    }
    
    private CorePlayer owner;
    private final Set<CorePlayer> players = new LinkedHashSet<>();
    private final ChatGroup chatGroup = new ChatGroup(new TextComponent(""));
    private boolean disbanded = false;
    
    public ChatGroup getChatGroup() {
        return chatGroup;
    }
     
    protected Party(CorePlayer owner) {
        this.owner = owner;
        Core.getInstance().sendMessage(owner, "You have created a party");
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
            Core.getInstance().sendMessage(cp, "You have joined a party");
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
            Core.getInstance().sendMessage(cp, "You were kicked from the party");
            chatGroup.sendMessage(cp.getDisplayName() + " was kicked from the party");
            return true;
        }
        return false;
    }
    
    public void leave(CorePlayer cp) {
        if (remove(cp)) {
            Core.getInstance().sendMessage(cp, "You have left the party");
            chatGroup.sendMessage(cp.getDisplayName() + " has left the party");
        }
    }
    
    public void openPartyList(CorePlayer cp) {
        InventoryMenuContainerChest menuContainer = InventoryMenuAPI.createContainer()
                .setTitle("Party List");
        
    }
    
}
