package com.spleefleague.core.command.commands;

import com.spleefleague.core.chat.Chat;
import com.spleefleague.core.command.HoldableCommand;
import com.spleefleague.core.command.annotation.CommandAnnotation;
import com.spleefleague.core.command.annotation.EnumArg;
import com.spleefleague.core.command.annotation.HelperArg;
import com.spleefleague.core.command.annotation.LiteralArg;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.collectible.Collectible;
import com.spleefleague.core.player.collectible.gear.Gear;
import com.spleefleague.core.player.rank.CoreRank;
import com.spleefleague.core.vendor.Vendorables;

import java.util.Objects;

/**
 * @author NickM13
 * @since 4/20/2020
 */
public class GearCommand extends HoldableCommand {

    public GearCommand() {
        super(Gear.class, "gear", CoreRank.DEVELOPER);
        this.setOptions("shovelTypes", cp -> Gear.getGearTypes());
    }

    @CommandAnnotation
    public void gearSetType(CorePlayer sender) {

    }

    @Override
    @CommandAnnotation(disabled = true)
    public void collectibleCreate(CorePlayer sender,
                                  @LiteralArg("create") String l,
                                  @HelperArg("<identifier>") String identifier,
                                  @HelperArg("<displayName>") String displayName) {
        error(sender, "Invalid command");
    }

    @CommandAnnotation
    public void collectibleCreate(CorePlayer sender,
                                  @LiteralArg("create") String l,
                                  @EnumArg Gear.GearType gearType,
                                  @HelperArg("<identifier>") String identifier,
                                  @HelperArg("<displayName>") String displayName) {
        identifier = identifier.toLowerCase();
        displayName = Chat.colorize(displayName);
        if (Vendorables.contains(Gear.class, identifier)) {
            error(sender, "That collectible already exists!");
        } else {
            Collectible collectible = Collectible.create(Objects.requireNonNull(gearType.create(identifier, displayName)));
            sender.setHeldItem(collectible.getDisplayItem());
            success(sender, "Created new " + gearType.name().toLowerCase() + " (" + identifier + ": " + displayName + Chat.DEFAULT + ")");
        }
    }

}
