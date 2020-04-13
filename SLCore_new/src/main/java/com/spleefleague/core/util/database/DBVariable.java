/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.util.database;

/**
 * @author NickM13
 * @param <SELF>
 * @param <T>
 */
public interface DBVariable<T> {

    public void load(T doc);
    public T save();
    
}
