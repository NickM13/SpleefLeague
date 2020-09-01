package com.spleefleague.core.menu;

import com.spleefleague.core.player.CorePlayer;

import java.util.function.Function;

/**
 * @author NickM13
 * @since 4/29/2020
 */
public abstract class InventoryMenuContainer {
    
    public InventoryMenuContainer() {
    
    }
    
    public abstract void open(CorePlayer cp);

}
