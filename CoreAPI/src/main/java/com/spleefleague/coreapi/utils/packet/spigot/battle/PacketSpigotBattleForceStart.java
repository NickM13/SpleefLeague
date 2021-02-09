package com.spleefleague.coreapi.utils.packet.spigot.battle;

import com.spleefleague.coreapi.database.variable.DBPlayer;
import com.spleefleague.coreapi.utils.packet.spigot.PacketSpigot;
import com.spleefleague.coreapi.utils.packet.PacketType;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author NickM13
 * @since 9/18/2020
 */
public class PacketSpigotBattleForceStart extends PacketSpigot {

    public String mode;
    public String query;
    public List<UUID> players;

    public PacketSpigotBattleForceStart() { }

    public PacketSpigotBattleForceStart(String mode, String query, List<? extends DBPlayer> players) {
        this.mode = mode;
        this.query = query;
        this.players = new ArrayList<>();
        for (DBPlayer dbp : players) {
            this.players.add(dbp.getUniqueId());
        }
    }

    @Nonnull
    @Override
    public PacketType.Spigot getSpigotTag() {
        return PacketType.Spigot.BATTLE_FORCE_START;
    }

}
