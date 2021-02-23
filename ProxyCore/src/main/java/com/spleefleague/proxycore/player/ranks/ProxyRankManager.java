package com.spleefleague.proxycore.player.ranks;

import com.mongodb.client.MongoCollection;
import com.spleefleague.coreapi.chat.ChatColor;
import com.spleefleague.coreapi.player.ranks.RankManager;
import com.spleefleague.proxycore.ProxyCore;
import org.bson.Document;

import javax.annotation.Nonnull;
import java.util.*;

/**
 * @author NickM13
 */
public class ProxyRankManager extends RankManager <ProxyRank> {

    protected MongoCollection<Document> rankCollection;

    public void init() {
        rankCollection = ProxyCore.getInstance().getDatabase().getCollection("Ranks");
        for (Document document : rankCollection.find()) {
            ProxyRank rank = new ProxyRank();
            rank.load(document);
            for (ProxyRank pr : ranks.values()) {
                if (pr.hasPermission(rank)) {
                    rank.addInheritingRank(pr);
                } else if (rank.hasPermission(pr)) {
                    pr.addInheritingRank(rank);
                }
            }
            ranks.put(rank.getIdentifier().toUpperCase(), rank);
        }

        reloadRanks();
    }

    public void close() {

    }

    public ProxyRank getDefaultRank() {
        return ProxyRank.DEFAULT;
    }

    public ProxyRank getRank(@Nonnull String name) {
        if (!ranks.containsKey(name.toUpperCase())) return null;
        return ranks.get(name.toUpperCase());
    }

    public List<ProxyRank> getRanks(String[] rankNames) {
        List<ProxyRank> ranks = new ArrayList<>();
        for (String str : rankNames) {
            ProxyRank r = getRank(str);
            if (r != null) ranks.add(r);
        }
        return ranks;
    }

    public Set<String> getRankNames() {
        return ranks.keySet();
    }

    protected ProxyRank getRankOrDefault(String rankName) {
        if (!ranks.containsKey(rankName)) {
            ProxyRank rank = new ProxyRank(rankName, 0, ChatColor.YELLOW);
            for (ProxyRank pr : ranks.values()) {
                if (pr.hasPermission(rank)) {
                    rank.addInheritingRank(pr);
                } else if (rank.hasPermission(pr)) {
                    pr.addInheritingRank(rank);
                }
            }
            ranks.put(rankName, rank);
            saveRank(ranks.get(rankName));
        }
        return ranks.get((rankName));
    }

    protected void reloadRanks() {
        ProxyRank.DEFAULT =           getRankOrDefault("DEFAULT");
        ProxyRank.ADMIN =             getRankOrDefault("ADMIN");
        ProxyRank.DEVELOPER =         getRankOrDefault("DEVELOPER");
        ProxyRank.MODERATOR =         getRankOrDefault("MODERATOR");
        ProxyRank.SENIOR_MODERATOR =  getRankOrDefault("SENIOR_MODERATOR");
        ProxyRank.BUILDER =           getRankOrDefault("BUILDER");
        ProxyRank.ORGANIZER =         getRankOrDefault("ORGANIZER");
        ProxyRank.VIP =               getRankOrDefault("VIP");
        ProxyRank.DONOR_1 =           getRankOrDefault("DONOR_1");
        ProxyRank.DONOR_2 =           getRankOrDefault("DONOR_2");
        ProxyRank.DONOR_3 =           getRankOrDefault("DONOR_3");
        ProxyRank.DONOR_4 =           getRankOrDefault("DONOR_4");
    }

    public boolean createRank(String identifier, int ladder, ChatColor chatColor) {
        if (ranks.containsKey(identifier)) {
            return false;
        }
        ProxyRank rank;
        if (ranks.containsKey(identifier)) {
            rank = ranks.get(identifier);
            rank.setIdentifier(identifier);
            rank.setLadder(ladder);
            rank.setColor(chatColor);
        } else {
            rank = new ProxyRank(identifier, ladder, chatColor);
        }
        for (ProxyRank pr : ranks.values()) {
            if (pr.hasPermission(rank)) {
                rank.addInheritingRank(pr);
            } else if (rank.hasPermission(pr)) {
                pr.addInheritingRank(rank);
            }
        }
        ranks.put(identifier, rank);
        reloadRanks();
        return true;
    }

    public boolean setRankName(String identifier, String displayName) {
        if (ranks.containsKey(identifier)) {
            ranks.get(identifier).setDisplayName(displayName);
            saveRank(ranks.get(identifier));
            return true;
        }
        return false;
    }

    public boolean setRankLadder(String identifier, int ladder) {
        if (ranks.containsKey(identifier)) {
            ranks.get(identifier).setLadder(ladder);
            saveRank(ranks.get(identifier));
            return true;
        }
        return false;
    }

    public boolean setRankMaxFriends(String identifier, int maxFriends) {
        if (ranks.containsKey(identifier)) {
            ranks.get(identifier).setMaxFriends(maxFriends);
            saveRank(ranks.get(identifier));
            return true;
        }
        return false;
    }

    public boolean setRankColor(String identifier, ChatColor color) {
        if (ranks.containsKey(identifier)) {
            ranks.get(identifier).setColor(color);
            saveRank(ranks.get(identifier));
            return true;
        }
        return false;
    }

    public boolean setRankOp(String identifier, boolean hasOp) {
        if (ranks.containsKey(identifier)) {
            ranks.get(identifier).setHasOp(hasOp);
            saveRank(ranks.get(identifier));
            return true;
        }
        return false;
    }

    protected void saveRank(ProxyRank rank) {
        rank.save(rankCollection);
    }

}
