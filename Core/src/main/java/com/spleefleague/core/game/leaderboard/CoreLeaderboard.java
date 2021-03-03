package com.spleefleague.core.game.leaderboard;

import com.spleefleague.core.game.BattleMode;
import com.spleefleague.core.game.season.SeasonManager;
import com.spleefleague.core.menu.InventoryMenuAPI;
import com.spleefleague.core.menu.InventoryMenuContainerChest;
import com.spleefleague.core.menu.InventoryMenuUtils;
import com.spleefleague.core.player.CoreOfflinePlayer;
import com.spleefleague.coreapi.database.annotation.DBLoad;
import com.spleefleague.coreapi.game.leaderboard.Leaderboard;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;

import java.util.*;

/**
 * @author NickM13
 * @since 4/28/2020
 */
public class CoreLeaderboard extends Leaderboard {

    public CoreLeaderboard() {
        super();
    }

    public CoreLeaderboard(String name, String season) {
        super(name, season);
    }

    public int findPage(CoreOfflinePlayer cp, int pageSize) {
        int place = getPlayerRanking(cp.getUniqueId());
        if (place != -1) return place / pageSize;
        return 0;
    }

    public void clear() {
        playerScoreMap.clear();
        scorePlayersMap.clear();
    }

    private InventoryMenuContainerChest menuContainer = null;

    private static String formatWinPercent(int wins, int losses) {
        float total = wins + losses;
        float percent = Math.round(wins / total * 1000f) / 10f;
        return (percent >= 0.6f ? ChatColor.GREEN : (percent > 0.4f ? ChatColor.YELLOW : ChatColor.RED)) + "" + percent + "%";
    }

    public InventoryMenuContainerChest createMenuContainer() {
        if (menuContainer != null) {
            return menuContainer;
        }
        menuContainer = InventoryMenuAPI.createContainer()
                .setTitle(cp -> ChatColor.stripColor(BattleMode.get(name).getDisplayName()) + " " + SeasonManager.getSeasonInfo(getSeason()).getDisplayName())
                .setPageBoundaries(1, 4, 0, 5)
                .setRefreshAction((container, cp) -> {
                    int page = cp.getMenu().getPage();
                    int skip = container.getPageItemTotal() * page;
                    container.setForcedPageCount((getPlayerCount() - 1) / container.getPageItemTotal() + 1);
                    container.setForcedPageStart(page * container.getPageItemTotal());
                    container.clearUnsorted();
                    List<LeaderboardEntry> players = getPlayers(skip, container.getPageItemTotal());
                    for (int i = 0; i < players.size(); i++) {
                        LeaderboardEntry leaderboardEntry = players.get(i);
                        container.addMenuItem(InventoryMenuAPI.createItemDynamic()
                                .setName(ChatColor.YELLOW + "" + ChatColor.BOLD + leaderboardEntry.getUsername() + " " + leaderboardEntry.getDisplayElo())
                                .setDescription(ChatColor.GRAY + "Rank: " + leaderboardEntry.getDivision().getDisplayName() + ChatColor.YELLOW + " (#" + (i + container.getPageItemTotal() * page + 1) + ")\n"
                                        + ChatColor.GRAY + "Wins: " + ChatColor.GREEN + leaderboardEntry.getWins() + "\n"
                                        + ChatColor.GRAY + "Losses: " + ChatColor.RED + leaderboardEntry.getLosses() + "\n"
                                        + ChatColor.GRAY + "Win Rate: " + formatWinPercent(leaderboardEntry.getWins(), leaderboardEntry.getLosses()))
                                .setDisplayItem(InventoryMenuUtils.createCustomSkullOrDefault(leaderboardEntry.getUniqueId()))
                                .setCloseOnAction(false), i + skip);
                    }
                })
                .setOpenAction((container, cp) -> {
                    if (cp.getMenu().hasMenuTag("rankSearch")) {
                        int place = getPlayerRanking(Bukkit.getOfflinePlayer(cp.getMenu().getMenuTag("rankSearch", String.class)).getUniqueId());
                        cp.getMenu().setPage(place / container.getPageItemTotal());
                        cp.getMenu().removeMenuTag("rankSearch");
                    }
                });

        menuContainer.addStaticItem(InventoryMenuAPI.createItemDynamic()
                        .setName(ChatColor.AQUA + "" + ChatColor.BOLD + "Search for Player")
                        .setDescription("")
                        .setDisplayItem(Material.NAME_TAG, 1),
                2, 0)
                .setAction(cp -> cp.getMenu().setInventoryMenuAnvil(InventoryMenuAPI.createAnvil()
                        .setTitle("Search for Player")
                        .setSuccessFunc(str -> containsPlayer(Bukkit.getOfflinePlayer(str).getUniqueId()))
                        .setAction((cp2, str) -> cp.getMenu().setMenuTag("rankSearch", str))
                        .setFailText("Player not rated!")))
                .setCloseOnAction(false);

        /*
        menuContainer.addStaticItem(InventoryMenuAPI.createItemDynamic()
                .setName(BattleMode.get(name).getDisplayName())
                .setDisplayItem(displayIcon)
                .setCloseOnAction(false), 6, 1);
        */

        menuContainer.addStaticItem(InventoryMenuAPI.createItemDynamic()
                .setName(cp -> ChatColor.YELLOW + "" + ChatColor.BOLD + cp.getName() + " " + cp.getRatings().getDisplayElo(name, getSeason()))
                .setDescription(cp -> {
                    return ChatColor.GRAY + "Rank: " + cp.getRatings().getDisplayDivision(name, getSeason()) +
                            (cp.getRatings().isRanked(name, getSeason()) ? ChatColor.YELLOW + " (#" + (getPlayerRanking(cp.getUniqueId()) + 1) + ")" : "") + "\n"
                            + ChatColor.GRAY + "Wins: " + ChatColor.GREEN + cp.getRatings().getWins(name, getSeason()) + "\n"
                            + ChatColor.GRAY + "Losses: " + ChatColor.RED + cp.getRatings().getLosses(name, getSeason()) + "\n"
                            + ChatColor.GRAY + "Win Rate: " + cp.getRatings().getWinPercent(name, getSeason());
                })
                .setDisplayItem(cp -> InventoryMenuUtils.createCustomSkullOrDefault(cp.getUniqueId()))
                .setCloseOnAction(false), 6, 2);

        return menuContainer;
    }

    @DBLoad(fieldName = "players")
    protected void loadPlayers(Document doc) {
        for (Map.Entry<String, Object> entry : doc.entrySet()) {
            UUID uuid = UUID.fromString(entry.getKey());
            LeaderboardEntry leaderboardEntry = new LeaderboardEntry(uuid);
            leaderboardEntry.load((Document) entry.getValue());
            setPlayerScore(uuid, leaderboardEntry);
        }
    }

}
