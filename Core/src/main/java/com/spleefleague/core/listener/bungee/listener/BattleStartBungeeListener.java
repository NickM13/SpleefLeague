package com.spleefleague.core.listener.bungee.listener;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import com.spleefleague.core.Core;
import com.spleefleague.core.game.Arena;
import com.spleefleague.core.game.BattleMode;
import com.spleefleague.core.game.arena.Arenas;
import com.spleefleague.core.game.battle.Battle;
import com.spleefleague.core.listener.bungee.BungeeListener;
import com.spleefleague.core.logger.CoreLogger;
import com.spleefleague.coreapi.queue.SubQuery;
import com.spleefleague.coreapi.utils.packet.bungee.PacketBattleStart;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BattleStartBungeeListener extends BungeeListener<PacketBattleStart> {

    @Override
    protected void receive(Player sender, PacketBattleStart packet) {
        BattleMode mode = BattleMode.get(packet.mode);
        SubQuery[] subQueries = SubQuery.splitQuery(packet.query);
        Arena arena = null;
        boolean success = false;
        for (SubQuery sq : subQueries) {
            if (sq.type.equals("arena")) {
                arena = Arenas.getByQuery(sq, mode);
                success = true;
                break;
            }
        }
        if (!success) return;
        if (arena == null) Arenas.getRandom(mode);
        try {
            Battle<?> battle = mode.getBattleClass()
                    .getDeclaredConstructor(List.class, Arena.class)
                    .newInstance(packet.players, arena);
            //battle.startBattle();
            Core.getInstance().getBattleManager(mode).startMatch(battle);
            mode.addBattle(battle);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException exception) {
            CoreLogger.logError(exception);
        }
    }

}
