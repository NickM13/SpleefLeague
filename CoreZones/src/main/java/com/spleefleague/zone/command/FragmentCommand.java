package com.spleefleague.zone.command;

import com.spleefleague.core.Core;
import com.spleefleague.core.command.CoreCommand;
import com.spleefleague.core.command.annotation.*;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.rank.CoreRank;
import com.spleefleague.zone.CoreZones;
import com.spleefleague.zone.player.ZonePlayer;
import com.spleefleague.zone.player.fragments.PlayerFragments;
import org.bukkit.Bukkit;
import org.bukkit.Sound;

import javax.annotation.Nullable;

/**
 * @author NickM13
 * @since 2/13/2021
 */
public class FragmentCommand extends CoreCommand {

    public FragmentCommand() {
        super("f", CoreRank.DEVELOPER);
        addAlias("fragment");

        setOptions("fragments", pi -> CoreZones.getInstance().getFragmentManager().getFragmentNames());
    }

    @CommandAnnotation
    public void fragmentWand(CorePlayer sender,
                             @LiteralArg("wand") String l1) {
        if (sender.getPlayer().getInventory().getItemInMainHand().getType().isAir()) {
            sender.getPlayer().getInventory().setItemInMainHand(CoreZones.getInstance().getFragmentManager().getFragmentWand());
        } else {
            sender.getPlayer().getInventory().addItem(CoreZones.getInstance().getFragmentManager().getFragmentWand());
        }
        CoreZones.getInstance().sendMessage(sender, "Left click: destroy fragment; Right click: place fragment");
    }

    @CommandAnnotation
    public void fragmentSetItems(CorePlayer sender,
                                 @LiteralArg("set") String l1,
                                 @LiteralArg("items") String l2,
                                 @OptionArg(listName = "fragments") String fragment,
                                 @HelperArg("uncollected") @NumberArg(minValue = 0) Integer uncollectedCmd,
                                 @HelperArg("collected") @NumberArg(minValue = 0) Integer collectedCmd) {
        CoreZones.getInstance().getFragmentManager().setItem(fragment, uncollectedCmd, collectedCmd);
        CoreZones.getInstance().sendMessage(sender, "Fragment " + fragment + " uncollected=" + uncollectedCmd + "; collected=" + collectedCmd);
    }

    @CommandAnnotation
    public void fragmentSetMenuItem(CorePlayer sender,
                                 @LiteralArg("set") String l1,
                                 @LiteralArg("menuItem") String l2,
                                 @OptionArg(listName = "fragments") String fragment,
                                 @HelperArg("uncollected") @NumberArg(minValue = 0) Integer menuCmd) {
        CoreZones.getInstance().getFragmentManager().setMenuItem(fragment, menuCmd);
        CoreZones.getInstance().sendMessage(sender, "Fragment " + fragment + " menuItem=" + menuCmd);
    }

    @CommandAnnotation
    public void fragmentSetSound(CorePlayer sender,
                                 @LiteralArg("set") String l1,
                                 @LiteralArg("pickupSound") String l2,
                                 @OptionArg(listName = "fragments") String fragment,
                                 @EnumArg Sound sound) {
        CoreZones.getInstance().getFragmentManager().setPickupSound(fragment, sound);
        CoreZones.getInstance().sendMessage(sender, "Fragment " + fragment + " sound=" + sound.name());
        for (int i = 0; i < 5; i++) {
            int finalI = i;
            Bukkit.getScheduler().runTaskLater(Core.getInstance(), () -> {
                sender.getPlayer().playSound(sender.getLocation(), sound, 0.2f, (float) Math.pow(2, finalI / 12.f));
            }, i * 5);
        }
    }

    @CommandAnnotation
    public void fragmentSetName(CorePlayer sender,
                                       @LiteralArg("set") String l1,
                                       @LiteralArg("name") String l2,
                                       @OptionArg(listName = "fragments") String fragment,
                                       String displayName) {
        CoreZones.getInstance().getFragmentManager().setDisplayName(fragment, displayName);
        CoreZones.getInstance().sendMessage(sender, "Fragment " + fragment + " displayName=" + displayName);
    }

    @CommandAnnotation
    public void fragmentSetDescription(CorePlayer sender,
                                       @LiteralArg("set") String l1,
                                       @LiteralArg("description") String l2,
                                       @OptionArg(listName = "fragments") String fragment,
                                       String description) {
        CoreZones.getInstance().getFragmentManager().setDescription(fragment, description);
        CoreZones.getInstance().sendMessage(sender, "Fragment " + fragment + " description=" + description);
    }

    @CommandAnnotation
    public void fragmentCreate(CorePlayer sender,
                               @LiteralArg("create") String l1,
                               @HelperArg("identifier") String identifier) {
        if (identifier.contains(" ")) {
            error(sender, "/fragment create <identifier>");
            return;
        }
        if (CoreZones.getInstance().getFragmentManager().create(identifier)) {
            CoreZones.getInstance().sendMessage(sender, "Fragment Created: " + identifier);
            return;
        }
        error(sender, "Fragment already exists");
    }

    @CommandAnnotation
    public void fragmentBrushZone(CorePlayer sender,
                                  @LiteralArg("brush") String l1,
                                  @LiteralArg("fragment") String l2,
                                  @Nullable @OptionArg(listName = "fragments") String fragment) {
        ZonePlayer editor = CoreZones.getInstance().getPlayers().get(sender.getUniqueId());
        if (fragment != null) {
            editor.setTargetFragment(fragment);
        }
        CoreZones.getInstance().sendMessage(sender, "Fragment Target: " + editor.getTargetFragment());
    }

    @CommandAnnotation
    public void fragmentClear(CorePlayer sender,
                              @LiteralArg("clear") String l1,
                              @CorePlayerArg(allowOffline = true) CorePlayer target,
                              @OptionArg(listName = "fragments") String fragment) {
        CoreZones.getInstance().getPlayers().get(target.getUniqueId()).getFragments().clear(fragment);
        CoreZones.getInstance().sendMessage(sender, "Fragments " + fragment + " for " + target.getDisplayName() + " cleared");
    }

    @CommandAnnotation
    public void fragmentAdmin(CorePlayer sender,
                              @LiteralArg("admin") String l1,
                              @OptionArg(listName = "fragments") String fragment) {
        CoreZones.getInstance().getPlayers().get(sender.getUniqueId()).getFragments().admin(fragment);
        CoreZones.getInstance().sendMessage(sender, "Fragments " + fragment + " all given to " + sender.getDisplayName());
    }

}
