package com.spleefleague.core.player.statistics;

import com.spleefleague.core.Core;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.coreapi.player.PlayerStatistics;
import com.spleefleague.coreapi.utils.packet.shared.NumAction;
import com.spleefleague.coreapi.utils.packet.shared.RatedPlayerInfo;
import com.spleefleague.coreapi.utils.packet.spigot.player.PacketSpigotPlayerStatistics;

import java.util.Map;

/**
 * @author NickM13
 * @since 2/3/2021
 */
public class CorePlayerStatistics extends PlayerStatistics {

    CorePlayer owner;

    private Map<String, Long> changesF;

    public CorePlayerStatistics(CorePlayer owner) {
        this.owner = owner;
    }

    @Override
    public long add(String parent, String statName, long value) {
        Core.getInstance().sendPacket(new PacketSpigotPlayerStatistics(parent, statName, new RatedPlayerInfo(NumAction.CHANGE, owner.getUniqueId(), (int) value)));
        return super.add(parent, statName, value);
    }

    @Override
    public void set(String parent, String statName, long value) {
        super.set(parent, statName, value);
        Core.getInstance().sendPacket(new PacketSpigotPlayerStatistics(parent, statName, new RatedPlayerInfo(NumAction.SET, owner.getUniqueId(), (int) value)));
    }

    @Override
    public long setHigher(String parent, String statName, long value) {
        long higher = super.setHigher(parent, statName, value);
        Core.getInstance().sendPacket(new PacketSpigotPlayerStatistics(parent, statName, new RatedPlayerInfo(NumAction.SET, owner.getUniqueId(), (int) higher)));
        return higher;
    }

    @Override
    public long setHigher(String parent, String statName, String compare) {
        long higher = super.setHigher(parent, statName, compare);
        Core.getInstance().sendPacket(new PacketSpigotPlayerStatistics(parent, statName, new RatedPlayerInfo(NumAction.SET, owner.getUniqueId(), (int) higher)));
        return higher;
    }

    public void pushChanges() {

    }

}
