/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.spleef.game.battle.classic;

import com.spleefleague.core.chat.Chat;
import com.spleefleague.core.game.Arena;
import com.spleefleague.core.game.battle.BattlePlayer;
import com.spleefleague.core.game.battle.versus.VersusBattle;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.world.FakeUtils;
import com.spleefleague.core.world.build.BuildStructures;
import com.spleefleague.spleef.Spleef;
import com.spleefleague.spleef.game.SpleefMode;
import com.spleefleague.spleef.game.battle.classic.affix.ClassicSpleefAffixes;
import com.spleefleague.spleef.util.SpleefUtils;
import org.bukkit.ChatColor;

import java.util.List;
import java.util.UUID;

/**
 * @author NickM13
 */
public class ClassicSpleefBattle extends VersusBattle<ClassicSpleefPlayer> {

    public ClassicSpleefBattle(List<UUID> players, Arena arena) {
        super(Spleef.getInstance(), players, arena, ClassicSpleefPlayer.class, SpleefMode.CLASSIC.getBattleMode());
    }

    @Override
    protected void setupScoreboard() {
        super.setupScoreboard();
        chatGroup.setScoreboardName(ChatColor.GOLD + "" + ChatColor.BOLD + getMode().getDisplayName());
        updateScoreboard();
    }

    /**
     * Initialize base battle settings such as GameWorld tools and Scoreboard values
     */
    @Override
    protected void setupBaseSettings() {
        SpleefUtils.setupBaseSettings(this);
        ClassicSpleefAffixes.startBattle(this);
        gameWorld.setBaseBlocks(
                FakeUtils.translateBlocks(
                        FakeUtils.rotateBlocks(BuildStructures.get("spleef:classic").getFakeBlocks(), (int) getArena().getOrigin().getYaw()),
                        getArena().getOrigin().toBlockPosition()));
    }

    @Override
    public void fillField() {
        SpleefUtils.fillFieldFast(this, BuildStructures.get("spleef:classic"));
    }
    
    @Override
    public void reset() {
        getGameWorld().reset();
        fillField();
        ClassicSpleefAffixes.startRound(this);
        for (ClassicSpleefPlayer csp : battlers.values()) {
            csp.respawn();
        }
    }

    @Override
    public void startRound() {
        getGameWorld().reset();
        super.startRound();
        ClassicSpleefAffixes.startRound(this);
    }

    @Override
    public void releaseBattlers() {
        super.releaseBattlers();
    }

    public void startCountdown(int seconds) {
        super.startCountdown();
        this.countdown = seconds;
    }

    @Override
    public void updateField() {
        ClassicSpleefAffixes.updateField(this);
    }

    @Override
    protected void applyRewards(ClassicSpleefPlayer classicSpleefPlayer) {

    }

}