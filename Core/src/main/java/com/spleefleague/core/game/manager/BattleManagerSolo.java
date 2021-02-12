/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.game.manager;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.spleefleague.core.Core;
import com.spleefleague.core.game.Arena;
import com.spleefleague.core.game.BattleMode;
import com.spleefleague.core.game.arena.Arenas;
import com.spleefleague.core.game.battle.Battle;
import com.spleefleague.core.logger.CoreLogger;
import com.spleefleague.core.player.CorePlayer;

import java.util.List;

/**
 * Single-player gamemodes, doesn't actually use a queue system!
 *
 * @author NickM13
 */
public class BattleManagerSolo extends BattleManager {

    public BattleManagerSolo(BattleMode mode) {
        super(mode);
    }

    @Override
    public void startMatch(List<CorePlayer> players, String arenaName) {
        Arena arena = Arenas.get(arenaName, mode);
        if (arena == null) {
            CoreLogger.logError("", new NullPointerException("Null arena: " + arenaName));
            return;
        }
        Battle<?> battle;
        for (CorePlayer cp : players) {
            Core.getInstance().getPartyManager().leave(cp);
            if (!cp.canJoinBattle()) {
                CoreLogger.logError("Player " + cp.getDisplayName() + " is already in a battle!", null);
                Core.getInstance().unqueuePlayerGlobally(cp);
                return;
            }
        }
        try {
            if (arena.isAvailable()) {
                ByteArrayDataOutput output = ByteStreams.newDataOutput();

                output.writeUTF(mode.getName());
                output.writeUTF(arena.getIdentifier());

                output.writeInt(players.size());
                for (CorePlayer cp : players) {
                    Core.getInstance().unqueuePlayerGlobally(cp);
                    output.writeUTF(cp.getUniqueId().toString());
                }

                players.get(0).getPlayer().sendPluginMessage(Core.getInstance(), "battle:start", output.toByteArray());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
