package com.spleefleague.spleef.game.battle.classic;

/**
 * @author NickM13
 * @since 5/1/2020
 */
public abstract class ClassicSpleefAffix {

    protected ClassicSpleefBattle battle;
    
    public ClassicSpleefAffix(ClassicSpleefBattle battle) {
        this.battle = battle;
    }
    
    public abstract void start();
    
    public abstract void update();

}
