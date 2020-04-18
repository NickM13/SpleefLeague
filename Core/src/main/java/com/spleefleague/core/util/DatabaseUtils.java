/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.util;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.spleefleague.core.Core;

import java.lang.reflect.InvocationTargetException;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.spleefleague.core.database.variable.DBVariable;
import org.bson.Document;
import org.bukkit.Bukkit;

/**
 * @author NickM13
 */
public class DatabaseUtils {
    
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

    public static DBVariable<?> createVariable(Class<? extends DBVariable<?>> dbvc, Object o) {
        try {
            DBVariable<?> dbv = dbvc.getDeclaredConstructor().newInstance();
            dbvc.getDeclaredMethod("load", Object.class).invoke(dbv, o);
            return dbv;
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException ex) {
            Logger.getLogger(dbvc.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
}
