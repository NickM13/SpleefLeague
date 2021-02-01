package com.spleefleague.coreapi.player.ranks;

import javax.annotation.Nonnull;
import java.util.*;

/**
 * @author NickM13
 */
public abstract class RankManager <R extends Rank> {

    protected final Map<String, R> ranks = new HashMap<>();

    public abstract R getDefaultRank();

    public R getRank(@Nonnull String name) {
        if (!ranks.containsKey(name.toUpperCase())) return null;
        return ranks.get(name.toUpperCase());
    }

    public List<R> getRanks(String[] rankNames) {
        List<R> ranks = new ArrayList<>();
        for (String str : rankNames) {
            R r = getRank(str);
            if (r != null) ranks.add(r);
        }
        return ranks;
    }

    public Set<String> getRankNames() {
        return ranks.keySet();
    }

    protected abstract R getRankOrDefault(String rankName);

    protected abstract void reloadRanks();

    public boolean hasPermission(R r, String permission) {
        for (String perm : r.getExclusivePermissions()) {
            if (perm.equals(permission)) {
                return true;
            }
        }
        for (R rank : ranks.values()) {
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

    public Set<String> getAllPermissions(R r) {
        Set<String> perms = new HashSet<>(r.getExclusivePermissions());
        perms.addAll(r.getPermissions());
        for (R rank : ranks.values()) {
            if (rank.getLadder() < r.getLadder()) {
                perms.addAll(rank.getPermissions());
            }
        }
        return perms;
    }

}
