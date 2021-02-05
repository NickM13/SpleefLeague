package com.spleefleague.core.command.commands;

import com.spleefleague.core.Core;
import com.spleefleague.core.chat.Chat;
import com.spleefleague.core.command.CoreCommand;
import com.spleefleague.core.command.annotation.*;
import com.spleefleague.core.crate.Crate;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.purse.CoreCurrency;
import com.spleefleague.core.player.rank.CoreRank;
import com.spleefleague.core.vendor.Vendorable;
import org.bukkit.Material;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.List;

/**
 * @author NickM13
 * @since 2/1/2021
 */
public class CrateCommand extends CoreCommand {

    public CrateCommand() {
        super("crate", CoreRank.DEVELOPER);
        setOptions("crates", priorInfo -> Core.getInstance().getCrateManager().getCrateNames());
    }

    @CommandAnnotation
    public void crateCreate(CorePlayer sender,
                            @LiteralArg("create") String l,
                            @HelperArg("identifier") String identifier,
                            @HelperArg("displayName") String displayName) {
        if (Core.getInstance().getCrateManager().create(identifier, Chat.colorize(displayName))) {
            success(sender, "Created crate with id " + identifier);
        } else {
            error(sender, "Crate with that id already exists");
        }
    }

    @CommandAnnotation
    public void crateDestroy(CorePlayer sender,
                             @LiteralArg("destroy") String l,
                             @OptionArg(listName = "crates") String identifier) {
        Core.getInstance().getCrateManager().destroy(identifier);
        success(sender, "Crate " + identifier + " destroyed");
    }

    @CommandAnnotation
    public void crateSetPriority(CorePlayer sender,
                              @LiteralArg("set") String l1,
                              @LiteralArg("priority") String l2,
                              @OptionArg(listName = "crates") String identifier,
                              @NumberArg(minValue = -1000, maxValue = 1000) Integer priority) {
        Core.getInstance().getCrateManager().get(identifier).setPriority(priority);
        success(sender, "Crate " + identifier + " priority set to " + priority);
    }

    @CommandAnnotation
    public void crateInfo(CorePlayer sender,
                          @LiteralArg("info") String l,
                          @OptionArg(listName = "crates") String identifier) {
        Crate crate = Core.getInstance().getCrateManager().get(identifier);
        for (Field field : crate.getFields()) {
            try {
                success(sender, field.getName() + ": " + field.get(crate));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    @CommandAnnotation
    public void crateSetName(CorePlayer sender,
                             @LiteralArg("set") String l1,
                             @LiteralArg("name") String l2,
                             @OptionArg(listName = "crates") String identifier,
                             @HelperArg("displayName") String displayName) {
        String colorized = Chat.colorize(displayName);
        if (Core.getInstance().getCrateManager().setName(identifier, colorized)) {
            success(sender, "Set Crate " + identifier + " name to " + colorized);
        } else {
            error(sender, "Crate " + identifier + " does not exist");
        }
    }

    @CommandAnnotation
    public void crateSetDescription(CorePlayer sender,
                                    @LiteralArg("set") String l1,
                                    @LiteralArg("description") String l2,
                                    @OptionArg(listName = "crates") String identifier,
                                    @HelperArg("description") String description) {
        String colorized = Chat.colorize(description);
        if (Core.getInstance().getCrateManager().setDescription(identifier, colorized)) {
            success(sender, "Set Crate " + identifier + " description to " + colorized);
        }
    }

    @CommandAnnotation
    public void crateSetMaterial(CorePlayer sender,
                                 @LiteralArg("set") String l1,
                                 @LiteralArg("material") String l2,
                                 @OptionArg(listName = "crates") String identifier,
                                 @EnumArg Material material) {
        Core.getInstance().getCrateManager().setMaterial(identifier, material);
        success(sender, "Set Crate " + identifier + " material to " + material.name());
    }

    @CommandAnnotation
    public void crateSetStyle(CorePlayer sender,
                              @LiteralArg("set") String l1,
                              @LiteralArg("style") String l2,
                              @OptionArg(listName = "crates") String identifier,
                              @HelperArg("style") @Nullable String style) {
        Core.getInstance().getCrateManager().setStyle(identifier, style);
        success(sender, "Set Crate " + identifier + " style to " + style);
    }

    @CommandAnnotation
    public void crateSetClosed(CorePlayer sender,
                               @LiteralArg("set") String l1,
                               @LiteralArg("closed") String l2,
                               @OptionArg(listName = "crates") String identifier,
                               @HelperArg("customModelData") Integer cmd) {
        Core.getInstance().getCrateManager().setClosedCmd(identifier, cmd);
        success(sender, "Set Crate " + identifier + " closed cmd to " + cmd);
    }

    @CommandAnnotation
    public void crateSetOpened(CorePlayer sender,
                               @LiteralArg("set") String l1,
                               @LiteralArg("opened") String l2,
                               @OptionArg(listName = "crates") String identifier,
                               @HelperArg("customModelData") Integer cmd) {
        Core.getInstance().getCrateManager().setOpenedCmd(identifier, cmd);
        success(sender, "Set Crate " + identifier + " opened cmd to " + cmd);
    }

    @CommandAnnotation
    public void crateSetHide(CorePlayer sender,
                             @LiteralArg("set") String l1,
                             @LiteralArg("hidden") String l2,
                             @OptionArg(listName = "crates") String identifier,
                             Boolean cmd) {
        Core.getInstance().getCrateManager().setHidden(identifier, cmd);
        success(sender, "Set Crate " + identifier + " hidden if none to " + cmd);
    }

    @CommandAnnotation
    public void crateSetWeightCollectible(CorePlayer sender,
                               @LiteralArg("set") String l1,
                               @LiteralArg("weight") String l2,
                               @LiteralArg("collectible") String l3,
                               @OptionArg(listName = "crates") String identifier,
                               @EnumArg Vendorable.Rarity rarity,
                               @NumberArg(minValue = 0) Double weight) {
        Core.getInstance().getCrateManager().setCollectibleWeight(identifier, rarity, weight);
        success(sender, "Set Crate " + identifier + " weight of Rarity " + rarity.name() + " to " + weight);
    }

    @CommandAnnotation
    public void crateSetWeightCurrency(CorePlayer sender,
                                       @LiteralArg("set") String l1,
                                       @LiteralArg("weight") String l2,
                                       @LiteralArg("currency") String l3,
                                       @OptionArg(listName = "crates") String identifier,
                                       @EnumArg CoreCurrency currency,
                                       @NumberArg(minValue = 0) Double weight) {
        Core.getInstance().getCrateManager().setCurrencyWeight(identifier, currency, weight);
        success(sender, "Set Crate " + identifier + " weight of Currency " + currency.name() + " to " + weight);
    }

    @CommandAnnotation
    public void crateSetCountCurrency(CorePlayer sender,
                                      @LiteralArg("set") String l1,
                                      @LiteralArg("count") String l2,
                                      @LiteralArg("currency") String l3,
                                      @OptionArg(listName = "crates") String identifier,
                                      @HelperArg("min") @NumberArg(minValue = 0) Double min,
                                      @HelperArg("max") @NumberArg(minValue = 0) Double max) {
        Core.getInstance().getCrateManager().setCurrencyCaps(identifier, min, max);
        success(sender, "Set Crate " + identifier + " num of currency to {" + min + " to " + max + "}");
    }

    @CommandAnnotation
    public void crateSetCountCollectible(CorePlayer sender,
                                         @LiteralArg("set") String l1,
                                         @LiteralArg("count") String l2,
                                         @LiteralArg("collectible") String l3,
                                         @OptionArg(listName = "crates") String identifier,
                                         @HelperArg("min") @NumberArg(minValue = 0) Double min,
                                         @HelperArg("max") @NumberArg(minValue = 0) Double max) {
        Core.getInstance().getCrateManager().setCollectibleCaps(identifier, min, max);
        success(sender, "Set Crate " + identifier + " num of collectibles to {" + min + " to " + max + "}");
    }

    @CommandAnnotation
    public void crateGive(CorePlayer sender,
                          @LiteralArg("give") String l1,
                          @CorePlayerArg CorePlayer target,
                          @OptionArg(listName = "crates") String identifier,
                          @NumberArg Integer count) {
        Crate crate = Core.getInstance().getCrateManager().get(identifier);
        target.getCrates().changeCrateCount(identifier, count);
        success(sender, "Gave " + count + " " + crate.getDisplayName() + " to " + target.getName());
    }

    @CommandAnnotation
    public void crateGive(CorePlayer sender,
                          @LiteralArg("give") String l1,
                          List<CorePlayer> targets,
                          @OptionArg(listName = "crates") String identifier,
                          @NumberArg Integer count) {
        Crate crate = Core.getInstance().getCrateManager().get(identifier);
        for (CorePlayer target : targets) {
            target.getCrates().changeCrateCount(identifier, count);
            success(sender, "Gave " + count + " " + crate.getDisplayName() + " to " + targets.size() + " players");
        }
    }

}
