/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.chat;

import com.spleefleague.core.util.database.DBPlayer;

/**
 * @author NickM13
 * @param <T>
 */
public class Request<T> {
    
    private DBPlayer requester;
    private boolean accepted;
    private T requestedValue;
    
    public Request() {
        requester = null;
        accepted = false;
    }
    
    public void setRequest(DBPlayer requester) {
        this.requester = requester;
        
        accepted = false;
    }
    
    public void setRequest(DBPlayer requester, T requestedValue) {
        this.requester = requester;
        this.requestedValue = requestedValue;
        
        accepted = false;
    }
    
    public DBPlayer getRequester() {
        return requester;
    }
    
    public boolean isAccepted() {
        return !accepted;
    }
    
    public void respond(DBPlayer dbp, boolean accepted) {
        if (!dbp.equals(requester)) {
            this.accepted = accepted;
        }
    }
    
    public T getRequestedValue() {
        return requestedValue;
    }
    
}
