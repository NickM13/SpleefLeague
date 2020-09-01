package com.spleefleague.core.game.leaderboard;

import com.spleefleague.core.Core;
import com.spleefleague.core.game.BattleMode;
import com.spleefleague.core.menu.InventoryMenuAPI;
import com.spleefleague.core.menu.InventoryMenuContainerChest;
import com.spleefleague.core.menu.InventoryMenuUtils;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.coreapi.game.leaderboard.ActiveLeaderboard;
import com.spleefleague.coreapi.game.leaderboard.ArchivedLeaderboard;
import com.spleefleague.coreapi.game.leaderboard.Leaderboard;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;

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
        int place = leaderboards.get(cp.getMenuTag("season", Integer.class)).getPlayerRank(cp.getUniqueId());
        if (place != -1) return place / pageSize;
        return 0;
    }
    
    public void scrollSeason(CorePlayer cp, int num) {
        cp.setMenuTag("season", cp.getMenuTag("season", Integer.class) + num);
        cp.setMenuTag("rankpage", findPage(cp, createMenuContainer().getPageItemTotal()));
    }
    
    public InventoryMenuContainerChest createMenuContainer() {
        InventoryMenuContainerChest menuContainer = InventoryMenuAPI.createContainer()
                .setTitle(cp -> BattleMode.get(name).getDisplayName() + " Season " + (cp.getMenuTag("season", Integer.class) + 1))
                .setPageBoundaries(1, 3, 1, 7)
                .setOpenAction((container, cp) -> {
                    cp.setMenuTag("season", activeLeaderboard.getSeason());
                    cp.setMenuTag("rankpage", findPage(cp, container.getPageItemTotal()));
                })
                .setRefreshAction((container, cp) -> {
                    container.clearUnsorted();
                    int season = cp.getMenuTag("season", Integer.class);
                    int page = cp.getMenuTag("rankpage", Integer.class);
                    List<UUID> players = new ArrayList<>();
                    Leaderboard leaderboard = leaderboards.get(season);
                    if (leaderboard != null) {
                        players.addAll(leaderboard.getPlayers(container.getPageItemTotal() * page, container.getPageItemTotal()));
                    }
                    for (int i = 0; i < players.size(); i++) {
                        CorePlayer cp2 = Core.getInstance().getPlayers().getOffline(players.get(i));
                        if (cp2 != null) {
                            container.addMenuItem(InventoryMenuAPI.createItem()
                                    .setName(ChatColor.YELLOW + "" + ChatColor.BOLD + cp2.getName() + " " + cp2.getRatings().getDisplayElo(name, season))
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
    
        menuContainer.addStaticItem(InventoryMenuAPI.createItem()
                .setName(ChatColor.RED + "" + ChatColor.BOLD + "Previous Season")
                .setDescription("")
                .setDisplayItem(InventoryMenuUtils.MenuIcon.PREVIOUS.getIconItem()),
                0, 0)
                .setVisibility(cp -> cp.getMenuTag("season", Integer.class) > 0)
                .setAction(cp -> scrollSeason(cp, -1))
                .setCloseOnAction(false);
    
        menuContainer.addStaticItem(InventoryMenuAPI.createItem()
                        .setName(cp -> "Season " + (cp.getMenuTag("season", Integer.class) - 2))
                        .setDescription(cp -> leaderboards.get(cp.getMenuTag("season", Integer.class) - 3).getDescription())
                        .setDisplayItem(cp -> InventoryMenuUtils.createCustomItemAmount(Material.WHITE_WOOL, cp.getMenuTag("season", Integer.class) - 2)),
                1, 0)
                .setVisibility(cp -> cp.getMenuTag("season", Integer.class) > 2)
                .setAction(cp -> scrollSeason(cp, -3))
                .setCloseOnAction(false);
    
        menuContainer.addStaticItem(InventoryMenuAPI.createItem()
                        .setName(cp -> "Season " + (cp.getMenuTag("season", Integer.class) - 1))
                        .setDescription(cp -> leaderboards.get(cp.getMenuTag("season", Integer.class) - 2).getDescription())
                        .setDisplayItem(cp -> InventoryMenuUtils.createCustomItemAmount(Material.WHITE_WOOL, cp.getMenuTag("season", Integer.class) - 1)),
                2, 0)
                .setVisibility(cp -> cp.getMenuTag("season", Integer.class) > 1)
                .setAction(cp -> scrollSeason(cp, -2))
                .setCloseOnAction(false);
    
        menuContainer.addStaticItem(InventoryMenuAPI.createItem()
                        .setName(cp -> "Season " + (cp.getMenuTag("season", Integer.class)))
                        .setDescription(cp -> leaderboards.get(cp.getMenuTag("season", Integer.class) - 1).getDescription())
                        .setDisplayItem(cp -> InventoryMenuUtils.createCustomItemAmount(Material.WHITE_WOOL, cp.getMenuTag("season", Integer.class))),
                3, 0)
                .setVisibility(cp -> cp.getMenuTag("season", Integer.class) > 0)
                .setAction(cp -> scrollSeason(cp, -1))
                .setCloseOnAction(false);
    
        /**
         * Current Season
         */
        menuContainer.addStaticItem(InventoryMenuAPI.createItem()
                        .setName(cp -> "Season " + (cp.getMenuTag("season", Integer.class) + 1))
                        .setDescription(cp -> leaderboards.get(cp.getMenuTag("season", Integer.class)).getDescription())
                        .setDisplayItem(cp -> InventoryMenuUtils.createCustomItemAmount(Material.RED_WOOL, cp.getMenuTag("season", Integer.class) + 1)),
                4, 0)
                .setCloseOnAction(false);
    
        menuContainer.addStaticItem(InventoryMenuAPI.createItem()
                        .setName(cp -> "Season " + (cp.getMenuTag("season", Integer.class) + 2))
                        .setDescription(cp -> leaderboards.get(cp.getMenuTag("season", Integer.class) + 1).getDescription())
                        .setDisplayItem(cp -> InventoryMenuUtils.createCustomItemAmount(Material.WHITE_WOOL, cp.getMenuTag("season", Integer.class) + 2)),
                5, 0)
                .setVisibility(cp -> cp.getMenuTag("season", Integer.class) < leaderboards.size() - 1)
                .setAction(cp -> scrollSeason(cp, 1))
                .setCloseOnAction(false);
    
        menuContainer.addStaticItem(InventoryMenuAPI.createItem()
                        .setName(cp -> "Season " + (cp.getMenuTag("season", Integer.class) + 3))
                        .setDescription(cp -> leaderboards.get(cp.getMenuTag("season", Integer.class) + 2).getDescription())
                        .setDisplayItem(cp -> InventoryMenuUtils.createCustomItemAmount(Material.WHITE_WOOL, cp.getMenuTag("season", Integer.class) + 3)),
                6, 0)
                .setVisibility(cp -> cp.getMenuTag("season", Integer.class) < leaderboards.size() - 2)
                .setAction(cp -> scrollSeason(cp, 2))
                .setCloseOnAction(false);
    
        menuContainer.addStaticItem(InventoryMenuAPI.createItem()
                        .setName(cp -> "Season " + (cp.getMenuTag("season", Integer.class) + 4))
                        .setDescription(cp -> leaderboards.get(cp.getMenuTag("season", Integer.class) + 3).getDescription())
                        .setDisplayItem(cp -> InventoryMenuUtils.createCustomItemAmount(Material.WHITE_WOOL, cp.getMenuTag("season", Integer.class) + 4)),
                7, 0)
                .setVisibility(cp -> cp.getMenuTag("season", Integer.class) < leaderboards.size()  - 3)
                .setAction(cp -> scrollSeason(cp, 3))
                .setCloseOnAction(false);
    
        menuContainer.addStaticItem(InventoryMenuAPI.createItem()
                .setName(ChatColor.GREEN + "" + ChatColor.BOLD + "Next Season")
                .setDescription("")
                .setDisplayItem(InventoryMenuUtils.MenuIcon.NEXT.getIconItem()),
                8, 0)
                .setVisibility(cp -> cp.getMenuTag("season", Integer.class) < leaderboards.size() - 1)
                .setAction(cp -> scrollSeason(cp, 1))
                .setCloseOnAction(false);
    
        menuContainer.addStaticItem(InventoryMenuAPI.createItem()
                        .setName(ChatColor.RED + "" + ChatColor.BOLD + "Previous Page")
                        .setDescription("")
                        .setDisplayItem(InventoryMenuUtils.MenuIcon.PREVIOUS.getIconItem()),
                2, 4)
                .setVisibility(cp -> cp.getMenuTag("rankpage", Integer.class) > 0)
                .setAction(cp -> cp.setMenuTag("rankpage", cp.getMenuTag("rankpage", Integer.class) - 1))
                .setCloseOnAction(false);
    
        menuContainer.addStaticItem(InventoryMenuAPI.createItem()
                .setName(ChatColor.GREEN + "" + ChatColor.BOLD + "Next Page")
                .setDescription("")
                .setDisplayItem(InventoryMenuUtils.MenuIcon.NEXT.getIconItem()),
                6, 4)
                .setVisibility(cp -> {
                    int playerCount = leaderboards.get(cp.getMenuTag("season", Integer.class)).getPlayerCount();
                    return cp.getMenuTag("rankpage", Integer.class) < playerCount / menuContainer.getPageItemTotal();
                })
                .setAction(cp -> cp.setMenuTag("rankpage", cp.getMenuTag("rankpage", Integer.class) + 1))
                .setCloseOnAction(false);
    
        menuContainer.addStaticItem(InventoryMenuAPI.createItem()
                .setName(ChatColor.AQUA + "" + ChatColor.BOLD + "Search for Player")
                .setDescription("")
                .setDisplayItem(Material.OAK_SIGN),
                4, 4)
                .setAction(cp -> {
                    cp.setInventoryMenuAnvil(InventoryMenuAPI.createAnvil()
                            .setTitle("Search for Player")
                            .setSuccessFunc(str -> {
                                return leaderboards.get(cp.getMenuTag("season", Integer.class)).containsPlayer(Bukkit.getOfflinePlayer(str).getUniqueId());
                            })
                            .setAction((cp2, str) -> {
                                int place = leaderboards.get(cp.getMenuTag("season", Integer.class)).getPlayerRank(Bukkit.getOfflinePlayer(str).getUniqueId());
                                cp.setMenuTag("rankpage", place / menuContainer.getPageItemTotal());
                            })
                            .setParentContainer(menuContainer)
                            .setFailText("Player not rated!"));
                })
                .setCloseOnAction(false);
        
        return menuContainer;
    }

}
