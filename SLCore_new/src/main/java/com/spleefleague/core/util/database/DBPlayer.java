/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.util.database;

import com.spleefleague.core.Core;
import com.spleefleague.core.annotation.DBField;
import com.spleefleague.core.annotation.DBLoad;
import com.spleefleague.core.annotation.DBSave;
import com.spleefleague.core.game.Battle;
import com.spleefleague.core.player.BattleState;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.PregameState;
import com.spleefleague.core.plugin.CorePlugin;
import java.util.UUID;
import javax.annotation.Nullable;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * @author NickM13
 * @param <B>
 */
public class DBPlayer<B extends Battle> extends DBEntity {
    
    protected UUID uuid;
    @DBField
    protected String username;
    
    // Current battle state the player is in (None, Battler, Spectator, or Ref)
    protected BattleState battleState;
    protected B battle;
    protected final PregameState pregameState;
    
    protected boolean online;
    
    public static DBPlayer convert(Player p) {
        return new DBPlayer(p.getUniqueId(), p.getName());
    }
    
    protected DBPlayer(UUID uuid, String username) {
        this.uuid = uuid;
        this.username = username;
        pregameState = null;
    }
    
    protected DBPlayer() {
        battleState = BattleState.NONE;
        battle = null;
        pregameState = new PregameState(this);
    }
    
    @DBLoad(fieldname="uuid")
    protected void setUuid(String uuid) {
        this.uuid = UUID.fromString(uuid);
    }
    @DBSave(fieldname="uuid")
    public String getUuid() {
        return uuid.toString();
    }
    public UUID getUniqueId() {
        return uuid;
    }
    
    public boolean isVanished() {
        return Core.getInstance().getPlayers().get(this).isVanished();
    }
    
    public String getName() {
        return username;
    }
    
    public String getDisplayName() {
        CorePlayer cp = Core.getInstance().getPlayers().get(this);
        if (cp != null) return cp.getDisplayName();
        return "";
    }
    
    public String getDisplayNamePossessive() {
        CorePlayer cp = Core.getInstance().getPlayers().get(this);
        if (cp != null) return cp.getDisplayNamePossessive();
        return "";
    }
    
    public void setGameMode(GameMode gameMode) {
        CorePlayer cp = Core.getInstance().getPlayers().get(this);
        cp.setGameMode(gameMode);
    }
    
    public Player getPlayer() {
        return Bukkit.getPlayer(uuid);
    }
    
    public void init(Document doc, Player player) {
        if (doc != null) {
            super.load(doc);
        }
        else {
            uuid = player.getUniqueId();
            username = player.getName();
            newPlayer();
        }
    }
    
    public void setOnline(boolean state) {
        online = state;
    }
    public boolean isOnline() {
        return online;
    }
    
    protected void newPlayer() { }
    public void init() { }
    public void close() { }
    
    public void checkMovement() { }
    public void printStats(DBPlayer dbp) { }
    
    @Override
    public Document save() {
        Document doc = super.save();
        
        return doc;
    }
    
    @Override
    public void load(Document doc) {
        super.load(doc);
    }
    
    public void joinBattle(B battle, BattleState battleState) {
        this.battle = battle;
        this.battleState = battleState;
        CorePlugin.setPlayerBattle(getPlayer(), battle);
    }
    public void leaveBattle() {
        battle = null;
        battleState = BattleState.NONE;
        CorePlugin.removePlayerBattle(getPlayer());
    }
    
    public B getBattle() {
        return battle;
    }
    public BattleState getBattleState() {
        return battleState;
    }
    
    public ItemStack[] getInventory() {
        if (pregameState.getInventory() != null) {
            return pregameState.getInventory();
        } else {
            return getPlayer().getInventory().getContents();
        }
    }

    public void savePregameState() {
        pregameState.save(PregameState.PSFlag.ALL);
    }

    public void loadPregameState(@Nullable Location arenaLoc) {
        pregameState.load(arenaLoc);
    }
    
}
