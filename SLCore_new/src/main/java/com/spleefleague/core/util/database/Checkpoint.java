/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.util.database;

import com.spleefleague.core.util.Warp;
import org.bson.Document;
import org.bukkit.Location;

/**
 * @author NickM13
 */
public class Checkpoint implements DBVariable<Document> {
    
    protected String warp;
    
    // Expiration time in seconds
    protected Integer expireTime;
    
    public Checkpoint(String warp, int duration) {
        this.warp = warp;
        if (duration == 0)
            this.expireTime = 0;
        else
            this.expireTime = (int)(System.currentTimeMillis() / 1000) + duration;
    }
    
    public Location getLocation() {
        Warp w = Warp.getWarp(warp);
        if (w != null) return w.getLocation();
        return null;
    }

    public boolean isActive() {
        return expireTime == 0 || (System.currentTimeMillis() / 1000 < expireTime);
    }

    @Override
    public void load(Document doc) {
        warp = doc.get("warp", String.class);
        expireTime = doc.get("duration", Integer.class);
    }

    @Override
    public Document save() {
        return new Document("warp", warp).append("duration", expireTime);
    }
    
}
