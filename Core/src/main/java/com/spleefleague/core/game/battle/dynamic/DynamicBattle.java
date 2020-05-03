package com.spleefleague.core.game.battle.dynamic;

import com.spleefleague.core.Core;
import com.spleefleague.core.chat.Chat;
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
public abstract class DynamicBattle<BP extends BattlePlayer> extends Battle<BP> {
    
    protected static final int MAX_SHOWN = 5;
    protected int playToPoints = 5;
    
    public DynamicBattle(CorePlugin<?> plugin, List<CorePlayer> players, Arena arena, Class<BP> battlePlayerClass, BattleMode battleMode) {
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
     * Initialize base battle settings such as GameWorld tools and Scoreboard values
     */
    @Override
    protected void setupBaseSettings() {
    
    }
    
    /**
     * Initialize the players, called in startBattle()
     */
    @Override
    protected void setupBattlers() {
    
    }
    
    @Override
    protected void setupScoreboard() {
        chatGroup.addTeam("playto", Chat.SCOREBOARD_DEFAULT + "Playing To " + Chat.SCORE + playToPoints);
        for (int i = 0; i < battlers.size() && i < 5; i++) {
            chatGroup.addTeam("p" + i, "");
        }
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
    
    /**
     *
     * @param winner Battle Player
     */
    protected void applyEloChange(BP winner) {
        getPlugin().sendMessage("Elo Change not set up for Dynamic Battles yet!");
    }
    
    /**
     * End a battle with a determined winner
     *
     * @param winner Winner
     */
    @Override
    protected void endBattle(BP winner) {
        applyEloChange(winner);
        getPlugin().sendMessage(Chat.PLAYER_NAME + winner.getPlayer().getName()
                + winner.getCorePlayer().getRatings().getDisplayElo(getMode().getName(), getMode().getSeason())
                + Chat.DEFAULT + " has " + BattleUtils.randomDefeatSynonym() + " all other players in "
                + Chat.GAMEMODE + getMode().getDisplayName());
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
        } else if (remainingPlayers.size() == 1) {
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
    
    }
    
    /**
     * Called every 1 second or on score updates
     * Updates the player scoreboards
     */
    @Override
    public void updateScoreboard() {
        chatGroup.setScoreboardName(Chat.DEFAULT + getRuntimeString() + "     " + Chat.SCORE + "Score  ");
        for (int i = 0; i < Math.min(sortedBattlers.size(), MAX_SHOWN); i++) {
            chatGroup.setTeamDisplayName("p" + i, Chat.PLAYER_NAME + sortedBattlers.get(i).getCorePlayer().getName()
                    + ": " + Chat.SCORE + sortedBattlers.get(i).getRoundWins());
        }
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
    
}
