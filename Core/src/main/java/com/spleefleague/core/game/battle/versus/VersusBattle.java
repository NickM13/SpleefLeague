package com.spleefleague.core.game.battle.versus;

import com.spleefleague.core.Core;
import com.spleefleague.core.chat.Chat;
import com.spleefleague.core.chat.ChatChannel;
import com.spleefleague.core.game.Arena;
import com.spleefleague.core.game.BattleUtils;
import com.spleefleague.core.game.battle.BattlePlayer;
import com.spleefleague.core.game.battle.Battle;
import com.spleefleague.core.game.request.ResetRequest;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.plugin.CorePlugin;
import com.spleefleague.core.util.CoreUtils;

import java.util.List;

/**
 * @author NickM13
 * @since 4/24/2020
 */
public abstract class VersusBattle<A extends Arena, BP extends BattlePlayer> extends Battle<A, BP> {
    
    protected int playToPoints = 5;
    
    public VersusBattle(CorePlugin<?> plugin, List<CorePlayer> players, A arena, Class<BP> battlePlayerClass) {
        super(plugin, players, arena, battlePlayerClass);
    }
    
    protected void setupBattleRequests() {
        addBattleRequest(new ResetRequest(this));
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
        chatGroup.setRightSideBuffer(70);
        chatGroup.addTeam("playto", Chat.SCOREBOARD_DEFAULT + "Playing To ");
        chatGroup.setTeamDisplayName("playto", Chat.SCOREBOARD_DEFAULT + "Playing To " + Chat.SCORE + playToPoints);
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
                + arena.getMode().getDisplayName()
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
     * Called when a battler is removed from the battle
     *
     * @param cp Core Player
     */
    @Override
    protected void saveBattlerStats(CorePlayer cp) {
    
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
            avgRating += bp.getCorePlayer().getRatings().get(getMode());
        }
        avgRating /= battlers.size();
    
        if (winner.getCorePlayer().getRatings().get(getMode()) > avgRating) {
            eloChange = 20 - (int)(Math.min((winner.getCorePlayer().getRatings().get((getMode())) - avgRating) / 100.0, 1) * 15);
        } else {
            eloChange = 20 + (int)(Math.min((avgRating - winner.getCorePlayer().getRatings().get((getMode()))) / 100.0, 1) * 20);
        }
    
        for (BattlePlayer bp : battlers.values()) {
            if (bp.equals(winner)) {
                bp.getCorePlayer().getRatings().add(getMode(), eloChange);
            } else {
                bp.getCorePlayer().getRatings().add(getMode(), -eloChange);
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
                + winner.getCorePlayer().getRatings().getDisplayElo(getMode())
                + Chat.DEFAULT + " has " + BattleUtils.randomDefeatSynonym() + " "
                + Chat.PLAYER_NAME + loser.getPlayer().getName()
                + loser.getCorePlayer().getRatings().getDisplayElo(getMode())
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
        if (sortedBattlers.get(0).getCorePlayer().equals(cp)) {
            endRound(sortedBattlers.get(1));
        } else {
            endRound(sortedBattlers.get(0));
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
     * Called when a player requests the game to end (/endgame)
     *
     * @param cp CorePlayer
     */
    @Override
    public void requestEndGame(CorePlayer cp) {
    
    }
    
    /**
     * Called when a player requests to pause the game with specified
     * time (/pause <seconds>)
     *
     * @param cp      CorePlayer
     * @param timeout Seconds
     */
    @Override
    public void requestPause(CorePlayer cp, int timeout) {
    
    }
    
    /**
     * Called when a player requests to pause the game (/pause)
     *
     * @param cp CorePlayer
     */
    @Override
    public void requestPause(CorePlayer cp) {
    
    }
    
    /**
     * Called when a player requests to reset the field (/reset)
     *
     * @param cp CorePlayer
     */
    @Override
    public void requestReset(CorePlayer cp) {
    
    }
    
    /**
     * Called when a player requests to change the
     * PlayTo score (/playto)
     *
     * @param cp CorePlayer
     */
    @Override
    public void requestPlayTo(CorePlayer cp) {
    
    }
    
    /**
     * Called when a player requests to change the
     * PlayTo score with specified score (/playto <score>)
     *
     * @param cp     CorePlayer
     * @param playTo Play To
     */
    @Override
    public void requestPlayTo(CorePlayer cp, int playTo) {
    
    }
    
    /**
     * Called when a battler wants to leave (/leave, /ff)
     *
     * @param cp Battler CorePlayer
     */
    @Override
    protected void leaveBattler(CorePlayer cp) {
        for (CorePlayer cp2 : battlers.keySet()) {
            if (!cp2.equals(cp)) {
                endBattle(battlers.get(cp2));
                return;
            }
        }
        endBattle(battlers.get(cp));
    }
    
}
