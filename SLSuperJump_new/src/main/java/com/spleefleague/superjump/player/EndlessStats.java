/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.superjump.player;

import com.spleefleague.core.game.Leaderboard;
import com.spleefleague.core.util.Day;
import com.spleefleague.core.util.database.DBVariable;
import com.spleefleague.core.util.database.DatabaseUtil;
import com.spleefleague.superjump.game.SJMode;
import com.spleefleague.superjump.game.endless.EndlessSJArena;
import org.bson.Document;

/**
 * @author NickM13
 */
public class EndlessStats implements DBVariable<Document> {
    // Days since epoch time
    private Integer day;
    
    // Furthest level today
    private Integer level;
    
    // Furthest level ever achieved
    private Integer highestLevel;
    
    // Falls (all time)
    private Integer falls;
    
    // Time spent playing (all time)
    private Long time;
    
    private SuperJumpPlayer sjp;
    
    public EndlessStats() {
        day = Day.getCurrentDay();
        level = highestLevel = 1;
        falls = 0;
        time = 0L;
    }
    
    public void setPlayer(SuperJumpPlayer sjp) {
        this.sjp = sjp;
    }
    
    public void renew(int day) {
        if (this.day != day) {
            highestLevel = Math.max(highestLevel, level);
            
            this.day = day;
            level = 1;
        }
    }
    
    public int getDay() {
        return day;
    }
    
    public int getLevel() {
        return level;
    }
    public void setLevel(int level) {
        this.level = level;
    }
    public int getHighestLevel() {
        return highestLevel;
    }
    public void setHighestLevel(int level) {
        highestLevel = level;
    }
    public void incrementLevel() {
        level++;
        if (level > highestLevel) setHighestLevel(level);
        Leaderboard.setPlayerScore(EndlessSJArena.EndlessLeaderboard.DAILY.getName(), sjp.getUniqueId(), level);
        Leaderboard.setPlayerScore(EndlessSJArena.EndlessLeaderboard.BEST.getName(), sjp.getUniqueId(), highestLevel);
    }
    
    public void incrementFalls() {
        falls++;
    }
    
    public void addTime(long time) {
        this.time += time;
    }

    @Override
    public void load(Document doc) {
        day = doc.get("day", Integer.class);
        level = doc.get("level", Integer.class);
        highestLevel = doc.get("highestLevel", Integer.class);
        falls = doc.get("falls", Integer.class);
        time = doc.get("time", Long.class);
        
        renew(Day.getCurrentDay());
    }

    @Override
    public Document save() {
        Document doc = new Document();
        
        doc.append("day", day);
        doc.append("level", level);
        doc.append("highestLevel", highestLevel);
        doc.append("falls", falls);
        doc.append("time", time);
        
        return doc;
    }
    
}
