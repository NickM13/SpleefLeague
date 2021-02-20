/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spleefleague.core.player;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.spleefleague.core.Core;
import com.spleefleague.core.chat.Chat;
import com.spleefleague.core.menu.InventoryMenuSkullManager;
import com.spleefleague.core.player.rank.CorePermanentRank;
import com.spleefleague.core.player.rank.CoreRank;
import com.spleefleague.core.player.rank.CoreTempRank;
import com.spleefleague.coreapi.database.annotation.DBField;
import com.spleefleague.coreapi.utils.packet.spigot.player.PacketSpigotPlayerRank;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * CorePlayer is an instance of DBPlayer that maintains various options and stats
 * unrelated to game modes, such as afk, ranking, and environment keys
 *
 * @author NickM13
 */
public class CoreOfflinePlayer extends CoreDBPlayer {

    /**
     * Database variables
     */

    @DBField protected final CorePermanentRank permRank = new CorePermanentRank();
    @DBField protected final List<CoreTempRank> tempRanks = new ArrayList<>();
    @DBField private Long lastOnline = -1L;

    private TextComponent chatName, chatNamePossessive, chatNameRanked;
    private String rankedDisplayName, menuName, displayName, displayNamePossessive;

    /**
     * Constructor for CorePlayer
     */
    public CoreOfflinePlayer() {
        super();
    }

    /**
     * Runs when a player who has never joined before logs in
     * This function always calls before init()
     */
    @Override
    public void newPlayer(UUID uuid, String username) {
        super.newPlayer(uuid, username);
        permRank.setRank(CoreRank.DEFAULT);
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(uuid);
    }

    /**
     * CorePlayer::init is called when a player comes online
     */
    @Override
    public void init() {
        updateRank();
    }

    @Override
    public void initOffline() {
        super.initOffline();
        updateChatNames();
    }

    /**
     * Called after loading data from the database DBEntity::load
     */
    @Override
    public void afterLoad() {
        super.afterLoad();
    }

    /**
     * Called on player quit
     */
    @Override
    public void close() {

    }

    private void updateChatNames() {
        menuName = Chat.PLAYER_NAME + ChatColor.BOLD + username;
        displayName = (getRank() == null ? Chat.PLAYER_NAME : getRank().getColor()) + username;
        displayNamePossessive = displayName + "'s";
        rankedDisplayName = ((getRank() != null && getRank().getDisplayNameUnformatted().length() > 0) ?
                Chat.TAG_BRACE + "[" + Chat.RANK + getRank().getDisplayName() + Chat.TAG_BRACE + "] " :
                "") + displayName;

        chatName = new TextComponent(getRank().getColor() + username);
        chatName.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/tell " + username + " "));

        chatNamePossessive = new TextComponent(getRank().getColor() + username + "'s");
        chatNamePossessive.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/tell " + username + " "));

        chatNameRanked = new TextComponent(getRank().getChatTag() + getRank().getColor() + username);
        chatNameRanked.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/tell " + username + " "));
    }

    public boolean isFlying() {
        return getPlayer().isFlying()
                || getPlayer().isGliding();
    }

    /**
     * @return Skull of player
     */
    public ItemStack getSkull() {
        return InventoryMenuSkullManager.getPlayerSkull(getUniqueId());
    }

    public String getTabName() {
        return getRankedDisplayName();
    }

    public String getRankedDisplayName() {
        return rankedDisplayName;
    }

    public String getMenuName() {
        return menuName;
    }

    /**
     * @return Display name of player including rank color, then resets color to default
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * @return Returns display name with an 's
     */
    public String getDisplayNamePossessive() {
        return displayNamePossessive;
    }

    /**
     * @return Name of player as TextComponent to allow for quick /tell
     */
    public TextComponent getChatName() {
        return chatName;
    }

    /**
     * @return Name of player as TextComponent to allow for quick /tell
     */
    public TextComponent getChatNamePossessive() {
        return chatNamePossessive;
    }

    /**
     * @return Name of player as TextComponent to allow for quick /tell
     */
    public TextComponent getChatNameRanked() {
        return chatNameRanked;
    }

    /**
     * Open the target player's inventory
     *
     * @param target Target Player
     */
    public void invsee(CorePlayer target) {
        getPlayer().openInventory(target.getPlayer().getInventory());
    }

    /**
     * Copies the target player's inventory and stores
     * Saves previous inventory to pregameState
     *
     * @param target Target Player
     */
    @Deprecated
    public void invcopy(CorePlayer target) {
        getPlayer().getInventory().setContents(target.getPlayer().getInventory().getContents());
    }

    /**
     * Updates all online player scoreboards of this player's rank
     * Updates tab list via sending packets
     * Updates all available permissions
     */
    public void updateRank() {
        updateChatNames();
    }

    /**
     * Removes expired TempRanks, calls updateRank() if any ranks were removed
     */
    public void checkTempRanks() {
        if (tempRanks.removeIf(tr -> tr.getExpireTime() < System.currentTimeMillis())) {
            updateRank();
        }
    }

    /**
     * Set permanent rank and calls updateRank()
     *
     * @param rank Rank
     */
    public void setRank(CoreRank rank) {
        permRank.setRank(rank);
        updateRank();
        PacketSpigotPlayerRank packet = new PacketSpigotPlayerRank(getUniqueId(), rank.getIdentifier(), 0L);
        Core.getInstance().sendPacket(packet);
    }

    /**
     * Adds a temporary rank and calls updateRank()
     *
     * @param rankName Rank Name
     * @param duration Duration
     * @return Whether rank was added
     */
    public boolean addTempRank(String rankName, long duration) {
        CoreRank rank = Core.getInstance().getRankManager().getRank(rankName);
        if (rank != null) {
            CoreTempRank tempRank = new CoreTempRank(rank, duration + System.currentTimeMillis());
            tempRanks.add(tempRank);
            updateRank();
            Core.getInstance().sendPacket(new PacketSpigotPlayerRank(getUniqueId(), rank.getIdentifier(), duration));
            return true;
        }
        return false;
    }

    /**
     * Removes all temporary ranks and calls updateRank()
     */
    public void clearTempRank() {
        if (tempRanks.isEmpty()) return;
        tempRanks.clear();
        updateRank();
        Core.getInstance().sendPacket(new PacketSpigotPlayerRank(getUniqueId(), "", 0));
    }

    public CoreRank getPermanentRank() {
        return permRank.getRank();
    }

    /**
     * Returns highest TempRank, if no TempRanks available then Permanent Rank
     * This allows us to set Admins to Default for a set time
     *
     * @return Highest TempRank, if no TempRanks available then Permanent Rank
     */
    public CoreRank getRank() {
        if (tempRanks.size() > 0) {
            CoreTempRank highestRank = null;
            for (CoreTempRank ctr : tempRanks) {
                if (highestRank == null ||
                        ctr.getRank().getLadder() > highestRank.getRank().getLadder()) {
                    highestRank = ctr;
                }
            }
            if (highestRank != null) {
                return highestRank.getRank();
            }
        }
        return permRank.getRank();
    }

    public int getRankLadder() {
        return getRank().getLadder();
    }

    @Override
    public void setOnline(OnlineState state) {
        super.setOnline(state);
    }

    /**
     * Send unformatted message to player
     *
     * @param string Message
     */
    public void sendMessage(String string) {
        getPlayer().sendMessage(string);
    }

    /**
     * Send BaseComponent message to player
     *
     * @param message BaseComponent
     */
    public void sendMessage(BaseComponent... message) {
        getPlayer().spigot().sendMessage(message);
    }

    /**
     * Send a title to player (Large text in middle of screen)
     *
     * @param title    Title
     * @param subtitle Subtitle
     * @param fadeIn   Ticks
     * @param stay     Ticks
     * @param fadeOut  Ticks
     */
    public void sendTitle(String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        getPlayer().sendTitle(title, subtitle, fadeIn, stay, fadeOut);
    }

    public void sendHotbarText(String text) {
        PacketContainer packet = new PacketContainer(PacketType.Play.Server.CHAT);
        packet.getChatTypes().write(0, EnumWrappers.ChatType.GAME_INFO);
        packet.getChatComponents().write(0, WrappedChatComponent.fromText(text));
        Core.sendPacket(this, packet);
    }

    public final boolean isOnline() {
        return onlineState != OnlineState.OFFLINE;
    }

    public final boolean isLocal() {
        return onlineState == OnlineState.HERE;
    }

    public final long getLastOnline() {
        if (onlineState != OnlineState.OFFLINE) {
            return System.currentTimeMillis();
        }
        return lastOnline;
    }

    /**
     * Used to get the inventory of a player, if they have a
     * pregameState saved (are ingame) then return their previous
     * inventory
     *
     * @return ItemStacks
     * @deprecated Under construction, pregameState is not a shared player variable
     */
    @Deprecated
    public final ItemStack[] getInventory() {
        return getPlayer().getInventory().getContents();
    }

    /**
     * Returns item in main hand
     *
     * @return ItemStack
     */
    public final ItemStack getHeldItem() {
        return getPlayer().getInventory().getItemInMainHand();
    }

    /**
     * Sets an item the player's main hand
     *
     * @param itemStack Held Item
     */
    public final void setHeldItem(ItemStack itemStack) {
        getPlayer().getInventory().setItemInMainHand(itemStack);
    }

    @Override
    public String toString() {
        return "CorePlayer{" +
                "uuid=" + getUniqueId() +
                ", username=" + getName() +
                '}';
    }

}
