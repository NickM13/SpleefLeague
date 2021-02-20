package com.spleefleague.core.player.options;

import com.spleefleague.core.Core;
import com.spleefleague.core.player.CoreOfflinePlayer;
import com.spleefleague.coreapi.player.options.PlayerOptions;
import com.spleefleague.coreapi.utils.packet.spigot.player.PacketSpigotPlayerOptions;

/**
 * @author NickM13
 * @since 2/1/2021
 */
public class CorePlayerOptions extends PlayerOptions {

    private final CoreOfflinePlayer owner;

    public CorePlayerOptions(CoreOfflinePlayer owner) {
        this.owner = owner;
    }

    @Override
    public void setBoolean(String option, boolean obj) {
        super.setBoolean(option, obj);
        Core.getInstance().sendPacket(new PacketSpigotPlayerOptions(owner.getUniqueId(), option, obj));
    }

    @Override
    public void setInteger(String option, int obj) {
        super.setInteger(option, obj);
        Core.getInstance().sendPacket(new PacketSpigotPlayerOptions(owner.getUniqueId(), option, obj));
    }

    @Override
    public void setDouble(String option, double obj) {
        super.setDouble(option, obj);
        Core.getInstance().sendPacket(new PacketSpigotPlayerOptions(owner.getUniqueId(), option, obj));
    }

    @Override
    public void setString(String option, String obj) {
        super.setString(option, obj);
        Core.getInstance().sendPacket(new PacketSpigotPlayerOptions(owner.getUniqueId(), option, obj));
    }

    @Override
    public Boolean toggle(String option) {
        super.toggle(option);
        boolean val = getBoolean(option);
        Core.getInstance().sendPacket(new PacketSpigotPlayerOptions(owner.getUniqueId(), option, getBoolean(option)));
        return val;
    }

    @Override
    public Integer addInteger(String option, int value, int min, int max) {
        super.addInteger(option, value, min, max);
        int val = getInteger(option);
        Core.getInstance().sendPacket(new PacketSpigotPlayerOptions(owner.getUniqueId(), option, val));
        return val;
    }

    @Override
    public Double addDouble(String option, double value, double min, double max) {
        super.addDouble(option, value, min, max);
        double val = getDouble(option);
        Core.getInstance().sendPacket(new PacketSpigotPlayerOptions(owner.getUniqueId(), option, val));
        return val;
    }

}
