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
    public void keyCreate(CorePlayer sender,
            @LiteralArg(value="create") String l,
            @HelperArg(value="<identifierName>") String name,
            @HelperArg(value="<damage>") Integer damage,
            @HelperArg(value="<displayName>") String displayName) {
        //Key keyItem = Key.createKeyItem(name, damage, displayName);
        //if (keyItem != null) {
            //sender.getPlayer().getInventory().setItemInMainHand(keyItem.createItem());
        //}
    }
    
    @CommandAnnotation
    public void keyRename(CorePlayer sender,
            @LiteralArg(value="rename") String l,
            @OptionArg(listName="keys") String key,
            @HelperArg(value="<displayName>") String displayName) {
        //Key.getKeyItem(key).setDisplayName(displayName);
    }
    
    @CommandAnnotation
    public void keyDamage(CorePlayer sender,
            @LiteralArg(value="damage") String l,
            @OptionArg(listName="keys") String key,
            @HelperArg(value="<damage>") Integer damage) {
        //Key.getKeyItem(key).setDamage(damage);
    }
    
    @CommandAnnotation
    public void keyUnlock(CommandSender sender,
            @LiteralArg(value="unlock") String l,
            CorePlayer target,
            @OptionArg(listName="keys") String key) {
        //target.addKey(Key.getKeyItem(key));
    }
    
    @CommandAnnotation
    public void keyLock(CommandSender sender,
            @LiteralArg(value="lock") String l,
            CorePlayer target,
            @OptionArg(listName="keys") String key) {
        //target.removeKey(Key.getKeyItem(key));
    }
    
    @CommandAnnotation
    public boolean keyHolding(CommandSender sender,
            @LiteralArg(value="holding") String l,
            List<CorePlayer> targets,
            @OptionArg(listName="keys") String key) {
        //KeyItem keyItem = KeyItem.getKeyItem(target.getHeldItem().getItem());
        for (CorePlayer target : targets) {
            //Key keyItem = Key.getKeyItem(target.getPlayer().getInventory().getItemInMainHand());
            //if (keyItem != null) {
                //return (keyItem.getIdentifier().equalsIgnoreCase(key));
            //}
        }
        return false;
    }
    
}
