package com.spleefleague.core.game.battle.solo;

import com.google.common.collect.Lists;
import com.spleefleague.core.Core;
import com.spleefleague.core.game.Arena;
import com.spleefleague.core.game.BattleMode;
import com.spleefleague.core.game.battle.BattlePlayer;
import com.spleefleague.core.game.battle.Battle;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.plugin.CorePlugin;
import com.spleefleague.coreapi.utils.packet.spigot.battle.PacketSpigotBattleEndUnrated;

import java.util.List;
import java.util.UUID;

/**
 * @author NickM13
 * @since 4/24/2020
 */
public abstract class SoloBattle<BP extends BattlePlayer> extends Battle<BP> {
    
    protected BP battler;
    
    public SoloBattle(CorePlugin<?> plugin, List<UUID> players, Arena arena, Class<BP> battlePlayerClass, BattleMode battleMode) {
        super(plugin, players, arena, battlePlayerClass, battleMode);
    }
    
    /**
     * Initialize base battle settings such as GameWorld tools and Scoreboard values
     */
    @Override
    protected void setupBaseSettings() {
    
    }
    
    /**
     * Initialize the players, called in startBattle()
     */
    @Override
    protected void setupBattlers() {
        battler = sortedBattlers.get(0);
    }
    
    /**
     * Send a message on the start of a battle
     */
    @Override
    protected void sendStartMessage() {
    
    }
    
    @Override
    protected void fillField() {
    
    }
    
    /**
     * Called when a battler joins mid-game (if available)
     *
     * @param cp Core Player
     */
    @Override
    protected final void joinBattler(CorePlayer cp) {
    
    }
    
    /**
     * Called when a battler leaves boundaries
     *
     * @param cp Core Player
     */
    @Override
    protected void failBattler(CorePlayer cp) {
    
    }
    
    /**
     * Called when a battler enters a goal area
     *
     * @param cp CorePlayer
     */
    @Override
    protected void winBattler(CorePlayer cp) {
    
    }
    
    /**
     * Called when a player surrenders (/ff, /leave)
     *
     * @param cp Core Player
     */
    @Override
    public void surrender(CorePlayer cp) {
    
    }
    
    /**
     * Called when a battler wants to leave (/leave, /ff)
     *
     * @param cp Battler CorePlayer
     */
    @Override
    protected void leaveBattler(CorePlayer cp) {
        endBattle(null);
    }
    
    /**
     * Called every 1 second or on score updates
     * Updates the player scoreboards
     */
    @Override
    public void updateScoreboard() {
    
    }
    
    /**
     * Called every 1/10 second
     * Updates the field on occasion for events such as
     * auto-regenerating maps
     */
    @Override
    public void updateField() {
    
    }
    
    /**
     * Updates the experience bar of players in the game
     */
    @Override
    public void updateExperience() {
    
    }
    
    /**
     * Called when a Play To request passes
     *
     * @param playTo Play To Value
     */
    public void setPlayTo(int playTo) { }

    /**
     * End a battle with a determined winner
     *
     * @param winner Winner
     */
    @Override
    public void endBattle(BP winner) {
        Core.getInstance().sendPacket(new PacketSpigotBattleEndUnrated(getMode().getName(), Lists.newArrayList(battler.getCorePlayer().getUniqueId())));
        destroy();
    }
    
}
