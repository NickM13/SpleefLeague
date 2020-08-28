/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.superjump.game.shuffle;

import com.spleefleague.core.game.Arena;
import com.spleefleague.core.game.battle.versus.VersusBattle;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.superjump.SuperJump;
import com.spleefleague.superjump.game.SJMode;

import java.util.List;
import java.util.UUID;

/**
 * @author NickM13
 */
public class ShuffleSJBattle extends VersusBattle<ShuffleSJPlayer> {

    public ShuffleSJBattle(List<UUID> players, Arena arena) {
        super(SuperJump.getInstance(), players, arena, ShuffleSJPlayer.class, SJMode.SHUFFLE.getBattleMode());
    }
    
    @Override
    public void updateExperience() {
        //chatGroup.setExperience((getLevelTime() % 1000) / 1000.f, (int)(getLevelTime() / 1000));
    }
    
    @Override
    protected void setupBaseSettings() {
    
    }
    
    @Override
    protected void fillField() {
        //generate2(getSpawn(0), arena.getJumpCount(), arena.getDifficulty(), true);
    }
    
    @Override
    public void reset() {
    
    }
    
    /*
    @Override
    protected void winPlayer(CorePlayer cp) {
        gameWorld.clear();
        fillField();
        doCountdown();
        String completeMessage;
        
        float levelTime = Math.floorDiv(getLevelTime(), 10L) / 100.f;
        if(levelTime < 30)      completeMessage = "" + ChatColor.GREEN;
        else if(levelTime < 60) completeMessage = "" + ChatColor.YELLOW;
        else                    completeMessage = "" + ChatColor.RED;
        completeMessage += "" + String.format("%.2f", levelTime) + " Seconds";
        chatGroup.sendTitle(ChatColor.GREEN + "Completed In", completeMessage, 5, 20, 5);
        
        timeLastLap = System.currentTimeMillis();
        
        Core.getInstance().sendMessage("Shuffle match complete: " + cp.getDisplayName() + " won! grats I think");
        
        endBattle();
    }
     */

}
