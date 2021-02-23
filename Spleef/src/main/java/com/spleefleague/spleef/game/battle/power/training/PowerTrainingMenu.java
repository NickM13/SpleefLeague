/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.spleef.game.battle.power.training;

import com.spleefleague.core.menu.InventoryMenuAPI;
import com.spleefleague.core.menu.InventoryMenuItem;
import com.spleefleague.core.menu.InventoryMenuItemHotbar;
import com.spleefleague.core.menu.InventoryMenuUtils;
import com.spleefleague.core.player.BattleState;
import com.spleefleague.core.world.build.BuildStructure;
import com.spleefleague.core.world.build.BuildStructures;
import com.spleefleague.spleef.Spleef;
import com.spleefleague.spleef.game.SpleefMode;
import com.spleefleague.spleef.game.battle.power.PowerSpleefPlayer;
import org.bukkit.ChatColor;
import org.bukkit.Material;

import java.util.List;

/**
 * @author NickM13
 */
public class PowerTrainingMenu {

    private static final String mainColor = ChatColor.AQUA + "" + ChatColor.BOLD;

    private static InventoryMenuItem menuItem;

    public static void createMenu() {
        if (menuItem == null) {
            menuItem = InventoryMenuAPI.createItemDynamic()
                    .setName("&6&lTraining Field")
                    .setDescription("Hone your skills in this solo sandbox version of Power Spleef! Change your powers in game, control field regeneration and learn new combos to best your foes.")
                    .setDisplayItem(Material.WOODEN_SHOVEL, 1)
                    .setAction(cp -> Spleef.getInstance().queuePlayer(SpleefMode.POWER_TRAINING.getBattleMode(), cp));

            InventoryMenuItemHotbar options = (InventoryMenuItemHotbar) InventoryMenuAPI.createItemHotbar(8, "pstOptions")
                    .setName(cp -> "&a&lTraining Options")
                    .setDisplayItem(InventoryMenuUtils.createCustomItem(Material.REDSTONE, 1))
                    .setDescription("Various training options for Power Spleef Training mode")
                    .setAvailability(cp -> cp.getBattleState() == BattleState.BATTLER &&
                            cp.getBattle() instanceof PowerTrainingBattle &&
                            cp.getBattle().getBattler(cp) != null &&
                            ((PowerSpleefPlayer) cp.getBattle().getBattler(cp)).getMobility() != null)
                    .setVisibility(cp -> ((PowerSpleefPlayer) cp.getBattle().getBattler(cp)).getMobility() != null)
                    .createLinkedContainer("Training Options");

            options.getLinkedChest().addMenuItem(InventoryMenuAPI.createItemDynamic()
                    .setName(cp -> "&a&lTraining Field")
                    .setDisplayItem(InventoryMenuUtils.createCustomItem(Material.BAKED_POTATO, 2))
                    .setDescription(cp -> {
                        PowerTrainingBattle battle = (PowerTrainingBattle) cp.getBattle();
                        List<BuildStructure> structures = BuildStructures.getAll("spleef:power");
                        StringBuilder stringBuilder = new StringBuilder();
                        for (int i = 0; i < structures.size(); i++) {
                            if (i == battle.getCurrFieldIndex()) {
                                stringBuilder.append(ChatColor.GREEN + structures.get(i).getName().substring("spleef:power".length()));
                            } else {
                                stringBuilder.append(ChatColor.GRAY + structures.get(i).getName().substring("spleef:power".length()));
                            }
                            if (i < structures.size() - 1) {
                                stringBuilder.append("\n");
                            }
                        }
                        return stringBuilder.toString();
                    })
                    .setAction(cp -> ((PowerTrainingBattle) cp.getBattle()).nextField())
                    .setCloseOnAction(false));

            options.getLinkedChest().addMenuItem(InventoryMenuAPI.createItemDynamic()
                    .setName(cp -> "&a&lPower Cooldowns")
                    .setDisplayItem(InventoryMenuUtils.createCustomItem(Material.BAKED_POTATO, 3))
                    .setDescription(cp -> {
                        PowerTrainingBattle battle = (PowerTrainingBattle) cp.getBattle();
                        return "Cooldowns are currently " + (battle.isCooldownEnabled() ? ChatColor.GREEN + "enabled" : ChatColor.GRAY + "disabled");
                    })
                    .setAction(cp -> ((PowerTrainingBattle) cp.getBattle()).setCooldownEnabled(!((PowerTrainingBattle) cp.getBattle()).isCooldownEnabled()))
                    .setCloseOnAction(false));

            options.getLinkedChest().addMenuItem(InventoryMenuAPI.createItemDynamic()
                    .setName(cp -> "&a&lReset Field")
                    .setDisplayItem(InventoryMenuUtils.createCustomItem(Material.BAKED_POTATO, 6))
                    .setDescription("")
                    .setAction(cp -> cp.getBattle().reset()));

            options.getLinkedChest().addMenuItem(InventoryMenuAPI.createItemDynamic()
                    .setName(cp -> "&a&lRegeneration Speed")
                    .setDisplayItem(InventoryMenuUtils.createCustomItem(Material.BAKED_POTATO, 4))
                    .setDescription(cp -> {
                        PowerTrainingBattle battle = (PowerTrainingBattle) cp.getBattle();
                        String str = (battle.getRegenSpeed() == 0 ? ChatColor.GREEN : ChatColor.GRAY) + "Normal (1x)\n" +
                                (battle.getRegenSpeed() == 1 ? ChatColor.GREEN : ChatColor.GRAY) + "Fast (2x)\n" +
                                (battle.getRegenSpeed() == 2 ? ChatColor.GREEN : ChatColor.GRAY) + "Faster (4x)\n" +
                                (battle.getRegenSpeed() == 3 ? ChatColor.GREEN : ChatColor.GRAY) + "Fastest (8x)";
                        return str;
                    })
                    .setAction(cp -> ((PowerTrainingBattle) cp.getBattle()).nextRegenSpeed())
                    .setCloseOnAction(false));

            options.getLinkedChest().addMenuItem(InventoryMenuAPI.createItemDynamic()
                    .setName(cp -> "&a&lLeave Match")
                    .setDisplayItem(InventoryMenuUtils.createCustomItem(Material.BAKED_POTATO, 5))
                    .setDescription("")
                    .setAction(cp -> cp.getBattle().leavePlayer(cp)));
        }

    }

}
