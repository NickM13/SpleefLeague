/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.superjump.game.endless;

import com.spleefleague.core.Core;
import com.spleefleague.core.chat.Chat;
import com.spleefleague.core.chat.ChatChannel;
import com.spleefleague.core.game.Leaderboard;
import com.spleefleague.core.player.BattleState;
import com.spleefleague.core.util.CoreUtils;
import com.spleefleague.core.util.Day;
import com.spleefleague.core.util.TimeUtils;
import com.spleefleague.core.util.database.DBPlayer;
import com.spleefleague.core.world.FakeBlock;
import com.spleefleague.superjump.game.SJBattle;
import com.spleefleague.superjump.player.SuperJumpPlayer;
import java.time.LocalDate;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;

/**
 * @author NickM13
 */
public class EndlessSJBattle extends SJBattle<EndlessSJArena> {

    private final SuperJumpPlayer endlessPlayer;
    
    public EndlessSJBattle(List<DBPlayer> players, EndlessSJArena arena) {
        super(players, arena);
        endlessPlayer = battlers.keySet().iterator().next();
    }
    
    @Override
    protected void startBattle() {
        if (getResetTime() <= 0) {
            for (BattlePlayer bp : battlers.values()) {
                Core.getInstance().sendMessage(bp.player, "Endless is currently disabled, try again in " + TimeUtils.gcdTimeToString(Day.getTomorrowMillis()));
            }
            endBattle();
            return;
        }
        super.startBattle();
        //chatGroup.addTeam("Level", Chat.SCORE + "Level");
        Core.getInstance().sendMessage(ChatChannel.getChannel(ChatChannel.Channel.SUPERJUMP), "A " +
                Chat.GAMEMODE + getMode().getDisplayName() + " " +
                Chat.DEFAULT + "match between " +
                playersFormatted +
                Chat.DEFAULT + " has begun on " +
                Chat.GAMEMAP + arena.getDisplayName());
        for (BattlePlayer bp : battlers.values()) {
            bp.player.joinBattle(this, BattleState.BATTLER);
            
            bp.player.getPlayer().getInventory().setHeldItemSlot(0);
            bp.player.getPlayer().getInventory().clear();
            
            bp.player.getPlayer().setGameMode(GameMode.SURVIVAL);
            
            chatGroup.addPlayer(bp.player);
        }
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
    }
    
    @Override
    public void endBattle() {
        super.endBattle();
        endlessPlayer.getEndlessStats().addTime(getLevelTime() / 1000);
    }
    
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
    public void updateExperience() {
        chatGroup.setExperience((getLevelTime() % 1000) / 1000.f, (int)(getLevelTime() / 1000));
    }
    
    @Override
    public void updateScoreboard() {
        BattlePlayer bp = battlers.values().iterator().next();
        chatGroup.setTeamDisplayName("TodayPersonal", ChatColor.AQUA + " Personal: " + bp.player.getEndlessStats().getLevel() +
                "[" + CoreUtils.getPlaceSuffixed(Leaderboard.getPlace(EndlessSJArena.EndlessLeaderboard.DAILY.getName(), bp.player.getUniqueId())) + "]");
        chatGroup.setTeamDisplayName("TodayServer", ChatColor.AQUA + " Server: " +
                Leaderboard.getLeadingPlayerName(EndlessSJArena.EndlessLeaderboard.DAILY.getName()) +
                ChatColor.AQUA + "[" + Leaderboard.getLeadingPlayerScore(EndlessSJArena.EndlessLeaderboard.DAILY.getName()) + "]");
        chatGroup.setTeamDisplayName("RecordPersonal", ChatColor.AQUA + " Personal: " + bp.player.getEndlessStats().getHighestLevel() +
                "[" + CoreUtils.getPlaceSuffixed(Leaderboard.getPlace(EndlessSJArena.EndlessLeaderboard.BEST.getName(), bp.player.getUniqueId())) + "]");
        chatGroup.setTeamDisplayName("RecordServer", ChatColor.AQUA + " Server: " +
                Leaderboard.getLeadingPlayerName(EndlessSJArena.EndlessLeaderboard.BEST.getName()) +
                ChatColor.AQUA + "[" + Leaderboard.getLeadingPlayerScore(EndlessSJArena.EndlessLeaderboard.BEST.getName()) + "]");
        chatGroup.setTeamDisplayName("TimeReset", ChatColor.AQUA + " Reset In: " + getResetTimeStr());
        
        if (getResetTime() <= 0) {
            endBattle();
        }
        
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
        */
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
        
        Iterator<FakeBlock> it = fakeBlocks.iterator();
        while (it.hasNext()) {
            FakeBlock fb = it.next();
            gameWorld.setBlock(fb.getBlockPosition(), fb.getBlockData());
        }
    }
    
    @Override
    public void releasePlayers() {
        super.releasePlayers();
    }
    
    @Override
    protected void failPlayer(SuperJumpPlayer sjp) {
        super.failPlayer(sjp);
        endlessPlayer.getEndlessStats().incrementFalls();
    }
    
    @Override
    protected void winPlayer(SuperJumpPlayer sjp) {
        resetPlayers();
        gameWorld.clear();
        fillField();
        doCountdown();
        String completeMessage;
        
        float levelTime = Math.floorDiv(getLevelTime(), 10) / 100.f;
        if(levelTime < 30)      completeMessage = "" + ChatColor.GREEN;
        else if(levelTime < 60) completeMessage = "" + ChatColor.YELLOW;
        else                    completeMessage = "" + ChatColor.RED;
        completeMessage += "" + String.format("%.2f", levelTime) + " Seconds";
        chatGroup.sendTitle(ChatColor.GREEN + "Completed In", completeMessage, 5, 20, 5);
        
        endlessPlayer.getEndlessStats().incrementLevel();
        endlessPlayer.getEndlessStats().addTime(getLevelTime() / 1000);
        
        timeLastLap = System.currentTimeMillis();
    }
    
    @Override
    public void doCountdown() {
        super.doCountdown();
    }

}
