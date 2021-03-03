package com.spleefleague.zone.command;

import com.spleefleague.core.chat.Chat;
import com.spleefleague.core.command.CoreCommand;
import com.spleefleague.core.command.annotation.*;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.rank.CoreRank;
import com.spleefleague.zone.CoreZones;
import com.spleefleague.zone.monuments.Monument;
import com.spleefleague.zone.player.ZonePlayer;
import com.spleefleague.zone.zones.Zone;
import org.bukkit.WeatherType;

import javax.annotation.Nullable;

/**
 * @author NickM13
 * @since 2/12/2021
 */
public class ZoneCommand extends CoreCommand {

    public ZoneCommand() {
        super("z", CoreRank.DEVELOPER);
        addAlias("zone");

        setOptions("zoneNames", pi -> CoreZones.getInstance().getZoneManager().getZoneNames());
    }

    @CommandAnnotation
    public void zoneWand(CorePlayer sender,
                          @LiteralArg("wand") String l1) {
        if (sender.getPlayer().getInventory().getItemInMainHand().getType().isAir()) {
            sender.getPlayer().getInventory().setItemInMainHand(CoreZones.getInstance().getZoneManager().getZoneWand());
        } else {
            sender.getPlayer().getInventory().addItem(CoreZones.getInstance().getZoneManager().getZoneWand());
        }
        CoreZones.getInstance().sendMessage(sender, "Left click: unset zone; Right click: set zone");
    }

    @CommandAnnotation
    public void zoneBrush(CorePlayer sender,
                          @LiteralArg("brush") String l1) {
        ZonePlayer editor = CoreZones.getInstance().getPlayers().get(sender.getUniqueId());
        CoreZones.getInstance().sendMessage(sender, "Brush Size: " + editor.getBrushSize());
        CoreZones.getInstance().sendMessage(sender, "Brush Height: " + editor.getBrushHeight());
        CoreZones.getInstance().sendMessage(sender, "Brush Drop: " + editor.getBrushDrop());
        CoreZones.getInstance().sendMessage(sender, "Zone Target: " + editor.getTargetZone());
    }

    @CommandAnnotation
    public void zoneBrushSize(CorePlayer sender,
                              @LiteralArg("brush") String l1,
                              @LiteralArg("size") String l2,
                              @Nullable @NumberArg(minValue = 1, maxValue = 64) Integer size) {
        ZonePlayer editor = CoreZones.getInstance().getPlayers().get(sender.getUniqueId());
        if (size != null) {
            editor.setBrushSize(size);
        }
        CoreZones.getInstance().sendMessage(sender, "Brush Size: " + editor.getBrushSize());
    }

    @CommandAnnotation
    public void zoneBrushHeight(CorePlayer sender,
                                @LiteralArg("brush") String l1,
                                @LiteralArg("height") String l2,
                                @Nullable @NumberArg(minValue = 1, maxValue = 128) Integer height) {
        ZonePlayer editor = CoreZones.getInstance().getPlayers().get(sender.getUniqueId());
        if (height != null) {
            editor.setBrushHeight(height);
        }
        CoreZones.getInstance().sendMessage(sender, "Brush Height: " + editor.getBrushHeight());
    }

    @CommandAnnotation
    public void zoneBrushDrop(CorePlayer sender,
                              @LiteralArg("brush") String l1,
                              @LiteralArg("drop") String l2,
                              @Nullable @NumberArg(minValue = -128, maxValue = 128) Integer drop) {
        ZonePlayer editor = CoreZones.getInstance().getPlayers().get(sender.getUniqueId());
        if (drop != null) {
            editor.setBrushDrop(drop);
        }
        CoreZones.getInstance().sendMessage(sender, "Brush Drop: " + editor.getBrushDrop());
    }

    @CommandAnnotation
    public void zoneBrushZone(CorePlayer sender,
                              @LiteralArg("brush") String l1,
                              @LiteralArg("zone") String l2,
                              @Nullable @OptionArg(listName = "zoneNames") String zone) {
        ZonePlayer editor = CoreZones.getInstance().getPlayers().get(sender.getUniqueId());
        if (zone != null) {
            editor.setTargetZone(zone);
        }
        CoreZones.getInstance().sendMessage(sender, "Zone Target: " + editor.getTargetZone());
    }

    @CommandAnnotation
    public void zoneCreate(CorePlayer sender,
                           @LiteralArg("create") String l1,
                           @HelperArg("identifier") String identifier) {
        if (identifier.contains(" ")) {
            error(sender, "/zone create <identifier>");
            return;
        }
        if (CoreZones.getInstance().getZoneManager().create(identifier)) {
            CoreZones.getInstance().sendMessage(sender, "Zone Created: " + identifier);
            return;
        }
        error(sender, "Zone already exists");
    }

    @CommandAnnotation
    public void zoneSetName(CorePlayer sender,
                            @LiteralArg("set") String l1,
                            @LiteralArg("name") String l2,
                            @OptionArg(listName = "zoneNames") String identifier,
                            @HelperArg("name") String name) {
        name = Chat.colorize(name);
        CoreZones.getInstance().getZoneManager().setDisplayName(identifier, name);
        CoreZones.getInstance().sendMessage(sender, "Zone " + identifier + " display name set to: " + name);
    }

    @CommandAnnotation
    public void zoneSetPriority(CorePlayer sender,
                                @LiteralArg("set") String l1,
                                @LiteralArg("priority") String l2,
                                @OptionArg(listName = "zoneNames") String identifier,
                                @NumberArg(minValue = -100, maxValue = 100) Double priority) {
        CoreZones.getInstance().getZoneManager().setPriority(identifier, priority);
        CoreZones.getInstance().sendMessage(sender, "Zone " + identifier + " priority set to: " + priority);
    }

    @CommandAnnotation
    public void zoneSetParent(CorePlayer sender,
                              @LiteralArg("set") String l1,
                              @LiteralArg("parent") String l2,
                              @OptionArg(listName = "zoneNames") String identifier,
                              @Nullable @OptionArg(listName = "zoneNames") String parent) {
        if (identifier.equals(parent)) {
            error(sender, "Parent cant be the same as child!");
            return;
        }
        CoreZones.getInstance().getZoneManager().setParent(identifier, parent);
        CoreZones.getInstance().sendMessage(sender, "Zone " + identifier + " parent set to: " + parent);
    }

    @CommandAnnotation
    public void zoneView(CorePlayer sender,
                         @LiteralArg("view") String l1,
                         @OptionArg(listName = "zoneNames") String identifier) {
        if (CoreZones.getInstance().getZoneManager().addZoneViewer(identifier, sender)) {
            CoreZones.getInstance().sendMessage(sender, "You are now receiving packets for zone " + identifier);
        } else {
            CoreZones.getInstance().sendMessage(sender, "You are no longer receiving packets for zone " + identifier);
        }
    }

    @CommandAnnotation
    public void zoneInfo(CorePlayer sender,
                         @LiteralArg("info") String l1,
                         @OptionArg(listName = "zoneNames") String identifier) {
        success(sender, CoreZones.getInstance().getZoneManager().get(identifier).toString());
    }

    @CommandAnnotation
    public void zoneSetRegen(CorePlayer sender,
                             @LiteralArg("set") String l1,
                             @LiteralArg("healthRegen") String l2,
                             @OptionArg(listName = "zoneNames") String identifier,
                             Boolean state) {
        CoreZones.getInstance().getZoneManager().setHealthRegen(identifier, state);
        success(sender, "Health regeneration for " + identifier + " set to " + state);
    }

    @CommandAnnotation
    public void zoneSetWeather(CorePlayer sender,
                               @LiteralArg("set") String l1,
                               @LiteralArg("weather") String l2,
                               @OptionArg(listName = "zoneNames") String identifier,
                               Boolean state) {
        CoreZones.getInstance().getZoneManager().setWeather(identifier, state);
        success(sender, "Weather set to " + state);
    }

    @CommandAnnotation
    public void zoneSetRain(CorePlayer sender,
                            @LiteralArg("set") String l1,
                            @LiteralArg("rain") String l2,
                            @OptionArg(listName = "zoneNames") String identifier,
                            @NumberArg(minValue = 0) Integer rainLevel) {
        CoreZones.getInstance().getZoneManager().setRain(identifier, rainLevel);
        success(sender, "Rain set to " + rainLevel);
    }

    @CommandAnnotation
    public void zoneSetThunder(CorePlayer sender,
                               @LiteralArg("set") String l1,
                               @LiteralArg("thunder") String l2,
                               @OptionArg(listName = "zoneNames") String identifier,
                               @NumberArg(minValue = 0) Integer thunderLevel) {
        CoreZones.getInstance().getZoneManager().setThunder(identifier, thunderLevel);
        success(sender, "Thunder level set to " + thunderLevel);
    }

}
