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
        if ("battle:start".equals(channel)) {
            ByteArrayDataInput input = ByteStreams.newDataInput(bytes);
            BattleMode mode = BattleMode.get(input.readUTF());
            String query = input.readUTF();
            Arena arena = Arenas.get(input.readUTF(), mode);
            int playerCount = input.readInt();
            List<CorePlayer> players = new ArrayList<>();
            for (int i = 0; i < playerCount; i++) {
                players.add(Core.getInstance().getPlayers().getOffline(UUID.fromString(input.readUTF())));
            }
            try {
                Battle<?> battle = mode.getBattleClass()
                        .getDeclaredConstructor(List.class, Arena.class)
                        .newInstance(players, arena);
                battle.startBattle();
                Core.getInstance().getBattleManager(mode).startMatch(battle);
                mode.addBattle(battle);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException exception) {
                CoreLogger.logError(exception);
            }
        }
    }

}
