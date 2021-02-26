/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.player.party;

import com.spleefleague.core.Core;
import com.spleefleague.core.chat.ChatChannel;
import com.spleefleague.core.menu.InventoryMenuAPI;
import com.spleefleague.core.menu.InventoryMenuContainerChest;
import com.spleefleague.core.player.CorePlayer;

import java.util.*;

import com.spleefleague.coreapi.database.variable.DBPlayer;
import com.spleefleague.coreapi.party.Party;
import net.md_5.bungee.api.chat.TextComponent;

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

    private final Map<UUID, CorePlayer> playerSet = new HashMap<>();
    private final Map<UUID, CorePlayer> localPlayers = new HashMap<>();

    public CoreParty(UUID owner, List<UUID> players) {
        super(owner);
        for (UUID uuid : players) {
            addPlayer(uuid);
        }
    }

    @Override
    public void setOwner(UUID owner) {
        super.setOwner(owner);
    }

    public boolean isOwner(CorePlayer cp) {
        return owner.equals(cp.getUniqueId());
    }

    public Collection<CorePlayer> getPlayerSet() {
        return playerSet.values();
    }

    public Collection<CorePlayer> getLocalPlayers() {
        return localPlayers.values();
    }

    public TextComponent getPlayersFormatted() {
        TextComponent message = new TextComponent("");

        Iterator<Map.Entry<UUID, CorePlayer>> it = playerSet.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<UUID, CorePlayer> entry = it.next();
            message.addExtra(entry.getValue().getChatName());
            if (it.hasNext()) {
                message.addExtra(new TextComponent(", "));
            }
        }

        return message;
    }

    public void sendMessage(TextComponent text) {
        for (CorePlayer cp : localPlayers.values()) {
            Core.getInstance().sendMessage(cp, text);
        }
    }

    public void addLocal(CorePlayer cp) {
        localPlayers.put(cp.getUniqueId(), cp);
    }

    public void removeLocal(UUID uuid) {
        localPlayers.remove(uuid);
        if (localPlayers.isEmpty()) {
            Core.getInstance().getPartyManager().removeParty(this);
        }
    }

    public boolean leave(UUID uuid) {
        CorePlayer cp = Core.getInstance().getPlayers().get(uuid);
        if (cp != null) {
            if (cp.getChatChannel() == ChatChannel.PARTY) {
                cp.setChatChannel(ChatChannel.GLOBAL);
            }
        }
        return removePlayer(uuid);
    }

    @Override
    public void addPlayer(UUID uuid) {
        super.addPlayer(uuid);

        CorePlayer cp = Core.getInstance().getPlayers().get(uuid);

        playerSet.put(uuid, cp);

        if (cp.getOnlineState() == DBPlayer.OnlineState.HERE) {
            localPlayers.put(uuid, cp);
        }
    }

    @Override
    public boolean removePlayer(UUID uuid) {
        playerSet.remove(uuid);
        localPlayers.remove(uuid);
        return super.removePlayer(uuid);
    }

    public void openPartyList(CorePlayer cp) {
        InventoryMenuContainerChest menuContainer = InventoryMenuAPI.createContainer()
                .setTitle("Party List");
    }

    public void refresh(CorePlayer owner, List<UUID> players) {
        if (!this.owner.equals(owner.getUniqueId())) {
            this.owner = owner.getUniqueId();
        }
        playerList.clear();
        playerSet.clear();
        localPlayers.clear();
        for (UUID uuid : players) {
            playerList.add(uuid);

            CorePlayer cp = Core.getInstance().getPlayers().get(uuid);

            playerSet.put(uuid, cp);
            if (cp.getOnlineState() == DBPlayer.OnlineState.HERE) {
                localPlayers.put(uuid, cp);
            }
        }
    }

}
