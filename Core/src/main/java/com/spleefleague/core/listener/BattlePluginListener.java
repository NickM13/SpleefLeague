package com.spleefleague.core.listener;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import com.spleefleague.core.Core;
import com.spleefleague.core.game.Arena;
import com.spleefleague.core.game.BattleMode;
import com.spleefleague.core.game.arena.Arenas;
import com.spleefleague.core.game.battle.Battle;
import com.spleefleague.core.logger.CoreLogger;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.coreapi.queue.SubQuery;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.jetbrains.annotations.NotNull;

public class BattlePluginListener implements PluginMessageListener {

    /**
     * Plugin Message Format
     * battle:start
     * String: mode
     * String: queryResult
     * Integer: playerCount
     * Array of Strings [playerCount]: playerUniqueIds
     *
     * @param channel
     * @param player
     * @param bytes
     */
    @Override
    public void onPluginMessageReceived(@NotNull String channel, @NotNull Player player, @NotNull byte[] bytes) {
        if (channel.equals("battle:start")) {
            ByteArrayDataInput input = ByteStreams.newDataInput(bytes);
            BattleMode mode = BattleMode.get(input.readUTF());
            SubQuery[] subQueries = SubQuery.splitQuery(input.readUTF());
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
            int playerCount = input.readInt();
            List<UUID> players = new ArrayList<>();
            for (int i = 0; i < playerCount; i++) {
                players.add(UUID.fromString(input.readUTF()));
            }
            try {
                Battle<?> battle = mode.getBattleClass()
                        .getDeclaredConstructor(List.class, Arena.class)
                        .newInstance(players, arena);
                //battle.startBattle();
                Core.getInstance().getBattleManager(mode).startMatch(battle);
                mode.addBattle(battle);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException exception) {
                CoreLogger.logError(exception);
            }
        } else if (channel.equals("battle:spectate")) {
            ByteArrayDataInput input = ByteStreams.newDataInput(bytes);
            Core.getInstance().getPlayers().addPlayerJoinAction(UUID.fromString(input.readUTF()), spectator -> {
                CorePlayer target = Core.getInstance().getPlayers().get(UUID.fromString(input.readUTF()));
                if (target.isInBattle()) {
                    target.getBattle().addSpectator(spectator, target);
                }
            }, true);
        }
    }

}
