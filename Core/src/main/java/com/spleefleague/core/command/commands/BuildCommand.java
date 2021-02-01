package com.spleefleague.core.command.commands;

import com.comphenix.protocol.wrappers.BlockPosition;
import com.spleefleague.core.chat.Chat;
import com.spleefleague.core.command.CoreCommand;
import com.spleefleague.core.command.annotation.*;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.rank.CoreRank;
import com.spleefleague.core.world.build.BuildStructure;
import com.spleefleague.core.world.build.BuildStructures;
import com.spleefleague.core.world.build.BuildWorld;
import com.spleefleague.core.world.build.BuildWorldPlayer;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Material;

import java.util.HashSet;
import java.util.Set;

/**
 * @author NickM13
 * @since 4/26/2020
 */
public class BuildCommand extends CoreCommand {
    
    public BuildCommand() {
        super("build", CoreRank.DEVELOPER);
        setDescription("For building structures used in fake worlds");
        setOptions("structures", cp -> BuildStructures.getNames());
        setOptions("materials", (cp) -> getMaterialNames());
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
    public void buildCreate(CorePlayer sender,
            @LiteralArg("create") String l,
            @HelperArg(value="<structureName>") String structureName) {
        if (BuildStructures.create(sender, structureName)) {
            BuildStructures.edit(sender, structureName);
            success(sender, "Structure " + structureName + " created and build world entered!");
        } else {
            error(sender, "Structure already exists!");
        }
    }
    
    @CommandAnnotation
    public void buildInvite(CorePlayer sender,
            @LiteralArg("invite") String l,
            @CorePlayerArg(allowSelf = false) CorePlayer target) {
        if (sender.isInBuildWorld()) {
            Chat.sendRequest(target,
                    sender,
                    (r, s) -> {
                        if (!r.isInGlobal()) {
                            error(r, "You're already in a fake world!");
                        } else if (!s.isInBuildWorld()) {
                            error(r, "They aren't in a build world anymore!");
                        } else {
                            s.getBuildWorld().addPlayer(r);
                            success(r, "Joined " + s.getDisplayNamePossessive() + " world");
                        }
                    },
                    sender.getChatName(), new TextComponent(" invited you to join their build world"));
            success(sender, "Invitation sent");
        } else {
            error(sender, "You aren't in a build world!");
        }
    }
    
    @CommandAnnotation
    public void buildJoin(CorePlayer sender,
            @LiteralArg("join") String l,
            @CorePlayerArg(allowSelf = false) CorePlayer target) {
        if (!sender.isInGlobal()) {
            error(sender, "You're already in a fake world!");
        } else if (!target.isInBuildWorld()) {
            error(sender, "They aren't in a build world!");
        } else {
            target.getBuildWorld().addPlayer(sender);
            success(sender, "Joined " + target.getDisplayNamePossessive() + " world");
        }
    }
    
    @CommandAnnotation
    public void buildEdit(CorePlayer sender,
            @LiteralArg("edit") String l,
            @OptionArg(listName="structures") String structureName) {
        switch (BuildStructures.edit(sender, structureName)) {
            case 0: success(sender, "Editing structure " + structureName);  break;
            case 1: error(sender, "Structure does not exist!");             break;
            case 2: error(sender, "Structure is under construction!");      break;
            case 3: error(sender, "You're already in a fake world!");       break;
            case 4: error(sender, "Structure would overlap with an existing block!"); break;
        }
    }
    
    @CommandAnnotation
    public void buildSave(CorePlayer sender,
            @LiteralArg("save") String l) {
        BuildWorld buildWorld = sender.getBuildWorld();
        if (buildWorld != null) {
            buildWorld.saveToStructure();
            success(sender, "Saved structure " + buildWorld.getStructure().getName());
        } else {
            error(sender, "You aren't in a build world!");
        }
    }
    
    @CommandAnnotation(confirmation = true)
    public void buildDestroy(CorePlayer sender,
            @LiteralArg("destroy") String l,
            @OptionArg(listName="structures") String structureName) {
        if (BuildStructures.get(structureName) != null) {
            if (BuildStructures.destroy(structureName)) {
                success(sender, "Structure destroyed");
            } else {
                error(sender, "Structure can not be destroyed!");
            }
        } else {
            error(sender, "Structure does not exist!");
        }
    }
    
    @CommandAnnotation
    public void buildClone(CorePlayer sender,
            @LiteralArg("clone") String l,
            @OptionArg(listName="structures") String structureName,
            @HelperArg("<structureName>") String newName) {
        BuildStructure structure = BuildStructures.get(structureName);
        if (!structure.isUnderConstruction()) {
            if (BuildStructures.create(sender, newName)) {
                BuildStructure clone = BuildStructures.get(newName);
                structure.getFakeBlocks().forEach(clone::setBlock);
                BuildStructures.save(clone);
                success(sender, "Cloned structure to " + newName);
            } else {
                error(sender, "Structure already exists!");
            }
        } else {
            error(sender, "Structure is under construction!");
        }
    }
    
    @CommandAnnotation(confirmation = true)
    public void buildSetOrigin(CorePlayer sender,
            @LiteralArg("set") String l,
            @LiteralArg("origin") String o) {
        if (sender.isInBuildWorld()) {
            BlockPosition origin = new BlockPosition(
                    sender.getLocation().getBlockX(),
                    sender.getLocation().getBlockY(),
                    sender.getLocation().getBlockZ());
            sender.getBuildWorld().setOrigin(origin);
            success(sender, "Origin relocated to " + origin);
        } else {
            error(sender, "You aren't in a build world!");
        }
    }
    
    @CommandAnnotation(confirmation = true)
    public void buildShift(CorePlayer sender,
            @LiteralArg("shift") String l,
            @HelperArg("<x>") Integer x,
            @HelperArg("<y>") Integer y,
            @HelperArg("<z>") Integer z) {
        if (sender.isInBuildWorld()) {
            sender.getBuildWorld().shift(new BlockPosition(x, y, z));
            success(sender, "Structure has been moved");
        } else {
            error(sender, "You aren't in a build world!");
        }
    }
    
    @CommandAnnotation
    public void buildPos1(CorePlayer sender,
            @LiteralArg("pos1") String l) {
        if (sender.isInBuildWorld()) {
            BuildWorldPlayer bwp = sender.getBuildWorld().getPlayerMap().get(sender.getUniqueId());
            BlockPosition pos = new BlockPosition(
                    sender.getLocation().getBlockX(),
                    sender.getLocation().getBlockY(),
                    sender.getLocation().getBlockZ());
            bwp.setPos1(pos);
            success(sender, "Pos1 set to " + pos);
        } else {
            error(sender, "You aren't in a build world!");
        }
    }
    
    @CommandAnnotation
    public void buildPos2(CorePlayer sender,
            @LiteralArg("pos2") String l) {
        if (sender.isInBuildWorld()) {
            BuildWorldPlayer bwp = sender.getBuildWorld().getPlayerMap().get(sender.getUniqueId());
            BlockPosition pos = new BlockPosition(
                    sender.getLocation().getBlockX(),
                    sender.getLocation().getBlockY(),
                    sender.getLocation().getBlockZ());
            bwp.setPos2(pos);
            success(sender, "Pos2 set to " + pos);
        } else {
            error(sender, "You aren't in a build world!");
        }
    }
    
    @CommandAnnotation(confirmation = true)
    public void buildFill(CorePlayer sender,
            @LiteralArg("fill") String l,
            @OptionArg(listName="materials") String materialName) {
        if (sender.isInBuildWorld()) {
            BuildWorldPlayer bwp = sender.getBuildWorld().getPlayerMap().get(sender.getUniqueId());
            sender.getBuildWorld().fill(bwp.getPosBox(), Material.valueOf(materialName.toUpperCase()));
            success(sender, "Structure has been filled");
        } else {
            error(sender, "You aren't in a build world!");
        }
    }

    @CommandAnnotation
    public void buildWorldify(CorePlayer sender,
                           @LiteralArg("worldify") String l) {
        if (sender.isInBuildWorld()) {
            BuildWorldPlayer bwp = sender.getBuildWorld().getPlayerMap().get(sender.getUniqueId());
            Chat.sendRequest(sender,
                    "StructureWorldify",
                    (r, s) -> {
                        sender.getBuildWorld().worldify(bwp.getPosBox());
                        success(sender, "Structure has been worldified");
                    },
                    new TextComponent("Are you sure you want to send the selected area to the real world?  This will overwrite current blocks, no undoing!"));
        } else {
            error(sender, "You aren't in a build world!");
        }
    }

    @CommandAnnotation
    public void buildBuildify(CorePlayer sender,
                          @LiteralArg("buildify") String l) {
        if (sender.isInBuildWorld()) {
            BuildWorldPlayer bwp = sender.getBuildWorld().getPlayerMap().get(sender.getUniqueId());
            Chat.sendRequest(sender,
                    "StructureBuildify",
                    (r, s) -> {
                        sender.getBuildWorld().buildify(bwp.getPosBox());
                        success(sender, "Structure has been buildified");
                    },
                    new TextComponent("Are you sure you want to send the selected area to the build world?  This will overwrite current blocks, no undoing!"));
        } else {
            error(sender, "You aren't in a build world!");
        }
    }
    
}
