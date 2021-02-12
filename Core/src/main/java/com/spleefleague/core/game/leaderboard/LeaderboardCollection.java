package com.spleefleague.core.game.leaderboard;

import com.spleefleague.core.Core;
import com.spleefleague.core.game.BattleMode;
import com.spleefleague.core.menu.InventoryMenuAPI;
import com.spleefleague.core.menu.InventoryMenuContainerChest;
import com.spleefleague.core.menu.InventoryMenuItemStatic;
import com.spleefleague.core.menu.InventoryMenuUtils;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.coreapi.game.leaderboard.ActiveLeaderboard;
import com.spleefleague.coreapi.game.leaderboard.ArchivedLeaderboard;
import com.spleefleague.coreapi.game.leaderboard.Leaderboard;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.*;

/**
 * @author NickM13
 * @since 4/28/2020
 */
public class LeaderboardCollection {

    protected String name;
    protected ActiveLeaderboard activeLeaderboard;
    protected final Map<Integer, Leaderboard> leaderboards = new TreeMap<>();

    public LeaderboardCollection(String name) {
        this.name = name;
        activeLeaderboard = new ActiveLeaderboard(name, 0);
        leaderboards.put(0, activeLeaderboard);
    }

    public ArchivedLeaderboard startNewSeason() {
        ArchivedLeaderboard archivedLeaderboard = new ArchivedLeaderboard(activeLeaderboard);
        leaderboards.put(activeLeaderboard.getSeason(), archivedLeaderboard);
        activeLeaderboard = new ActiveLeaderboard(name, leaderboards.size());
        leaderboards.put(activeLeaderboard.getSeason(), activeLeaderboard);
        return archivedLeaderboard;
    }

    public String getName() {
        return name;
    }

    public void addLeaderboard(Leaderboard leaderboard) {
        if (leaderboard.isActive()) {
            activeLeaderboard = (ActiveLeaderboard) leaderboard;
        }
        leaderboards.put(leaderboard.getSeason(), leaderboard);
    }

    public ActiveLeaderboard getActive() {
        return activeLeaderboard;
    }

    public Map<Integer, Leaderboard> getLeaderboards() {
        return leaderboards;
    }

    public int findPage(CorePlayer cp, int pageSize) {
        int place = leaderboards.get(activeLeaderboard.getSeason()).getPlayerRank(cp.getUniqueId());
        if (place != -1) return place / pageSize;
        return 0;
    }

    private InventoryMenuContainerChest menuContainer = null;

    public InventoryMenuContainerChest createMenuContainer() {
        if (menuContainer != null) {
            return menuContainer;
        }
        menuContainer = InventoryMenuAPI.createContainer()
                .setTitle(cp -> ChatColor.stripColor(BattleMode.get(name).getDisplayName()) + " Season " + activeLeaderboard.getSeason())
                .setPageBoundaries(1, 4, 0, 5)
                .setRefreshAction((container, cp) -> {
                    int season = activeLeaderboard.getSeason();
                    int page = cp.getMenu().getPage();
                    int skip = container.getPageItemTotal() * page;
                    container.setForcedPageCount(activeLeaderboard.getPlayerCount() / container.getPageItemTotal() + 1);
                    container.setForcedPageStart(page * container.getPageItemTotal());
                    container.clearUnsorted();
                    List<UUID> players = new ArrayList<>(activeLeaderboard.getPlayers(skip, container.getPageItemTotal()));
                    for (int i = 0; i < players.size(); i++) {
                        CorePlayer cp2 = Core.getInstance().getPlayers().getOffline(players.get(i));
                        if (cp2 != null) {
                            container.addMenuItem(InventoryMenuAPI.createItemDynamic()
                                    .setName(cp2.getMenuName() + " " + cp2.getRatings().getDisplayElo(name, season))
                                    .setDescription(ChatColor.GRAY + "Rank: " + cp2.getRatings().getDisplayDivision(name, season) + ChatColor.YELLOW + " (#" + (i + container.getPageItemTotal() * page + 1) + ")\n"
                                            + ChatColor.GRAY + "Wins: " + ChatColor.GREEN + cp2.getRatings().getWins(name, season) + "\n"
                                            + ChatColor.GRAY + "Losses: " + ChatColor.RED + cp2.getRatings().getLosses(name, season) + "\n"
                                            + ChatColor.GRAY + "Win Rate: " + cp2.getRatings().getWinPercent(name, season))
                                    .setDisplayItem(InventoryMenuUtils.createCustomSkullOrDefault(cp2.getUniqueId()))
                                    .setCloseOnAction(false));
                        } else {
                            container.addMenuItem(InventoryMenuUtils.createLockedMenuItem("Invalid Player"));
                        }
                    }
                });

        menuContainer.addStaticItem(InventoryMenuAPI.createItemDynamic()
                        .setName(ChatColor.AQUA + "" + ChatColor.BOLD + "Search for Player")
                        .setDescription("")
                        .setDisplayItem(Material.NAME_TAG, 1),
                2, 0)
                .setAction(cp -> cp.getMenu().setInventoryMenuAnvil(InventoryMenuAPI.createAnvil()
                        .setTitle("Search for Player")
                        .setSuccessFunc(str -> leaderboards.get(activeLeaderboard.getSeason()).containsPlayer(Bukkit.getOfflinePlayer(str).getUniqueId()))
                        .setAction((cp2, str) -> {
                            int place = leaderboards.get(activeLeaderboard.getSeason()).getPlayerRank(Bukkit.getOfflinePlayer(str).getUniqueId());
                            cp.getMenu().setPage(place / menuContainer.getPageItemTotal());
                        })
                        .setFailText("Player not rated!")))
                .setCloseOnAction(false);

        /*
        menuContainer.addStaticItem(InventoryMenuAPI.createItemDynamic()
                .setName(BattleMode.get(name).getDisplayName())
                .setDisplayItem(displayIcon)
                .setCloseOnAction(false), 6, 1);
        */

        menuContainer.addStaticItem(InventoryMenuAPI.createItemDynamic()
                .setName(cp -> ChatColor.YELLOW + "" + ChatColor.BOLD + cp.getName() + " " + cp.getRatings().getDisplayElo(name, activeLeaderboard.getSeason()))
                .setDescription(cp -> ChatColor.GRAY + "Rank: " + cp.getRatings().getDisplayDivision(name, activeLeaderboard.getSeason()) + ChatColor.YELLOW + " (#" + (activeLeaderboard.getPlayerRank(cp.getUniqueId()) + 1) + ")\n"
                        + ChatColor.GRAY + "Wins: " + ChatColor.GREEN + cp.getRatings().getWins(name, activeLeaderboard.getSeason()) + "\n"
                        + ChatColor.GRAY + "Losses: " + ChatColor.RED + cp.getRatings().getLosses(name, activeLeaderboard.getSeason()) + "\n"
                        + ChatColor.GRAY + "Win Rate: " + cp.getRatings().getWinPercent(name, activeLeaderboard.getSeason()))
                .setDisplayItem(cp -> InventoryMenuUtils.createCustomSkullOrDefault(cp.getUniqueId()))
                .setCloseOnAction(false), 6, 2);

        return menuContainer;
    }

}
