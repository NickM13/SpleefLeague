/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.command.commands;

import com.spleefleague.core.Core;
import com.spleefleague.core.chat.Chat;
import com.spleefleague.core.command.CoreCommand;
import com.spleefleague.core.command.annotation.*;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.purse.CoreCurrency;
import com.spleefleague.core.player.rank.CoreRank;
import com.spleefleague.core.vendor.Artisan;
import com.spleefleague.core.vendor.Artisans;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 * @author NickM13
 */
public class ArtisanCommand extends CoreCommand {

    public ArtisanCommand() {
        super("artisan", CoreRank.DEVELOPER);
        setUsage("/artisan");
        setOptions("artisans", pi -> Artisans.getArtisans().keySet());
        setOptions("crates", pi -> Core.getInstance().getCrateManager().getCrateNames());
    }

    @CommandAnnotation
    public void artisan(CorePlayer sender) {

    }

    @CommandAnnotation
    public void artisanCreate(CorePlayer sender,
                              @LiteralArg(value = "create") String l,
                              @HelperArg(value = "identifier") String artisan,
                              @HelperArg(value = "displayName") String displayName) {
        displayName = Chat.colorize(displayName);
        if (Artisans.createArtisan(artisan, displayName)) {
            success(sender, "Created vendor {" + artisan + ", " + displayName + ChatColor.GRAY + "}");
        }
    }

    @CommandAnnotation
    public void artisanSetName(CorePlayer sender,
                               @LiteralArg(value = "set") String l1,
                               @LiteralArg(value = "name") String l2,
                               @OptionArg(listName = "artisans") String artisan,
                               @HelperArg(value = "displayName") String displayName) {
        displayName = Chat.colorize(displayName);
        Artisans.setDisplayName(artisan, displayName);
        success(sender, artisan + " display name set to " + displayName);
    }

    @CommandAnnotation
    public void artisanSetCurrency(CorePlayer sender,
                                   @LiteralArg(value = "set") String l1,
                                   @LiteralArg(value = "currency") String l2,
                                   @OptionArg(listName = "artisans") String artisan,
                                   @EnumArg CoreCurrency currency) {
        Artisans.setCurrency(artisan, currency);
        success(sender, artisan + " currency set to " + currency.displayName);
    }

    @CommandAnnotation
    public void artisanSetCrate(CorePlayer sender,
                                @LiteralArg(value = "set") String l1,
                                @LiteralArg(value = "crate") String l2,
                                @OptionArg(listName = "artisans") String artisan,
                                @OptionArg(listName = "crates") String crate) {
        Artisans.setCrate(artisan, crate);
        success(sender, artisan + " crate set to " + Core.getInstance().getCrateManager().get(crate).getDisplayName());
    }

    @CommandAnnotation
    public void artisanSetBackground(CorePlayer sender,
                                     @LiteralArg(value = "set") String l1,
                                     @LiteralArg(value = "background") String l2,
                                     @OptionArg(listName = "artisans") String artisan,
                                     @NumberArg Integer background) {
        Artisans.setBackground(artisan, background);
        success(sender, artisan + " crate set to " + background);
    }

    @CommandAnnotation
    public void artisanSetCoins(CorePlayer sender,
                                     @LiteralArg(value = "set") String l1,
                                     @LiteralArg(value = "coinCost") String l2,
                                     @OptionArg(listName = "artisans") String artisan,
                                     @NumberArg Integer coinCost) {
        Artisans.setCoinCost(artisan, coinCost);
        success(sender, artisan + " coin cost set to " + coinCost);
    }

    @CommandAnnotation
    public void artisanOpen(CorePlayer sender,
                            @LiteralArg(value = "open") String l,
                            CorePlayer cp,
                            @OptionArg(listName = "artisans") String name) {
        Artisan artisan = Artisans.getVendor(name);
        if (artisan != null) {
            artisan.openShop(cp);
            success(sender, "Opened artisan " + name + " for player " + cp.getDisplayName());
        } else {
            error(sender, "Unknown artisan " + name);
        }
    }

    @CommandAnnotation
    public void artisanOpen(CommandSender sender,
                            @LiteralArg(value = "open") String l,
                            CorePlayer cp,
                            @OptionArg(listName = "artisans") String name) {
        Artisan artisan = Artisans.getVendor(name);
        if (artisan != null) {
            artisan.openShop(cp);
            success(sender, "Opened artisan " + name + " for player " + cp.getDisplayName());
        } else {
            error(sender, "Unknown artisan " + name);
        }
    }

    @CommandAnnotation
    public void artisanSet(CorePlayer sender,
                           @LiteralArg(value = "punch") String l,
                           @OptionArg(listName = "artisans") String name) {
        if (Artisans.setPlayerVendor(sender, name)) {
            success(sender, "Punch an entity to set it's artisan to " + name);
        } else {
            error(sender, "Unknown entity " + name);
        }
    }

    @CommandAnnotation
    public void artisanUnset(CorePlayer sender,
                             @LiteralArg(value = "unpunch") String l) {
        Artisans.unsetPlayerVendor(sender);
        success(sender, "Punch a artisan to clear it");
    }

    @CommandAnnotation
    public void artisanDestroy(CorePlayer sender,
                              @LiteralArg(value = "destroy") String l,
                              @OptionArg(listName = "artisans") String name) {
        if (Artisans.deleteArtisan(Artisans.getVendor(name))) {
            success(sender, "Deleted artisan " + name);
        } else {
            error(sender, "Unknown artisan " + name);
        }
    }

}
