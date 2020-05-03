package com.spleefleague.core.command.commands;

import com.spleefleague.core.command.CommandTemplate;
import com.spleefleague.core.command.annotation.CommandAnnotation;
import com.spleefleague.core.command.annotation.HelperArg;
import com.spleefleague.core.command.annotation.LiteralArg;
import com.spleefleague.core.command.annotation.OptionArg;
import com.spleefleague.core.game.Arena;
import com.spleefleague.core.game.BattleMode;
import com.spleefleague.core.game.arena.Arenas;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.rank.Rank;
import com.spleefleague.core.util.variable.Dimension;
import com.spleefleague.core.util.variable.Point;
import com.spleefleague.core.world.build.BuildStructures;
import org.bukkit.Location;
import org.bukkit.World;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author NickM13
 * @since 5/2/2020
 */
public class ArenaCommand extends CommandTemplate {
    
    private static class Pos {
        double x, y, z;
        long yaw, pitch;
        
        public Pos(Location loc) {
            x = Math.round(loc.getX() * 4) / 4D;
            y = Math.round(loc.getY() * 4) / 4D;
            z = Math.round(loc.getZ() * 4) / 4D;
            yaw = Math.round(loc.getYaw() / 15) * 15;
            pitch = Math.round(loc.getPitch() / 15) * 15;
        }
        
        public Location toLocation(World world) {
            return new Location(world, x, y, z, yaw, pitch);
        }
        
        @Override
        public String toString() {
            return "(" + x + ", " + y + ", " + z + ", Y:" + yaw + ", P:" + pitch + ")";
        }
        
    }
    
    private final Map<UUID, Pos> posOneMap = new HashMap<>();
    private final Map<UUID, Pos> posTwoMap = new HashMap<>();
    
    public ArenaCommand() {
        super(ArenaCommand.class, "arena", Rank.DEVELOPER);
        setOptions("battleModes", cp -> BattleMode.getAllNames());
        setOptions("arenas", cp -> Arenas.getAll().keySet());
        setOptions("structures", cp -> BuildStructures.getNames());
    }
    
    @CommandAnnotation
    public void arenaCreate(CorePlayer sender,
            @LiteralArg("create") String l,
            @HelperArg("<displayName>") String name) {
        Arena arena = Arenas.createArena(name);
        if (arena == null) {
            error(sender, "Name already in use!");
        } else {
            success(sender, "Arena " + arena.getDisplayName() + "(" + arena.getName() + ") has been created");
        }
    }
    
    @CommandAnnotation
    public void arenaRename(CorePlayer sender,
            @LiteralArg("rename") String l,
            @OptionArg(listName="arenas") String arenaName,
            @HelperArg("<displayName>") String newName) {
        if (Arenas.renameArena(arenaName, newName)) {
            success(sender, "Arena renamed to " + newName);
        } else {
            error(sender, "Name already in use!");
        }
    }
    
    @CommandAnnotation
    public void arenaAddBorder(CorePlayer sender,
            @LiteralArg("add") String l,
            @LiteralArg("border") String l2,
            @OptionArg(listName="arenas") String arenaName) {
        if (!posOneMap.containsKey(sender.getUniqueId())) {
            error(sender, "Your pos1 isn't set!");
        } else if (!posTwoMap.containsKey(sender.getUniqueId())) {
            error(sender, "Your pos2 isn't set!");
        } else {
            Arena arena = Arenas.get(arenaName);
            Pos pos1 = posOneMap.get(sender.getUniqueId());
            Pos pos2 = posTwoMap.get(sender.getUniqueId());
            Dimension dim = new Dimension(
                    new Point(pos1.x, pos1.y, pos1.z),
                    new Point(pos2.x, pos2.y, pos2.z));
            arena.addBorder(dim);
            success(sender, "Added arena border: " + dim);
        }
    }
    
    @CommandAnnotation
    public void arenaPos1(CorePlayer sender,
            @LiteralArg("pos1") String l) {
        Pos pos1 = new Pos(sender.getLocation());
        posOneMap.put(sender.getUniqueId(), pos1);
        success(sender, "Pos1 set to " + pos1);
    }
    
    @CommandAnnotation
    public void arenaPos2(CorePlayer sender,
            @LiteralArg("pos2") String l) {
        Pos pos2 = new Pos(sender.getLocation());
        posTwoMap.put(sender.getUniqueId(), pos2);
        success(sender, "Pos2 set to " + pos2);
    }
    
    @CommandAnnotation
    public void arenaAddSpawn(CorePlayer sender,
            @LiteralArg("add") String l1,
            @LiteralArg("spawn") String l2,
            @OptionArg(listName="arenas") String arenaName,
            @Nullable @HelperArg("[insertPos]") Integer index) {
        Pos pos = new Pos(sender.getLocation());
        if (index != null) {
            if (Arenas.get(arenaName).insertSpawn(pos.toLocation(sender.getLocation().getWorld()), index)) {
                success(sender, "Spawn added " + pos);
            } else {
                error(sender, "Index out of bounds!");
            }
        } else {
            Arenas.get(arenaName).addSpawn(pos.toLocation(sender.getLocation().getWorld()));
            success(sender, "Spawn added " + pos);
        }
    }
    
    @CommandAnnotation
    public void arenaSetSpectator(CorePlayer sender,
            @LiteralArg("set") String l,
            @LiteralArg("spectator") String l2,
            @OptionArg(listName="arenas") String arenaName) {
        Pos pos = new Pos(sender.getLocation());
        Arenas.get(arenaName).setSpectatorSpawn(pos.toLocation(sender.getLocation().getWorld()));
        success(sender, "Spectator Spawn set to " + pos);
    }
    
    @CommandAnnotation
    public void arenaUnsetSpectator(CorePlayer sender,
            @LiteralArg("unset") String l,
            @LiteralArg("spectator") String l2,
            @OptionArg(listName="arenas") String arenaName) {
        Arenas.get(arenaName).setSpectatorSpawn(null);
        success(sender, "Spectator Spawn has been removed");
    }
    
    @CommandAnnotation
    public void arenaAddMode(CorePlayer sender,
            @LiteralArg("add") String l1,
            @LiteralArg("mode") String l2,
            @OptionArg(listName="arenas") String arenaName,
            @OptionArg(listName="battleModes") String modeName) {
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
            @OptionArg(listName="arenas") String arenaName,
            @OptionArg(listName="battleModes") String modeName) {
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
            @OptionArg(listName="arenas") String arenaName,
            @OptionArg(listName="structures") String structureName) {
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
            @OptionArg(listName="arenas") String arenaName,
            @OptionArg(listName="structures") String structureName) {
        if (Arenas.get(arenaName).removeStructure(structureName)) {
            success(sender, "Removed structure from arena");
        } else {
            error(sender, "Arena doesn't use that structure!");
        }
    }
    
    @CommandAnnotation
    public void arenaSetTeamSize(CorePlayer sender,
            @LiteralArg("set") String l,
            @LiteralArg("teamsize") String l2,
            @OptionArg(listName="arenas") String arenaName,
            Integer teamSize) {
        Arenas.get(arenaName).setTeamSize(teamSize);
        success(sender, "Team size set to " + teamSize);
    }
    
}
