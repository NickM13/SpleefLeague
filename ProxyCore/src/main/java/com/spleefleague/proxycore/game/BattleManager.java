package com.spleefleague.proxycore.game;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author NickM13
 * @since 6/11/2020
 */
public class BattleManager {

    private Map<UUID, String> playerBattleMap = new HashMap<>();

    private BattleManager() {

    }

    public void onPlayerConnect() {

    }

    public void onPlayerDisconnect() {

    }

    public String getPlayerBattleServer(UUID player) {
        return playerBattleMap.get(player);
    }

    private static BattleManager instance = null;

    public static BattleManager getInstance() {
        if (instance == null) {
            instance = new BattleManager();
        }
        return instance;
    }

}
