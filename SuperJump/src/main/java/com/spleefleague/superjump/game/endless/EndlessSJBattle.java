/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.superjump.game.endless;

import com.comphenix.protocol.wrappers.BlockPosition;
import com.google.common.collect.Lists;
import com.spleefleague.core.game.Arena;
import com.spleefleague.core.game.battle.solo.SoloBattle;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.util.Direction;
import com.spleefleague.core.util.TimeUtils;
import com.spleefleague.core.util.variable.Day;
import com.spleefleague.core.util.variable.Dimension;
import com.spleefleague.core.util.variable.Point;
import com.spleefleague.core.util.variable.Position;
import com.spleefleague.core.world.FakeBlock;
import com.spleefleague.superjump.SuperJump;
import com.spleefleague.superjump.game.SJMode;
import com.spleefleague.superjump.util.SJUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;

import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.UUID;

/**
 * @author NickM13
 */
public class EndlessSJBattle extends SoloBattle<EndlessSJPlayer> {
    
    public EndlessSJBattle(UUID battleId, List<UUID> players, Arena arena) {
        super(SuperJump.getInstance(), battleId, players, arena, EndlessSJPlayer.class, SJMode.ENDLESS.getBattleMode());
    }
    
    @Override
    protected void fillField() {
        getGameWorld().clear();
        Position spawn = arena.getSpawns().get(0);
        Dimension border = arena.getBorders().get(0).expand(-1);
        Direction facing = Direction.fromYaw((int) spawn.getYaw());

        int rightMin = 0, rightMax = 0, upMin, upMax;

        switch (facing) {
            case NORTH:
                rightMin = (int) (border.getLow().x - spawn.getX());
                rightMax = (int) (border.getHigh().x - spawn.getX());
                break;
            case SOUTH:
                rightMin = (int) (border.getHigh().x - spawn.getX());
                rightMax = (int) (border.getLow().x - spawn.getX());
                break;
            case EAST:
                rightMin = (int) (border.getLow().z - spawn.getX());
                rightMax = (int) (border.getHigh().z - spawn.getX());
                break;
            case WEST:
                rightMin = (int) (border.getHigh().z - spawn.getX());
                rightMax = (int) (border.getLow().z - spawn.getX());
                break;
        }
        upMin = (int) (border.getLow().y - spawn.getY());
        upMax = (int) (border.getHigh().y - spawn.getY());

        Iterator<BlockPosition> bpit = SJUtils.generateJumpsFrom(this,
                25,
                spawn.toBlockPosition().add(new BlockPosition(0, -1, 0)),
                rightMin,
                rightMax,
                upMin,
                upMax,
                facing,
                battler.getLevel() / 50D,
                new Random(Day.getDailyRandom() + battler.getLevel())).iterator();
        while (bpit.hasNext()) {
            BlockPosition pos = bpit.next();
            if (!bpit.hasNext()) {
                getGameWorld().setBlock(pos, new FakeBlock(Material.REDSTONE_LAMP.createBlockData()));
                Dimension goal = new Dimension(new Point(pos.getX() - 0.29, pos.getY(), pos.getZ() - 0.29), new Point(pos.getX() + 1.29, pos.getY() + 1.1, pos.getZ() + 1.29));
                setGoals(Lists.newArrayList(goal));
            } else {
                getGameWorld().setBlockDelayed(pos, Material.WHITE_CONCRETE.createBlockData(), 0.05, arena.getSpawns());
            }
        }
    }
    
    /*
    @Override
    public void startBattle() {
        if (getResetTime() <= 0) {
            for (BattlePlayer bp : battlers.values()) {
                Core.getInstance().sendMessage(bp.getDBPlayer(), "Endless is currently disabled, try again in " + TimeUtils.gcdTimeToString(Day.getTomorrowMillis()));
            }
            endBattle();
            return;
        }
        super.startBattle();
    }
    */
    
    private long getResetTime() {
        // 5 minute head start
        long millis = Day.getTomorrowMillis() - 1000 * 60 * 5;
        if (millis < 0) {
            return 0;
        }
        return millis;
    }
    
    private String getResetTimeStr() {
        return TimeUtils.gcdTimeToString(getResetTime());
    }
    
    @Override
    protected void setupBattleRequests() {
    
    }
    
    @Override
    protected void setupBattlers() {
        super.setupBattlers();
    }
    
    @Override
    protected void sendStartMessage() {
        getPlugin().sendMessage(battler.getCorePlayer(), "You're now playing " + getMode().getDisplayName() + "!");
    }
    
    @Override
    protected void setupScoreboard() {
        chatGroup.setScoreboardName(ChatColor.GREEN + "" + ChatColor.BOLD + "ENDLESS");
        chatGroup.addTeam("Today", ChatColor.WHITE + "" + ChatColor.BOLD + "Today");
        chatGroup.addTeam("TodayPersonal", " Personal: ");
        chatGroup.addTeam("TodayServer", " Server: ");
        chatGroup.addTeam("0", " ");
        chatGroup.addTeam("Record", ChatColor.WHITE + "" + ChatColor.BOLD + "Record");
        chatGroup.addTeam("RecordPersonal", " Personal: ");
        chatGroup.addTeam("RecordServer", " Server: ");
        chatGroup.addTeam("1", " ");
        chatGroup.addTeam("Time", ChatColor.WHITE + "" + ChatColor.BOLD + "Time");
        chatGroup.addTeam("TimeReset", " Reset In: ");
        updateScoreboard();
    }
    
    @Override
    protected void saveBattlerStats(EndlessSJPlayer endlessSJPlayer) {
        endlessSJPlayer.getCorePlayer().getStatistics().add("superjump", "endless:playTime", endlessSJPlayer.getLevel());
        endlessSJPlayer.getCorePlayer().getStatistics().add("superjump", "endless:falls", endlessSJPlayer.getFalls());
        endlessSJPlayer.getCorePlayer().getStatistics().setHigher("superjump", "endless:bestLevel", "endless:level");
    }
    
    @Override
    protected void endRound(EndlessSJPlayer endlessSJPlayer) {
    
    }
    
    @Override
    public void updateScoreboard() {
        chatGroup.setTeamDisplayName("TodayPersonal", " Personal: " + battler.getLevel());
        chatGroup.setTeamDisplayName("TodayServer", " Server: ");
        chatGroup.setTeamDisplayName("RecordPersonal", " Personal: ");
        chatGroup.setTeamDisplayName("RecordServer", " Server: ");
        chatGroup.setTeamDisplayName("TimeReset", " Reset In: ");
        for (CorePlayer cp : players) {
            cp.sendHotbarText(ChatColor.WHITE + getRuntimeString());
        }
    }
    
    @Override
    protected void failBattler(CorePlayer cp) {
        battler.respawn();
        battler.addFall();
    }
    
    @Override
    protected void winBattler(CorePlayer cp) {
        battler.addRoundWin();
        battler.respawn();
        fillField();
        //startRound();
    }
    
    @Override
    public void reset() {
        battler.respawn();
    }
    
    @Override
    public void surrender(CorePlayer cp) {
        leaveBattler(cp);
    }
    
    @Override
    protected void leaveBattler(CorePlayer cp) {
        super.endBattle(null);
    }
    
    /*
    @Override
    public void updateScoreboard() {
        BattlePlayer bp = battlers.values().iterator().next();
        SuperJumpPlayer sjp = SuperJump.getInstance().getPlayers().get(bp.getCorePlayer());
        chatGroup.setTeamDisplayName("TodayPersonal", ChatColor.AQUA + " Personal: " + sjp.getEndlessStats().getLevel() +
                "[" + CoreUtils.getPlaceSuffixed(Leaderboard.getPlace(EndlessSJArena.EndlessLeaderboard.DAILY.getName(), sjp.getUniqueId())) + "]");
        chatGroup.setTeamDisplayName("TodayServer", ChatColor.AQUA + " Server: " +
                Leaderboard.getLeadingPlayerName(EndlessSJArena.EndlessLeaderboard.DAILY.getName()) +
                ChatColor.AQUA + "[" + Leaderboard.getLeadingPlayerScore(EndlessSJArena.EndlessLeaderboard.DAILY.getName()) + "]");
        chatGroup.setTeamDisplayName("RecordPersonal", ChatColor.AQUA + " Personal: " + sjp.getEndlessStats().getHighestLevel() +
                "[" + CoreUtils.getPlaceSuffixed(Leaderboard.getPlace(EndlessSJArena.EndlessLeaderboard.BEST.getName(), sjp.getUniqueId())) + "]");
        chatGroup.setTeamDisplayName("RecordServer", ChatColor.AQUA + " Server: " +
                Leaderboard.getLeadingPlayerName(EndlessSJArena.EndlessLeaderboard.BEST.getName()) +
                ChatColor.AQUA + "[" + Leaderboard.getLeadingPlayerScore(EndlessSJArena.EndlessLeaderboard.BEST.getName()) + "]");
        chatGroup.setTeamDisplayName("TimeReset", ChatColor.AQUA + " Reset In: " + getResetTimeStr());
        
        if (getResetTime() <= 0) {
            endBattle();
        }
        */
        
        /*
        sidebar.getScore(" Personal: " + ChatColor.GOLD + players.get(0).getEndlessLevel() 
                + ChatColor.GRAY + " [" + place + post + "]").setScore(9);
        sidebar.getScore(" Server: " + ChatColor.GOLD + arena.getHighscoreToday().getTopPlayer().score + ChatColor.GRAY + " [" 
                + arena.getHighscoreToday().getTopPlayer().player + ChatColor.GRAY + "] ").setScore(8);
        sidebar.getScore(ChatColor.BOLD + " ").setScore(6);
        sidebar.getScore(ChatColor.BOLD + "Record").setScore(5);
        sidebar.getScore(" Personal: " + ChatColor.GOLD + players.get(0).getEndlessLevelRecord()).setScore(4);
        sidebar.getScore(" Server: " + ChatColor.GOLD + arena.getHighscoreAlltime().score + ChatColor.GRAY + " [" 
                + arena.getHighscoreAlltime().player + ChatColor.GRAY + "]").setScore(3);
        sidebar.getScore(ChatColor.BOLD + "  ").setScore(2);
        sidebar.getScore(ChatColor.BOLD + "Time").setScore(1);
        sidebar.getScore(" Level: " + ChatColor.GOLD + levelTime).setScore(0); // "Remove this" - Sinsie, probably
        sidebar.getScore(" Reset In: " + ChatColor.GOLD + resetTime).setScore(0);
    }
        */
    
        /*
    @Override
    public void updateExperience() {
        //chatGroup.setExperience((getLevelTime() % 1000) / 1000.f, (int)(getLevelTime() / 1000));
    }
    
    @Override
    protected void fillField() {
        float difficulty;
        if(endlessPlayer.getEndlessStats().getLevel() <= 200) {
            difficulty = 0.f + (float)Math.floor(endlessPlayer.getEndlessStats().getLevel() / 10.f) * 0.15f;
        } else {
            difficulty = Math.max(3.f + (float)Math.floor((endlessPlayer.getEndlessStats().getLevel() - 200) / 10.f) * 0.2f, 4.f);
        }
        random = new Random(LocalDate.now().getDayOfYear());
        random = new Random(random.nextInt() + endlessPlayer.getEndlessStats().getLevel());
        List<FakeBlock> fakeBlocks = generate1(getSpawn(0), arena.getJumpCount(), difficulty, false);

        for (FakeBlock fb : fakeBlocks) {
            gameWorld.setBlock(fb.getBlockPosition(), fb.getBlockData());
        }
    }
         */

    /*
    @Override
    protected void failPlayer(DBPlayer dbp) {
        super.failPlayer(dbp);
        endlessPlayer.getEndlessStats().incrementFalls();
    }
     */

    /*
    @Override
    protected void winPlayer(DBPlayer dbp) {
        resetPlayers();
        gameWorld.clear();
        fillField();
        doCountdown();
        String completeMessage;
        
        float levelTime = Math.floorDiv(getLevelTime(), 10L) / 100.f;
        if(levelTime < 30)      completeMessage = "" + ChatColor.GREEN;
        else if(levelTime < 60) completeMessage = "" + ChatColor.YELLOW;
        else                    completeMessage = "" + ChatColor.RED;
        completeMessage += "" + String.format("%.2f", levelTime) + " Seconds";
        chatGroup.sendTitle(ChatColor.GREEN + "Completed In", completeMessage, 5, 20, 5);
        
        endlessPlayer.getEndlessStats().incrementLevel();
        endlessPlayer.getEndlessStats().addTime(getLevelTime() / 1000);
        
        timeLastLap = System.currentTimeMillis();
    }
     */

}
