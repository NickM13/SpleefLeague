package com.spleefleague.core.command.commands;

import com.spleefleague.core.command.CommandTemplate;
import com.spleefleague.core.command.annotation.CommandAnnotation;
import com.spleefleague.core.command.annotation.LiteralArg;
import com.spleefleague.core.command.annotation.OptionArg;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.collectible.hat.Hat;
import com.spleefleague.core.player.rank.Rank;
import com.spleefleague.core.vendor.Vendorables;
import org.bukkit.command.CommandSender;

/**
 * @author NickM13
 * @since 4/20/2020
 */
public class HatCommand extends CommandTemplate {
    
    public HatCommand() {
        super(HatCommand.class, "hat", Rank.DEVELOPER);
        setOptions("hats", (cp) -> Vendorables.getAll(Hat.class).keySet());
    }
    
    @CommandAnnotation
    public void hatAdd(CommandSender sender,
            @LiteralArg("add") String l,
            CorePlayer target,
            @OptionArg(listName="hats") String identifier) {
        Hat hat = Vendorables.get(Hat.class, identifier);
        if (hat == null) {
            error(sender, "Invalid hat " + identifier);
        } else {
            if (target.getCollectibles().contains(hat)) {
                error(sender, target.getName() + " already has the hat " + identifier);
            } else {
                target.getCollectibles().add(hat);
                success(sender, "Hat " + identifier + " added to " + target.getName() + "'s collection");
            }
        }
    }
    
    @CommandAnnotation
    public void hatRemove(CommandSender sender,
            @LiteralArg("remove") String l,
            CorePlayer target,
            @OptionArg(listName="hats") String identifier) {
        Hat hat = Vendorables.get(Hat.class, identifier);
        if (hat == null) {
            error(sender, "Invalid hat " + identifier);
        } else {
            if (target.getCollectibles().contains(hat)) {
                target.getCollectibles().remove(hat);
                success(sender, "Hat " + identifier + " removed from " + target.getName() + "'s collection");
            } else {
                error(sender, target.getName() + " does not have the hat " + identifier);
            }
        }
    }
    
}
