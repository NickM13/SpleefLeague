package com.spleefleague.core.listener.bungee.battle;

import com.spleefleague.core.Core;
import com.spleefleague.core.game.Arena;
import com.spleefleague.core.game.BattleMode;
import com.spleefleague.core.game.arena.Arenas;
import com.spleefleague.core.game.battle.Battle;
import com.spleefleague.core.listener.bungee.BungeeListener;
import com.spleefleague.core.logger.CoreLogger;
import com.spleefleague.coreapi.queue.SubQuery;
import com.spleefleague.coreapi.utils.packet.bungee.battle.PacketBungeeBattleStart;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.UUID;

public class BungeeListenerBattleStart extends BungeeListener<PacketBungeeBattleStart> {

    @Override
    protected void receive(Player sender, PacketBungeeBattleStart packet) {
        BattleMode mode = BattleMode.get(packet.mode);
        SubQuery[] subQueries = SubQuery.splitQuery(packet.query);
        Arena arena = null;
        boolean success = false;
        for (SubQuery sq : subQueries) {
            if (sq.type.equals("arena")) {
                arena = Arenas.getByQuery(sq, mode, packet.teamSize);
                success = true;
                break;
            }
        }
        if (!success) return;
        if (arena == null) Arenas.getRandom(mode, packet.teamSize);
        if (arena == null) {
            Bukkit.getScheduler().runTaskLater(Core.getInstance(), () -> {
                for (UUID uuid : packet.players) {
                    Core.getInstance().returnToHub(Core.getInstance().getPlayers().get(uuid));
                }
            }, 100L);
            return;
        }
        try {
            Battle<?> battle = mode.getBattleClass()
                    .getDeclaredConstructor(UUID.class, List.class, Arena.class)
                    .newInstance(packet.battleId, packet.players, arena);
            battle.setForced(packet.challenge);
            Core.getInstance().getBattleManager(mode).startMatch(battle);
            mode.addBattle(battle);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException exception) {
            CoreLogger.logError(exception);
        }
    }

}
