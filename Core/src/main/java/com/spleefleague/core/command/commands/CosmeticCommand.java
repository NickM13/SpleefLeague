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
import com.spleefleague.core.player.collectible.armor.CosmeticArmor;

/**
 * @author NickM13
 */
public class CosmeticCommand extends CommandTemplate {
    
    public CosmeticCommand() {
        super(CosmeticCommand.class, "cosmetic", Rank.DEVELOPER);
        setUsage("/cosmetic <name>");
        setDescription("¯\\_(ツ)_/¯");
    }
    
    @CommandAnnotation
    public void cosmetic(CorePlayer sender) {
        success(sender, "Cosmetics: ");
        for (String armor : CosmeticArmor.getAll().keySet()) {
            sender.sendMessage(armor);
        }
    }
    
    @CommandAnnotation
    public void cosmetic(CorePlayer sender, String name) {
        CosmeticArmor armor = CosmeticArmor.getArmor(name);
        if (armor != null) {
            sender.getPlayer().getInventory().setItem(armor.getArmorSlot().toSlot(), armor.getItem());
            sender.updateArmor();
        }
    }
    
}
