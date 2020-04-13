/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.util.database;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.spleefleague.core.Core;
import java.util.function.Consumer;
import org.bson.Document;
import org.bukkit.Bukkit;

/**
 * @author NickM13
 */
public class DatabaseUtil {
    
    public static void findAndRun(MongoCollection<Document> collection, Document query, Consumer<FindIterable<Document>> consumer) {
        Bukkit.getScheduler().runTaskAsynchronously(Core.getInstance(), () -> {
            consumer.accept(collection.find(query));
        });
    }
    
    public static void saferead(Object o1, Object o2) {
        if (o2 != null) {
            o1 = o2;
        }
    }
    
}
