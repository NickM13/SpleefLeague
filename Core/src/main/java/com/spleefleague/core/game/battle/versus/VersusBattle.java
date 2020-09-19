package com.spleefleague.core.game.battle.versus;

import com.comphenix.protocol.wrappers.BlockPosition;
import com.google.common.collect.Iterables;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
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
import com.spleefleague.core.game.request.PlayToRequest;
import com.spleefleague.core.game.request.ResetRequest;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.plugin.CorePlugin;
import com.spleefleague.core.util.CoreUtils;
import com.spleefleague.core.util.variable.Position;
import com.spleefleague.core.world.FakeBlock;
import com.spleefleague.core.world.FakeUtils;
import com.spleefleague.core.world.build.BuildStructure;
import com.spleefleague.core.world.build.BuildStructures;
import com.spleefleague.coreapi.utils.packet.RatedPlayerInfo;
import com.spleefleague.coreapi.utils.packet.spigot.PacketBattleEndRated;
import com.spleefleague.coreapi.utils.packet.spigot.PacketBattleEndUnrated;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author NickM13
 * @since 4/24/2020
 */
public abstract class VersusBattle<BP extends BattlePlayer> extends Battle<BP> {
    
    protected int playToPoints = 5;
    
    public VersusBattle(CorePlugin<?> plugin, List<UUID> players, Arena arena, Class<BP> battlePlayerClass, BattleMode battleMode) {
        super(plugin, players, arena, battlePlayerClass, battleMode);
    }
    
    /**
     * Initialize battle requests (/request)
     */
    protected void setupBattleRequests() {
        addBattleRequest(new EndGameRequest(this));
        addBattleRequest(new ResetRequest(this));
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
        chatGroup.setScoreboardName(ChatColor.GOLD + "" + ChatColor.BOLD + getMode().getDisplayName());
        chatGroup.addTeam("time", "00:00:00:000");
        chatGroup.addTeam("p1", "  " + Chat.PLAYER_NAME + "" + ChatColor.BOLD + sortedBattlers.get(0).getCorePlayer().getName());
        chatGroup.addTeam("p1score", BattleUtils.toScoreSquares(sortedBattlers.get(0), playToPoints));
        chatGroup.addTeam("p2", "  " + Chat.PLAYER_NAME + "" + ChatColor.BOLD + sortedBattlers.get(1).getCorePlayer().getName());
        chatGroup.addTeam("p2score", BattleUtils.toScoreSquares(sortedBattlers.get(1), playToPoints));
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
        Core.getInstance().getPlayers().save(bp.getCorePlayer());
    }
    
    /**
     * Called every 0.1 second or on score updates
     * Updates the player scoreboards
     */
    @Override
    public void updateScoreboard() {
        chatGroup.setTeamDisplayName("time", Chat.DEFAULT + getRuntimeString());
        chatGroup.setTeamDisplayName("p1score", BattleUtils.toScoreSquares(sortedBattlers.get(0), playToPoints));
        chatGroup.setTeamDisplayName("p2score", BattleUtils.toScoreSquares(sortedBattlers.get(1), playToPoints));
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

    @Override
    public void updatePhysicalScoreboard() {
        for (Position scoreboard : scoreboards) {
            BuildStructure score0 = BuildStructures.get("score" + sortedBattlers.get(0).getRoundWins());
            if (score0 != null) {
                Map<BlockPosition, FakeBlock> blocks = score0.getFakeBlocks();
                blocks = FakeUtils.translateBlocks(FakeUtils.rotateBlocks(blocks, (int) scoreboard.getYaw()), scoreboard.toBlockPosition());
                gameWorld.setBlocks(blocks);
            }
            BuildStructure score1 = BuildStructures.get("score" + sortedBattlers.get(1).getRoundWins());
            if (score1 != null) {
                Map<BlockPosition, FakeBlock> blocks = score1.getFakeBlocks();
                blocks = FakeUtils.translateBlocks(FakeUtils.rotateBlocks(FakeUtils.translateBlocks(blocks, new BlockPosition(6, 0, 0)), (int) scoreboard.getYaw()), scoreboard.toBlockPosition());
                gameWorld.setBlocks(blocks);
            }
        }
    }

    protected void onScorePoint(BP winner) {
        chatGroup.sendMessage(Chat.PLAYER_NAME + winner.getCorePlayer().getDisplayName() + Chat.DEFAULT + " has scored a point!");
        updatePhysicalScoreboard();
    }
    
    /**
     * End a round with a determined winner
     *
     * @param winner Winner
     */
    @Override
    protected void endRound(BP winner) {
        matchPointing = false;
        if (winner == null) {
            chatGroup.sendMessage(Chat.ERROR + "No other player found!");
            startRound();
            return;
        }
        winner.addRoundWin();
        if (winner.getRoundWins() < playToPoints) {
            onScorePoint(winner);
            startRound();
            if (winner.getRoundWins() == playToPoints - 1) {
                chatGroup.sendTitle(ChatColor.GOLD + "Match Point: " + winner.getCorePlayer().getName(), "", 5, 50, 5);
                matchPointing = true;
            }
        } else {
            endBattle(winner);
        }
    }
    
    protected int applyEloChange(BP winner) {
        int eloChange;
        int avgRating = 0;
    
        for (BattlePlayer bp : battlers.values()) {
            avgRating += bp.getCorePlayer().getRatings().getElo(getMode().getName(), getMode().getSeason());
        }
        avgRating /= battlers.size();
    
        if (winner.getCorePlayer().getRatings().getElo(getMode().getName(), getMode().getSeason()) > avgRating) {
            eloChange = 20 - (int) (Math.min((winner.getCorePlayer().getRatings().getElo(getMode().getName(), getMode().getSeason()) - avgRating) / 500.0, 1) * 15);
        } else {
            eloChange = 20 + (int)(Math.min((avgRating - winner.getCorePlayer().getRatings().getElo(getMode().getName(), getMode().getSeason())) / 500.0, 1) * 15);
        }

        for (BattlePlayer bp : battlers.values()) {
            int initialElo = bp.getCorePlayer().getRatings().getElo(getMode().getName(), getMode().getSeason());
            if (bp.equals(winner)) {
                //bp.getCorePlayer().addElo(getMode().getName(), getMode().getSeason(), eloChange);
                boolean divChange = bp.getCorePlayer().getRatings().addRating(getMode().getName(), getMode().getSeason(), eloChange);
                bp.getCorePlayer().sendMessage(ChatColor.GRAY + " You have gained "
                        + ChatColor.GREEN + eloChange
                        + ChatColor.GRAY + " Rating Points ("
                        + ChatColor.RED + initialElo
                        + ChatColor.GRAY + "->"
                        + ChatColor.GREEN + (initialElo + eloChange)
                        + ChatColor.GRAY + ")");
            } else {
                //bp.getCorePlayer().addElo(getMode().getName(), getMode().getSeason(), -eloChange);
                bp.getCorePlayer().getRatings().addRating(getMode().getName(), getMode().getSeason(), -eloChange);
                bp.getCorePlayer().sendMessage(ChatColor.GRAY + " You have lost "
                        + ChatColor.GREEN + eloChange
                        + ChatColor.GRAY + " Rating Points ("
                        + ChatColor.RED + initialElo
                        + ChatColor.GRAY + "->"
                        + ChatColor.GREEN + (initialElo - eloChange)
                        + ChatColor.GRAY + ")");
            }
        }
        return eloChange;
    }

    protected abstract void applyRewards(BP winner);

    /**
     * End a battle with a determined winner
     *
     * @param winner Winner
     */
    @Override
    public void endBattle(BP winner) {
        if (winner == null) {
            Core.getInstance().sendPacket(new PacketBattleEndUnrated(
                    getMode().getName(),
                    battlers.values().stream().map(bp -> bp.getCorePlayer().getUniqueId()).collect(Collectors.toList())));
            getPlugin().sendMessage(Chat.DEFAULT + "Battle between "
                    + Chat.PLAYER_NAME + CoreUtils.mergePlayerNames(battlers.keySet())
                    + Chat.DEFAULT + " was peacefully concluded.");
        } else {
            BP loser = null;
            for (CorePlayer cp : battlers.keySet()) {
                if (cp != null && !cp.equals(winner.getCorePlayer())) {
                    loser = battlers.get(cp);
                    break;
                }
            }
            if (loser == null) {
                Core.getInstance().sendPacket(new PacketBattleEndUnrated(
                        getMode().getName(),
                        battlers.values().stream().map(bp -> bp.getCorePlayer().getUniqueId()).collect(Collectors.toList())));
                loser = winner;
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
            } else {
                for (BattlePlayer bp : battlers.values()) {
                    bp.getCorePlayer().sendMessage(Chat.colorize("             &6&l" + getMode().getDisplayName()));
                    StringBuilder linebreak = new StringBuilder(Chat.colorize("             &8"));
                    for (int i = 0; i < ChatUtils.getPixelCount(ChatColor.BOLD + getMode().getDisplayName()) / (double) (ChatUtils.getPixelCount("-")); i++) {
                        linebreak.append("-");
                    }
                    bp.getCorePlayer().sendMessage(linebreak.toString());
                }
                applyRewards(winner);
                applyEloChange(winner);
                Core.getInstance().sendPacket(new PacketBattleEndRated(
                        getMode().getName(),
                        getMode().getSeason(),
                        battlers.values().stream().map(bp -> new RatedPlayerInfo(
                                bp.getCorePlayer().getUniqueId(),
                                bp.getCorePlayer().getRatings().getElo(getMode().getName(), getMode().getSeason()))
                        ).collect(Collectors.toList())));
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
            }
        }
        destroy();
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
     * Called when a battler enters a goal area
     *
     * @param cp CorePlayer
     */
    @Override
    protected void winBattler(CorePlayer cp) {
        endRound(battlers.get(cp));
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
