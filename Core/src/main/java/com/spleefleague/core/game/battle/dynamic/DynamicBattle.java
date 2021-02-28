package com.spleefleague.core.game.battle.dynamic;

import com.spleefleague.core.Core;
import com.spleefleague.core.chat.Chat;
import com.spleefleague.core.chat.ChatUtils;
import com.spleefleague.core.game.Arena;
import com.spleefleague.core.game.BattleMode;
import com.spleefleague.core.game.BattleUtils;
import com.spleefleague.core.game.battle.BattlePlayer;
import com.spleefleague.core.game.battle.Battle;
import com.spleefleague.core.game.request.EndGameRequest;
import com.spleefleague.core.game.request.PauseRequest;
import com.spleefleague.core.game.request.ResetRequest;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.plugin.CorePlugin;
import com.spleefleague.core.util.CoreUtils;
import com.spleefleague.coreapi.utils.packet.spigot.battle.PacketSpigotBattleEnd;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.UUID;

/**
 * @author NickM13
 * @since 4/24/2020
 */
public abstract class DynamicBattle<BP extends BattlePlayer> extends Battle<BP> {

    protected static final int MAX_SHOWN = 5;
    protected int playToPoints = 1;
    protected int initBattlerCount;
    protected int avgBattlerRating;

    public DynamicBattle(CorePlugin plugin, UUID battleId, List<UUID> players, Arena arena, Class<BP> battlePlayerClass, BattleMode battleMode) {
        super(plugin, battleId, players, arena, battlePlayerClass, battleMode);
        roundCountdown = 10;
    }

    /**
     * Initialize battle requests (/request)
     */
    protected void setupBattleRequests() {
        addBattleRequest(new ResetRequest(this));
        addBattleRequest(new EndGameRequest(this));
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
        initBattlerCount = battlers.size();
        if (battlers.isEmpty()) return;
        avgBattlerRating = 0;
        for (CorePlayer cp : battlers.keySet()) {
            int rating = cp.getRatings().getElo(getMode().getName(), getMode().getSeason());
            avgBattlerRating += rating;
        }
        avgBattlerRating /= battlers.size();
    }

    @Override
    protected void setupScoreboard() {
        chatGroup.setScoreboardName(ChatColor.GOLD + "" + ChatColor.BOLD + "   " + getMode().getDisplayName() + "   ");
        chatGroup.addTeam("arena", ChatColor.GREEN + "  " + arena.getName());
        chatGroup.addTeam("time", "  00:00:00");
        chatGroup.addTeam("l1", "");
        chatGroup.addTeam("remain", remainingPlayers.size() + " Players Left");
    }

    /**
     * Called every 1 second or on score updates
     * Updates the player scoreboards
     */
    @Override
    public void updateScoreboard() {
        chatGroup.setTeamDisplayName("time", "  " + Chat.DEFAULT + getRuntimeStringNoMillis());
        chatGroup.setTeamDisplayName("remain", remainingPlayers.size() + " Players Left");
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

    /**
     * Start a round<br>
     * Resets the field and its players, also used in Reset Request
     */
    @Override
    public void startRound() {
        super.startRound();
        frozen = false;
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
    public void joinBattler(CorePlayer cp) {

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
        winner.addRoundWin();
        if (winner.getRoundWins() < playToPoints) {
            chatGroup.sendMessage(Chat.PLAYER_NAME + winner.getCorePlayer().getDisplayName() + Chat.DEFAULT + " won the round");
            startRound();
        } else {
            endBattle(winner);
        }
    }

    private void applyEloChange(BP battler, int place) {
        battler.getCorePlayer().sendMessage(Chat.colorize("             &6&l" + getMode().getDisplayName()));
        StringBuilder linebreak = new StringBuilder(Chat.colorize("             &8"));
        for (int i = 0; i < ChatUtils.getPixelCount(ChatColor.BOLD + getMode().getDisplayName()) / (double) (ChatUtils.getPixelCount("-")); i++) {
            linebreak.append("-");
        }
        battler.getCorePlayer().sendMessage(linebreak.toString());
        if (forced) return;
        int diffFromAvg = avgBattlerRating - battler.getCorePlayer().getRatings().getElo(getMode().getName(), getMode().getSeason());
        diffFromAvg = Math.min(Math.max(diffFromAvg * 2, -750), 750);
        int eloChange = (int) (0.00001f * diffFromAvg * diffFromAvg + 0.014f * diffFromAvg + 20.f);
        float placePercent = (2 * (initBattlerCount - place - 1f) / (initBattlerCount - 1f)) - 1f;
        eloChange = (int) (eloChange * placePercent);

        int initialElo = battler.getCorePlayer().getRatings().getElo(getMode().getName(), getMode().getSeason());
        battler.getCorePlayer().getRatings().addRating(getMode().getName(), getMode().getSeason(), eloChange);
        battler.getCorePlayer().sendMessage(ChatColor.GRAY + " You have " +
                (eloChange >= 0 ? "gained " : "lost ") +
                ChatColor.GREEN + eloChange +
                ChatColor.GRAY + " Rating Points (" +
                ChatColor.RED + initialElo +
                ChatColor.GRAY + "->" +
                ChatColor.GREEN + (initialElo + eloChange) +
                ChatColor.GRAY + ")");

        applyRewards(battler, place == 0);
    }

    /**
     * @param winner Battle Player
     */
    protected void applyEloChange(@Nonnull BP winner) {
        applyEloChange(winner, 0);
    }

    /**
     * End a battle with a determined winner
     *
     * @param winner Winner
     */
    @Override
    public void endBattle(BP winner) {
        if (winner != null) {
            applyEloChange(winner);
            sendEndMessage(winner);
        }
        Core.getInstance().sendPacket(new PacketSpigotBattleEnd(battleId));
        Bukkit.getScheduler().runTaskLater(Core.getInstance(), this::destroy, 200L);
        finished = true;
    }

    protected abstract void sendEndMessage(BP winner);

    /**
     * Called when a battler leaves boundaries
     *
     * @param cp CorePlayer
     */
    @Override
    protected void failBattler(CorePlayer cp) {
        remainingPlayers.remove(battlers.get(cp));
        applyEloChange(battlers.get(cp), remainingPlayers.size());
        if (remainingPlayers.isEmpty()) {
            endRound(battlers.get(cp));
        } else if (remainingPlayers.size() == 1) {
            endRound(remainingPlayers.iterator().next());
        }
        addBattlerGhost(cp);
        if (remainingPlayers.size() > 1) {
            chatGroup.sendMessage(cp.getDisplayName() + ChatColor.GRAY + " has been eliminated, " + remainingPlayers.size() + " remaining");
        }
    }

    /**
     * Called when a battler enters a goal area
     *
     * @param cp CorePlayer
     */
    @Override
    protected void winBattler(CorePlayer cp) {

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
        applyEloChange(battlers.get(cp), remainingPlayers.size() - 1);
        if (battlers.size() <= 2) {
            removeBattler(cp);
            if (battlers.isEmpty()) {
                endBattle(null);
            } else {
                endBattle(battlers.values().iterator().next());
            }
        } else {
            remainingPlayers.remove(battlers.get(cp));
            removeBattler(cp);
            sortedBattlers.clear();
            sortedBattlers.addAll(battlers.values());
            if (battlers.size() < 5) {
                //chatGroup.removeTeam("p" + (battlers.size() - 1));
            }
            if (remainingPlayers.isEmpty()) {
                startRound();
            } else if (remainingPlayers.size() == 1) {
                endRound(remainingPlayers.iterator().next());
            }
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
