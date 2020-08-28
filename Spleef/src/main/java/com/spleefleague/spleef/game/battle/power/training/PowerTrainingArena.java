/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.spleef.game.battle.power.training;

import com.spleefleague.core.game.arena.Arenas;
import com.spleefleague.core.game.leaderboard.LeaderboardCollection;
import com.spleefleague.core.game.leaderboard.Leaderboards;
import com.spleefleague.core.menu.InventoryMenuAPI;
import com.spleefleague.core.menu.InventoryMenuItem;
import com.spleefleague.core.menu.InventoryMenuItemHotbar;
import com.spleefleague.core.menu.hotbars.main.LeaderboardMenu;
import com.spleefleague.core.player.BattleState;
import com.spleefleague.core.world.build.BuildStructure;
import com.spleefleague.core.world.build.BuildStructures;
import com.spleefleague.spleef.Spleef;
import com.spleefleague.spleef.game.SpleefMode;
import com.spleefleague.spleef.game.battle.power.PowerSpleefPlayer;
import com.spleefleague.spleef.game.battle.power.ability.Ability;
import com.spleefleague.spleef.game.battle.power.ability.abilities.AbilityMobility;
import com.spleefleague.spleef.game.battle.power.ability.abilities.AbilityOffensive;
import com.spleefleague.spleef.game.battle.power.ability.abilities.AbilityUtility;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * @author NickM13
 */
public class PowerTrainingArena {

    private static final String mainColor = ChatColor.AQUA + "" + ChatColor.BOLD;

    private static InventoryMenuItem menuItem;

    public static InventoryMenuItem createMenu() {
        if (menuItem == null) {
            menuItem = InventoryMenuAPI.createItem()
                    .setName("&6&lTraining Field")
                    .setDescription("!")
                    .setDisplayItem(Material.GOLDEN_SHOVEL, 32)
                    .setAction(cp -> Spleef.getInstance().queuePlayer(SpleefMode.POWER_TRAINING.getBattleMode(), cp));

            InventoryMenuItemHotbar options = (InventoryMenuItemHotbar) InventoryMenuAPI.createItemHotbar(8, "pstOptions")
                    .setName(cp -> "&a&lTraining Options")
                    .setDisplayItem(cp -> ((PowerSpleefPlayer) cp.getBattle().getBattler(cp)).getMobility().getDisplayItem())
                    .setDescription("Various training options for Power Spleef Training mode")
                    .setAvailability(cp -> cp.getBattleState() == BattleState.BATTLER &&
                            cp.getBattle() instanceof PowerTrainingBattle &&
                            cp.getBattle().getBattler(cp) != null &&
                            ((PowerSpleefPlayer) cp.getBattle().getBattler(cp)).getMobility() != null)
                    .setVisibility(cp -> ((PowerSpleefPlayer) cp.getBattle().getBattler(cp)).getMobility() != null)
                    .createLinkedContainer("Training Options");

            options.getLinkedChest().addMenuItem(InventoryMenuAPI.createItem()
                    .setName(cp -> "&a&lTraining Field")
                    .setDisplayItem(cp -> ((PowerSpleefPlayer) cp.getBattle().getBattler(cp)).getMobility().getDisplayItem())
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

            options.getLinkedChest().addMenuItem(InventoryMenuAPI.createItem()
                    .setName(cp -> "&a&lPower Cooldowns")
                    .setDisplayItem(cp -> ((PowerSpleefPlayer) cp.getBattle().getBattler(cp)).getMobility().getDisplayItem())
                    .setDescription(cp -> {
                        PowerTrainingBattle battle = (PowerTrainingBattle) cp.getBattle();
                        return "Cooldowns are currently " + (battle.isCooldownEnabled() ? ChatColor.GREEN + "enabled" : ChatColor.GRAY + "disabled");
                    })
                    .setAction(cp -> ((PowerTrainingBattle) cp.getBattle()).setCooldownEnabled(!((PowerTrainingBattle) cp.getBattle()).isCooldownEnabled()))
                    .setCloseOnAction(false));

            options.getLinkedChest().addMenuItem(InventoryMenuAPI.createItem()
                    .setName(cp -> "&a&lReset Field")
                    .setDisplayItem(cp -> ((PowerSpleefPlayer) cp.getBattle().getBattler(cp)).getMobility().getDisplayItem())
                    .setDescription("")
                    .setAction(cp -> cp.getBattle().reset()));

            options.getLinkedChest().addMenuItem(InventoryMenuAPI.createItem()
                    .setName(cp -> "&a&lRegeneration Speed")
                    .setDisplayItem(cp -> ((PowerSpleefPlayer) cp.getBattle().getBattler(cp)).getMobility().getDisplayItem())
                    .setDescription(cp -> {
                        PowerTrainingBattle battle = (PowerTrainingBattle) cp.getBattle();
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append((battle.getRegenSpeed() == 0 ? ChatColor.GREEN : ChatColor.GRAY) + "Normal (1x)\n");
                        stringBuilder.append((battle.getRegenSpeed() == 1 ? ChatColor.GREEN : ChatColor.GRAY) + "Fast (2x)\n");
                        stringBuilder.append((battle.getRegenSpeed() == 2 ? ChatColor.GREEN : ChatColor.GRAY) + "Faster (4x)\n");
                        stringBuilder.append((battle.getRegenSpeed() == 3 ? ChatColor.GREEN : ChatColor.GRAY) + "Fastest (8x)");
                        return stringBuilder.toString();
                    })
                    .setAction(cp -> ((PowerTrainingBattle) cp.getBattle()).nextRegenSpeed())
                    .setCloseOnAction(false));

            options.getLinkedChest().addMenuItem(InventoryMenuAPI.createItem()
                    .setName(cp -> "&a&lLeave Match")
                    .setDisplayItem(cp -> ((PowerSpleefPlayer) cp.getBattle().getBattler(cp)).getMobility().getDisplayItem())
                    .setDescription("")
                    .setAction(cp -> cp.getBattle().leavePlayer(cp)));
        }

        return menuItem;
    }

}
