package com.spleefleague.core.game.request;

import com.spleefleague.core.game.BattleUtils;
import com.spleefleague.core.game.battle.Battle;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.coreapi.chat.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;

/**
 * @author NickM13
 * @since 4/27/2020
 */
public abstract class BattleRequest {


    protected final Battle<?> battle;
    protected String chatName, scoreboardName;
    protected final boolean requireLiving;
    protected final double required;
    protected final String requestName;
    protected final Set<CorePlayer> requestingPlayers = new HashSet<>();

    protected BattleRequest(Battle<?> battle, String requestName, boolean requireLiving, double required) {
        this.battle = battle;
        this.requestName = requestName;
        this.requireLiving = requireLiving;
        this.required = required;
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

    public final boolean isRequireLiving() {
        return requireLiving;
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
                addRequester(cp, total, false);
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
        text.addExtra(".");
        battle.getChatGroup().sendMessage(text);
    }

    /**
     * Add a requesting player, returns false if player was already a requester
     *
     * @param cp    Battle Player
     * @param total Total Possible Request Players
     */
    public void addRequester(CorePlayer cp, int total, boolean personalConfirmation) {
        requestingPlayers.add(cp);
        battle.getChatGroup().setTeamDisplayName(requestName, getScoreboardName() + ": [" + BattleUtils.toRequestSquares(getPercent(total) / required) + ChatColor.WHITE + "]");
        if (!checkRequired(total) && personalConfirmation) {
            battle.getPlugin().sendMessage(cp, "You requested to " + getChatName() + ".");
        }
    }

    /**
     * Removes a player from the requesting set, returns false if they weren't a requester
     *
     * @param cp Battle Player
     */
    public void removeRequester(CorePlayer cp, boolean confirmation) {
        if (confirmation) {
            battle.getPlugin().sendMessage(cp, "You are no longer requesting to " + getChatName() + ".");
        }
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
        if (getPercent(total) >= required) {
            meetsRequirement();
            battle.getChatGroup().sendMessage("Request to " + getChatName() + " has passed.");
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
