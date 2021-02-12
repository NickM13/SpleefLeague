package com.spleefleague.core.game.battle.team;

import com.spleefleague.core.Core;
import com.spleefleague.core.chat.Chat;
import com.spleefleague.core.chat.ChatUtils;
import com.spleefleague.core.game.Arena;
import com.spleefleague.core.game.BattleMode;
import com.spleefleague.core.game.battle.Battle;
import com.spleefleague.core.game.battle.BattlePlayer;
import com.spleefleague.core.game.request.EndGameRequest;
import com.spleefleague.core.game.request.PauseRequest;
import com.spleefleague.core.game.request.ResetRequest;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.plugin.CorePlugin;
import com.spleefleague.core.util.CoreUtils;
import com.spleefleague.coreapi.utils.packet.spigot.battle.PacketSpigotBattleEnd;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.util.*;

/**
 * @author NickM13
 * @since 4/24/2020
 */
public abstract class TeamBattle<BP extends TeamBattlePlayer> extends Battle<BP> {

    protected Set<TeamBattleTeam<BP>> teams = new HashSet<>();
    protected List<TeamBattleTeam<BP>> sortedTeams = new ArrayList<>();
    protected Map<BP, TeamBattleTeam<BP>> teamBattleTeamMap = new HashMap<>();
    protected Set<TeamBattleTeam<BP>> remainingTeams = new HashSet<>();
    protected int playToPoints = 1;

    public TeamBattle(CorePlugin<?> plugin, UUID battleId, List<UUID> players, Arena arena, Class<BP> battlePlayerClass, BattleMode battleMode) {
        super(plugin, battleId, players, arena, battlePlayerClass, battleMode);
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
        int teamIndex = 0;
        int teamSize = 0;
        TeamBattleTeam<BP> team = new TeamBattleTeam<>(0);
        for (BP bp : sortedBattlers) {
            if (teamSize == 0) {
                team = new TeamBattleTeam<>(teamIndex);
                sortedTeams.add(team);
                teams.add(team);
            }
            teamBattleTeamMap.put(bp, team);
            team.addPlayer(bp);
            teamSize++;
            if (teamSize >= arena.getTeamSize()) {
                teamIndex++;
                teamSize = 0;
            }
        }
        for (TeamBattleTeam<?> tbt : teams) {
            tbt.initRating(getMode());
        }
    }

    @Override
    protected void resetBattlers() {
        super.resetBattlers();
        remainingTeams.addAll(teams);
        for (TeamBattleTeam<?> tbt : teams) {
            tbt.reset();
        }
    }

    /**
     * Send a message on the start of a battle
     */
    @Override
    protected void sendStartMessage() {

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

    @Override
    protected void onRejoin(BP bp) {
        teamBattleTeamMap.get(bp).addPlayer(bp);
        addBattlerGhost(bp.getCorePlayer());
    }

    /**
     * Called when a battler leaves boundaries
     *
     * @param cp CorePlayer
     */
    @Override
    protected void failBattler(CorePlayer cp) {
        BP battler = battlers.get(cp);
        if (remainingPlayers.remove(battler)) {
            TeamBattleTeam<BP> team = teamBattleTeamMap.get(battler);
            team.failPlayer(battler);
            if (team.remaining.isEmpty()) {
                remainingTeams.remove(team);
                if (remainingTeams.isEmpty()) {
                    endRoundTeam(null);
                } else if (remainingTeams.size() == 1) {
                    endRoundTeam(remainingTeams.iterator().next());
                } else {
                    addBattlerGhost(cp);
                }
            } else {
                addBattlerGhost(cp);
            }
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
        BP bp = battlers.get(cp);
        TeamBattleTeam<BP> tbt = teamBattleTeamMap.get(bp);
        if (tbt.removePlayer(bp)) {
            if (tbt.getPlayers().isEmpty()) {
                teams.remove(tbt);
                if (teams.size() == 0) {
                    endBattleTeam(null);
                } else if (teams.size() == 1) {
                    endBattleTeam(teams.iterator().next());
                } else {
                    if (remainingTeams.isEmpty()) {
                        startRound();
                    } else {
                        endRoundTeam(remainingTeams.iterator().next());
                    }
                }
            } else {
                failBattler(cp);
                // Pew!
            }
        }
    }

    public TeamBattleTeam<BP> getTeam(CorePlayer cp) {
        return teamBattleTeamMap.get(battlers.get(cp));
    }

    /**
     * Called every 1 second or on score updates
     * Updates the player scoreboards
     */
    @Override
    public void updateScoreboard() {

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

    public void endRoundTeam(TeamBattleTeam<BP> winner) {
        if (winner == null) return;
        winner.addRoundWin();
        if (winner.getRoundWins() < playToPoints) {
            chatGroup.sendMessage(winner.getTeamInfo().getName() + Chat.DEFAULT + " won the round");
            startRound();
        } else {
            endBattleTeam(winner);
        }
    }

    protected int applyEloChange(TeamBattleTeam<BP> winner) {
        int avgRating = 0;
        int winnerRating = winner.getRating();

        for (CorePlayer cp : battlers.keySet()) {
            int rating = cp.getRatings().getElo(getMode().getName(), getMode().getSeason());
            avgRating += rating;
        }
        avgRating /= battlers.size();

        int d = (avgRating - winnerRating) * battlers.size();
        d = Math.min(Math.max(d, -750), 750);

        int eloChange = (int) (0.00001f * d * d + 0.014f * d + 20.f);

        for (BP bp : battlers.values()) {
            boolean isWinner = teamBattleTeamMap.get(bp).equals(winner);
            int initialElo = bp.getCorePlayer().getRatings().getElo(getMode().getName(), getMode().getSeason());
            int toChange = isWinner ? eloChange : -eloChange;
            boolean divChange = bp.getCorePlayer().getRatings().addRating(getMode().getName(), getMode().getSeason(), toChange);
            TextComponent text = new TextComponent();
            text.setColor(net.md_5.bungee.api.ChatColor.GRAY);
            text.addExtra(" You have " + (toChange >= 0 ? "gained " : "lost "));
            text.addExtra(ChatColor.GREEN + "" + eloChange);
            text.addExtra(" Rating Points (");
            text.addExtra(ChatColor.RED + "" + initialElo);
            text.addExtra("->");
            text.addExtra(ChatColor.GREEN + "" + (initialElo + toChange));
            text.addExtra(")");
            bp.getCorePlayer().sendMessage(text);
        }
        return eloChange;
    }

    protected void applyRewards(TeamBattleTeam<BP> winner) {

    }

    protected abstract void sendEndMessage(TeamBattleTeam<BP> winner);

    /**
     * End a battle with a determined winner
     *
     * @param winner Winner
     */
    public void endBattleTeam(TeamBattleTeam<BP> winner) {
        if (winner == null) {
            TextComponent text = new TextComponent();
            text.addExtra("Battle between ");
            text.addExtra(CoreUtils.mergePlayerNames(battlers.keySet()));
            text.addExtra(" was peacefully concluded.");
            sendNotification(text);
        } else {
            TeamBattleTeam<BP> loser = null;
            for (TeamBattleTeam<BP> tbt : teams) {
                if (tbt != null && !tbt.equals(winner)) {
                    loser = tbt;
                    break;
                }
            }
            if (loser == null) {
                /*
                loser = winner;
                TextComponent text = new TextComponent();
                text.addExtra(winner.getCorePlayer().getChatName());
                text.addExtra(winner.getCorePlayer().getRatings().getDisplayElo(getMode().getName(), getMode().getSeason()));
                text.addExtra(" has " + BattleUtils.randomDefeatSynonym() + " ");
                text.addExtra(loser.getCorePlayer().getChatName());
                text.addExtra(loser.getCorePlayer().getRatings().getDisplayElo(getMode().getName(), getMode().getSeason()));
                text.addExtra("(");
                text.addExtra(Chat.SCORE + winner.getRoundWins());
                text.addExtra("-");
                text.addExtra(Chat.SCORE + loser.getRoundWins());
                text.addExtra(")");
                sendNotification(text);
                */
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
                int change = applyEloChange(winner);

                /*
                TextComponent text = new TextComponent();
                text.addExtra(winner.getCorePlayer().getChatName());
                text.addExtra(winner.getCorePlayer().getRatings().getDisplayElo(getMode().getName(), getMode().getSeason()));
                text.addExtra(" has " + BattleUtils.randomDefeatSynonym() + " ");
                text.addExtra(loser.getCorePlayer().getChatName());
                text.addExtra(loser.getCorePlayer().getRatings().getDisplayElo(getMode().getName(), getMode().getSeason()));
                text.addExtra("(");
                text.addExtra(Chat.SCORE + winner.getRoundWins());
                text.addExtra("-");
                text.addExtra(Chat.SCORE + loser.getRoundWins());
                text.addExtra(")");
                sendNotification(text);
                 */
            }
        }
        Core.getInstance().sendPacket(new PacketSpigotBattleEnd(battleId));
        Bukkit.getScheduler().runTaskLater(Core.getInstance(), this::destroy, 200L);
        finished = true;
    }

    @Override
    public void endRound(BP tbp) {
        Thread.dumpStack();
    }

    @Override
    public void endBattle(BP tbp) {
        Thread.dumpStack();
    }

}
