/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.command.commands;

import com.spleefleague.core.command.CoreCommand;
import com.spleefleague.core.command.annotation.CommandAnnotation;
import com.spleefleague.core.command.annotation.EnumArg;
import com.spleefleague.core.command.annotation.HelperArg;
import com.spleefleague.core.menu.InventoryMenuUtils;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.rank.Rank;
import org.bukkit.Material;

import javax.annotation.Nullable;

/**
 * @author NickM13
 */
public class JecthCommand extends CoreCommand {

    public JecthCommand() {
        super("jecth", Rank.DEVELOPER);
    }
    
    @CommandAnnotation
    public void jecth(CorePlayer sender,
                      @EnumArg Material material,
                      @HelperArg("<cmdStart") Integer start,
                      @Nullable @HelperArg("[cmdForward]=1") Integer forward) {
        if (forward == null) {
            forward = 1;
        }
        for (int i = 0; i < forward; i++) {
            sender.getPlayer().getInventory().addItem(InventoryMenuUtils.createCustomItem(material, start + i));
        }
    }

}
