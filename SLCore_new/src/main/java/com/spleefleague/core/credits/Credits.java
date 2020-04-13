/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.credits;

import com.mongodb.client.MongoCursor;
import com.spleefleague.core.Core;
import com.spleefleague.core.annotation.DBField;
import com.spleefleague.core.util.database.DBEntity;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.bson.Document;

/**
 * @author NickM13
 */
public class Credits extends DBEntity {
    
    protected static List<Credits> credits = new ArrayList<>();
    
    public static void init() {
        MongoCursor<Document> mc = Core.getInstance().getPluginDB().getCollection("ServerCredits").find().iterator();
        while (mc.hasNext()) {
            Credits credit = new Credits();
            credit.load(mc.next());
            credits.add(credit);
        }
    }
    
    public static List<Credits> getCredits() {
        return credits;
    }
    
    @DBField
    private String uuid;
    @DBField
    private String role;
    @DBField
    private String description;
    @DBField
    private Integer slot;
    
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
