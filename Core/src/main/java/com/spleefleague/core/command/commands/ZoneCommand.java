package com.spleefleague.core.command.commands;

import com.spleefleague.core.command.CoreCommand;
import com.spleefleague.core.command.annotation.CommandAnnotation;
import com.spleefleague.core.command.annotation.HelperArg;
import com.spleefleague.core.command.annotation.LiteralArg;
import com.spleefleague.core.command.annotation.OptionArg;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.rank.Rank;
import com.spleefleague.core.util.variable.Dimension;
import com.spleefleague.core.util.variable.Point;
import com.spleefleague.core.util.variable.Position;
import com.spleefleague.core.world.global.zone.GlobalZone;
import com.spleefleague.core.world.global.zone.ZoneLeaf;
import com.spleefleague.coreapi.chat.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author NickM13
 * @since 5/8/2020
 */
public class ZoneCommand extends CoreCommand {

    private final Map<UUID, Position> posOneMap = new HashMap<>();
    private final Map<UUID, Position> posTwoMap = new HashMap<>();

    public ZoneCommand() {
        super("zone", Rank.DEVELOPER);
        setOptions("zoneNames", pi -> GlobalZone.getAll().keySet());
        setOptions("leafNames", pi -> {
            System.out.println(pi.getArgs().get(pi.getArgs().size() - 1));
            GlobalZone zone = GlobalZone.getZone(pi.getArgs().get(pi.getArgs().size() - 1));
            return zone.getLeafIds();
        });
    }

    @CommandAnnotation
    public void zoneCreate(CorePlayer sender,
                           @LiteralArg("create") String l,
                           @HelperArg("<identifier>") String identifier,
                           @HelperArg("<name>") String name) {
        GlobalZone.createZone(identifier, name);
    }

    @CommandAnnotation
    public void zoneSetName(CorePlayer sender,
                            @LiteralArg("set") String l1,
                            @LiteralArg("name") String l2,
                            @OptionArg(listName = "zoneNames") String zoneName,
                            @HelperArg("<displayName>") String name) {
        GlobalZone.getZone(zoneName).setName(name);
    }

    @CommandAnnotation
    public void zoneAddLeaf(CorePlayer sender,
                            @LiteralArg("leaf") String l2,
                            @LiteralArg("add") String l1,
                            @OptionArg(listName = "zoneNames") String zoneName) {
        Point pos = new Point(sender.getLocation()).rounded(2);
        GlobalZone zone = GlobalZone.getZone(zoneName);
        ZoneLeaf leaf = zone.addLeaf(pos);
        if (leaf == null) {
            error(sender, "Leaf already here!");
        } else {
            success(sender, "Added leaf " + zoneName + ":" + leaf.getId() + " to " + pos);
        }
    }

    @CommandAnnotation
    public void zoneRemoveLeaf(CorePlayer sender,
                               @LiteralArg("leaf") String l2,
                               @LiteralArg("remove") String l1) {
        success(sender, "Removed " + GlobalZone.removeLeaves(new Point(sender.getLocation()), 1) + " leaves");
    }

    @CommandAnnotation
    public void zoneGotoLeaf(CorePlayer sender,
                             @LiteralArg("leaf") String l2,
                             @LiteralArg("goto") String l1,
                             @OptionArg(listName = "zoneNames") String zoneName,
                             @OptionArg(listName = "leafNames") String leafId) {
        GlobalZone zone = GlobalZone.getZone(zoneName);
        ZoneLeaf leaf = zone.getLeaf(leafId);
        if (leaf != null) {
            sender.teleport(leaf.getPos().getX(), leaf.getPos().getY(), leaf.getPos().getZ());
            success(sender, "Teleported to leaf " + zoneName + ":" + leaf.getId());
        } else {
            error(sender, "Leaf does not exist!");
        }
    }

    @CommandAnnotation
    public void zoneScanner(CorePlayer sender,
                             @LiteralArg("scanner") String l1) {
        if (GlobalZone.toggleScanner(sender)) {
            success(sender, "You've been given a zone scanner, right click to place a fragment at current location and shift-right click to remove");
        } else {
            success(sender, "Zone scanner removed");
        }
        sender.refreshHotbar();
    }

    @CommandAnnotation
    public void zoneAddBorder(CorePlayer sender,
                              @LiteralArg("add") String l1,
                              @LiteralArg("border") String l2,
                              @OptionArg(listName = "zoneNames") String zoneName) {
        if (!posOneMap.containsKey(sender.getUniqueId())) {
            error(sender, "Your pos1 isn't set!");
        } else if (!posTwoMap.containsKey(sender.getUniqueId())) {
            error(sender, "Your pos2 isn't set!");
        } else {
            GlobalZone zone = GlobalZone.getZone(zoneName);
            Position pos1 = posOneMap.get(sender.getUniqueId());
            Position pos2 = posTwoMap.get(sender.getUniqueId());
            Dimension dim = new Dimension(
                    new Point(pos1.x, pos1.y, pos1.z),
                    new Point(pos2.x, pos2.y, pos2.z));
            zone.addBorder(dim.expand(1));
            success(sender, "Added border " + dim.expand(1) + " to " + zoneName);
        }
    }

    @CommandAnnotation
    public void zoneRemoveBorder(CorePlayer sender,
                              @LiteralArg("remove") String l1,
                              @LiteralArg("border") String l2,
                              @OptionArg(listName = "zoneNames") String zoneName) {
        GlobalZone zone = GlobalZone.getZone(zoneName);
        for (Dimension border : zone.getBorders()) {
            if (border.isContained(new Point(sender.getLocation()))) {

            }
            TextComponent borderComponent = new TextComponent((border.isContained(new Point(sender.getLocation())) ? ChatColor.GREEN : ChatColor.YELLOW) + border.toString());
            borderComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Remove this border ").create()));
            borderComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/zone remove border " + zoneName + " "
                    + border.toCommandFormat()));
            sender.sendMessage(borderComponent);
        }
    }

    @CommandAnnotation(hidden = true)
    public void zoneRemoveBorder(CorePlayer sender,
                                 @LiteralArg("remove") String l1,
                                 @LiteralArg("border") String l2,
                                 @OptionArg(listName = "zoneNames") String zoneName,
                                 Double x, Double y, Double z,
                                 Double w, Double h, Double d) {
        GlobalZone zone = GlobalZone.getZone(zoneName);
        if (zone.removeBorder(new Dimension(new Point(x, y, z), new Point(w, h, d)))) {
            success(sender, "Removed border");
        } else {
            error(sender, "Border does not exist");
        }
    }

    @CommandAnnotation
    public void zonePos1(CorePlayer sender,
                         @LiteralArg("pos1") String l) {
        Position pos1 = new Position(sender.getLocation());
        posOneMap.put(sender.getUniqueId(), pos1);
        success(sender, "Pos1 set to " + pos1);
    }

    @CommandAnnotation
    public void zonePos2(CorePlayer sender,
                         @LiteralArg("pos2") String l) {
        Position pos2 = new Position(sender.getLocation());
        posTwoMap.put(sender.getUniqueId(), pos2);
        success(sender, "Pos2 set to " + pos2);
    }

    @CommandAnnotation
    public void zoneClearLeaves(CommandSender sender,
                                @LiteralArg("leaf") String l2,
                                @LiteralArg("clear") String l1,
                                CorePlayer target,
                                @OptionArg(listName = "zoneNames") String zoneName) {
        target.getCollectibles().clearLeaves(zoneName);
    }

    @CommandAnnotation
    public void zoneClearLeaves(CommandSender sender,
                                @LiteralArg("leaf") String l2,
                                @LiteralArg("clear") String l1,
                                List<CorePlayer> targets,
                                @OptionArg(listName = "zoneNames") String zoneName) {
        for (CorePlayer target : targets) {
            target.getCollectibles().clearLeaves(zoneName);
        }
    }

}
