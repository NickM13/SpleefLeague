package com.spleefleague.core.game.battle.team;

import com.spleefleague.core.game.BattleMode;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.util.*;

/**
 * @author NickM13
 * @since 4/25/2020
 */
public class TeamBattleTeam<BP extends TeamBattlePlayer> {

    protected final UUID uuid = UUID.randomUUID();
    protected final TeamInfo teamName;
    protected Set<UUID> original = new HashSet<>();
    protected Set<BP> players = new LinkedHashSet<>();
    protected Set<BP> remaining = new HashSet<>();
    private int roundWins;
    private long lastWin = 0;
    private int rating;

    public TeamBattleTeam(int index) {
        teamName = TeamInfo.values()[index % TeamInfo.values().length];
    }

    public void initRating(BattleMode mode) {
        rating = 0;
        for (BP bp : players) {
            rating += bp.getCorePlayer().getRatings().getElo(mode.getName(), mode.getSeason());
        }
        rating /= players.size();
    }

    public int getRating() {
        return rating;
    }

    public void reset() {
        remaining.addAll(players);
    }

    public TeamInfo getTeamInfo() {
        return teamName;
    }

    public void addPlayer(BP player) {
        players.add(player);
        original.add(player.getCorePlayer().getUniqueId());
    }

    public void rejoin(BP player) {
        if (original.contains(player.getCorePlayer().getUniqueId())) {
            players.add(player);
        }
    }

    public Set<BP> getPlayers() {
        return players;
    }

    public boolean failPlayer(BP player) {
        return remaining.remove(player);
    }

    public boolean removePlayer(BP player) {
        remaining.remove(player);
        return players.remove(player);
    }

    public UUID getUniqueId() {
        return uuid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TeamBattleTeam<?> that = (TeamBattleTeam<?>) o;
        return uuid.equals(that.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid);
    }

    public void addRoundWin() {
        roundWins++;
        lastWin = System.currentTimeMillis();
    }

    public int getRoundWins() {
        return roundWins;
    }

    public long getLastWin() {
        return lastWin;
    }

}
