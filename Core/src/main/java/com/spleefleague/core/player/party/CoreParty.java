/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.player.party;

import com.spleefleague.core.Core;
import com.spleefleague.core.chat.Chat;
import com.spleefleague.core.chat.ChatGroup;
import com.spleefleague.core.menu.InventoryMenuAPI;
import com.spleefleague.core.menu.InventoryMenuContainerChest;
import com.spleefleague.core.menu.InventoryMenuItemHotbar;
import com.spleefleague.core.player.CorePlayer;

import java.util.*;

import com.spleefleague.coreapi.database.variable.DBPlayer;
import com.spleefleague.coreapi.party.Party;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Material;

/**
 * @author NickM13
 */
public class CoreParty extends Party {

    /*
    private static final InventoryMenuItemHotbar partyListItem = (InventoryMenuItemHotbar) InventoryMenuAPI.createItemHotbar(3, "party")
            .setName("Party List")
            .setDescription("Open a list of all the current players in your party")
            .setDisplayItem(Material.JUNGLE_BOAT)
            .setAvailability(CorePlayer::isInParty)
            .setLinkedContainer(InventoryMenuAPI.createContainer()
                    .setTitle("Party List")
                    .setOpenAction((container, cp) -> {
                        container.clearUnsorted();
                        CoreParty party = cp.getParty();
                        if (party != null) {
                            party.getPlayerSet().forEach(cp2 -> container.addMenuItem(InventoryMenuAPI.createItemDynamic()
                                    .setName(cp2.getDisplayName())
                                    .setDescription(party.getOwner().equals(cp2) ? "Owner" : "")
                                    .setDisplayItem(cp2.getSkull())));
                        }
                    }));
    */

    private final Set<CorePlayer> playerSet = new HashSet<>();
    private final Set<CorePlayer> localPlayers = new HashSet<>();
     
    public CoreParty(UUID owner) {
        super(owner);
    }

    @Override
    public void setOwner(UUID owner) {
        super.setOwner(owner);
        TextComponent text = new TextComponent();
        text.addExtra(Core.getInstance().getPlayers().get(owner).getChatName());
        text.addExtra(" is now the party owner");
        sendMessage(text);
    }

    public boolean isOwner(CorePlayer cp) {
        return owner.equals(cp.getUniqueId());
    }
    
    public Set<CorePlayer> getPlayerSet() {
        return playerSet;
    }

    public Set<CorePlayer> getLocalPlayers() {
        return localPlayers;
    }
    
    public TextComponent getPlayersFormatted() {
        TextComponent message = new TextComponent("");
        
        Iterator<CorePlayer> cpit = playerSet.iterator();
        while (cpit.hasNext()) {
            CorePlayer cp = cpit.next();
            message.addExtra(cp.getChatName());
            if (cpit.hasNext()) {
                message.addExtra(new TextComponent(", "));
            }
        }
        
        return message;
    }

    public void sendMessage(TextComponent text) {
        for (CorePlayer cp : localPlayers) {
            Core.getInstance().sendMessage(cp, text);
        }
    }

    public void addLocal(CorePlayer cp) {
        localPlayers.add(cp);
    }

    public void removeLocal(CorePlayer cp) {
        localPlayers.remove(cp);
        if (localPlayers.isEmpty()) {
            Core.getInstance().getPartyManager().removeParty(this);
        }
    }

    public boolean leave(UUID uuid) {
        if (removePlayer(uuid)) {
            CorePlayer cp = Core.getInstance().getPlayers().get(uuid);
            cp.leaveParty();
            return true;
        }
        return false;
    }

    public boolean kick(UUID uuid) {
        if (removePlayer(uuid)) {
            CorePlayer cp = Core.getInstance().getPlayers().get(uuid);
            cp.leaveParty();

            return true;
        }
        return false;
    }

    @Override
    public void addPlayer(UUID uuid) {
        super.addPlayer(uuid);

        CorePlayer cp = Core.getInstance().getPlayers().get(uuid);

        cp.joinParty(this);

        TextComponent text = new TextComponent();
        text.addExtra(cp.getChatName());
        text.addExtra(" has joined the party");
        sendMessage(text);

        playerSet.add(cp);

        if (cp.getOnlineState() == DBPlayer.OnlineState.HERE) {
            localPlayers.add(cp);
        }
    }

    @Override
    public boolean removePlayer(UUID uuid) {
        CorePlayer cp = Core.getInstance().getPlayers().get(uuid);
        playerSet.remove(cp);
        localPlayers.remove(cp);
        return super.removePlayer(uuid);
    }
    
    public void openPartyList(CorePlayer cp) {
        InventoryMenuContainerChest menuContainer = InventoryMenuAPI.createContainer()
                .setTitle("Party List");
    }

    public void refresh(List<UUID> players) {
        if (!players.get(0).equals(owner)) {
            setOwner(players.get(0));
        }
        for (UUID uuid : playerList) {
            CorePlayer cp = Core.getInstance().getPlayers().get(uuid);
            if (cp != null && cp.isOnline()) {
                cp.leaveParty();
            }
        }
        playerList.clear();
        playerSet.clear();
        localPlayers.clear();
        for (UUID uuid : players) {
            playerList.add(uuid);

            CorePlayer cp = Core.getInstance().getPlayers().get(uuid);

            cp.joinParty(this);

            playerSet.add(cp);
            if (cp.getOnlineState() == DBPlayer.OnlineState.HERE) {
                localPlayers.add(cp);
            }
        }
    }
    
}
