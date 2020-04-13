/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.spleef.commands;

import com.spleefleague.core.Core;
import com.spleefleague.core.command.CommandAnnotation;
import com.spleefleague.core.command.HelperArg;
import com.spleefleague.core.command.LiteralArg;
import com.spleefleague.core.command.OptionArg;
import com.spleefleague.core.chat.Chat;
import com.spleefleague.core.command.CommandTemplate;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.Rank;
import com.spleefleague.core.request.ConsoleRequest;
import com.spleefleague.core.request.Request;
import com.spleefleague.core.request.RequestManager;
import com.spleefleague.spleef.Spleef;
import com.spleefleague.spleef.game.Shovel;
import com.spleefleague.spleef.player.SpleefPlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;

/**
 * @author NickM13
 */
public class ShovelCommand extends CommandTemplate {
    
    public ShovelCommand() {
        super(ShovelCommand.class, "shovel", Rank.DEVELOPER);
        setUsage("/shovel");
        setOptions("shovelTypes", (cp) -> Shovel.getShovelTypes());
    }
    
    @CommandAnnotation
    public void shovel(CorePlayer sender) {
        sender.sendMessage(Chat.fillTitle("[ Shovel Commands ]"));
        sender.sendMessage(Chat.DEFAULT + "/shovel create <damage>: Creates a Spleef Shovel");
        sender.sendMessage(Chat.DEFAULT + "/shovel name <name>: Sets the name of a shovel");
        sender.sendMessage(Chat.DEFAULT + "/shovel description <desc>: ^ for description");
        sender.sendMessage(Chat.DEFAULT + "/shovel type <type>: ^ for type");
        sender.sendMessage(Chat.DEFAULT + "/shovel cost <coins>: ^ for coins");
        sender.sendMessage(Chat.DEFAULT + "/shovel unlock <player> <damage>: Unlock shovel for player");
        sender.sendMessage(Chat.DEFAULT + "/shovel lock <player> <damage>: Lock shovel for player");
    }
    
    @CommandAnnotation
    public void shovelCreate(CorePlayer sender,
            @LiteralArg(value="create") String l,
            @HelperArg(value="<damage>") Integer id) {
        if (Shovel.createShovel(id)) {
            sender.getPlayer().getInventory().addItem(Shovel.getShovel(id).getItem());
        } else {
            error(sender, "Shovel already exists!");
        }
    }
    
    @CommandAnnotation
    public void shovelDestroy(CorePlayer sender,
            @LiteralArg(value="destroy") String l,
            @HelperArg(value="<damage>") Integer id) {
        if (Shovel.getShovel(id) != null) {
            RequestManager.sendRequest(Core.getChatPrefix(), "Do you want to destroy shovel " + id + "?", sender, "shoveldestroy", new ConsoleRequest((r, s) -> {
                Shovel.destroyShovel(id);
            }));
        } else {
            error(sender, "Shovel doesn't exist!");
        }
    }
    
    @CommandAnnotation
    public void shovelName(CorePlayer sender,
            @LiteralArg(value="name") String l,
            @HelperArg(value="<displayName>") String displayName) {
        displayName = Chat.colorize(displayName);
        ItemStack item = sender.getPlayer().getInventory().getItemInMainHand();
        if (Shovel.isShovel(item)) {
            Shovel shovel = Shovel.getShovel(((Damageable) item.getItemMeta()).getDamage());
            shovel.setDisplayName(displayName);
            Shovel.save(shovel);
            sender.getPlayer().getInventory().setItemInMainHand(shovel.getItem());
            success(sender, "Set name to: " + displayName);
        } else {
            error(sender, "The item you're holding is not a valid shovel!");
        }
    }
    
    @CommandAnnotation
    public void shovelDescription(CorePlayer sender,
            @LiteralArg(value="description") String l,
            @HelperArg(value="<description>") String description) {
        ItemStack item = sender.getPlayer().getInventory().getItemInMainHand();
        description = Chat.colorize(description);
        if (Shovel.isShovel(item)) {
            Shovel shovel = Shovel.getShovel(((Damageable) item.getItemMeta()).getDamage());
            shovel.setDescription(description);
            Shovel.save(shovel);
            sender.getPlayer().getInventory().setItemInMainHand(shovel.getItem());
            success(sender, "Description set to: ");
            sender.sendMessage(description);
        } else {
            error(sender, "The item you're holding is not a valid shovel!");
        }
    }
    
    @CommandAnnotation
    public void shovelType(CorePlayer sender,
            @LiteralArg(value="type") String l,
            @OptionArg(listName="shovelTypes") String type) {
        ItemStack item = sender.getPlayer().getInventory().getItemInMainHand();
        if (Shovel.isShovel(item)) {
            Shovel shovel = Shovel.getShovel(((Damageable) item.getItemMeta()).getDamage());
            shovel.setShovelType(type);
            Shovel.save(shovel);
            sender.getPlayer().getInventory().setItemInMainHand(shovel.getItem());
            success(sender, "Shovel type set to: " + type);
        } else {
            error(sender, "The item you're holding is not a valid shovel!");
        }
    }
    
    @CommandAnnotation
    public void shovelCoins(CorePlayer sender,
            @LiteralArg(value="cost") String l,
            @HelperArg(value="<coins>") Integer coins) {
        ItemStack item = sender.getPlayer().getInventory().getItemInMainHand();
        if (Shovel.isShovel(item)) {
            Shovel shovel = Shovel.getShovel(((Damageable) item.getItemMeta()).getDamage());
            shovel.setCoinCost(coins);
            Shovel.save(shovel);
            sender.getPlayer().getInventory().setItemInMainHand(shovel.getItem());
            success(sender, "Coin cost set to: " + coins);
        } else {
            error(sender, "The item you're holding is not a valid shovel!");
        }
    }
    
    @CommandAnnotation
    public void shovelUnlock(CorePlayer sender,
            @LiteralArg(value="unlock") String l,
            CorePlayer cp,
            @HelperArg(value="<damage>") Integer id) {
        SpleefPlayer sp = Spleef.getInstance().getPlayers().get(cp);
        switch(sp.addShovel(id)) {
            case 0:
                success(sender, "Unlocked shovel "
                        + Shovel.getShovelName(id)
                        + Chat.DEFAULT + " for " + cp.getDisplayName());
                break;
            case 1:
                error(sender, cp.getDisplayName()
                        + " already owns the shovel "
                        + Shovel.getShovelName(id)
                        + Chat.DEFAULT + "!");
                break;
            case 2:
                error(sender, "That shovel is a default!");
            case 3:
                error(sender, "No shovel found with damage value of " + id + "!");
                break;
            default: break;
        }
    }
    
    @CommandAnnotation
    public void shovelLock(CorePlayer sender,
            @LiteralArg(value="lock") String l,
            CorePlayer cp,
            @HelperArg(value="<damage>") Integer id) {
        SpleefPlayer sp = Spleef.getInstance().getPlayers().get(cp);
        if (sp.removeShovel(id)) {
            success(sender, "Removed shovel " + Shovel.getShovelName(id) + " from " + cp.getDisplayName());
        }
    }

}
