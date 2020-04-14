/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.error;

/**
 * @author NickM13
 */
public enum CoreError {
    UNABLE("You can't do that!"),
    SETUP("Not set up yet!"),
    PLAYER("Player doesn't exist!"),
    RANK("Rank doesn't exist!"),
    WORLD("World doesn't exist!"),
    OTHER_NOT_INGAME("They aren't in a game!"),
    INGAME("You can't do that while ingame!"),
    NOT_INGAME("You aren't in a game!"),
    PARTY_OWNER("You aren't the party owner!"),
    PARTY_NONE("You aren't in a party!");
    
    String message;
    
    CoreError(String message) {
        this.message = message;
    }
    
    public String getMessage() {
        return message;
    }
}
