package com.spleefleague.zone.command;

import com.spleefleague.core.chat.Chat;
import com.spleefleague.core.command.HoldableCommand;
import com.spleefleague.core.command.annotation.*;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.collectible.Collectible;
import com.spleefleague.core.player.rank.CoreRank;
import com.spleefleague.core.vendor.Vendorable;
import com.spleefleague.core.vendor.Vendorables;
import com.spleefleague.zone.CoreZones;
import com.spleefleague.zone.gear.Gear;
import org.bukkit.Material;

import javax.annotation.Nullable;
import java.util.Objects;

/**
 * @author NickM13
 * @since 2/13/2021
 */
public class GearCommand extends HoldableCommand {

    public GearCommand() {
        super(Gear.class, "gear", CoreRank.DEVELOPER);
        this.setOptions("shovelTypes", cp -> Gear.getGearTypes());
        this.setOptions("zones", cp -> CoreZones.getInstance().getZoneManager().getZoneNames());
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
    public void gearCreate(CorePlayer sender,
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

    @CommandAnnotation
    public void gearSetZone(CorePlayer sender,
                            @LiteralArg("set") String l1,
                            @LiteralArg("usageZone") String l2,
                            @Nullable @OptionArg(listName = "zones") String zoneName) {
        Vendorable vendorable = Vendorables.get(sender.getHeldItem());
        if (vendorable instanceof Gear) {
            Gear gear = (Gear) vendorable;
            gear.setUsageZone(zoneName);
            success(sender, "Usage zone set to " + zoneName);
        }
    }

    @CommandAnnotation
    public void gearSetMaterial(CorePlayer sender,
                            @LiteralArg("set") String l1,
                            @LiteralArg("material") String l2,
                            @EnumArg Material material) {
        Vendorable vendorable = Vendorables.get(sender.getHeldItem());
        if (vendorable instanceof Gear) {
            Gear gear = (Gear) vendorable;
            gear.setMaterial(material);
            gear.saveChanges();
            success(sender, "Material set to " + material);
        }
    }

}
