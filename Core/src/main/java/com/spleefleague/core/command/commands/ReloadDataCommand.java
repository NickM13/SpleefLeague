/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.command.commands;

import com.spleefleague.core.command.annotation.CommandAnnotation;
import com.spleefleague.core.command.CoreCommand;
import com.spleefleague.core.command.annotation.LiteralArg;
import com.spleefleague.core.command.error.CoreError;
import com.spleefleague.core.game.arena.Arenas;
import com.spleefleague.core.menu.hotbars.main.CollectiblesMenu;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.collectible.Collectible;
import com.spleefleague.core.player.rank.Rank;
import com.spleefleague.core.settings.Settings;

/**
 * @author NickM13
 */
public class ReloadDataCommand extends CoreCommand {
    
    public ReloadDataCommand() {
        super("reloaddata", Rank.DEVELOPER);
    }
    
    @CommandAnnotation
    public void reloaddata(CorePlayer sender) {
        error(sender, CoreError.SETUP);
    }

    @CommandAnnotation
    public void reloaddataSettings(CorePlayer sender,
                                   @LiteralArg("settings") String l) {
        Settings.reload();
        success(sender, "Reloaded settings from database");
    }

    @CommandAnnotation
    public void reloaddataArenas(CorePlayer sender,
                                 @LiteralArg("arenas") String l) {
        Arenas.init();
        success(sender, "Reloaded arenas from database");
    }

    @CommandAnnotation
    public void reloaddataCollectibles(CorePlayer sender,
                                 @LiteralArg("collectibles") String l) {
        Collectible.init();
        success(sender, "Reloaded collectibles from database");
    }

}
