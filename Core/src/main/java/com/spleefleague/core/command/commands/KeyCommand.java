/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.command.commands;

import com.spleefleague.core.command.HoldableCommand;
import com.spleefleague.core.command.annotation.*;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.collectible.Collectible;
import com.spleefleague.core.player.collectible.key.Key;
import com.spleefleague.core.player.rank.CoreRank;
import com.spleefleague.core.vendor.Vendorables;
import org.bukkit.Material;

/**
 * @author NickM13
 */
public class KeyCommand extends HoldableCommand {

    public KeyCommand() {
        super(Key.class, "key", CoreRank.DEVELOPER);
    }


    @CommandAnnotation
    public void collectibleSetItem(CorePlayer sender,
                                    @LiteralArg("set") String e,
                                    @LiteralArg("item") String l,
                                    @OptionArg(listName = "collectibles") String identifier,
                                    @EnumArg Material material) {
        Key key = Vendorables.get(Key.class, identifier);
        if (key != null) {
            Material prevMaterial = key.getMaterial();
            key.setMaterial(material);
            success(sender, "Changed customModelData value of " + key.getIdentifier() + " from " + prevMaterial + " to " + material);
            sender.setHeldItem(key.getDisplayItem());
        } else {
            error(sender, "That's not a registered key!");
        }
    }

}
