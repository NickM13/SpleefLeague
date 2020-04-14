/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.superjump.game;

import com.spleefleague.core.annotation.DBLoad;
import com.spleefleague.core.game.Arena;
import com.spleefleague.core.util.Dimension;
import com.spleefleague.superjump.SuperJump;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.bson.Document;

/**
 * @author NickM13
 */
public class SJArena extends Arena {
    
    protected List<Dimension> goals = new ArrayList<>();
    
    @DBLoad(fieldname="goals")
    protected void setGoals(List<Document> goalList) {
        goalList.forEach(g -> {
            Dimension dim = new Dimension();
            dim.load(g);
            goals.add(dim);
        });
    }
    
    protected List<Dimension> getGoals() {
        return goals;
    }
    
    public static void init() {
        for (SJMode sjm : SJMode.values()) {
            Iterator<Document> itArenas = SuperJump.getInstance().getPluginDB().getCollection("Arenas").find(new Document("parkourMode", sjm.name())).iterator();
            int amount = loadArenas(itArenas, sjm.getArenaMode());
            if (amount > 0) System.out.println("Loaded " + amount + " " + sjm.getArenaMode().getDisplayName() + " arenas.");
        }
    }
    
}
