/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.database.variable;

/**
 * Custom loadable variable to work
 * with DBEntity's load/save function
 *
 * @author NickM13
 * @param <T>
 */
public interface DBVariable<T> {

    void load(T doc);
    T save();
    
}
