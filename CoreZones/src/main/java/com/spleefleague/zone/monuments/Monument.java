package com.spleefleague.zone.monuments;

import com.spleefleague.core.Core;
import com.spleefleague.core.chat.Chat;
import com.spleefleague.core.chat.NpcMessage;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.collectible.Collectible;
import com.spleefleague.core.player.collectible.CollectibleSkin;
import com.spleefleague.core.util.variable.Position;
import com.spleefleague.core.vendor.Vendorable;
import com.spleefleague.core.vendor.Vendorables;
import com.spleefleague.core.world.FakeUtils;
import com.spleefleague.core.world.build.BuildStructure;
import com.spleefleague.core.world.build.BuildStructures;
import com.spleefleague.core.world.game.GameUtils;
import com.spleefleague.core.world.global.GlobalWorld;
import com.spleefleague.core.world.personal.PersonalWorld;
import com.spleefleague.coreapi.database.annotation.DBField;
import com.spleefleague.coreapi.database.variable.DBEntity;
import com.spleefleague.zone.CoreZones;
import com.spleefleague.zone.fragments.FragmentContainer;
import com.spleefleague.zone.player.ZonePlayer;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author NickM13
 * @since 2/15/2021
 */
public class Monument extends DBEntity {

    @DBField private String fragment = "";
    @DBField private String structure = "";
    @DBField private Position position = null;
    @DBField private Position drain = null;
    @DBField private Position drainTo = null;
    @DBField private final List<MonumentStage> stages = new ArrayList<>();
    @DBField private Sound drainSound = Sound.BLOCK_NOTE_BLOCK_BIT;
    @DBField private Integer priority = 0;

    private FragmentContainer fragmentContainer = null;

    public Monument() {

    }

    @Override
    public void afterLoad() {
        this.fragmentContainer = CoreZones.getInstance().getFragmentManager().getContainer(fragment);
    }

    public Monument(String identifier) {
        this.identifier = identifier;
    }

    public String getFragment() {
        return fragment;
    }

    public void setFragment(String fragment) {
        this.fragment = fragment;
        this.fragmentContainer = CoreZones.getInstance().getFragmentManager().getContainer(fragment);
    }

    public FragmentContainer getFragmentContainer() {
        return fragmentContainer;
    }

    public void setStructure(String structure) {
        this.structure = structure;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public void setDrain(Position position) {
        this.drain = position;
    }

    public void setDrainTo(Position position) {
        this.drainTo = position;
    }

    public void setDrainSound(Sound sound) {
        this.drainSound = sound;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getStructure() {
        return structure;
    }

    public Position getPosition() {
        return position;
    }

    public Position getDrain() {
        return drain;
    }

    public Position getDrainTo() {
        return drainTo;
    }

    public Sound getDrainSound() {
        return drainSound;
    }

    public int getPriority() {
        return priority;
    }

    public void setStageMessage(int stage, NpcMessage message) {
        for (MonumentStage monumentStage : stages) {
            if (monumentStage.getStage() == stage) {
                monumentStage.setMessage(message);
                return;
            }
        }
        MonumentStage monumentStage = new MonumentStage(stage);
        monumentStage.setMessage(message);
        stages.add(monumentStage);
    }

    public void addStageReward(int stage, MonumentStage.Reward reward) {
        for (MonumentStage monumentStage : stages) {
            if (monumentStage.getStage() == stage) {
                monumentStage.addReward(reward);
                return;
            }
        }
        MonumentStage monumentStage = new MonumentStage(stage);
        monumentStage.addReward(reward);
        stages.add(monumentStage);
    }

    public void clearStageRewards(int stage) {
        for (MonumentStage monumentStage : stages) {
            if (monumentStage.getStage() == stage) {
                monumentStage.clearRewards();
                return;
            }
        }
    }

    public String getRewardDescription(int stageId, boolean turnedIn, CorePlayer corePlayer) {
        StringBuilder description = new StringBuilder();
        for (MonumentStage.Reward reward : stages.get(stageId).getRewards()) {
            if (description.length() > 0) description.append("\n");
            description.append(reward.getDisplayName(turnedIn, corePlayer));
        }
        return description.toString();
    }

    public String printInfo(int stage) {
        for (MonumentStage monumentStage : stages) {
            if (monumentStage.getStage() == stage) {
                return monumentStage.toString();
            }
        }
        return "";
    }

    private final Map<UUID, PersonalWorld> monumentWorlds = new HashMap<>();

    public void createWorld(CorePlayer corePlayer) {
        PersonalWorld world = new PersonalWorld(100, Core.OVERWORLD);
        monumentWorlds.put(corePlayer.getUniqueId(), world);
        world.addPlayer(corePlayer);
        ZonePlayer zonePlayer = CoreZones.getInstance().getPlayers().get(corePlayer.getUniqueId());
        updateMonument(corePlayer.getUniqueId(), Math.max(0, zonePlayer.getFragments().getStage(this)));
    }

    public void updateMonument(UUID uuid, int stage) {
        if (getStructure().isEmpty() || getPosition() == null) return;
        BuildStructure structure = BuildStructures.get(getStructure() + "_" + stage);
        if (structure == null) return;
        monumentWorlds.get(uuid)
                .overwriteBlocks(FakeUtils.transformBlocks(
                structure.getFakeBlocks(),
                getPosition()));
    }

    public void onUpgrade(CorePlayer corePlayer, int stage) {
        PersonalWorld world = monumentWorlds.get(corePlayer.getUniqueId());
        AtomicInteger step = new AtomicInteger();
        Vector pos = getDrainTo().toBlockPosition().toVector().add(new Vector(0, -12, 0));
        world.addRepeatingTask(() -> {
            GameUtils.spawnCircleParticles(world,
                    pos.add(new Vector(0, 0.3, 0)),
                    new Particle.DustOptions(Color.YELLOW, 2),
                    10,
                    Math.PI * 0.1 * step.getAndIncrement(),
                    Math.PI * 0.1,
                    20);
            GameUtils.spawnCircleParticles(world,
                    pos.add(new Vector(0, 0.3, 0)),
                    new Particle.DustOptions(Color.YELLOW, 2),
                    10,
                    Math.PI * 0.1 * step.get() + Math.PI,
                    Math.PI * 0.1,
                    20);
        }, 160, 1);
        corePlayer.getPlayer().playSound(corePlayer.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 0.2f, 1);
        updateMonument(corePlayer.getUniqueId(), stage);
        for (MonumentStage monumentStage : stages) {
            if (monumentStage.getStage() == stage) {
                if (monumentStage.getMessage() != null) {
                    Chat.sendNpcMessage(corePlayer, monumentStage.getMessage());
                }
                int delay = 0;
                for (MonumentStage.Reward reward : monumentStage.getRewards()) {
                    reward.apply(corePlayer);
                    Bukkit.getScheduler().runTaskLater(CoreZones.getInstance(), () -> {
                        switch (reward.rewardType) {
                            case COLLECTIBLE:
                                MonumentStage.RewardCollectible collectible = (MonumentStage.RewardCollectible) reward;
                                Vendorable vendorable = Vendorables.get(collectible.type, collectible.collectible);
                                CoreZones.getInstance().sendMessage(corePlayer, "You've received " + vendorable.getDisplayName());
                                break;
                            case CURRENCY:
                                MonumentStage.RewardCurrency currency = (MonumentStage.RewardCurrency) reward;
                                CoreZones.getInstance().sendMessage(corePlayer, "You've received " + currency.currency.color + currency.amount + " " + currency.currency.displayName);
                                break;
                            case SKIN:
                                MonumentStage.RewardCollectibleSkin skin = (MonumentStage.RewardCollectibleSkin) reward;
                                Collectible collectible2 = (Collectible) Vendorables.get(skin.type, skin.collectible);
                                CollectibleSkin collectibleSkin = collectible2.getSkin(skin.skin);
                                CoreZones.getInstance().sendMessage(corePlayer, "You've received " + collectibleSkin.getFullDisplayName());
                                break;
                        }
                        corePlayer.getGlobalWorld().addRotationItem(corePlayer, reward.getDisplayItem());
                    }, (delay++) * 20);
                }
                break;
            }
        }
    }

    public void destroyWorld(UUID uuid) {
        monumentWorlds.remove(uuid);
    }

}
