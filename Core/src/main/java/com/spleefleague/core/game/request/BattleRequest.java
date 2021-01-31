package com.spleefleague.core.game.request;

import com.spleefleague.core.game.battle.Battle;
import com.spleefleague.core.player.CorePlayer;
import net.md_5.bungee.api.chat.TextComponent;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;

/**
 * @author NickM13
 * @since 4/27/2020
 */
public abstract class BattleRequest {
    
    private static final double REQUIRED = 0.65D;
    
    protected final Battle<?> battle;
    protected String chatName, scoreboardName;
    protected final boolean battlerRequest;
    protected final String requestName;
    protected final Set<CorePlayer> requestingPlayers = new HashSet<>();
    
    protected BattleRequest(Battle<?> battle, boolean isBattlerRequest, String requestName) {
        this.battle = battle;
        this.battlerRequest = isBattlerRequest;
        this.requestName = requestName;
    }
    
    public final String getRequestName() {
        return requestName;
    }
    
    public final String getChatName() {
        return chatName;
    }
    
    public final String getScoreboardName() {
        return scoreboardName;
    }
    
    /**
     * Whether this request requires the player to be a battler or in the battle
     *
     * @return Battler Request
     */
    public boolean isBattlerRequest() {
        return battlerRequest;
    }
    
    public boolean isOngoing() {
        return !requestingPlayers.isEmpty();
    }
    
    public void startRequest(CorePlayer cp, int total, @Nullable String requestValue) {
        if (attemptStartRequest(cp, total, requestValue)) {
            if (total == 1) {
                meetsRequirement();
            } else {
                startRequestMessage(cp);
                battle.getChatGroup().addTeam(requestName, getScoreboardName());
                addRequester(cp, total);
            }
        } else {
            battle.getPlugin().sendMessage(cp, "Not a valid requested value!");
        }
    }
    protected abstract boolean attemptStartRequest(CorePlayer cp, int total, @Nullable String requestValue);
    
    protected void startRequestMessage(CorePlayer cp) {
        TextComponent text = new TextComponent("Request to ");
        text.addExtra(getChatName());
        text.addExtra(" was started by ");
        text.addExtra(cp.getChatName());
        battle.getChatGroup().sendMessage(text);
    }
    
    /**
     * Add a requesting player, returns false if player was already a requester
     *
     * @param cp Battle Player
     * @param total Total Possible Request Players
     */
    public void addRequester(CorePlayer cp, int total) {
        battle.getPlugin().sendMessage(cp, "You requested to " + getChatName());
        requestingPlayers.add(cp);
        battle.getChatGroup().setTeamDisplayName(requestName, getScoreboardName() + ": " + (int)(getPercent(total) * 100) + "%");
        checkRequired(total);
    }
    
    /**
     * Removes a player from the requesting set, returns false if they weren't a requester
     *
     * @param cp Battle Player
     */
    public void removeRequester(CorePlayer cp) {
        battle.getPlugin().sendMessage(cp, "You are no longer requesting to " + getChatName());
        requestingPlayers.remove(cp);
        if (requestingPlayers.isEmpty()) {
            battle.getChatGroup().removeTeam(requestName);
        }
    }
    
    /**
     * Returns whether or not a player is currently requesting this
     *
     * @param cp Core Player
     * @return Is Requesting
     */
    public boolean isRequesting(CorePlayer cp) {
        return requestingPlayers.contains(cp);
    }
    
    /**
     * Remove all requesting players from the set
     */
    public void clear() {
        requestingPlayers.clear();
        battle.getChatGroup().removeTeam(requestName);
    }
    
    /**
     * Get percent of players that are currently requesting vs total players in game
     *
     * @param total Total Players
     * @return Percent
     */
    public double getPercent(int total) {
        return (double) requestingPlayers.size() / total;
    }
    
    /**
     * Check whether enough players are requesting for the action to be called
     *
     * @param total Total Players
     * @return Requirement Met
     */
    public boolean checkRequired(int total) {
        if (getPercent(total) >= REQUIRED) {
            meetsRequirement();
            battle.getChatGroup().sendMessage("Request to " + getChatName() + " has passed");
            clear();
            return true;
        }
        return false;
    }
    
    /**
     * Called when enough players are requesting this
     */
    protected abstract void meetsRequirement();
    
}
