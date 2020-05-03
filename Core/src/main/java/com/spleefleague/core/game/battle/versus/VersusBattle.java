package com.spleefleague.core.game.battle.versus;

import com.spleefleague.core.Core;
import com.spleefleague.core.chat.Chat;
import com.spleefleague.core.chat.ChatChannel;
import com.spleefleague.core.game.Arena;
import com.spleefleague.core.game.BattleMode;
import com.spleefleague.core.game.BattleUtils;
import com.spleefleague.core.game.battle.BattlePlayer;
import com.spleefleague.core.game.battle.Battle;
import com.spleefleague.core.game.request.EndGameRequest;
import com.spleefleague.core.game.request.PauseRequest;
import com.spleefleague.core.game.request.PlayToRequest;
import com.spleefleague.core.game.request.ResetRequest;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.plugin.CorePlugin;
import com.spleefleague.core.util.CoreUtils;

import java.util.List;

/**
 * @author NickM13
 * @since 4/24/2020
 */
public abstract class VersusBattle<BP extends BattlePlayer> extends Battle<BP> {
    
    protected int playToPoints = 5;
    
    public VersusBattle(CorePlugin<?> plugin, List<CorePlayer> players, Arena arena, Class<BP> battlePlayerClass, BattleMode battleMode) {
        super(plugin, players, arena, battlePlayerClass, battleMode);
    }
    
    /**
     * Initialize battle requests (/request)
     */
    protected void setupBattleRequests() {
        addBattleRequest(new ResetRequest(this));
        addBattleRequest(new EndGameRequest(this));
        addBattleRequest(new PlayToRequest(this));
        addBattleRequest(new PauseRequest(this));
    }
    
    /**
     * Initialize the players, called in startBattle()
     */
    @Override
    protected void setupBattlers() {
    
    }
    
    /**
     * Called in startBattle()<br>
     * Initialize scoreboard
     */
    protected void setupScoreboard() {
        chatGroup.addTeam("playto", Chat.SCOREBOARD_DEFAULT + "Playing To " + Chat.SCORE + playToPoints);
        chatGroup.addTeam("p1", sortedBattlers.get(0).getCorePlayer().getName());
        chatGroup.addTeam("p2", sortedBattlers.get(1).getCorePlayer().getName());
        updateScoreboard();
    }
    
    /**
     * Send a message on the start of a battle
     */
    @Override
    protected void sendStartMessage() {
        getPlugin().sendMessage("Starting "
                + getMode().getDisplayName()
                + " match on "
                + arena.getName()
                + " between "
                + CoreUtils.mergePlayerNames(battlers.keySet()) + "!");
    }
    
    @Override
    protected void fillField() {
    
    }
    
    /**
     * Called when a battler joins mid-game (if available)
     *
     * @param cp Core Player
     */
    @Override
    protected void joinBattler(CorePlayer cp) {
    
    }
    
    /**
     * Save the battlers stats
     * Called when a battler is being removed from the battle
     *
     * @param bp Battle Player
     */
    @Override
    protected void saveBattlerStats(BP bp) {
    
    }
    
    /**
     * Called every 1 second or on score updates
     * Updates the player scoreboards
     */
    @Override
    public void updateScoreboard() {
        chatGroup.setScoreboardName(Chat.DEFAULT + getRuntimeString() + "     " + Chat.SCORE + "Score  ");
        chatGroup.setTeamDisplayName("p1", Chat.PLAYER_NAME + sortedBattlers.get(0).getCorePlayer().getName()
                + ": " + Chat.SCORE + sortedBattlers.get(0).getRoundWins());
        chatGroup.setTeamDisplayName("p2", Chat.PLAYER_NAME + sortedBattlers.get(1).getCorePlayer().getName()
                + ": " + Chat.SCORE + sortedBattlers.get(1).getRoundWins());
    }
    
    /**
     * Called every 1/10 second
     * Updates the field on occasion for events such as
     * auto-regenerating maps
     */
    @Override
    public void updateField() {
    
    }
    
    /**
     * Updates the experience bar of players in the game
     */
    @Override
    public void updateExperience() {
    
    }
    
    /**
     * End a round with a determined winner
     *
     * @param winner Winner
     */
    @Override
    protected void endRound(BP winner) {
        winner.addRoundWin();
        if (winner.getRoundWins() < playToPoints) {
            Core.getInstance().sendMessage(chatGroup, Chat.PLAYER_NAME + winner.getCorePlayer().getDisplayName() + Chat.DEFAULT + " won the round");
            startRound();
        } else {
            endBattle(winner);
        }
    }
    
    protected void applyEloChange(BP winner) {
        int eloChange;
        int avgRating = 0;
    
        for (BattlePlayer bp : battlers.values()) {
            avgRating += bp.getCorePlayer().getRatings().getElo(getMode().getName(), getMode().getSeason());
        }
        avgRating /= battlers.size();
    
        if (winner.getCorePlayer().getRatings().getElo(getMode().getName(), getMode().getSeason()) > avgRating) {
            eloChange = 20 - (int)(Math.min((winner.getCorePlayer().getRatings().getElo(getMode().getName(), getMode().getSeason()) - avgRating) / 100.0, 1) * 15);
        } else {
            eloChange = 20 + (int)(Math.min((avgRating - winner.getCorePlayer().getRatings().getElo(getMode().getName(), getMode().getSeason())) / 100.0, 1) * 20);
        }
    
        for (BattlePlayer bp : battlers.values()) {
            if (bp.equals(winner)) {
                bp.getCorePlayer().getRatings().addElo(getMode().getName(), getMode().getSeason(), eloChange);
            } else {
                bp.getCorePlayer().getRatings().addElo(getMode().getName(), getMode().getSeason(), -eloChange);
            }
        }
    }
    
    /**
     * End a battle with a determined winner
     *
     * @param winner Winner
     */
    @Override
    protected void endBattle(BP winner) {
        BP loser = null;
        for (CorePlayer cp : battlers.keySet()) {
            if (!cp.equals(winner.getCorePlayer())) {
                loser = battlers.get(cp);
                break;
            }
        }
        if (loser == null) {
            loser = winner;
        } else {
            applyEloChange(winner);
        }
        getPlugin().sendMessage(Chat.PLAYER_NAME + winner.getPlayer().getName()
                + winner.getCorePlayer().getRatings().getDisplayElo(getMode().getName(), getMode().getSeason())
                + Chat.DEFAULT + " has " + BattleUtils.randomDefeatSynonym() + " "
                + Chat.PLAYER_NAME + loser.getPlayer().getName()
                + loser.getCorePlayer().getRatings().getDisplayElo(getMode().getName(), getMode().getSeason())
                + Chat.DEFAULT + " in "
                + Chat.GAMEMODE + getMode().getDisplayName() + " "
                + Chat.DEFAULT + "("
                + Chat.SCORE + winner.getRoundWins() + Chat.DEFAULT + "-" + Chat.SCORE + loser.getRoundWins()
                + Chat.DEFAULT + ")");
        endBattle();
    }
    
    /**
     * Called when a battler leaves boundaries
     *
     * @param cp CorePlayer
     */
    @Override
    protected void failBattler(CorePlayer cp) {
        remainingPlayers.remove(battlers.get(cp));
        if (remainingPlayers.isEmpty()) {
            endRound(null);
        } else {
            endRound(remainingPlayers.iterator().next());
        }
    }
    
    /**
     * Called when a player surrenders (/ff, /leave)
     *
     * @param cp CorePlayer
     */
    @Override
    public void surrender(CorePlayer cp) {
        leaveBattler(cp);
    }
    
    /**
     * Called when a Play To request passes
     *
     * @param playToPoints Play To Value
     */
    @Override
    public void setPlayTo(int playToPoints) {
        this.playToPoints = playToPoints;
    }
    
    /**
     * Called when a battler wants to leave (/leave, /ff)
     *
     * @param cp Battler CorePlayer
     */
    @Override
    protected void leaveBattler(CorePlayer cp) {
        remainingPlayers.remove(battlers.get(cp));
        if (remainingPlayers.isEmpty()) {
            endBattle(null);
        } else {
            endBattle(remainingPlayers.iterator().next());
        }
    }
    
}
