package com.spleefleague.core.game;

import com.spleefleague.core.Core;
import com.spleefleague.core.menu.InventoryMenuAPI;
import com.spleefleague.core.menu.InventoryMenuContainer;
import com.spleefleague.core.player.CorePlayer;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

/**
 * @author NickM13
 * @since 4/27/2020
 */
public class SeasonalLeaderboard extends Leaderboard {
    
    public SeasonalLeaderboard(String name, String displayName, ItemStack displayItem, String description) {
        super(name, displayName, displayItem, description);
    }
    
    @Override
    public void checkResetDay() { }
    
    public InventoryMenuContainer getMenuContainer() {
        InventoryMenuContainer menuContainer = InventoryMenuAPI.createContainer()
                .setTitle(displayName)
                .setPageBoundaries(1, 3, 1, 8);
        menuContainer.setOpenAction((container, cp) -> {
            container.clearUnsorted();
            for (int i = cp.getPage() * container.getPageItemTotal(); i < (cp.getPage() + 1) * container.getPageItemTotal() && i < players.size(); i++) {
                UUID uuid = players.get(i);
                CorePlayer cp2 = Core.getInstance().getPlayers().get(uuid);
                
            }
        });
        return menuContainer;
    }
    
}
