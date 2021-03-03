/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.spleef.game.battle.classic;

import com.spleefleague.core.chat.Chat;
import com.spleefleague.core.game.Arena;
import com.spleefleague.core.game.battle.Battle;
import com.spleefleague.core.game.battle.BattlePlayer;
import com.spleefleague.core.game.battle.versus.VersusBattle;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.collectible.hat.Hat;
import com.spleefleague.core.player.purse.CoreCurrency;
import com.spleefleague.core.world.FakeUtils;
import com.spleefleague.core.world.build.BuildStructure;
import com.spleefleague.core.world.build.BuildStructures;
import com.spleefleague.spleef.Spleef;
import com.spleefleague.spleef.game.Shovel;
import com.spleefleague.spleef.game.SpleefMode;
import com.spleefleague.spleef.game.battle.classic.affix.ClassicSpleefAffix;
import com.spleefleague.spleef.game.battle.classic.affix.ClassicSpleefAffixes;
import com.spleefleague.spleef.game.battle.power.PowerSpleefPlayer;
import com.spleefleague.spleef.util.SpleefUtils;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

/**
 * @author NickM13
 */
public class ClassicSpleefBattle extends VersusBattle<ClassicSpleefPlayer> {

    private BuildStructure randomField;

    private List<ClassicSpleefAffix> affixes = new ArrayList<>();

    public ClassicSpleefBattle(UUID battleId, List<UUID> players, Arena arena) {
        super(Spleef.getInstance(), battleId, players, arena, ClassicSpleefPlayer.class, SpleefMode.CLASSIC.getBattleMode());
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
        randomField = arena.getRandomStructure("spleef:classic");
        if (randomField != null) {
            gameWorld.setBaseBlocks(
                    FakeUtils.translateBlocks(
                            FakeUtils.rotateBlocks(randomField.getFakeBlocks(), (int) getArena().getOrigin().getYaw()),
                            getArena().getOrigin().toBlockPosition()));
        }
        affixes = ClassicSpleefAffixes.startBattle(this);
    }

    @Override
    protected void setupBattlers() {
        super.setupBattlers();
        for (ClassicSpleefPlayer csp : battlers.values()) {
            gameHistory.addPlayerAdditional(csp.getCorePlayer().getUniqueId(),
                    "shovel", csp.getCorePlayer().getCollectibles().getActive(Shovel.class).getIdentifier());
        }
    }

    @Override
    public void fillField() {
        SpleefUtils.fillFieldFast(this, randomField);
    }
    
    @Override
    public void reset() {
        getGameWorld().reset();
        fillField();
        affixes.forEach(ClassicSpleefAffix::startRound);
        for (ClassicSpleefPlayer csp : battlers.values()) {
            csp.respawn();
        }
    }

    @Override
    public void startRound() {
        getGameWorld().reset();
        super.startRound();
        affixes.forEach(ClassicSpleefAffix::startRound);
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
        affixes.forEach(ClassicSpleefAffix::update);
    }

    @Override
    public void onBlockBreak(CorePlayer cp) {
        super.onBlockBreak(cp);
        affixes.forEach(affix -> affix.onBlockBreak(battlers.get(cp)));
    }

    @Override
    public void onRightClick(CorePlayer cp) {
        super.onRightClick(cp);
        affixes.forEach(affix -> affix.onRightClick(battlers.get(cp)));
    }

}
