/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.util.database;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author NickM13
 */
public class EntityBuilder {
    
    public static DBVariable create(Class<? extends DBVariable> dbvc, Object o) {
        try {
            DBVariable dbv = dbvc.newInstance();
            dbv.load(o);
            return dbv;
        } catch (InstantiationException | IllegalAccessException ex) {
            Logger.getLogger(EntityBuilder.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
}
