package com.spleefleague.proxycore.game.queue;

import com.google.common.collect.Lists;
import com.spleefleague.coreapi.chat.ChatColor;
import com.spleefleague.coreapi.queue.SubQuery;
import com.spleefleague.proxycore.ProxyCore;

import java.util.*;

/**
 * @author NickM13
 * @since 6/23/2020
 */
public abstract class QueueContainer {

    protected static final int MAX_PARTY_QUEUE_SIZE = 8;

    public static int getMaxPartyQueueSize() {
        return MAX_PARTY_QUEUE_SIZE;
    }

    protected static class QueueTeam {
        List<UUID> players = new ArrayList<>();
        int total = 0;
        final int teamSize;
        final int id;

        public QueueTeam(int teamSize, int id) {
            this.teamSize = teamSize;
            this.id = id;
        }

        public QueueTeam(QueueTeam queueTeam) {
            this.players.addAll(queueTeam.players);
            this.total = queueTeam.total;
            this.teamSize = queueTeam.teamSize;
            this.id = queueTeam.id;
        }

        public boolean add(UUID uuid) {
            if (total + 1 > teamSize) {
                return false;
            } else {
                players.add(uuid);
                total++;
                return true;
            }
        }

        public boolean add(List<UUID> uuid) {
            if (total + uuid.size() > teamSize) {
                return false;
            } else {
                players.addAll(uuid);
                total += uuid.size();
                return true;
            }
        }

        public boolean isFull() {
            return teamSize == total;
        }

        public int getRemaining() {
            return teamSize - total;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            QueueTeam queueTeam = (QueueTeam) o;
            return id == queueTeam.id;
        }

        @Override
        public int hashCode() {
            return Objects.hash(id);
        }
    }

    protected static class QueuedChunk {
        List<QueueTeam> openTeams = new ArrayList<>();
        List<QueueTeam> filledTeams = new ArrayList<>();

        final boolean teamSplitting;
        final int teamSize;
        StringBuilder query;
        int minElo, maxElo;

        public QueuedChunk(QueueEntity entity, int teamSize, int maxTeamCount) {
            this.teamSize = teamSize;
            this.teamSplitting = teamSize == 1;
            for (int i = 0; i < maxTeamCount; i++) {
                QueueTeam team = new QueueTeam(teamSize, i);
                openTeams.add(team);
            }

            minElo = entity.getRatingMin();
            maxElo = entity.getRatingMax();
            query = new StringBuilder(entity.query);

            pushEntity(entity);
        }

        public QueuedChunk(QueuedChunk queuedChunk) {
            for (QueueContainer.QueueTeam team : queuedChunk.openTeams) {
                this.openTeams.add(new QueueContainer.QueueTeam(team));
            }
            for (QueueContainer.QueueTeam team : queuedChunk.filledTeams) {
                this.filledTeams.add(new QueueContainer.QueueTeam(team));
            }

            this.teamSplitting = queuedChunk.teamSplitting;
            this.teamSize = queuedChunk.teamSize;
            this.query = new StringBuilder(queuedChunk.query);
            this.minElo = queuedChunk.minElo;
            this.maxElo = queuedChunk.maxElo;
        }

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

        private boolean pushEntity(QueueEntity entity) {
            if (teamSplitting) {
                List<UUID> toAdd = new ArrayList<>();
                if (entity instanceof QueueParty) {
                    toAdd.addAll(((QueueParty) entity).party.getPlayerList());
                } else if (entity instanceof QueuePlayer) {
                    toAdd.add(((QueuePlayer) entity).pcp.getUniqueId());
                } else {
                    Thread.dumpStack();
                    return false;
                }
                boolean success = false;
                int remaining = toAdd.size();
                for (QueueTeam team : openTeams) {
                    remaining -= team.getRemaining();
                    if (remaining <= 0) success = true;
                }
                if (success) {
                    for (UUID uuid : toAdd) {
                        QueueTeam team = openTeams.get(0);
                        team.add(uuid);
                        if (team.isFull()) {
                            filledTeams.add(team);
                            openTeams.remove(0);
                        }
                    }
                }
                return success;
            } else {
                if (entity instanceof QueueParty) {
                    Iterator<QueueTeam> it = openTeams.iterator();
                    while (it.hasNext()) {
                        QueueTeam team = it.next();
                        if (team.add(((QueueParty) entity).party.getPlayerList())) {
                            if (team.isFull()) {
                                filledTeams.add(team);
                                it.remove();
                            }
                            return true;
                        }
                    }
                    return false;
                } else if (entity instanceof QueuePlayer) {
                    Iterator<QueueTeam> it = openTeams.iterator();
                    while (it.hasNext()) {
                        QueueTeam team = it.next();
                        if (team.add(((QueuePlayer) entity).pcp.getUniqueId())) {
                            if (team.isFull()) {
                                filledTeams.add(team);
                                it.remove();
                            }
                            return true;
                        }
                    }
                    return false;
                }
            }
            Thread.dumpStack();
            return false;
        }

        public boolean join(QueueEntity entity, boolean compareRating) {
            if (compareRating) {
                if (entity.getRatingMin() > maxElo || entity.getRatingMax() < minElo) {
                    return false;
                }
            }
            String newQuery = matchQueries(query.toString(), entity.query);
            if (newQuery == null) {
                return false;
            }

            if (pushEntity(entity)) {
                query = new StringBuilder(newQuery);
                minElo = Math.max(minElo, entity.getRatingMin());
                maxElo = Math.min(maxElo, entity.getRatingMax());
                return true;
            }
            return false;
        }

    }

    public enum TeamStyle {
        SOLO(   true,  false, true,  1, 1 ),
        VERSUS( false, false, true,  2, 2 ),
        DYNAMIC(true,  true,  false, 4, 12),
        BONANZA(true,  false, true,  1, 1 );

        boolean allowPartySplit;
        boolean delayStart;
        boolean compareRating;
        boolean spectatable;

        int minSize;
        int maxSize;

        TeamStyle(boolean allowPartySplit, boolean delayStart, boolean compareRating, int minSize, int maxSize) {
            this.allowPartySplit = allowPartySplit;
            this.delayStart = delayStart;
            this.compareRating = compareRating;

            this.minSize = minSize;
            this.maxSize = maxSize;
        }
    }

    protected final String identifier;
    protected final String displayName;
    protected final int reqTeams;
    protected final int maxTeams;
    protected final Set<UUID> spectating;
    protected final Set<UUID> playing;
    protected int queueSize = 0;

    protected List<QueueEntity> queuedEntities = new ArrayList<>();

    protected final int DYNAMIC_DELAY_START = 60;
    protected final String SEASON;

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
