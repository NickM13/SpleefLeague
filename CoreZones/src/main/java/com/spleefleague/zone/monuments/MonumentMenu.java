package com.spleefleague.zone.monuments;

import com.spleefleague.core.menu.*;
import com.spleefleague.zone.CoreZones;
import com.spleefleague.zone.fragments.FragmentContainer;
import com.spleefleague.zone.player.ZonePlayer;
import com.spleefleague.zone.player.fragments.PlayerFragments;
import org.bukkit.Material;

/**
 * @author NickM13
 * @since 2/24/2021
 */
public class MonumentMenu {

    public static InventoryMenuItem createMenu() {
        InventoryMenuItemStatic menuItem = InventoryMenuAPI.createItemStatic();

        menuItem.setName("Monuments")
                .setDescription("")
                .setDisplayItem(Material.HONEYCOMB, 1)
                .createLinkedContainer("Monuments");

        menuItem.getLinkedChest()
                .setPageBoundaries(1, 5, 0, 6)
                .setOpenAction((container, corePlayer) -> {
            container.clear();
            int i = 0;
            ZonePlayer zonePlayer = CoreZones.getInstance().getPlayers().get(corePlayer);
            PlayerFragments fragments = zonePlayer.getFragments();
            for (Monument monument : CoreZones.getInstance().getMonumentManager().getAll()) {
                FragmentContainer fragment = monument.getFragmentContainer();
                int collected = fragments.getCollectedCount(fragment.getIdentifier());
                double percent = fragments.getCollectedPercent(fragment);
                double percentPerPiece = 1D / 5;

                InventoryMenuItem fragmentMenu = InventoryMenuAPI.createItemDynamic()
                        .setDisplayItem(fragment.getMenuItem())
                        .setName(fragment.getDisplayName())
                        .setDescription(fragment.getDescription())
                        .setCloseOnAction(false);
                for (int stage = 0; stage < 5; stage++) {
                    double startPercent = (double) stage / 5;
                    double chunkPercent = Math.max(Math.min((percent - startPercent) / percentPerPiece, 1D), 0D);
                    int state = (int) Math.floor(4 * chunkPercent);
                    int required = (int) (((double) (stage + 1) / 5) * fragment.getTotal());
                    //String name = (int) (chunkPercent * 100) + "%";
                    String name;
                    if (chunkPercent >= 1) {
                        name = "Tier " + (stage + 1) + " - " + (stage + 1) * 20 + "%";
                    } else {
                        name = "Tier " + (stage + 1) + " - " + (required - collected) + " Remaining";
                    }
                    InventoryMenuItemStatic item = (InventoryMenuItemStatic) InventoryMenuAPI.createItemStatic()
                            .setName(name)
                            .setDescription(monument.getRewardDescription(stage, fragments.getStage(monument) >= stage, corePlayer))
                            .setDisplayItem(InventoryMenuUtils.createCustomItem(Material.GLOWSTONE_DUST, state + 1))
                            .setCloseOnAction(false);
                    container.addMenuItem(item, 1 + stage, i);
                }

                i++;
                //.createLinkedContainer(fragment.getDisplayName());

                //fragmentMenu.getLinkedChest().addMenuItem(fragment.createMenu());

                container.addMenuItem(fragmentMenu);
            }
        });

        return menuItem;
    }

}
