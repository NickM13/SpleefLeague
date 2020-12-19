package com.spleefleague.core.player.rank;

import com.mongodb.client.MongoCollection;
import com.spleefleague.core.Core;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author NickM13
 */
public class Ranks {

    private static final Map<String, Rank> ranks = new HashMap<>();
    private static final Set<String> defaultedRanks = new HashSet<>();
    private static MongoCollection<Document> rankCollection;

    public static Rank getDefaultRank() {
        return Rank.DEFAULT;
    }

    public static Rank getRank(String name) {
        if (name == null || !ranks.containsKey(name.toUpperCase())) return null;
        return ranks.get(name.toUpperCase());
    }

    public static Set<String> getRankNames() {
        return ranks.keySet();
    }

    private static Rank getRankOrDefault(String rankName) {
        if (!ranks.containsKey(rankName)) {
            ranks.put(rankName, new Rank(rankName, 0, ChatColor.YELLOW));
            saveRank(ranks.get(rankName));
        }
        return ranks.get((rankName));
    }

    public static void init() {
        rankCollection = Core.getInstance().getPluginDB().getCollection("Ranks");
        for (Document document : rankCollection.find()) {
            Rank rank = new Rank();
            rank.load(document);
            ranks.put(rank.getIdentifier().toUpperCase(), rank);
        }

        reloadRanks();

        Scoreboard mainScoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
        Set<Team> teams = mainScoreboard.getTeams();
        teams.forEach(Team::unregister);
        initScoreboard(mainScoreboard);
    }

    private static void reloadRanks() {
        Rank.DEFAULT =           getRankOrDefault("DEFAULT");
        Rank.ADMIN =             getRankOrDefault("ADMIN");
        Rank.DEVELOPER =         getRankOrDefault("DEVELOPER");
        Rank.MODERATOR =         getRankOrDefault("MODERATOR");
        Rank.SENIOR_MODERATOR =  getRankOrDefault("SENIOR_MODERATOR");
        Rank.BUILDER =           getRankOrDefault("BUILDER");
        Rank.ORGANIZER =         getRankOrDefault("ORGANIZER");
        Rank.VIP =               getRankOrDefault("VIP");
        Rank.DONOR_1 =           getRankOrDefault("DONOR_1");
        Rank.DONOR_2 =           getRankOrDefault("DONOR_2");
        Rank.DONOR_3 =           getRankOrDefault("DONOR_3");
    }

    public static boolean createRank(String identifier, int ladder, ChatColor chatColor) {
        if (ranks.containsKey(identifier) && !defaultedRanks.contains(identifier)) {
            return false;
        }
        Rank rank;
        if (ranks.containsKey(identifier)) {
            rank = ranks.get(identifier);
            rank.setIdentifier(identifier);
            rank.setLadder(ladder);
            rank.setColor(chatColor);
        } else {
            rank = new Rank(identifier, ladder, chatColor);
        }
        ranks.put(identifier, rank);
        defaultedRanks.remove(identifier);
        reloadRanks();
        return true;
    }

    public static boolean setRankName(String identifier, String displayName) {
        if (ranks.containsKey(identifier)) {
            ranks.get(identifier).setDisplayName(displayName);
            saveRank(ranks.get(identifier));
            return true;
        }
        return false;
    }

    public static boolean setRankLadder(String identifier, int ladder) {
        if (ranks.containsKey(identifier)) {
            ranks.get(identifier).setLadder(ladder);
            saveRank(ranks.get(identifier));
            return true;
        }
        return false;
    }

    public static boolean setRankColor(String identifier, ChatColor color) {
        if (ranks.containsKey(identifier)) {
            ranks.get(identifier).setColor(color);
            saveRank(ranks.get(identifier));
            return true;
        }
        return false;
    }

    public static boolean setRankOp(String identifier, boolean hasOp) {
        if (ranks.containsKey(identifier)) {
            ranks.get(identifier).setHasOp(hasOp);
            saveRank(ranks.get(identifier));
            return true;
        }
        return false;
    }

    private static void saveRank(Rank rank) {
        rank.save(rankCollection);
    }

    public static void initScoreboard(Scoreboard scoreboard) {
        for (Rank rank : ranks.values()) {
            Team team = scoreboard.registerNewTeam(rank.getIdentifierShort());
            team.setColor(rank.getColor());
            if (rank.getDisplayNameUnformatted().length() > 0)
                team.setPrefix("[" + rank.getDisplayNameUnformatted() + "] ");
            team.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER);
            team.setAllowFriendlyFire(true);
        }
    }

    public static boolean hasPermission(Rank r, String permission) {
        for (String perm : r.getExclusivePermissions()) {
            if (perm.equals(permission)) {
                return true;
            }
        }
        for (Rank rank : ranks.values()) {
            if (rank.getLadder() < r.getLadder()) {
                for (String perm : rank.getPermissions()) {
                    if (perm.equals(permission)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static Set<String> getAllPermissions(Rank r) {
        Set<String> perms = new HashSet<>(r.getExclusivePermissions());
        perms.addAll(r.getPermissions());
        for (Rank rank : ranks.values()) {
            if (rank.getLadder() < r.getLadder()) {
                perms.addAll(rank.getPermissions());
            }
        }
        return perms;
    }

}
