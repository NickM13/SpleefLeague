/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.command.commands;

import com.spleefleague.core.command.annotation.CommandAnnotation;
import com.spleefleague.core.command.CommandTemplate;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.Rank;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

/**
 * @author NickM13
 */
public class ColorArmorCommand extends CommandTemplate {
    
    public ColorArmorCommand() {
        super(ColorArmorCommand.class, "colorarmor", Rank.SENIOR_MODERATOR);
        setUsage("/colorarmor <r=0-255> <g=0-255> <b=0-255>");
        setDescription("¯\\_(ツ)_/¯");
    }
    
    @CommandAnnotation
    public void colorarmor(CorePlayer sender, Integer r, Integer g, Integer b) {
        ItemStack itemStack = sender.getPlayer().getInventory().getItemInMainHand();
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta instanceof LeatherArmorMeta) {
            ((LeatherArmorMeta) itemMeta).setColor(org.bukkit.Color.fromRGB(r, g, b));
            itemStack.setItemMeta(itemMeta);
            success(sender, "Color applied");
        } else {
            error(sender, "Must be holding a leather armor piece");
        }
    }

}
