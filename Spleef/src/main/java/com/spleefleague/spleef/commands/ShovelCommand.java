/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.spleef.commands;

import com.spleefleague.core.Core;
import com.spleefleague.core.chat.Chat;
import com.spleefleague.core.chat.ChatUtils;
import com.spleefleague.core.command.CommandTemplate;
import com.spleefleague.core.command.annotation.*;
import com.spleefleague.core.command.error.CoreError;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.rank.Rank;
import com.spleefleague.core.request.ConsoleRequest;
import com.spleefleague.core.request.RequestManager;
import com.spleefleague.core.vendor.Vendorable;
import com.spleefleague.core.vendor.Vendorables;
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
        sender.sendMessage(ChatUtils.centerTitle("[ Shovel Commands ]"));
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
            sender.getPlayer().getInventory().addItem(Vendorables.get(Shovel.class, "" + id).getDisplayItem());
        } else {
            error(sender, "Shovel already exists!");
        }
    }
    
    @CommandAnnotation
    public void shovelDestroy(CorePlayer sender,
            @LiteralArg(value="destroy") String l,
            @HelperArg(value="<damage>") Integer id) {
        if (Vendorables.get(Shovel.class, "" + id) != null) {
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
        Vendorable vendorable = Vendorables.get(item);
        if (vendorable instanceof Shovel) {
            Shovel shovel = (Shovel) vendorable;
            //shovel.setDisplayName(displayName);
            Shovel.save(shovel);
            //sender.getPlayer().getInventory().setItemInMainHand(shovel.getItem());
            success(sender, "Set name to: " + displayName);
        } else {
            error(sender, "The item you're holding is not a valid shovel!");
        }
        error(sender, CoreError.SETUP);
    }
    
    @CommandAnnotation
    public void shovelDescription(CorePlayer sender,
            @LiteralArg(value="description") String l,
            @HelperArg(value="<description>") String description) {
        ItemStack item = sender.getPlayer().getInventory().getItemInMainHand();
        description = Chat.colorize(description);
        Vendorable vendorable = Vendorables.get(item);
        if (vendorable instanceof Shovel) {
            Shovel shovel = (Shovel) vendorable;
            //shovel.setDescription(description);
            Shovel.save(shovel);
            //sender.getPlayer().getInventory().setItemInMainHand(shovel.getItem());
            success(sender, "Description set to: ");
            sender.sendMessage(description);
        } else {
            error(sender, "The item you're holding is not a valid shovel!");
        }
        error(sender, CoreError.SETUP);
    }
    
    @CommandAnnotation
    public void shovelType(CorePlayer sender,
            @LiteralArg(value="type") String l,
            @OptionArg(listName="shovelTypes") String type) {
        ItemStack item = sender.getPlayer().getInventory().getItemInMainHand();
        Vendorable vendorable = Vendorables.get(item);
        if (vendorable instanceof Shovel) {
            Shovel shovel = (Shovel) vendorable;
            shovel.setShovelType(type);
            Shovel.save(shovel);
            sender.getPlayer().getInventory().setItemInMainHand(shovel.getDisplayItem());
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
        Vendorable vendorable = Vendorables.get(item);
        if (vendorable instanceof Shovel) {
            Shovel shovel = (Shovel) vendorable;
            //shovel.setCoinCost(coins);
            Shovel.save(shovel);
            //sender.getPlayer().getInventory().setItemInMainHand(shovel.getItem());
            success(sender, "Coin cost set to: " + coins);
        } else {
            error(sender, "The item you're holding is not a valid shovel!");
        }
    }
    
    /**
     * Adds a shovel to a player's collection
     *
     * @param sender Core Player
     * @param l "add"
     * @param cp Core Player
     * @param id Shovel ID
     */
    @CommandAnnotation
    public void shovelAdd(CorePlayer sender,
            @LiteralArg(value="add") String l,
            CorePlayer cp,
            @HelperArg(value="<damage>") Integer id) {
        SpleefPlayer sp = Spleef.getInstance().getPlayers().get(cp);
        Vendorable vendorable = Vendorables.get(Shovel.class, "" + id);
        if (vendorable instanceof Shovel) {
            Shovel shovel = (Shovel) vendorable;
            if (shovel.isDefault()) {
                error(sender, "That shovel is a default!");
            } else if (cp.getCollectibles().contains(shovel)) {
                error(sender, cp.getDisplayName()
                        + " already owns the shovel "
                        + shovel.getName()
                        + Chat.DEFAULT + "!");
            } else {
                cp.getCollectibles().add(shovel);
                success(sender, "Unlocked shovel "
                        + shovel.getName()
                        + Chat.DEFAULT + " for " + cp.getDisplayName());
            }
        } else {
            error(sender, "No shovel found with damage value of " + id + "!");
        }
    }
    
    /**
     * For removing a shovel from a player's collection
     *
     * @param sender Core Player
     * @param l "remove"
     * @param cp Core Player
     * @param id Shovel ID
     */
    @CommandAnnotation
    public void shovelRemove(CorePlayer sender,
            @LiteralArg(value="remove") String l,
            CorePlayer cp,
            @HelperArg(value="<damage>") Integer id) {
        SpleefPlayer sp = Spleef.getInstance().getPlayers().get(cp);
        Vendorable vendorable = Vendorables.get(Shovel.class, "" + id);
        if (vendorable instanceof Shovel) {
            Shovel shovel = (Shovel) vendorable;
            if (cp.getCollectibles().contains(shovel)) {
                cp.getCollectibles().remove(shovel);
                success(sender, "Removed shovel " + shovel.getName() + " from " + cp.getDisplayName());
            } else {
                error(sender, "They don't have the shovel " + shovel.getName());
            }
        } else {
            error(sender, "No shovel found with damage value of " + id + "!");
        }
    }

}
