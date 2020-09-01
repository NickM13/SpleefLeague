/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.coreapi.database.variable;

/**
 * Custom loadable variable to work
 * with DBEntity's load/save function
 *
 * @author NickM13
 * @param <T> Database Type
 */
public abstract class DBVariable<T> {

    public DBVariable() {}
    
    public DBVariable(T t) {
        load(t);
    }
    
    public abstract void load(T doc);
    public abstract T save();
    
}
