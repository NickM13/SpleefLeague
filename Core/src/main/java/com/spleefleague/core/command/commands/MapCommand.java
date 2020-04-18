/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.command.commands;

import com.spleefleague.core.command.annotation.CommandAnnotation;
import com.spleefleague.core.command.CommandTemplate;
import com.spleefleague.core.command.error.CoreError;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.Rank;

/**
 * @author NickM13
 * 
 * Create a map from a URL image used for wall art
 */
public class MapCommand extends CommandTemplate {
    
    public MapCommand() {
        super(MapCommand.class, "map", Rank.DEVELOPER);
        setUsage("/map <create|destroy> <URL ID>");
        setDescription("Gives the player an animated map");
    }
    
    @CommandAnnotation
    public void map(CorePlayer sender, String cd, String urlId) {
        error(sender, CoreError.SETUP);
    }

}
