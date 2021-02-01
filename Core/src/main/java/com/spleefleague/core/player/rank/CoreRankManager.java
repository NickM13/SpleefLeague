package com.spleefleague.core.player.rank;

import com.mongodb.client.MongoCollection;
import com.spleefleague.core.Core;
import com.spleefleague.coreapi.chat.ChatColor;
import com.spleefleague.coreapi.player.ranks.RankManager;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import javax.annotation.Nonnull;
import java.util.*;

/**
 * @author NickM13
 */
public class CoreRankManager extends RankManager<CoreRank> {

    private MongoCollection<Document> rankCollection;

    @Override
    public CoreRank getDefaultRank() {
        return CoreRank.DEFAULT;
    }

    @Override
    protected CoreRank getRankOrDefault(String rankName) {
        if (!ranks.containsKey(rankName)) {
            CoreRank rank = new CoreRank(rankName, 0, ChatColor.YELLOW);
            for (CoreRank cr : ranks.values()) {
                if (cr.hasPermission(rank)) {
                    rank.addInheritingRank(cr);
                } else if (rank.hasPermission(cr)) {
                    cr.addInheritingRank(rank);
                }
            }
            ranks.put(rankName, rank);
        }
        return ranks.get((rankName));
    }

    public void init() {
        rankCollection = Core.getInstance().getPluginDB().getCollection("Ranks");
        for (Document document : rankCollection.find()) {
            CoreRank rank = new CoreRank();
            rank.load(document);
            for (CoreRank cr : ranks.values()) {
                if (cr.hasPermission(rank)) {
                    rank.addInheritingRank(cr);
                } else if (rank.hasPermission(cr)) {
                    cr.addInheritingRank(rank);
                }
            }
            ranks.put(rank.getIdentifier().toUpperCase(), rank);
            System.out.println("Loaded rank " + rank.getIdentifier().toUpperCase());
        }

        reloadRanks();

        List<CoreRank> sortedRanks = new ArrayList<>(ranks.values());
        sortedRanks.sort(Comparator.comparingInt(CoreRank::getLadder).reversed());
        for (int i = 0; i < sortedRanks.size(); i++) {
            sortedRanks.get(i).setPriority(i);
        }

        Scoreboard mainScoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
        Set<Team> teams = mainScoreboard.getTeams();
        teams.forEach(Team::unregister);
        initScoreboard(mainScoreboard);
    }

    @Override
    protected void reloadRanks() {
        CoreRank.DEFAULT =           getRankOrDefault("DEFAULT");
        CoreRank.ADMIN =             getRankOrDefault("ADMIN");
        CoreRank.DEVELOPER =         getRankOrDefault("DEVELOPER");
        CoreRank.MODERATOR =         getRankOrDefault("MODERATOR");
        CoreRank.SENIOR_MODERATOR =  getRankOrDefault("SENIOR_MODERATOR");
        CoreRank.BUILDER =           getRankOrDefault("BUILDER");
        CoreRank.ORGANIZER =         getRankOrDefault("ORGANIZER");
        CoreRank.VIP =               getRankOrDefault("VIP");
        CoreRank.DONOR_1 =           getRankOrDefault("DONOR_1");
        CoreRank.DONOR_2 =           getRankOrDefault("DONOR_2");
        CoreRank.DONOR_3 =           getRankOrDefault("DONOR_3");
    }

    public void initScoreboard(Scoreboard scoreboard) {
        for (CoreRank rank : ranks.values()) {
            Team team = scoreboard.registerNewTeam(rank.getIdentifierShort());
            team.setColor(org.bukkit.ChatColor.valueOf(rank.getColor().name()));
            if (rank.getDisplayNameUnformatted().length() > 0)
                team.setPrefix(rank.getChatTag());
            team.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER);
            team.setAllowFriendlyFire(true);
        }
    }

}
