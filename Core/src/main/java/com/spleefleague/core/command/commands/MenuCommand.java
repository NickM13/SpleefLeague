/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.command.commands;

import com.spleefleague.core.command.CoreCommand;
import com.spleefleague.core.command.annotation.CommandAnnotation;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.rank.Rank;

/**
 * @author NickM13
 */
public class MenuCommand extends CoreCommand {
    
    public MenuCommand() {
        super("menu", Rank.DEFAULT);
    }
    
    @CommandAnnotation
    public void menu(CorePlayer sender) {
        sender.refreshHotbar();
    }
    
    @CommandAnnotation(minRank="MODERATOR")
    public void menu(CorePlayer sender, CorePlayer cp) {
        cp.refreshHotbar();
    }
    
}
