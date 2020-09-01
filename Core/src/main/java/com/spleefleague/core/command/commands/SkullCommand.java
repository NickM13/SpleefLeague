/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.command.commands;

import com.spleefleague.core.command.annotation.CommandAnnotation;
import com.spleefleague.core.command.CoreCommand;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.rank.Rank;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.SkullMeta;

/**
 * @author NickM13
 */
public class SkullCommand extends CoreCommand {
    
    public SkullCommand() {
        super("skull", Rank.SENIOR_MODERATOR);
        setUsage("/skull <player>");
        setDescription("Get head of a player");
    }
    
    @CommandAnnotation
    public void skull(CorePlayer cp, OfflinePlayer op) {
        PlayerInventory inventory = cp.getPlayer().getInventory();
        ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta skullmeta = (SkullMeta)skull.getItemMeta();
        skullmeta.setOwningPlayer(op);
        skull.setItemMeta(skullmeta);
        inventory.addItem(skull);
    }

}
