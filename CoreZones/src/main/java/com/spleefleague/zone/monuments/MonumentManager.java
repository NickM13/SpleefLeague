package com.spleefleague.zone.monuments;

import com.mongodb.client.MongoCollection;
import com.spleefleague.core.Core;
import com.spleefleague.core.chat.NpcMessage;
import com.spleefleague.core.logger.CoreLogger;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.util.variable.Position;
import com.spleefleague.zone.CoreZones;
import com.spleefleague.zone.fragments.FragmentUtils;
import com.spleefleague.zone.player.ZonePlayer;
import com.spleefleague.zone.player.fragments.PlayerFragments;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.*;

/**
 * @author NickM13
 * @since 2/15/2021
 */
public class MonumentManager {

    private final Map<String, Monument> monumentMap = new HashMap<>();
    private final List<Monument> sortedMonuments = new ArrayList<>();

    private MongoCollection<Document> monumentColl;

    private final Map<ZonePlayer, Monument> playerDrainMap = new HashMap<>();
    private final List<MonumentDrainFragment> drainingFragments = new ArrayList<>();

    private BukkitTask drainingTask;
    private BukkitTask animationTask;

    public void init() {
        monumentColl = CoreZones.getInstance().getPluginDB().getCollection("Monuments");

        for (Document doc : monumentColl.find()) {
            Monument monument = new Monument();
            monument.load(doc);
            monumentMap.put(monument.getIdentifier(), monument);
            sortedMonuments.add(monument);
        }

        sortedMonuments.sort(Comparator.comparingInt(Monument::getPriority));

        drainingTask = Bukkit.getScheduler().runTaskTimerAsynchronously(CoreZones.getInstance(), () -> {
            synchronized (playerDrainMap) {
                Iterator<Map.Entry<ZonePlayer, Monument>> it = playerDrainMap.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry<ZonePlayer, Monument> entry = it.next();
                    ZonePlayer zonePlayer = entry.getKey();
                    Monument monument = entry.getValue();
                    PlayerFragments playerFragments = zonePlayer.getFragments();
                    Player player = zonePlayer.getPlayer();
                    CorePlayer corePlayer = Core.getInstance().getPlayers().get(player);
                    if (zonePlayer.getFragments().onDepositBuffer(monument.getFragment())) {
                        if (corePlayer == null || corePlayer.getPlayer() == null) {
                            if (corePlayer != null) {
                                CoreLogger.logError(corePlayer.getName() + " logged out before depositing a fragment");
                            } else {
                                CoreLogger.logError("NULL" + " logged out before depositing a fragment");
                            }
                            it.remove();
                            continue;
                        }
                        player.playSound(player.getLocation(), monument.getDrainSound(), 0.2f, zonePlayer.getDrainComboPitchAndIncrement());
                        int ticks = launchDrainedFragment(monument, player) * 2;
                        Bukkit.getScheduler().runTaskLater(CoreZones.getInstance(), () -> {
                            if (playerFragments.onDeposit(monument.getFragment())) {
                                int deposited = playerFragments.getDeposited(monument.getFragment());
                                int prevStage = zonePlayer.getFragments().getStage(monument);
                                int newStage = monument.getFragmentContainer().getStage(deposited);

                                if (newStage > prevStage) {
                                    zonePlayer.getFragments().incrementStage(monument);
                                    monument.onUpgrade(corePlayer, zonePlayer.getFragments().getStage(monument));
                                }
                            }
                        }, ticks);
                    } else {
                        if (playerFragments.getStage(monument) == -1 ||
                                playerFragments.getStage(monument) != monument.getFragmentContainer().getStage(playerFragments.getDeposited(monument.getFragment()))) {
                            zonePlayer.getFragments().incrementStage(monument);
                            monument.onUpgrade(corePlayer, zonePlayer.getFragments().getStage(monument));
                        }
                        it.remove();
                    }
                }
            }
        }, 20L, 3L);

        animationTask = Bukkit.getScheduler().runTaskTimerAsynchronously(CoreZones.getInstance(), () -> {
            synchronized (playerDrainMap) {
                Iterator<MonumentDrainFragment> it = drainingFragments.iterator();
                while (it.hasNext()) {
                    MonumentDrainFragment fragment = it.next();
                    if (!fragment.hasNext()) {
                        FragmentUtils.sendDestroyPacket(fragment.player, fragment.entityId);
                        it.remove();
                        continue;
                    }
                    FragmentUtils.sendEntityMovePacket(fragment.player,
                            fragment.entityId,
                            fragment.changeX,
                            fragment.changeY,
                            fragment.changeZ);
                }
            }
        }, 20L, 2L);
    }

    public void close() {
        drainingTask.cancel();
        animationTask.cancel();
    }

    public List<Monument> getAll() {
        return sortedMonuments;
    }

    private int launchDrainedFragment(Monument monument, Player player) {
        MonumentDrainFragment drainFragment = new MonumentDrainFragment(player,
                new Vector(
                monument.getDrainTo().getX() - player.getLocation().getX(),
                monument.getDrainTo().getY() - player.getLocation().getY(),
                monument.getDrainTo().getZ() - player.getLocation().getZ()));

        drainingFragments.add(drainFragment);

        Bukkit.getScheduler().runTask(CoreZones.getInstance(), () -> {
            FragmentUtils.sendSpawnPacket(player,
                    drainFragment.entityId,
                    player.getLocation().getX(),
                    player.getLocation().getY() + 2,
                    player.getLocation().getZ(),
                    monument.getFragmentContainer().getUncollectedItemNMS());
        });
        return drainFragment.remainingTicks;
    }

    public Set<String> getMonumentNames() {
        return monumentMap.keySet();
    }

    public boolean create(String identifier) {
        if (monumentMap.containsKey(identifier)) return false;
        Monument monument = new Monument(identifier);
        monumentMap.put(identifier, monument);
        sortedMonuments.add(monument);
        sortedMonuments.sort(Comparator.comparingInt(Monument::getPriority));
        return true;
    }

    public void setFragment(String identifier, String fragment) {
        Monument monument = monumentMap.get(identifier);
        monument.setFragment(fragment);
        monument.save(monumentColl);
    }

    public void setStructure(String identifier, String structure) {
        Monument monument = monumentMap.get(identifier);
        monument.setStructure(structure);
        monument.save(monumentColl);
    }

    public void setPosition(String identifier, Position position) {
        Monument monument = monumentMap.get(identifier);
        monument.setPosition(position);
        monument.save(monumentColl);
    }

    public void setDrain(String identifier, Position position) {
        Monument monument = monumentMap.get(identifier);
        monument.setDrain(position);
        monument.save(monumentColl);
    }

    public void setDrainTo(String identifier, Position position) {
        Monument monument = monumentMap.get(identifier);
        monument.setDrainTo(position);
        monument.save(monumentColl);
    }

    public void setDrainSound(String identifier, Sound sound) {
        Monument monument = monumentMap.get(identifier);
        monument.setDrainSound(sound);
        monument.save(monumentColl);
    }

    public void setPriority(String identifier, int priority) {
        Monument monument = monumentMap.get(identifier);
        monument.setPriority(priority);
        monument.save(monumentColl);
        sortedMonuments.sort(Comparator.comparingInt(Monument::getPriority));
    }

    public void setStageMessage(String identifier, int stage, NpcMessage message) {
        Monument monument = monumentMap.get(identifier);
        monument.setStageMessage(stage, message);
        monument.save(monumentColl);
    }

    public void addStageReward(String identifier, int stage, MonumentStage.Reward reward) {
        Monument monument = monumentMap.get(identifier);
        monument.addStageReward(stage, reward);
        monument.save(monumentColl);
    }

    public void clearStageRewards(String identifier, int stage) {
        Monument monument = monumentMap.get(identifier);
        monument.clearStageRewards(stage);
        monument.save(monumentColl);
    }

    public String printStageInfo(String identifier, int stage) {
        return monumentMap.get(identifier).printInfo(stage);
    }

    public void onPlayerMove(Player player, Location loc) {
        ZonePlayer zp = CoreZones.getInstance().getPlayers().get(player);
        for (Monument monument : monumentMap.values()) {
            if (monument.getDrain() != null &&
                    monument.getDrain().distance(new Position(loc)) < 1) {
                playerDrainMap.put(zp, monument);
                return;
            }
        }
        playerDrainMap.remove(zp);
    }

    public void onPlayerJoin(UUID uuid) {
        CorePlayer corePlayer = Core.getInstance().getPlayers().get(uuid);
        if (corePlayer == null) {
            Thread.dumpStack();
            return;
        }
        for (Monument monument : monumentMap.values()) {
            monument.createWorld(corePlayer);
        }
    }

    public void onPlayerQuit(ZonePlayer zonePlayer) {
        playerDrainMap.remove(zonePlayer);
        for (Monument monument : monumentMap.values()) {
            monument.destroyWorld(zonePlayer.getUniqueId());
        }
    }

    public Monument get(String identifier) {
        return monumentMap.get(identifier);
    }

}
