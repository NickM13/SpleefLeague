/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.command.commands;

import com.spleefleague.core.command.annotation.CommandAnnotation;
import com.spleefleague.core.command.annotation.HelperArg;
import com.spleefleague.core.command.annotation.LiteralArg;
import com.spleefleague.core.command.annotation.OptionArg;
import com.spleefleague.core.command.CommandTemplate;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.collectible.Holdable;
import com.spleefleague.core.player.rank.Rank;
import com.spleefleague.core.player.collectible.key.Key;
import java.util.List;

import com.spleefleague.core.vendor.Vendorables;
import org.bukkit.command.CommandSender;

/**
 * @author NickM13
 */
public class KeyCommand extends CommandTemplate {
    
    public KeyCommand() {
        super(KeyCommand.class, "key", Rank.DEVELOPER);
        setUsage("/key");
        setOptions("keys", (cp) -> Vendorables.getAll(Key.class).keySet());
        //setOptions("itemTypes", (cp) -> VendorItem.getItemTypes());
    }
    
    @CommandAnnotation
    public void keyGet(CorePlayer sender,
            @LiteralArg("get") String l,
            @OptionArg(listName="keys") String identifier) {
        if (Vendorables.contains(Key.class, identifier)) {
            sender.getPlayer().getInventory().addItem(Vendorables.get(Key.class, identifier).getDisplayItem());
            success(sender, "Given copy of display item for key " + identifier);
        } else {
            error(sender, "Key not found " + identifier);
        }
    }
    
    @CommandAnnotation
    public void keyCreate(CorePlayer sender,
            @LiteralArg(value="create") String l,
            @HelperArg(value="<identifier>") String identifier,
            @HelperArg(value="<damage>") Integer damage,
            @HelperArg(value="<displayName>") String displayName) {
        if (Vendorables.contains(Key.class, identifier)) {
            error(sender, "That key already exists!");
        } else {
            Key keyItem = Key.createKeyItem(identifier, displayName, damage);
            sender.getPlayer().getInventory().addItem(keyItem.getDisplayItem());
            success(sender, "Created key {" + identifier + ", " + displayName + ", " + damage + "}");
        }
    }
    
    @CommandAnnotation
    public void keyEditRename(CorePlayer sender,
            @LiteralArg("edit") String e,
            @LiteralArg(value="rename") String l,
            @OptionArg(listName="keys") String identifier,
            @HelperArg(value="<displayName>") String displayName) {
        Key key = Vendorables.get(Key.class, identifier);
        if (key != null) {
            String prevName = key.getName();
            key.setName(displayName);
            success(sender, "Renamed key " + identifier + " from " + prevName + " to " + displayName);
        }
    }
    
    @CommandAnnotation
    public void keyEditDamage(CorePlayer sender,
            @LiteralArg("edit") String e,
            @LiteralArg(value="damage") String l,
            @OptionArg(listName="keys") String identifier,
            @HelperArg(value="<damage>") Integer damage) {
        Key key = Vendorables.get(Key.class, identifier);
        if (key != null) {
            Integer prevDamage = key.getDamageNbt();
            key.setDamageNbt(damage);
            success(sender, "Changed damage value of key " + identifier + " from " + prevDamage + " to " + damage);
        }
    }
    
    /**
     * Adds a key to the players collection
     *
     * @param sender Sender
     * @param l add
     * @param target Target
     * @param identifier Key Identifier
     */
    @CommandAnnotation
    public void keyAdd(CommandSender sender,
            @LiteralArg(value="add") String l,
            CorePlayer target,
            @OptionArg(listName="keys") String identifier) {
        target.getCollectibles().add(Vendorables.get(Key.class, identifier));
    }
    
    /**
     * Removes a key from a players collection
     *
     * @param sender Sender
     * @param l remove
     * @param target Target
     * @param identifier Key Identifier
     */
    @CommandAnnotation
    public void keyRemove(CommandSender sender,
            @LiteralArg(value="remove") String l,
            CorePlayer target,
            @OptionArg(listName="keys") String identifier) {
        target.getCollectibles().remove(Vendorables.get(Key.class, identifier));
    }
    
    /**
     * Returns whether a player in the list is holding a key
     *
     * @param sender Command Sender
     * @param l holding
     * @param targets Target List
     * @param identifier Key Identifier
     * @return Success
     */
    @CommandAnnotation
    public boolean keyHolding(CommandSender sender,
            @LiteralArg(value="holding") String l,
            List<CorePlayer> targets,
            @OptionArg(listName="keys") String identifier) {
        Key keyMatch = Vendorables.get(Key.class, identifier);
        if (keyMatch == null) {
            sender.sendMessage("Key not valid " + identifier);
            return false;
        }
        for (CorePlayer target : targets) {
            Holdable heldItem = target.getCollectibles().getHeldItem();
            if (heldItem != null && heldItem.equalsSoft(keyMatch)) {
                return true;
            }
        }
        return false;
    }
    
}
