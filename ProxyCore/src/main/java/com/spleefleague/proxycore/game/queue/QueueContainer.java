package com.spleefleague.proxycore.game.queue;

import com.spleefleague.coreapi.chat.ChatColor;
import com.spleefleague.coreapi.queue.SubQuery;
import com.spleefleague.proxycore.ProxyCore;

import java.util.*;

/**
 * @author NickM13
 * @since 6/23/2020
 */
public abstract class QueueContainer {

    protected static class QueuedChunk {
        List<QueueEntity> entities = new ArrayList<>();
        StringBuilder query;
        int minElo, maxElo;
        int total;

        /**
         * Attemps to match two query searches, returning null if any sub queries return an empty string
         *
         * @param query1 Query String
         * @param query2 Query String
         * @return Query
         */
        protected String matchQueries(String query1, String query2) {
            StringBuilder matchedQuery = new StringBuilder();
            SubQuery[] subQueries1 = SubQuery.splitQuery(query1);
            SubQuery[] subQueries2 = SubQuery.splitQuery(query2);
            boolean matched;
            for (SubQuery sq1 : subQueries1) {
                matched = false;
                for (SubQuery sq2 : subQueries2) {
                    if (sq2.type.equals(sq1.type)) {
                        String newQuery = sq1.compareValue(sq2);
                        if (newQuery.isEmpty()) return null;
                        matchedQuery.append(matchedQuery.length() > 0 ? ";" : "").append(sq1.type).append(":").append(newQuery);
                        matched = true;
                    }
                }
                if (!matched) {
                    ProxyCore.getInstance().getLogger().severe("QueueContainer.java: Something went wrong with matching queries!");
                    return null;
                }
            }
            return matchedQuery.toString();
        }

        public void start(QueueEntity entity) {
            entities.clear();
            minElo = entity.getRatingMin();
            maxElo = entity.getRatingMax();
            entities.add(entity);
            query = new StringBuilder(entity.query);
            total = entity.size;
        }

        public boolean join(QueueEntity entity) {
            if (entity.getRatingMin() > maxElo || entity.getRatingMax() < minElo) {
                System.out.println("Ratings were out of bounds!");
                return false;
            }
            String newQuery = matchQueries(query.toString(), entity.query);
            if (newQuery == null) {
                return false;
            }
            query = new StringBuilder(newQuery);
            minElo = Math.max(minElo, entity.getRatingMin());
            maxElo = Math.min(maxElo, entity.getRatingMax());
            entities.add(entity);
            total += entity.size;
            return true;
        }

        public boolean joinNoRating(QueueEntity entity) {
            String newQuery = matchQueries(query.toString(), entity.query);
            if (newQuery == null) {
                return false;
            }
            query = new StringBuilder(newQuery);
            minElo = Math.max(minElo, entity.getRatingMin());
            maxElo = Math.min(maxElo, entity.getRatingMax());
            entities.add(entity);
            total += entity.size;
            return true;
        }

    }

    public enum TeamStyle {
        SOLO,
        VERSUS,
        TEAM,
        DYNAMIC,
        BONANZA
    }

    protected final String identifier;
    protected final String displayName;
    protected final int reqTeams;
    protected final int maxTeams;
    protected final Set<UUID> spectating;
    protected final Set<UUID> playing;
    protected int queueSize = 0;

    protected List<QueueEntity> queuedEntities = new ArrayList<>();

    protected final int DYNAMIC_DELAY_START = 3;
    protected final int SEASON;

    public QueueContainer(String identifier, String displayName, int reqTeams, int maxTeams) {
        this.identifier = identifier;
        this.displayName = displayName;
        this.reqTeams = reqTeams;
        this.maxTeams = maxTeams;
        this.spectating = new HashSet<>();
        this.playing = new HashSet<>();
        this.SEASON = ProxyCore.getInstance().getSeasonManager().getCurrentSeason();
    }

    public String getDisplayName() {
        return ChatColor.GOLD + displayName + ChatColor.GRAY;
    }

    public void removePlayer(UUID uuid) {
        spectating.remove(uuid);
        playing.remove(uuid);
    }

    public Set<UUID> getSpectating() {
        return spectating;
    }

    public void addSpectator(UUID uuid) {
        spectating.add(uuid);
    }

    public Set<UUID> getPlaying() {
        return playing;
    }

    public abstract void checkQueue();

    public int getQueueSize() {
        return queueSize;
    }

}
