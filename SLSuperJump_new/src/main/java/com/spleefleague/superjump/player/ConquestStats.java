/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.superjump.player;

import com.spleefleague.core.Core;
import com.spleefleague.core.chat.Chat;
import com.spleefleague.core.util.database.DBVariable;
import com.spleefleague.superjump.game.conquest.ConquestSJArena;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bson.Document;

/**
 * @author NickM13
 */
public class ConquestStats implements DBVariable<List<Document>> {
    
    public class ConquestScore {
        double bestTime;
        int totalRuns;
        
        ConquestScore(double bestTime, int totalRuns) {
            this.bestTime = bestTime;
            this.totalRuns = totalRuns;
        }
    }
    
    public enum RecordState {
        NEW, BEAT, NONE
    }
    
    private final Map<String, ConquestScore> conquestScores = new HashMap<>();
    
    private SuperJumpPlayer sjp;
    
    public ConquestStats() {
        
    }
    
    public void setPlayer(SuperJumpPlayer sjp) {
        this.sjp = sjp;
    }
    
    public RecordState tryNewTime(ConquestSJArena arena, double time) {
        if (conquestScores.containsKey(arena.getName())) {
            conquestScores.get(arena.getName()).totalRuns++;
            if (time < conquestScores.get(arena.getName()).bestTime) {
                conquestScores.get(arena.getName()).bestTime = time;
                return RecordState.BEAT;
            } else {
                return RecordState.NONE;
            }
        } else {
            ConquestScore score = new ConquestScore(time, 1);
            conquestScores.put(arena.getName(), score);
            return RecordState.NEW;
        }
    }
    
    public int getStars(ConquestSJArena arena) {
        if (conquestScores.containsKey(arena.getName())) {
            return arena.getStar(conquestScores.get(arena.getName()).bestTime);
        } else {
            return 0;
        }
    }
    
    public String getDescription(ConquestSJArena arena) {
        String desc = "";
        
        if (conquestScores.containsKey(arena.getName())) {
            desc += "Best time: " + conquestScores.get(arena.getName()).bestTime;
        } else {
            desc += "You haven't played this map yet";
        }
        
        return desc;
    }

    @Override
    public void load(List<Document> docs) {
        for (Document doc : docs) {
            ConquestScore score = new ConquestScore(doc.get("bestTime", Double.class), doc.get("totalRuns", Integer.class));
            conquestScores.put(doc.get("arena", String.class), score);
        }
    }

    @Override
    public List<Document> save() {
        List<Document> docs = new ArrayList<>();
        
        for (Map.Entry<String, ConquestScore> score : conquestScores.entrySet()) {
            Document doc = new Document("arena", score.getKey());
            doc.append("bestTime", score.getValue().bestTime);
            doc.append("totalRuns", score.getValue().totalRuns);
            docs.add(doc);
        }
        
        return docs;
    }
    
}
