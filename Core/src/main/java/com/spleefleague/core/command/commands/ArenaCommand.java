package com.spleefleague.core.command.commands;

import com.spleefleague.core.Core;
import com.spleefleague.core.chat.Chat;
import com.spleefleague.core.chat.ChatUtils;
import com.spleefleague.core.command.CoreCommand;
import com.spleefleague.core.command.annotation.*;
import com.spleefleague.core.game.Arena;
import com.spleefleague.core.game.BattleMode;
import com.spleefleague.core.game.arena.Arenas;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.rank.CoreRank;
import com.spleefleague.core.util.variable.Dimension;
import com.spleefleague.core.util.variable.Point;
import com.spleefleague.core.util.variable.Position;
import com.spleefleague.core.world.build.BuildStructures;
import org.bukkit.Material;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * @author NickM13
 * @since 5/2/2020
 */
public class ArenaCommand extends CoreCommand {

    private final Map<UUID, Position> posOneMap = new HashMap<>();
    private final Map<UUID, Position> posTwoMap = new HashMap<>();

    public ArenaCommand() {
        super("arena", CoreRank.DEVELOPER);
        setOptions("battleModes", pi -> BattleMode.getAllNames());
        setOptions("arenas", pi -> Arenas.getAll().keySet());
        setOptions("structures", pi -> BuildStructures.getNames());
        setOptions("materials", pi -> getMaterialNames());
    }

    private static Set<String> materialNameSet;
    private static Set<String> getMaterialNames() {
        if (materialNameSet == null) {
            materialNameSet = new HashSet<>();
            for (Material mat : Material.values()) {
                materialNameSet.add(mat.toString().toLowerCase());
            }
        }
        return materialNameSet;
    }

    @CommandAnnotation
    public void arenaInfo(CorePlayer sender,
                          @LiteralArg("info") String l,
                          @OptionArg(listName = "arenas") String identifier) {
        Arena arena = Arenas.get(identifier);
        sender.sendMessage(ChatUtils.centerChat(Chat.colorize("&7[&c" + identifier + " &7Info]")));
        Chat.sendMessageToPlayer(sender, "Name: " + arena.getName());
        Chat.sendMessageToPlayer(sender, "Description: " + arena.getDescription());
        Chat.sendMessageToPlayer(sender, "Modes: " + arena.getModes());
    }

    @CommandAnnotation
    public void arenaCreate(CorePlayer sender,
                            @LiteralArg("create") String l,
                            @HelperArg("<identifier>") String identifier,
                            @HelperArg("<displayName>") String name) {
        identifier = identifier.toLowerCase();
        Arena arena = Arenas.createArena(identifier, name);
        if (arena == null) {
            error(sender, "Identifier already in use!");
        } else {
            success(sender, "Arena " + arena.getIdentifier() + " (" + arena.getName() + ") has been created");
        }
    }

    @CommandAnnotation
    public void arenaDestroy(CorePlayer sender,
                             @LiteralArg("destroy") String l,
                             @OptionArg(listName = "arenas") String identifier) {
        Arenas.destroyArena(identifier);
        success(sender, "Arena " + identifier + " has been deleted");
    }

    @CommandAnnotation
    public void arenaClone(CorePlayer sender,
                             @LiteralArg("clone") String l1,
                             @OptionArg(listName = "arenas") String arenaName,
                             @HelperArg("<identifier>") String newIdentifier) {
        Arena newArena = Arenas.cloneArena(arenaName, newIdentifier);
        if (newArena == null) {
            error(sender, "Name already in use!");
        } else {
            success(sender, "Arena cloned to " + newArena.getIdentifier());
        }
    }

    @CommandAnnotation
    public void arenaSetName(CorePlayer sender,
                             @LiteralArg("set") String l1,
                             @LiteralArg("name") String l2,
                             @OptionArg(listName = "arenas") String arenaName,
                             @HelperArg("<displayName>") String newName) {
        if (Arenas.renameArena(arenaName, newName)) {
            success(sender, "Arena renamed to " + newName);
        } else {
            error(sender, "Name already in use!");
        }
    }

    @CommandAnnotation
    public void arenaSetDescription(CorePlayer sender,
                             @LiteralArg("set") String l1,
                             @LiteralArg("description") String l2,
                             @OptionArg(listName = "arenas") String arenaName,
                             @HelperArg("<description>") String newDescription) {
        Arena arena = Arenas.get(arenaName);
        arena.setDescription(newDescription);
        success(sender, "Set description of " + arenaName + " to:\n" + newDescription);
    }

    @CommandAnnotation
    public void arenaSetItem(CorePlayer sender,
                             @LiteralArg("set") String l1,
                             @LiteralArg("item") String l2,
                             @OptionArg(listName = "arenas") String arenaName,
                             @EnumArg Material material,
                             @HelperArg("<cmd>") Integer cmd) {
        Arena arena = Arenas.get(arenaName);
        arena.setDisplayItem(material, cmd);
        success(sender, "Set display item of " + arenaName + " to " + material.name());
    }

    @CommandAnnotation
    public void arenaGoto(CorePlayer sender,
                          @LiteralArg("goto") String l1,
                          @OptionArg(listName = "arenas") String arenaName) {
        Arena arena = Arenas.get(arenaName);
        sender.teleport(arena.getOrigin().toLocation(Core.DEFAULT_WORLD));
        success(sender, "Teleported to arena " + arena.getName());
    }

    @CommandAnnotation
    public void arenaAddBorder(CorePlayer sender,
                               @LiteralArg("add") String l1,
                               @LiteralArg("border") String l2,
                               @OptionArg(listName = "arenas") String arenaName) {
        if (!posOneMap.containsKey(sender.getUniqueId())) {
            error(sender, "Your pos1 isn't set!");
        } else if (!posTwoMap.containsKey(sender.getUniqueId())) {
            error(sender, "Your pos2 isn't set!");
        } else {
            Arena arena = Arenas.get(arenaName);
            Position pos1 = posOneMap.get(sender.getUniqueId());
            Position pos2 = posTwoMap.get(sender.getUniqueId());
            Dimension dim = new Dimension(
                    new Point(pos1.x, pos1.y, pos1.z),
                    new Point(pos2.x, pos2.y, pos2.z));
            arena.addBorder(dim);
            success(sender, "Added arena border: " + dim);
        }
    }

    @CommandAnnotation
    public void arenaAddGoal(CorePlayer sender,
                             @LiteralArg("add") String l,
                             @LiteralArg("goal") String l2,
                             @OptionArg(listName = "arenas") String arenaName) {
        if (!posOneMap.containsKey(sender.getUniqueId())) {
            error(sender, "Your pos1 isn't set!");
        } else if (!posTwoMap.containsKey(sender.getUniqueId())) {
            error(sender, "Your pos2 isn't set!");
        } else {
            Arena arena = Arenas.get(arenaName);
            Position pos1 = posOneMap.get(sender.getUniqueId());
            Position pos2 = posTwoMap.get(sender.getUniqueId());
            Dimension dim = new Dimension(
                    new Point(pos1.x, pos1.y, pos1.z),
                    new Point(pos2.x, pos2.y, pos2.z));
            arena.addGoal(dim);
            success(sender, "Added arena goal: " + dim);
        }
    }

    @CommandAnnotation
    public void arenaPos1(CorePlayer sender,
                          @LiteralArg("pos1") String l) {
        Position pos1 = new Position(sender.getLocation());
        posOneMap.put(sender.getUniqueId(), pos1);
        success(sender, "Pos1 set to " + pos1);
    }

    @CommandAnnotation
    public void arenaPos2(CorePlayer sender,
                          @LiteralArg("pos2") String l) {
        Position pos2 = new Position(sender.getLocation());
        posTwoMap.put(sender.getUniqueId(), pos2);
        success(sender, "Pos2 set to " + pos2);
    }

    @CommandAnnotation
    public void arenaAddSpawn(CorePlayer sender,
                              @LiteralArg("add") String l1,
                              @LiteralArg("spawn") String l2,
                              @OptionArg(listName = "arenas") String arenaName,
                              @Nullable @HelperArg("[insertPos]") Integer index) {
        Position pos = new Position(sender.getLocation());
        if (index != null) {
            if (Arenas.get(arenaName).insertSpawn(pos, index)) {
                success(sender, "Spawn added " + pos);
            } else {
                error(sender, "Index out of bounds!");
            }
        } else {
            Arenas.get(arenaName).addSpawn(pos);
            success(sender, "Spawn added " + pos);
        }
    }

    @CommandAnnotation
    public void arenaGetScoreboard(CorePlayer sender,
                                   @LiteralArg("get") String l1,
                                   @LiteralArg("scoreboard") String l2,
                                   @OptionArg(listName = "arenas") String arenaName) {
        success(sender, "Scoreboards: ");
        for (Position pos : Arenas.get(arenaName).getScoreboards()) {
            success(sender, " " + pos);
        }
    }

    @CommandAnnotation
    public void arenaAddScoreboard(CorePlayer sender,
                                   @LiteralArg("add") String l1,
                                   @LiteralArg("scoreboard") String l2,
                                   @OptionArg(listName = "arenas") String arenaName) {
        Position pos = new Position(sender.getLocation());
        Arenas.get(arenaName).addScoreboard(pos);
        success(sender, "Scoreboard added at " + pos);
    }

    @CommandAnnotation
    public void arenaClearScoreboard(CorePlayer sender,
                                   @LiteralArg("clear") String l1,
                                   @LiteralArg("scoreboards") String l2,
                                   @OptionArg(listName = "arenas") String arenaName) {
        Arenas.get(arenaName).clearScoreboards();
        success(sender, "Scoreboards cleared");
    }

    @CommandAnnotation
    public void arenaGetOrigin(CorePlayer sender,
                                   @LiteralArg("get") String l1,
                                   @LiteralArg("origin") String l2,
                                   @OptionArg(listName = "arenas") String arenaName) {
        Position pos = Arenas.get(arenaName).getOrigin();
        success(sender, "Origin is " + pos);
    }

    @CommandAnnotation
    public void arenaSetOrigin(CorePlayer sender,
                               @LiteralArg("set") String l1,
                               @LiteralArg("origin") String l2,
                               @OptionArg(listName = "arenas") String arenaName) {
        Position pos = new Position(sender.getLocation());
        Arenas.get(arenaName).setOrigin(pos);
        success(sender, "Origin set to " + pos);
    }

    @CommandAnnotation
    public void arenaSetSpectator(CorePlayer sender,
                                  @LiteralArg("set") String l,
                                  @LiteralArg("spectator") String l2,
                                  @OptionArg(listName = "arenas") String arenaName) {
        Position pos = new Position(sender.getLocation());
        Arenas.get(arenaName).setSpectatorSpawn(pos);
        success(sender, "Spectator Spawn set to " + pos);
    }

    @CommandAnnotation
    public void arenaUnsetSpectator(CorePlayer sender,
                                    @LiteralArg("unset") String l,
                                    @LiteralArg("spectator") String l2,
                                    @OptionArg(listName = "arenas") String arenaName) {
        Arenas.get(arenaName).setSpectatorSpawn(null);
        success(sender, "Spectator Spawn has been removed");
    }

    @CommandAnnotation
    public void arenaAddMode(CorePlayer sender,
                             @LiteralArg("add") String l1,
                             @LiteralArg("mode") String l2,
                             @OptionArg(listName = "arenas") String arenaName,
                             @OptionArg(listName = "battleModes") String modeName) {
        if (Arenas.addArenaMode(arenaName, BattleMode.get(modeName))) {
            success(sender, "Added mode to arena");
        } else {
            error(sender, "Arena already uses that mode!");
        }
    }

    @CommandAnnotation
    public void arenaRemoveMode(CorePlayer sender,
                                @LiteralArg("remove") String l1,
                                @LiteralArg("mode") String l2,
                                @OptionArg(listName = "arenas") String arenaName,
                                @OptionArg(listName = "battleModes") String modeName) {
        if (Arenas.removeArenaMode(arenaName, BattleMode.get(modeName))) {
            success(sender, "Removed mode from arena");
        } else {
            error(sender, "Arena doesn't use that mode!");
        }
    }

    @CommandAnnotation
    public void arenaAddStructure(CorePlayer sender,
                                  @LiteralArg("add") String l1,
                                  @LiteralArg("structure") String l2,
                                  @OptionArg(listName = "arenas") String arenaName,
                                  @OptionArg(listName = "structures") String structureName) {
        if (Arenas.get(arenaName).addStructure(structureName)) {
            success(sender, "Added structure to arena");
        } else {
            error(sender, "Arena already uses that structure!");
        }
    }

    @CommandAnnotation
    public void arenaRemoveStructure(CorePlayer sender,
                                     @LiteralArg("remove") String l1,
                                     @LiteralArg("structure") String l2,
                                     @OptionArg(listName = "arenas") String arenaName,
                                     @OptionArg(listName = "structures") String structureName) {
        if (Arenas.get(arenaName).removeStructure(structureName)) {
            success(sender, "Removed structure from arena");
        } else {
            error(sender, "Arena doesn't use that structure!");
        }
    }

    @CommandAnnotation
    public void arenaSetTeamCount(CorePlayer sender,
                                 @LiteralArg("set") String l,
                                 @LiteralArg("teamcount") String l2,
                                 @OptionArg(listName = "arenas") String arenaName,
                                 Integer teamCount) {
        Arenas.get(arenaName).setTeamCount(teamCount);
        success(sender, "Team count set to " + teamCount);
    }

    @CommandAnnotation
    public void arenaSetTeamSize(CorePlayer sender,
                                 @LiteralArg("set") String l,
                                 @LiteralArg("teamsize") String l2,
                                 @OptionArg(listName = "arenas") String arenaName,
                                 Integer teamSize) {
        Arenas.get(arenaName).setTeamSize(teamSize);
        success(sender, "Team size set to " + teamSize);
    }

}
