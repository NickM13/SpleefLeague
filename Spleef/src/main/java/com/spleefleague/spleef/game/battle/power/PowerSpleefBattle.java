/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.spleef.game.battle.power;

import com.comphenix.protocol.wrappers.BlockPosition;
import com.spleefleague.core.chat.Chat;
import com.spleefleague.core.game.Arena;
import com.spleefleague.core.game.BattleUtils;
import com.spleefleague.core.game.battle.BattlePlayer;
import com.spleefleague.core.game.battle.versus.VersusBattle;
import com.spleefleague.core.music.NoteBlockMusic;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.util.variable.Dimension;
import com.spleefleague.core.world.FakeBlock;
import com.spleefleague.core.world.FakeUtils;
import com.spleefleague.core.world.build.BuildStructure;
import com.spleefleague.core.world.build.BuildStructures;
import com.spleefleague.core.world.game.GameWorld;
import com.spleefleague.spleef.Spleef;
import com.spleefleague.spleef.game.SpleefMode;
import com.spleefleague.spleef.game.battle.classic.ClassicSpleefPlayer;
import com.spleefleague.spleef.game.battle.power.ability.Ability;
import com.spleefleague.spleef.util.SpleefUtils;
import jdk.internal.joptsimple.internal.Strings;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * @author NickM13
 */
public class PowerSpleefBattle extends VersusBattle<PowerSpleefPlayer> {

    private BuildStructure randomField;

    public PowerSpleefBattle(List<CorePlayer> players, Arena arena) {
        super(Spleef.getInstance(), players, arena, PowerSpleefPlayer.class, SpleefMode.POWER.getBattleMode());
    }
    
    /**
     * Initialize base battle settings such as GameWorld tools and Scoreboard values
     */
    @Override
    protected void setupBaseSettings() {
        SpleefUtils.setupBaseSettings(this);
        randomField = BuildStructures.getRandom("spleef:power");
        gameWorld.setBaseBlocks(
                FakeUtils.translateBlocks(
                        FakeUtils.rotateBlocks(randomField.getFakeBlocks(), (int) getArena().getOrigin().getYaw()),
                        getArena().getOrigin().toBlockPosition()));
    }

    private static final String LB = ChatColor.GRAY + "" + ChatColor.BOLD + "[";
    private static final String RB = ChatColor.GRAY + "" + ChatColor.BOLD + "]";

    @Override
    protected void setupScoreboard() {
        chatGroup.setScoreboardName(ChatColor.GOLD + "" + ChatColor.BOLD + getMode().getDisplayName());
        chatGroup.addTeam("time", "  00:00:00");
        chatGroup.addTeam("p1", "  ");
        chatGroup.addTeam("p1score", "");
        chatGroup.addTeam("p1o", "");
        chatGroup.addTeam("p1u", "");
        chatGroup.addTeam("p1m", "");
        chatGroup.addTeam("l1", "");
        chatGroup.addTeam("p2", "  ");
        chatGroup.addTeam("p2score", "");
        chatGroup.addTeam("p2o", "");
        chatGroup.addTeam("p2u", "");
        chatGroup.addTeam("p2m", "");
    }

    @Override
    public void updateScoreboard() {
        this.chatGroup.setTeamDisplayName("time", "  " + Chat.DEFAULT + getRuntimeStringNoMillis());
        chatGroup.setTeamDisplayName("p1score", BattleUtils.toScoreSquares(sortedBattlers.get(0), playToPoints));
        chatGroup.setTeamDisplayName("p1o", LB + Ability.Type.OFFENSIVE.getColor() + sortedBattlers.get(0).getOffensiveName() + RB);
        chatGroup.setTeamDisplayName("p1u", LB + Ability.Type.UTILITY.getColor() + sortedBattlers.get(0).getUtilityName() + RB);
        chatGroup.setTeamDisplayName("p1m", LB + Ability.Type.MOBILITY.getColor() + sortedBattlers.get(0).getMobilityName() + RB);
        chatGroup.setTeamDisplayName("p2score", BattleUtils.toScoreSquares(sortedBattlers.get(1), playToPoints));
        chatGroup.setTeamDisplayName("p2o", LB + Ability.Type.OFFENSIVE.getColor() + sortedBattlers.get(1).getOffensiveName() + RB);
        chatGroup.setTeamDisplayName("p2u", LB + Ability.Type.UTILITY.getColor() + sortedBattlers.get(1).getUtilityName() + RB);
        chatGroup.setTeamDisplayName("p2m", LB + Ability.Type.MOBILITY.getColor() + sortedBattlers.get(1).getMobilityName() + RB);
    }

    @Override
    public void fillField() {
        BuildStructures.getNames();
        SpleefUtils.fillFieldFast(this, randomField);
    }
    
    @Override
    public void reset() {
        fillField();
    }
    
    @Override
    public void updateField() {
        for (PowerSpleefPlayer psp : battlers.values()) {
            psp.updateAbilities();
        }
        gameWorld.performBaseBreakRegen();
    }

    @Override
    public void startRound() {
        super.startRound();
        for (PowerSpleefPlayer psp : battlers.values()) {
            psp.getPlayer().getActivePotionEffects().forEach(pe -> psp.getPlayer().removePotionEffect(pe.getType()));
            NoteBlockMusic.playSong(psp.getCorePlayer(), NoteBlockMusic.getSong("biogra.nbs"), 0.2f);
        }
    }

    @Override
    public void startCountdown() {
        super.startCountdown();
        countdown = 5;
    }

    public BlockPosition createRespawnPlatform() {
        BlockPosition pos = null;
        Set<BlockPosition> positions = getGameWorld().getBaseBlocks().keySet();
        while (!positions.isEmpty() && pos == null) {
            int r = new Random().nextInt(positions.size());
            int i = 0;
            for (BlockPosition bp : positions) {
                if (i == r) {
                    boolean valid = true;
                    for (Dimension dim : arena.getBorders()) {
                        if (!dim.expand(-2).isContained(bp)) {
                            valid = false;
                            break;
                        }
                    }
                    if (valid) {
                        pos = bp;
                    }
                    positions.remove(bp);
                    break;
                }
                i++;
            }
        }
        if (pos == null) {
            pos = getArena().getOrigin().toBlockPosition();
        }
        pos = pos.add(new BlockPosition(0, 15, 0));
        BuildStructure platform = BuildStructures.get("power:respawn");
        Map<BlockPosition, FakeBlock> transformed = FakeUtils.translateBlocks(platform.getFakeBlocks(), pos);
        getGameWorld().setBlocks(transformed);
        for (Map.Entry<BlockPosition, FakeBlock> entry : transformed.entrySet()) {
            getGameWorld().setBlockDelayed(entry.getKey(), Material.AIR.createBlockData(), 6 * 20);
        }
        return pos;
    }

    @Override
    protected void endBattle(PowerSpleefPlayer winner) {
        for (PowerSpleefPlayer psp : battlers.values()) {
            Bukkit.getScheduler().runTaskLater(Spleef.getInstance(), psp::resetCooldowns, 2L);
        }
        super.endBattle(winner);
    }

    @Override
    protected void failBattler(CorePlayer cp) {
        for (PowerSpleefPlayer psp : sortedBattlers) {
            if (!psp.getCorePlayer().equals(cp)) {
                psp.addRoundWin();
                if (psp.getRoundWins() >= playToPoints) {
                    endBattle(psp);
                    return;
                }
            }
        }

        gameWorld.doFailBlast(cp);
        battlers.get(cp).respawn();

        for (Map.Entry<BlockPosition, FakeBlock> baseBlock : gameWorld.getBaseBlocks().entrySet()) {
            if (!gameWorld.getFakeBlocks().containsKey(baseBlock.getKey()) ||
                    gameWorld.getFakeBlocks().get(baseBlock.getKey()).getBlockData().getMaterial() != baseBlock.getValue().getBlockData().getMaterial()) {
                gameWorld.setBlockDelayed(baseBlock.getKey(), baseBlock.getValue().getBlockData(), (int) (Math.random() * 60));
            }
        }
    }

}
