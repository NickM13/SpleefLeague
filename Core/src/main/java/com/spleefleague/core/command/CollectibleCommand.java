package com.spleefleague.core.command;

import com.spleefleague.core.Core;
import com.spleefleague.core.chat.Chat;
import com.spleefleague.core.command.annotation.*;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.collectible.Collectible;
import com.spleefleague.core.player.collectible.CollectibleSkin;
import com.spleefleague.core.player.rank.CoreRank;
import com.spleefleague.core.vendor.Vendorable;
import com.spleefleague.core.vendor.Vendorables;
import com.spleefleague.coreapi.utils.packet.spigot.removal.PacketSpigotRemovalCollectible;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Objects;

/**
 * @author NickM13
 * @since 5/5/2020
 */
public class CollectibleCommand extends CoreCommand {

    private final Class<? extends Collectible> collectibleClass;
    private final String name;

    protected CollectibleCommand(Class<? extends Collectible> collectibleClazz, String name, CoreRank requiredRank, CoreRank... additionalRanks) {
        super(name, requiredRank, additionalRanks);
        this.name = name;
        this.collectibleClass = collectibleClazz;
        setOptions("collectibles", cp -> Vendorables.getAll(collectibleClazz).keySet());
        setOptions("crates", cp -> Core.getInstance().getCrateManager().getCrateNames());
        setOptions("skins", pi -> Vendorables.get(collectibleClazz, pi.getReverse().get(0)).getSkinIds());
    }

    @CommandAnnotation
    public void collectibleInfo(CorePlayer sender,
                                @LiteralArg("info") String l) {
        Collectible collectible = Vendorables.get(collectibleClass, sender.getHeldItem());
        if (collectible != null) {
            for (Field field : collectible.getFields()) {
                try {
                    success(sender, field.getName() + ": " + field.get(collectible));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        } else {
            error(sender, "That's not a registered collectible!");
        }
    }

    @CommandAnnotation
    public void collectibleGet(CorePlayer sender,
                               @LiteralArg("get") String l,
                               @OptionArg(listName = "collectibles") String identifier) {
        Collectible collectible = Vendorables.get(collectibleClass, identifier);
        if (collectible != null) {
            sender.getPlayer().getInventory().addItem(collectible.getDisplayItem());
            success(sender, "Given copy of display item for collectible " + identifier);
        } else {
            error(sender, "Collectible not found " + identifier);
        }
    }

    @CommandAnnotation
    public void collectibleCreate(CorePlayer sender,
                                  @LiteralArg("create") String l,
                                  @HelperArg("identifier") String identifier,
                                  @HelperArg("displayName") String displayName) {
        identifier = identifier.toLowerCase();
        displayName = Chat.colorize(displayName);
        if (Vendorables.contains(collectibleClass, identifier)) {
            error(sender, "That collectible already exists!");
        } else {
            Collectible collectible = Collectible.create(collectibleClass, identifier, displayName);
            if (collectible != null) {
                sender.setHeldItem(collectible.getDisplayItem());
                success(sender, "Created collectible (" + identifier + ": " + displayName + Chat.DEFAULT + ")");
                sender.getCollectibles().add(collectible);
            } else {
                error(sender, "Could not create item!");
            }
        }
    }

    @CommandAnnotation
    public void collectibleDestroy(CorePlayer sender,
                                   @LiteralArg("destroy") String l,
                                   @OptionArg(listName = "collectibles") String identifier,
                                   @OptionArg(listName = "crates") String crateName) {
        if (!Vendorables.contains(collectibleClass, identifier)) {
            error(sender, "That collectible doesn't exists!");
        } else {
            Collectible collectible = Vendorables.get(collectibleClass, identifier);
            if (collectible != null) {
                Core.getInstance().sendPacket(new PacketSpigotRemovalCollectible(sender.getUniqueId(), collectible.getParentType(), collectible.getIdentifier(), crateName));
                Collectible.destroy(collectibleClass, identifier);
                success(sender, "Destroyed collectible " + identifier);
            } else {
                error(sender, "Failed to destroy collectible " + identifier);
            }
        }
    }

    @CommandAnnotation
    public void collectibleClone(CorePlayer sender,
                                 @LiteralArg("clone") String l,
                                 @OptionArg(listName = "collectibles") String identifier,
                                 @HelperArg("cloneTo") String cloneTo) {
        cloneTo = cloneTo.toLowerCase();
        Collectible collectible = Collectible.clone(collectibleClass, identifier, cloneTo);
        if (collectible != null) {
            sender.setHeldItem(collectible.getDisplayItem());
            success(sender, "Cloned collectible " + identifier + " to " + cloneTo);
        } else {
            error(sender, "That collectible already exists!");
        }
    }

    @CommandAnnotation
    public void collectibleSetName(CorePlayer sender,
                                   @LiteralArg("set") String e,
                                   @LiteralArg("name") String l,
                                   @OptionArg(listName = "collectibles") String identifier,
                                   @HelperArg("displayName") String displayName) {
        Collectible collectible = Vendorables.get(collectibleClass, identifier);
        if (collectible != null) {
            String prevName = collectible.getName();
            collectible.setName(displayName);
            success(sender, "Renamed " + collectible.getIdentifier() + " from " + prevName + Chat.DEFAULT + " to " + displayName);
            sender.setHeldItem(collectible.getDisplayItem());
        } else {
            error(sender, "That's not a registered collectible!");
        }
    }

    @CommandAnnotation
    public void collectibleSetDescription(CorePlayer sender,
                                          @LiteralArg("set") String e,
                                          @LiteralArg("description") String l,
                                          @OptionArg(listName = "collectibles") String identifier,
                                          @HelperArg("description") String description) {
        Collectible collectible = Vendorables.get(collectibleClass, identifier);
        if (collectible != null) {
            collectible.setDescription(description);
            success(sender, "Changed " + collectible.getIdentifier() + " description to:");
            sender.sendMessage(description);
            sender.setHeldItem(collectible.getDisplayItem());
        } else {
            error(sender, "That's not a registered collectible!");
        }
    }

    @CommandAnnotation
    public void collectibleSetModel(CorePlayer sender,
                                    @LiteralArg("set") String e,
                                    @LiteralArg("model") String l,
                                    @OptionArg(listName = "collectibles") String identifier,
                                    @HelperArg("customModelData") Integer customModelData) {
        Collectible collectible = Vendorables.get(collectibleClass, identifier);
        if (collectible != null) {
            Integer prevModel = collectible.getCustomModelData();
            collectible.setCustomModelData(customModelData);
            success(sender, "Changed customModelData value of " + collectible.getIdentifier() + " from " + prevModel + " to " + customModelData);
            sender.setHeldItem(collectible.getDisplayItem());
        } else {
            error(sender, "That's not a registered collectible!");
        }
    }

    @CommandAnnotation
    public void collectibleSetCoin(CorePlayer sender,
                                   @LiteralArg("set") String l1,
                                   @LiteralArg("cost") String l,
                                   @OptionArg(listName = "collectibles") String identifier,
                                   @HelperArg("coins") Integer coins) {
        Collectible collectible = Vendorables.get(collectibleClass, identifier);
        if (collectible != null) {
            collectible.setCoinCost(coins);
            sender.setHeldItem(collectible.getDisplayItem());
            success(sender, "Coin cost set to " + coins);
        } else {
            error(sender, "That's not a registered collectible!");
        }
    }

    @CommandAnnotation
    public void collectibleSetRarity(CorePlayer sender,
                                     @LiteralArg("set") String l1,
                                     @LiteralArg("rarity") String l,
                                     @OptionArg(listName = "collectibles") String identifier,
                                     @EnumArg Vendorable.Rarity rarity) {
        Collectible collectible = Vendorables.get(collectibleClass, identifier);
        if (collectible != null) {
            collectible.setRarity(rarity);
            sender.setHeldItem(collectible.getDisplayItem());
            success(sender, "Rarity set to " + rarity);
        } else {
            error(sender, "That's not a registered collectible!");
        }
    }

    @CommandAnnotation
    public void collectibleSetUnlock(CorePlayer sender,
                                     @LiteralArg("set") String l1,
                                     @LiteralArg("unlock") String l,
                                     @OptionArg(listName = "collectibles") String identifier,
                                     @EnumArg Vendorable.UnlockType type) {
        Collectible collectible = Vendorables.get(collectibleClass, identifier);
        if (collectible != null) {
            collectible.setUnlockType(type);
            sender.setHeldItem(collectible.getDisplayItem());
            success(sender, "Unlock type set to " + type);
        } else {
            error(sender, "That's not a registered collectible!");
        }
    }

    /**
     * Adds a collectible to the players collection
     *
     * @param sender     Sender
     * @param l          add
     * @param target     Target
     * @param identifier Collectible Identifier
     */
    @CommandAnnotation
    public void collectibleAdd(CommandSender sender,
                               @LiteralArg("add") String l,
                               CorePlayer target,
                               @OptionArg(listName = "collectibles") String identifier) {
        Collectible collectible = Vendorables.get(collectibleClass, identifier);
        if (target.getCollectibles().add(collectible)) {
            sender.sendMessage("Added collectible " + identifier + " to " + target.getDisplayNamePossessive() + " collection");
            Core.getInstance().sendMessage(target,
                    ChatColor.GRAY + "You've received " + collectible.getDisplayName() + ChatColor.GRAY + "!");
        } else {
            sender.sendMessage(target.getDisplayName() + " already had " + identifier);
        }
    }

    /**
     * Adds a collectible to the players collection
     *
     * @param sender     Sender
     * @param l          add
     * @param targets    Targets
     * @param identifier Collectible Identifier
     */
    @CommandAnnotation
    public void collectibleAdds(CommandSender sender,
                                @LiteralArg("add") String l,
                                List<CorePlayer> targets,
                                @OptionArg(listName = "collectibles") String identifier) {
        Collectible collectible = Vendorables.get(collectibleClass, identifier);
        for (CorePlayer target : targets) {
            if (target.getCollectibles().add(collectible)) {
                sender.sendMessage("Added collectible " + identifier + " to " + target.getDisplayNamePossessive() + " collection");
                Core.getInstance().sendMessage(target,
                        ChatColor.GRAY + "You've received " + collectible.getDisplayName() + ChatColor.GRAY + "!");
            } else {
                sender.sendMessage(target.getDisplayName() + " already had " + identifier);
            }
        }
    }

    /**
     * Removes a collectible from a players collection
     *
     * @param sender     Sender
     * @param l          remove
     * @param target     Target
     * @param identifier Collectible Identifier
     */
    @CommandAnnotation
    public void collectibleRemove(CommandSender sender,
                                  @LiteralArg("remove") String l,
                                  CorePlayer target,
                                  @OptionArg(listName = "collectibles") String identifier) {
        if (target.getCollectibles().remove(Vendorables.get(collectibleClass, identifier))) {
            success(sender, "Removed collectible " + identifier + " from " + target.getDisplayNamePossessive() + " collection");
        } else {
            error(sender, target.getDisplayName() + " didn't have " + identifier);
        }
    }

    @CommandAnnotation
    public void collectibleSkinCreate(CorePlayer sender,
                                      @LiteralArg("skin") String l1,
                                      @LiteralArg("create") String l2,
                                      @OptionArg(listName = "collectibles") String parent,
                                      @HelperArg("identifier") String identifier,
                                      @NumberArg @HelperArg("cmd") Integer cmd,
                                      @HelperArg("displayName") String name) {
        Collectible collectible = Vendorables.get(collectibleClass, parent);
        if (collectible == null) {
            error(sender, "Collectible not found");
            return;
        }
        if (collectible.createSkin(identifier, cmd, name)) {
            sender.getCollectibles().addSkin(collectible, identifier);
            success(sender, "Skin created " + parent + ":" + identifier + " with cmd=" + cmd + " and name " + name);
        } else {
            error(sender, "Skin already exists!");
        }
    }

    @CommandAnnotation
    public void collectibleSkinDestroy(CorePlayer sender,
                                       @LiteralArg("skin") String l1,
                                       @LiteralArg("destroy") String l2,
                                       @OptionArg(listName = "collectibles") String parent,
                                       @OptionArg(listName = "skins") String identifier) {
        Collectible collectible = Vendorables.get(collectibleClass, parent);
        if (collectible == null) {
            error(sender, "Collectible not found");
            return;
        }
        collectible.destroySkin(identifier);
        success(sender, "Skin destroyed " + parent + ":" + identifier);
    }

    @CommandAnnotation
    public void collectibleSkinAdd(CommandSender sender,
                                   @LiteralArg("skin") String l1,
                                   @LiteralArg("add") String l2,
                                   CorePlayer target,
                                   @OptionArg(listName = "collectibles") String parent,
                                   @OptionArg(listName = "skins") String identifier) {
        switch (target.getCollectibles().addSkin(Objects.requireNonNull(Vendorables.get(collectibleClass, parent)), identifier)) {
            case 0:
                success(sender, "Added collectible skin " + parent + ":" + identifier + " to " + target.getDisplayNamePossessive() + " collection");
                break;
            case 1:
                sender.sendMessage("They don't have the parent of that skin!");
                break;
            case 2:
                sender.sendMessage("They already have that!");
                break;
            case 3:
                sender.sendMessage("Null collectible");
                break;
        }
    }

    @CommandAnnotation
    public void collectibleSkinRemove(CommandSender sender,
                                      @LiteralArg("skin") String l1,
                                      @LiteralArg("remove") String l2,
                                      CorePlayer target,
                                      @OptionArg(listName = "collectibles") String parent,
                                      @OptionArg(listName = "skins") String identifier) {
        if (target.getCollectibles().removeSkin(Vendorables.get(collectibleClass, parent), identifier)) {
            success(sender, "Removed collectible " + parent + ":" + identifier + " from " + target.getDisplayNamePossessive() + " collection");
        } else {
            error(sender, "They don't have that skin!");
        }
    }

    @CommandAnnotation
    public void collectibleSkinSetName(CorePlayer sender,
                                       @LiteralArg("skin") String l1,
                                       @LiteralArg("set") String l2,
                                       @LiteralArg("name") String l3,
                                       @OptionArg(listName = "collectibles") String parent,
                                       @OptionArg(listName = "skins") String identifier,
                                       @HelperArg("name") String name) {
        Collectible collectible = Vendorables.get(collectibleClass, parent);
        if (collectible == null) {
            error(sender, "Collectible not found");
            return;
        }
        CollectibleSkin skin = collectible.getSkin(identifier);
        if (skin == null) {
            error(sender, "Skin not found");
            return;
        }
        skin.setDisplayName(name);
        success(sender, "Skin name set to " + name);
    }

    @CommandAnnotation
    public void collectibleSkinSetCmd(CorePlayer sender,
                                      @LiteralArg("skin") String l1,
                                      @LiteralArg("set") String l2,
                                      @LiteralArg("model") String l3,
                                      @OptionArg(listName = "collectibles") String parent,
                                      @OptionArg(listName = "skins") String identifier,
                                      @NumberArg @HelperArg("cmd") Integer cmd) {
        Collectible collectible = Vendorables.get(collectibleClass, parent);
        if (collectible == null) {
            error(sender, "Collectible not found");
            return;
        }
        CollectibleSkin skin = collectible.getSkin(identifier);
        if (skin == null) {
            error(sender, "Skin not found");
            return;
        }
        skin.setCmd(cmd);
        success(sender, "Skin cmd set to " + cmd);
    }

    @CommandAnnotation
    public void collectibleAddall(CorePlayer sender,
                                  @LiteralArg("admin") String l1) {
        for (Collectible col : Vendorables.getAll(collectibleClass).values()) {
            sender.getCollectibles().add(col);
            for (String skin : col.getSkinIds()) {
                sender.getCollectibles().addSkin(col, skin);
            }
        }
        success(sender, "All collectibles have been added to your collection");
    }

}
