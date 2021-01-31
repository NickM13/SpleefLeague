/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.menu.hotbars.main.socialmedia;

import com.spleefleague.core.Core;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.spleefleague.coreapi.database.annotation.DBField;
import com.spleefleague.coreapi.database.variable.DBEntity;
import org.bson.Document;

/**
 * Credits for players who helped build the server
 *
 * @author NickM13
 */
public class Credits extends DBEntity {
    
    protected static List<Credits> credits = new ArrayList<>();
    
    public static void init() {
        for (Document document : Core.getInstance().getPluginDB().getCollection("ServerCredits").find()) {
            Credits credit = new Credits();
            credit.load(document);
            credits.add(credit);
        }
    }
    
    public static List<Credits> getCredits() {
        return credits;
    }
    
    @DBField private String uuid;
    @DBField private String role;
    @DBField private String description;
    @DBField private Integer slot;
    
    public UUID getUuid() {
        return UUID.fromString(uuid);
    }
    public String getRole() {
        return role;
    }
    public String getDescription() {
        return "Role: " + role
                + "\n" + description;
    }
    public Integer getSlot() {
        return slot;
    }
    
}
