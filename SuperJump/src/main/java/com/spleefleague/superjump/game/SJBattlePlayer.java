package com.spleefleague.superjump.game;

import com.spleefleague.core.database.variable.DBPlayer;
import com.spleefleague.core.game.Battle;
import com.spleefleague.core.game.BattlePlayer;
import org.bukkit.Location;
import org.bukkit.event.player.PlayerTeleportEvent;

/**
 * @author NickM
 * @since 4/15/2020
 */
public class SJBattlePlayer extends BattlePlayer {

    private int falls;
    private Location spawn;

    public SJBattlePlayer(DBPlayer dbp, Battle battle) {
        super(dbp, battle);
    }

    @Override
    public void respawn() {
        getPlayer().teleport(spawn, PlayerTeleportEvent.TeleportCause.PLUGIN);
    }

    public void addFall() {
        falls++;
    }
    public int getFalls() {
        return falls;
    }

    public void setSpawn(Location spawn) {
        this.spawn = spawn;
    }
    public Location getSpawn() {
        return spawn;
    }
}
