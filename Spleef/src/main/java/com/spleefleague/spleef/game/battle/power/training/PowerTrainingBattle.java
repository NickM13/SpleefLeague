/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.spleef.game.battle.power.training;

import com.comphenix.protocol.wrappers.BlockPosition;
import com.spleefleague.core.chat.Chat;
import com.spleefleague.core.game.Arena;
import com.spleefleague.core.game.battle.solo.SoloBattle;
import com.spleefleague.core.music.NoteBlockMusic;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.util.variable.Dimension;
import com.spleefleague.core.world.FakeBlock;
import com.spleefleague.core.world.FakeUtils;
import com.spleefleague.core.world.FakeWorld;
import com.spleefleague.core.world.build.BuildStructure;
import com.spleefleague.core.world.build.BuildStructures;
import com.spleefleague.spleef.Spleef;
import com.spleefleague.spleef.game.SpleefMode;
import com.spleefleague.spleef.game.battle.power.ability.Ability;
import com.spleefleague.spleef.util.SpleefUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

/**
 * @author NickM13
 */
public class PowerTrainingBattle extends SoloBattle<PowerTrainingPlayer> {

    private BuildStructure currField;
    private int currFieldIndex = 0;
    private int regenSpeed = 0;
    private boolean cooldowns;

    public PowerTrainingBattle(UUID battleId, List<UUID> players, Arena arena) {
        super(Spleef.getInstance(), battleId, players, arena, PowerTrainingPlayer.class, SpleefMode.POWER.getBattleMode());
    }
    @Override
    protected void setupBattlers() {
        super.setupBattlers();
        battler.setOpponents(battler);
    }

    @Override
    protected void sendStartMessage() {
        Spleef.getInstance().sendMessage(battler.getCorePlayer(), "You have joined Power Training");
    }

    @Override
    protected void setupBattleRequests() {

    }

    /**
     * Initialize base battle settings such as GameWorld tools and Scoreboard values
     */
    @Override
    protected void setupBaseSettings() {
        SpleefUtils.setupBaseSettings(this);
        setupField();
    }

    private void setupField() {
        gameWorld.clear();
        List<BuildStructure> fields = BuildStructures.getAll("spleef:power");
        if (currFieldIndex >= fields.size()) currFieldIndex = 0;
        currField = BuildStructures.getAll("spleef:power").get(currFieldIndex);
        gameWorld.clearBaseBlocks();
        gameWorld.setBaseBlocks(
                FakeUtils.translateBlocks(
                        FakeUtils.rotateBlocks(currField.getFakeBlocks(), (int) getArena().getOrigin().getYaw()),
                        getArena().getOrigin().toBlockPosition()));
    }

    private static final String LB = ChatColor.GRAY + "" + ChatColor.BOLD + "[";
    private static final String RB = ChatColor.GRAY + "" + ChatColor.BOLD + "]";

    @Override
    protected void setupScoreboard() {
        chatGroup.setScoreboardName(ChatColor.GOLD + "" + ChatColor.BOLD + "Training");
        chatGroup.addTeam("time", "00:00:00");
        chatGroup.addTeam("p1", Chat.PLAYER_NAME + ChatColor.BOLD + sortedBattlers.get(0).getCorePlayer().getName());
        chatGroup.addTeam("p1o", "");
        chatGroup.addTeam("p1u", "");
        chatGroup.addTeam("p1m", "");
    }

    @Override
    public void updateScoreboard() {
        this.chatGroup.setTeamDisplayName("time", Chat.DEFAULT + getRuntimeStringNoMillis());
        chatGroup.setTeamDisplayName("p1o", LB + Ability.Type.OFFENSIVE.getColor() + sortedBattlers.get(0).getOffensiveName() + RB);
        chatGroup.setTeamDisplayName("p1u", LB + Ability.Type.UTILITY.getColor() + sortedBattlers.get(0).getUtilityName() + RB);
        chatGroup.setTeamDisplayName("p1m", LB + Ability.Type.MOBILITY.getColor() + sortedBattlers.get(0).getMobilityName() + RB);
    }

    public void updatePowers() {
        for (PowerTrainingPlayer ptp : battlers.values()) {
            ptp.selectPowers();
            ptp.getCorePlayer().refreshHotbar();
        }
    }

    public void nextField() {
        currFieldIndex++;
        setupField();
        fillField();
    }

    public int getRegenSpeed() {
        return regenSpeed;
    }

    public void nextRegenSpeed() {
        regenSpeed++;
        if (regenSpeed > 3) {
            regenSpeed = 0;
        }
        gameWorld.setRegenSpeed(Math.pow(2, regenSpeed));
    }

    public int getCurrFieldIndex() {
        return currFieldIndex;
    }

    public boolean isCooldownEnabled() {
        return cooldowns;
    }

    public void setCooldownEnabled(boolean enabled) {
        cooldowns = enabled;
    }

    @Override
    public void fillField() {
        BuildStructures.getNames();
        SpleefUtils.fillFieldFast(this, currField);
    }

    @Override
    protected void saveBattlerStats(PowerTrainingPlayer powerTrainingPlayer) {

    }

    @Override
    protected void endRound(PowerTrainingPlayer powerTrainingPlayer) {

    }

    @Override
    public void reset() {
        fillField();
    }

    @Override
    public void updateField() {
        for (PowerTrainingPlayer psp : battlers.values()) {
            psp.updateAbilities();
        }
        gameWorld.performBaseBreakRegen();
    }

    @Override
    public void startRound() {
        super.startRound();
        for (PowerTrainingPlayer psp : battlers.values()) {
            psp.getPlayer().getActivePotionEffects().forEach(pe -> psp.getPlayer().removePotionEffect(pe.getType()));
            NoteBlockMusic.playSong(psp.getCorePlayer().getUniqueId(), NoteBlockMusic.getSong("biogra.nbs"), 0.2f);
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
            getGameWorld().setBlockDelayed(entry.getKey(), FakeWorld.AIR, 6 * 20);
        }
        return pos;
    }

    @Override
    public void endBattle(PowerTrainingPlayer winner) {
        for (PowerTrainingPlayer psp : battlers.values()) {
            Spleef.getInstance().sendMessage(psp.getCorePlayer(), "You have left Power Training");
            Bukkit.getScheduler().runTaskLater(Spleef.getInstance(), psp::resetCooldowns, 2L);
        }
        super.destroy();
        super.endBattle(winner);
    }

    @Override
    protected void failBattler(CorePlayer cp) {
        gameWorld.doFailBlast(cp);
        battlers.get(cp).respawn();

        for (Map.Entry<BlockPosition, FakeBlock> baseBlock : gameWorld.getBaseBlocks().entrySet()) {
            FakeBlock fakeBlock = gameWorld.getFakeBlock(baseBlock.getKey());
            if (fakeBlock == null || fakeBlock.getBlockData().getMaterial() != baseBlock.getValue().getBlockData().getMaterial()) {
                gameWorld.setBlockDelayed(baseBlock.getKey(), baseBlock.getValue(), (int) (Math.random() * 60));
            }
        }
    }

}
