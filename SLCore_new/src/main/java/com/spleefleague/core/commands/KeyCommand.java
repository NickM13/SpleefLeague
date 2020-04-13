/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.commands;

import com.spleefleague.core.command.CommandAnnotation;
import com.spleefleague.core.command.HelperArg;
import com.spleefleague.core.command.LiteralArg;
import com.spleefleague.core.command.OptionArg;
import com.spleefleague.core.command.CommandTemplate;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.Rank;
import com.spleefleague.core.vendor.KeyItem;
import java.util.List;
import org.bukkit.command.CommandSender;

/**
 * @author NickM13
 */
public class KeyCommand extends CommandTemplate {
    
    public KeyCommand() {
        super(KeyCommand.class, "key", Rank.DEVELOPER);
        setUsage("/key");
        setOptions("keys", (cp) -> KeyItem.getKeyItemNames());
        //setOptions("itemTypes", (cp) -> VendorItem.getItemTypes());
    }
    
    @CommandAnnotation
    public void keyCreate(CorePlayer sender,
            @LiteralArg(value="create") String l,
            @HelperArg(value="<identifierName>") String name,
            @HelperArg(value="<damage>") Integer damage,
            @HelperArg(value="<displayName>") String displayName) {
        KeyItem keyItem = KeyItem.createKeyItem(name, damage, displayName);
        if (keyItem != null) {
            sender.getPlayer().getInventory().setItemInMainHand(keyItem.getItem());
        }
    }
    
    @CommandAnnotation
    public void keyRename(CorePlayer sender,
            @LiteralArg(value="rename") String l,
            @OptionArg(listName="keys") String key,
            @HelperArg(value="<displayName>") String displayName) {
        KeyItem.getKeyItem(key).setDisplayName(displayName);
    }
    
    @CommandAnnotation
    public void keyDamage(CorePlayer sender,
            @LiteralArg(value="damage") String l,
            @OptionArg(listName="keys") String key,
            @HelperArg(value="<damage>") Integer damage) {
        KeyItem.getKeyItem(key).setDamage(damage);
    }
    
    @CommandAnnotation
    public void keyUnlock(CommandSender sender,
            @LiteralArg(value="unlock") String l,
            CorePlayer target,
            @OptionArg(listName="keys") String key) {
        target.addKey(KeyItem.getKeyItem(key));
    }
    
    @CommandAnnotation
    public void keyLock(CommandSender sender,
            @LiteralArg(value="lock") String l,
            CorePlayer target,
            @OptionArg(listName="keys") String key) {
        target.removeKey(KeyItem.getKeyItem(key));
    }
    
    @CommandAnnotation
    public boolean keyHolding(CommandSender sender,
            @LiteralArg(value="holding") String l,
            List<CorePlayer> targets,
            @OptionArg(listName="keys") String key) {
        //KeyItem keyItem = KeyItem.getKeyItem(target.getHeldItem().getItem());
        for (CorePlayer target : targets) {
            KeyItem keyItem = KeyItem.getKeyItem(target.getPlayer().getInventory().getItemInMainHand());
            if (keyItem != null) {
                return (keyItem.getKeyName().equalsIgnoreCase(key));
            }
        }
        return false;
    }
    
}
