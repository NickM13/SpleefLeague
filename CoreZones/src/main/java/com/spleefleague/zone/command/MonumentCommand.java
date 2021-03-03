package com.spleefleague.zone.command;

import com.google.common.collect.Sets;
import com.spleefleague.core.Core;
import com.spleefleague.core.chat.Chat;
import com.spleefleague.core.chat.NpcMessage;
import com.spleefleague.core.command.CoreCommand;
import com.spleefleague.core.command.annotation.*;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.collectible.Collectible;
import com.spleefleague.core.player.purse.CoreCurrency;
import com.spleefleague.core.player.rank.CoreRank;
import com.spleefleague.core.util.variable.Position;
import com.spleefleague.core.vendor.Vendorable;
import com.spleefleague.core.vendor.Vendorables;
import com.spleefleague.zone.CoreZones;
import com.spleefleague.zone.monuments.Monument;
import com.spleefleague.zone.monuments.MonumentManager;
import com.spleefleague.zone.monuments.MonumentStage;
import com.spleefleague.zone.player.ZonePlayer;
import com.spleefleague.zone.player.fragments.PlayerFragments;
import org.bukkit.Bukkit;
import org.bukkit.Sound;

import javax.annotation.Nullable;

/**
 * @author NickM13
 * @since 2/13/2021
 */
public class MonumentCommand extends CoreCommand {

    public MonumentCommand() {
        super("monument", CoreRank.DEVELOPER);

        setOptions("monuments", pi -> CoreZones.getInstance().getMonumentManager().getMonumentNames());
        setOptions("fragments", pi -> CoreZones.getInstance().getFragmentManager().getFragmentNames());

        setOptions("types", pi -> Vendorable.getParentTypeNames());
        setOptions("collectibles", pi -> Vendorables.getAll(pi.getReverse().get(0)).keySet());
        setOptions("skins", pi -> ((Collectible) Vendorables.get(pi.getReverse().get(1), pi.getReverse().get(0))).getSkinIds());
    }

    @CommandAnnotation
    public void monumentCreate(CorePlayer sender,
                               @LiteralArg("create") String l1,
                               @HelperArg("identifier") String identifier) {
        if (identifier.contains(" ")) {
            error(sender, "/fragment create <identifier>");
            return;
        }
        if (CoreZones.getInstance().getMonumentManager().create(identifier)) {
            CoreZones.getInstance().sendMessage(sender, "Monument Created: " + identifier);
            return;
        }
        error(sender, "Monument already exists");
    }

    @CommandAnnotation(description = "Prefix of the monument, uses 'structure'_0, 'structure'_1, ... up to _5")
    public void monumentSetStructure(CorePlayer sender,
                                     @LiteralArg("set") String l1,
                                     @LiteralArg("structure") String l2,
                                     @OptionArg(listName = "monuments") String identifier,
                                     String structure) {
        CoreZones.getInstance().getMonumentManager().setStructure(identifier, structure);
        success(sender, "Monument structure set to: " + structure);
    }

    @CommandAnnotation
    public void monumentSetFragment(CorePlayer sender,
                                    @LiteralArg("set") String l1,
                                    @LiteralArg("fragment") String l2,
                                    @OptionArg(listName = "monuments") String identifier,
                                    @OptionArg(listName = "fragments") String fragment) {
        CoreZones.getInstance().getMonumentManager().setFragment(identifier, fragment);
        success(sender, "Monument fragment set to: " + fragment);
    }

    @CommandAnnotation
    public void monumentSetPosition(CorePlayer sender,
                                    @LiteralArg("set") String l1,
                                    @LiteralArg("position") String l2,
                                    @OptionArg(listName = "monuments") String identifier) {
        Position pos = new Position(sender.getLocation(), 1);
        CoreZones.getInstance().getMonumentManager().setPosition(identifier, pos);
        success(sender, "Monument position set to: " + pos.toString());
    }

    @CommandAnnotation
    public void monumentSetDrain(CorePlayer sender,
                                 @LiteralArg("set") String l1,
                                 @LiteralArg("drain") String l2,
                                 @OptionArg(listName = "monuments") String identifier) {
        Position pos = new Position(sender.getLocation().add(-0.5, 0, -0.5), 1).add(0.5, 0, 0.5);
        CoreZones.getInstance().getMonumentManager().setDrain(identifier, pos);
        success(sender, "Monument drain position set to: " + pos.toString());
    }

    @CommandAnnotation
    public void monumentSetDrainTo(CorePlayer sender,
                                 @LiteralArg("set") String l1,
                                 @LiteralArg("drainTo") String l2,
                                 @OptionArg(listName = "monuments") String identifier) {
        Position pos = new Position(sender.getLocation().add(-0.5, 0, -0.5), 1).add(0.5, 0, 0.5);
        CoreZones.getInstance().getMonumentManager().setDrainTo(identifier, pos);
        success(sender, "Monument draining to position set to: " + pos.toString());
    }

    @CommandAnnotation
    public void monumentSetDrainSound(CorePlayer sender,
                                      @LiteralArg("set") String l1,
                                      @LiteralArg("drainSound") String l2,
                                      @OptionArg(listName = "monuments") String identifier,
                                      @EnumArg Sound sound) {
        CoreZones.getInstance().getMonumentManager().setDrainSound(identifier, sound);
        success(sender, "Monument drain sound set to: " + sound.toString());
        for (int i = 0; i < 12; i++) {
            int finalI = i;
            Bukkit.getScheduler().runTaskLater(Core.getInstance(), () -> {
                sender.getPlayer().playSound(sender.getLocation(), sound, 0.2f, (float) Math.pow(2, finalI / 12.f));
            }, i * 5);
        }
    }

    @CommandAnnotation
    public void monumentSetDrainSound(CorePlayer sender,
                                      @LiteralArg("set") String l1,
                                      @LiteralArg("priority") String l2,
                                      @OptionArg(listName = "monuments") String identifier,
                                      @NumberArg Integer priority) {
        CoreZones.getInstance().getMonumentManager().setPriority(identifier, priority);
        success(sender, "Monument priority set to: " + priority);
    }

    @CommandAnnotation
    public void monumentSetStageMessage(CorePlayer sender,
                                        @LiteralArg("stage") String l2,
                                        @LiteralArg("msg") String l3,
                                        @OptionArg(listName = "monuments") String identifier,
                                        @HelperArg("stage") @NumberArg(minValue = 0, maxValue = 5) Integer stage,
                                        @HelperArg("profilePic") String profile,
                                        @HelperArg("name") String name,
                                        @HelperArg("message") String message) {
        NpcMessage npcMessage = NpcMessage.fromCommand(profile, name, message);
        CoreZones.getInstance().getMonumentManager().setStageMessage(identifier, stage, npcMessage);
        success(sender, "Stage message set");
        Chat.sendNpcMessage(sender, npcMessage);
    }

    @CommandAnnotation
    public void monumentStageAddCurrency(CorePlayer sender,
                                         @LiteralArg("stage") String l1,
                                         @LiteralArg("add") String l2,
                                         @LiteralArg("currency") String l3,
                                         @OptionArg(listName = "monuments") String identifier,
                                         @HelperArg("stage") @NumberArg(minValue = 0, maxValue = 5) Integer stage,
                                         @EnumArg CoreCurrency currency,
                                         @NumberArg Integer count) {
        CoreZones.getInstance().getMonumentManager().addStageReward(identifier, stage, new MonumentStage.RewardCurrency(currency, count));
        success(sender, "Stage currency reward added: " + currency.name() + " x" + count);
    }

    @CommandAnnotation
    public void monumentStageAddCollectible(CorePlayer sender,
                                            @LiteralArg("stage") String l1,
                                            @LiteralArg("add") String l2,
                                            @LiteralArg("collectible") String l3,
                                            @OptionArg(listName = "monuments") String identifier,
                                            @HelperArg("stage") @NumberArg(minValue = 0, maxValue = 5) Integer stage,
                                            @OptionArg(listName = "types") String type,
                                            @OptionArg(listName = "collectibles") String collectible) {
        CoreZones.getInstance().getMonumentManager().addStageReward(identifier, stage, new MonumentStage.RewardCollectible(type, collectible));
        success(sender, "Stage collectible reward added: " + type + ":" + collectible);
    }

    @CommandAnnotation
    public void monumentStageAddSkin(CorePlayer sender,
                                     @LiteralArg("stage") String l1,
                                     @LiteralArg("add") String l2,
                                     @LiteralArg("skin") String l3,
                                     @OptionArg(listName = "monuments") String identifier,
                                     @HelperArg("stage") @NumberArg(minValue = 0, maxValue = 5) Integer stage,
                                     @OptionArg(listName = "types") String type,
                                     @OptionArg(listName = "collectibles") String collectible,
                                     @OptionArg(listName = "skins") String skin) {
        CoreZones.getInstance().getMonumentManager().addStageReward(identifier, stage, new MonumentStage.RewardCollectibleSkin(type, collectible, skin));
        success(sender, "Stage collectible skin reward added: " + type + ":" + collectible + ":" + skin);
    }

    @CommandAnnotation
    public void monumentStageInfo(CorePlayer sender,
                                  @LiteralArg("stage") String l1,
                                  @LiteralArg("info") String l2,
                                  @OptionArg(listName = "monuments") String identifier,
                                  @HelperArg("stage") @NumberArg(minValue = 0, maxValue = 5) Integer stage) {
        success(sender, CoreZones.getInstance().getMonumentManager().printStageInfo(identifier, stage));
    }

    @CommandAnnotation
    public void monumentStageClearRewards(CorePlayer sender,
                                          @LiteralArg("stage") String l1,
                                          @LiteralArg("clear") String l2,
                                          @LiteralArg("rewards") String l3,
                                          @OptionArg(listName = "monuments") String identifier,
                                          @HelperArg("stage") @NumberArg(minValue = 0, maxValue = 5) Integer stage) {
        CoreZones.getInstance().getMonumentManager().clearStageRewards(identifier, stage);
        success(sender, "Cleared rewards for " + identifier + ":" + stage);
    }

    @CommandAnnotation
    public void monumentStageClearMsg(CorePlayer sender,
                                      @LiteralArg("stage") String l1,
                                      @LiteralArg("clear") String l2,
                                      @LiteralArg("msg") String l3,
                                      @OptionArg(listName = "monuments") String identifier,
                                      @HelperArg("stage") @NumberArg(minValue = 0, maxValue = 5) Integer stage) {
        CoreZones.getInstance().getMonumentManager().setStageMessage(identifier, stage, null);
        success(sender, "Cleared message for " + identifier + ":" + stage);
    }

    @CommandAnnotation
    public void monumentStageClear(CorePlayer sender,
                                   @LiteralArg("clear") String l2) {
        CoreZones.getInstance().getPlayers().get(sender.getUniqueId()).getFragments().clearStages();
        success(sender, "Cleared all of your monument stage");
    }

    @CommandAnnotation
    public void monumentCollected(CorePlayer sender,
                                  @LiteralArg("collected") String l1,
                                  CorePlayer target,
                                  @OptionArg(listName = "monuments") String identifier) {
        Monument monument = CoreZones.getInstance().getMonumentManager().get(identifier);
        PlayerFragments fragments = CoreZones.getInstance().getPlayers().get(target.getUniqueId()).getFragments();
        CoreZones.getInstance().sendMessage(sender, target.getName() + "'s monument info:");
        CoreZones.getInstance().sendMessage(sender, "Monument ID: " + monument.getIdentifier());
        CoreZones.getInstance().sendMessage(sender, "Fragment ID: " + monument.getFragment());
        CoreZones.getInstance().sendMessage(sender, "Collected: " + fragments.getCollected().getOrDefault(monument.getFragment(), Sets.newHashSet()).size() + " out of " + monument.getFragmentContainer().getTotal());
        CoreZones.getInstance().sendMessage(sender, "Undeposited: " + fragments.getUndeposited(monument.getFragment()));
    }

}
