package com.spleefleague.coreapi.utils.packet.spigot.player;

import com.spleefleague.coreapi.player.options.PlayerOptions;
import com.spleefleague.coreapi.utils.packet.PacketType;
import com.spleefleague.coreapi.utils.packet.spigot.PacketSpigot;

import java.util.UUID;

/**
 * @author NickM13
 * @since 2/1/2021
 */
public class PacketSpigotPlayerOptions extends PacketSpigot {

    public UUID uuid;
    public String optionName;
    public PlayerOptions.OptionType type;
    public boolean boolOption = false;
    public int intOption = 0;
    public double doubleOption = 0D;
    public String strOption = "";

    public PacketSpigotPlayerOptions() { }

    public PacketSpigotPlayerOptions(UUID uuid, String optionName, Boolean val) {
        this.uuid = uuid;
        this.optionName = optionName;
        this.type = PlayerOptions.OptionType.BOOLEAN;
        this.boolOption = val;
    }

    public PacketSpigotPlayerOptions(UUID uuid, String optionName, Integer val) {
        this.uuid = uuid;
        this.optionName = optionName;
        this.type = PlayerOptions.OptionType.INTEGER;
        this.intOption = val;
    }

    public PacketSpigotPlayerOptions(UUID uuid, String optionName, Double val) {
        this.uuid = uuid;
        this.optionName = optionName;
        this.type = PlayerOptions.OptionType.DOUBLE;
        this.doubleOption = val;
    }

    public PacketSpigotPlayerOptions(UUID uuid, String optionName, String val) {
        this.uuid = uuid;
        this.optionName = optionName;
        this.type = PlayerOptions.OptionType.STRING;
        this.strOption = val;
    }

    @Override
    public PacketType.Spigot getSpigotTag() {
        return PacketType.Spigot.PLAYER_OPTIONS;
    }

}
