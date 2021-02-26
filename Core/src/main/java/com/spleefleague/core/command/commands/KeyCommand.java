/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.command.commands;

import com.spleefleague.core.Core;
import com.spleefleague.core.command.HoldableCommand;
import com.spleefleague.core.command.annotation.*;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.collectible.Collectible;
import com.spleefleague.core.player.collectible.key.Key;
import com.spleefleague.core.player.rank.CoreRank;
import com.spleefleague.core.vendor.Vendorables;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;

import java.util.List;

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

    /**
     * Adds a collectible to the players collection
     *
     * @param sender     Sender
     * @param l          add
     * @param target     Target
     * @param identifier Collectible Identifier
     */
    @CommandAnnotation
    @Override
    public void collectibleAdd(CommandSender sender,
                               @LiteralArg("add") String l,
                               CorePlayer target,
                               @OptionArg(listName = "collectibles") String identifier) {
        Key key = Vendorables.get(Key.class, identifier);
        if (target.getCollectibles().add(key)) {
            sender.sendMessage("Added collectible " + identifier + " to " + target.getDisplayNamePossessive() + " collection");
            target.getPlayer().playSound(target.getPlayer().getLocation(), Sound.BLOCK_BREWING_STAND_BREW, 1.f, 0.5f);
            Core.getInstance().sendMessage(target,
                    ChatColor.GRAY + "You've received " + key.getDisplayName() + ChatColor.GRAY + "!");
        } else {
            sender.sendMessage(target.getDisplayName() + " already had " + identifier);
        }
    }

    /**
     * Adds a collectible to the players collection
     *
     * @param sender     Sender
     * @param l          add
     * @param targets    Targets
     * @param identifier Collectible Identifier
     */
    @CommandAnnotation
    public void collectibleAdds(CommandSender sender,
                                @LiteralArg("add") String l,
                                List<CorePlayer> targets,
                                @OptionArg(listName = "collectibles") String identifier) {
        Collectible collectible = Vendorables.get(Key.class, identifier);
        for (CorePlayer target : targets) {
            if (target.getCollectibles().add(collectible)) {
                sender.sendMessage("Added collectible " + identifier + " to " + target.getDisplayNamePossessive() + " collection");
                target.getPlayer().playSound(target.getPlayer().getLocation(), Sound.BLOCK_BREWING_STAND_BREW, 1.f, 0.5f);
                Core.getInstance().sendMessage(target,
                        ChatColor.GRAY + "You've received " + collectible.getDisplayName() + ChatColor.GRAY + "!");
            } else {
                sender.sendMessage(target.getDisplayName() + " already had " + identifier);
            }
        }
    }

}
