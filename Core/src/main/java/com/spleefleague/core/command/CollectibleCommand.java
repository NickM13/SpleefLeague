package com.spleefleague.core.command;

import com.spleefleague.core.chat.Chat;
import com.spleefleague.core.chat.ChatUtils;
import com.spleefleague.core.command.annotation.CommandAnnotation;
import com.spleefleague.core.command.annotation.HelperArg;
import com.spleefleague.core.command.annotation.LiteralArg;
import com.spleefleague.core.command.annotation.OptionArg;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.collectible.Collectible;
import com.spleefleague.core.player.rank.Rank;
import com.spleefleague.core.vendor.Vendorables;
import org.bukkit.command.CommandSender;

/**
 * @author NickM13
 * @since 5/5/2020
 */
public class CollectibleCommand extends CoreCommand {

    private final Class<? extends Collectible> collectibleClass;
    private final String name;

    protected CollectibleCommand(Class<? extends Collectible> collectibleClazz, String name, Rank requiredRank, Rank... additionalRanks) {
        super(name, requiredRank, additionalRanks);
        this.name = name;
        this.collectibleClass = collectibleClazz;
        setOptions("collectibles", cp -> Vendorables.getAll(collectibleClazz).keySet());
    }

    @CommandAnnotation
    public void collectible(CorePlayer sender) {
        sender.sendMessage(ChatUtils.centerChat("[ Collectible Commands ]"));
        sender.sendMessage(Chat.DEFAULT + "/" + name + " create <identifier>");
        sender.sendMessage(Chat.DEFAULT + "/" + name + " set name <displayName>");
        sender.sendMessage(Chat.DEFAULT + "/" + name + " set description <desc>");
        sender.sendMessage(Chat.DEFAULT + "/" + name + " set type <type>");
        sender.sendMessage(Chat.DEFAULT + "/" + name + " set cost <coins>");
        sender.sendMessage(Chat.DEFAULT + "/" + name + " add <player> <customModelData>");
        sender.sendMessage(Chat.DEFAULT + "/" + name + " remove <player> <customModelData>");
    }

    @CommandAnnotation
    public void collectibleGet(CorePlayer sender,
                               @LiteralArg("get") String l,
                               @OptionArg(listName = "collectibles") String identifier) {
        Collectible collectible = Vendorables.get(collectibleClass, identifier);
        if (collectible != null) {
            sender.setHeldItem(collectible.getDisplayItem());
            success(sender, "Given copy of display item for collectible " + identifier);
        } else {
            error(sender, "Collectible not found " + identifier);
        }
    }

    @CommandAnnotation
    public void collectibleCreate(CorePlayer sender,
                                  @LiteralArg("create") String l,
                                  @HelperArg("<identifier>") String identifier,
                                  @HelperArg("<displayName>") String displayName) {
        identifier = identifier.toLowerCase();
        displayName = Chat.colorize(displayName);
        if (Vendorables.contains(collectibleClass, identifier)) {
            error(sender, "That collectible already exists!");
        } else {
            Collectible collectible = Collectible.create(collectibleClass, identifier, displayName);
            if (collectible != null) {
                sender.setHeldItem(collectible.getDisplayItem());
                success(sender, "Created collectible (" + identifier + ": " + displayName + Chat.DEFAULT + ")");
            } else {
                error(sender, "Could not create item!");
            }
        }
    }

    @CommandAnnotation
    public void collectibleDestroy(CorePlayer sender,
                                   @LiteralArg("destroy") String l,
                                   @OptionArg(listName = "collectibles") String identifier) {
        if (!Vendorables.contains(collectibleClass, identifier)) {
            error(sender, "That collectible doesn't exists!");
        } else {
            Collectible.destroy(collectibleClass, identifier);
            success(sender, "Destroyed collectible " + identifier);
        }
    }

    @CommandAnnotation
    public void collectibleClone(CorePlayer sender,
                                 @LiteralArg("clone") String l,
                                 @OptionArg(listName = "collectibles") String identifier,
                                 @HelperArg("<cloneTo>") String cloneTo) {
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
                                   @HelperArg("<displayName>") String displayName) {
        Collectible collectible = Vendorables.get(collectibleClass, sender.getHeldItem());
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
                                          @HelperArg("<description>") String description) {
        Collectible collectible = Vendorables.get(collectibleClass, sender.getHeldItem());
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
                                    @HelperArg("<customModelData>") Integer customModelData) {
        Collectible collectible = Vendorables.get(collectibleClass, sender.getHeldItem());
        if (collectible != null) {
            Integer prevModel = collectible.getCustomModelDataNbt();
            collectible.setCustomModelDataNbt(customModelData);
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
                                   @HelperArg("<coins>") Integer coins) {
        Collectible collectible = Vendorables.get(collectibleClass, sender.getHeldItem());
        if (collectible != null) {
            collectible.setCoinCost(coins);
            sender.setHeldItem(collectible.getDisplayItem());
            success(sender, "Coin cost set to " + coins);
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
        if (target.getCollectibles().add(Vendorables.get(collectibleClass, identifier))) {
            sender.sendMessage("Added collectible " + identifier + " to " + target.getDisplayNamePossessive() + " collection");
        } else {
            sender.sendMessage(target.getDisplayName() + " already had " + identifier);
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

}
