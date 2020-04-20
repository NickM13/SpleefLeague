package com.spleefleague.splegg.player;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.spleefleague.core.Core;
import com.spleefleague.core.chat.Chat;
import com.spleefleague.core.database.annotation.DBLoad;
import com.spleefleague.core.database.annotation.DBSave;
import com.spleefleague.core.database.variable.DBPlayer;
import com.spleefleague.core.game.ArenaMode;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.splegg.Splegg;
import com.spleefleague.splegg.game.SpleggGun;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author NickM
 * @since 4/16/2020
 */
public class SpleggPlayer extends DBPlayer {

    public SpleggPlayer() {
        super();
    }

    @Override
    public void init() {

    }

    @Override
    public void close() {

    }

    /**
     * @param dbPlayer DBPlayer
     * @deprecated
     */
    @Override
    public void printStats(DBPlayer dbPlayer) {

    }

    @Override
    public int getRating(ArenaMode arenaMode) {
        return 0;
    }

    @Override
    public void addRating(ArenaMode arenaMode, int i) {

    }

    @Override
    public String getDisplayElo(ArenaMode arenaMode) {
        return null;
    }

}
