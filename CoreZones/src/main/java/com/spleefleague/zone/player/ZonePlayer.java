package com.spleefleague.zone.player;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.spleefleague.core.Core;
import com.spleefleague.core.chat.Chat;
import com.spleefleague.core.player.CoreDBPlayer;
import com.spleefleague.coreapi.database.annotation.DBField;
import com.spleefleague.coreapi.player.options.PlayerOptions;
import com.spleefleague.zone.CoreZones;
import com.spleefleague.zone.gear.Gear;
import com.spleefleague.zone.player.fragments.PlayerFragments;
import com.spleefleague.zone.player.world.HarvestWorld;
import com.spleefleague.zone.zones.Zone;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.WeatherType;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author NickM13
 * @since 2/11/2021
 */
public class ZonePlayer extends CoreDBPlayer {

    private Zone zone;

    @DBField private int brushSize = 1;
    @DBField private int brushHeight = 1;
    @DBField private int brushDrop = 0;
    @DBField private String targetZone = "";

    @DBField private String targetFragment = "";

    @DBField private final PlayerFragments fragments = new PlayerFragments();

    @DBField private final Set<String> discoveredZones = new HashSet<>();

    @DBField private final PlayerOptions playerOptions = new PlayerOptions();

    @DBField private final Map<Gear.GearType, Long> gearCooldowns = new HashMap<>();

    private long drainComboTime = 0;
    private int drainCombo = 0;

    private long pickupComboTime = 0;
    private int pickupCombo = 0;

    private boolean raining = false;

    // TODO: More involved custom rain
    //private float rainLevel = 0, rainTarget = 0;

    @Override
    public void init() {
        CoreZones.getInstance().getFragmentManager().onPlayerLoaded(this);
        CoreZones.getInstance().getMonumentManager().onPlayerJoin(getUniqueId());
        CoreZones.getInstance().getZoneManager().onPlayerJoin(this);
        HarvestWorld.getGlobal().addPlayer(Core.getInstance().getPlayers().get(getUniqueId()));
    }

    @Override
    public void close() {
        CoreZones.getInstance().getFragmentManager().onPlayerQuit(getUniqueId());
        CoreZones.getInstance().getMonumentManager().onPlayerQuit(this);
    }

    public PlayerFragments getFragments() {
        return fragments;
    }

    public void setZone(Zone zone) {
        if (zone != this.zone) {
            this.zone = zone;
            if (zone != null && zone.isMain()) {
                if (discoveredZones.add(zone.getIdentifier())) {
                    Chat.sendTitle(
                            Core.getInstance().getPlayers().get(getUniqueId()),
                            zone.getDisplayName(),
                            ChatColor.YELLOW + "Discovered new area!",
                            20,
                            100,
                            20);
                    getPlayer().playSound(getPlayer().getLocation(), Sound.UI_CARTOGRAPHY_TABLE_TAKE_RESULT, 1, 1);
                }
            }
            showZoneHotbar();
            updateZone(zone);
        }
    }

    public void updateWeather() {
        /*
        if (Math.abs(rainLevel - rainTarget) > 0.01f) {
            rainLevel += (rainTarget - rainLevel) / 3;
            PacketContainer container;
            container = new PacketContainer(PacketType.Play.Server.GAME_STATE_CHANGE);
            container.getIntegers().write(0, 7);
            container.getFloat().write(0, rainLevel);
            if (rainLevel <= 0.01f) {
                if (raining) {
                    sendRainEnd();
                    raining = false;
                }
            } else {
                if (!raining) {
                    sendRainStart();
                    raining = true;
                }
            }
        }
         */
    }

    private void updateZone(Zone zone) {
        if (zone == null || !zone.hasWeather()) {
            if (raining) {
                getPlayer().setPlayerWeather(WeatherType.CLEAR);
                raining = false;
            }
            //rainTarget = 0;
        } else {
            if (!raining) {
                getPlayer().setPlayerWeather(WeatherType.DOWNFALL);
                raining = true;
            }
            //rainTarget = zone.getRain();
        }
    }

    private void sendRainEnd() {
        PacketContainer container = new PacketContainer(PacketType.Play.Server.GAME_STATE_CHANGE);
        container.getIntegers().write(0, 1);
        Core.sendPacket(getPlayer(), container);
    }

    private void sendRainStart() {
        PacketContainer container = new PacketContainer(PacketType.Play.Server.GAME_STATE_CHANGE);
        container.getIntegers().write(0, 2);
        Core.sendPacket(getPlayer(), container);
    }

    public Zone getZone() {
        return zone;
    }

    public boolean hasZonePermissions(String usageZone) {
        return zone != null && zone.hasPermission(usageZone);
    }

    public void showZoneHotbar() {
        if (zone != null) {
            sendHotbarText(zone.getDisplayName());
        } else {
            sendHotbarText(ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "Wilderness");
        }
    }

    public void sendHotbarText(String text) {
        PacketContainer packet = new PacketContainer(PacketType.Play.Server.CHAT);
        packet.getChatTypes().write(0, EnumWrappers.ChatType.GAME_INFO);
        packet.getChatComponents().write(0, WrappedChatComponent.fromText(text));
        Core.sendPacket(player, packet);
    }

    public PlayerOptions getOptions() {
        return playerOptions;
    }

    public boolean isReady(Gear.GearType gearType) {
        if (gearCooldowns.containsKey(gearType)) {
            if (gearCooldowns.get(gearType) < System.currentTimeMillis()) {
                gearCooldowns.remove(gearType);
                return true;
            }
            return false;
        }
        return true;
    }

    public void startCooldown(Gear.GearType gearType) {
        gearCooldowns.put(gearType, System.currentTimeMillis() + gearType.cooldown);
    }

    public void endCooldown(Gear.GearType gearType) {
        gearCooldowns.remove(gearType);
    }

    public Map<Gear.GearType, Long> getGearCooldowns() {
        return gearCooldowns;
    }

    public void setBrushSize(int size) {
        brushSize = size;
    }

    public int getBrushSize() {
        return brushSize;
    }

    public void setBrushHeight(int height) {
        brushHeight = height;
    }

    public int getBrushHeight() {
        return brushHeight;
    }

    public int getBrushDrop() {
        return brushDrop;
    }

    public void setBrushDrop(int brushDrop) {
        this.brushDrop = brushDrop;
    }

    public String getTargetZone() {
        return targetZone;
    }

    public void setTargetZone(String targetZone) {
        this.targetZone = targetZone;
    }

    public String getTargetFragment() {
        return targetFragment;
    }

    public void setTargetFragment(String fragment) {
        this.targetFragment = fragment;
    }

    public float getDrainComboPitchAndIncrement() {
        if (drainComboTime < System.currentTimeMillis() || drainCombo > 20) {
            drainCombo = 0;
        } else if (drainCombo < 12) {
            drainCombo++;
        }
        drainComboTime = System.currentTimeMillis() + 5000;

        return (float) Math.pow(2, drainCombo / 12.f);
    }

    public float getPickupComboPitchAndIncrement() {
        if (pickupComboTime < System.currentTimeMillis() || pickupCombo > 20) {
            pickupCombo = 0;
        } else if (pickupCombo < 5) {
            pickupCombo++;
        }
        pickupComboTime = System.currentTimeMillis() + 15000;

        return (float) Math.pow(2, pickupCombo / 12.f);
    }

}
