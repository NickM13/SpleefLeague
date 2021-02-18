/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.command.commands;

import com.spleefleague.core.Core;
import com.spleefleague.core.command.annotation.CommandAnnotation;
import com.spleefleague.core.command.CoreCommand;
import com.spleefleague.core.command.annotation.LiteralArg;
import com.spleefleague.core.command.error.CoreError;
import com.spleefleague.core.crate.CrateManager;
import com.spleefleague.core.game.arena.Arenas;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.collectible.Collectible;
import com.spleefleague.core.player.rank.CoreRank;
import com.spleefleague.core.plugin.CorePlugin;
import com.spleefleague.core.settings.Settings;
import com.spleefleague.core.world.build.BuildStructures;

/**
 * @author NickM13
 */
public class ReloadDataCommand extends CoreCommand {

    public ReloadDataCommand() {
        super("reloaddata", CoreRank.DEVELOPER);
    }

    @CommandAnnotation
    public void reloaddata(CorePlayer sender) {
        error(sender, CoreError.SETUP);
    }

    @CommandAnnotation
    public void reloaddataSettings(CorePlayer sender,
                                   @LiteralArg("settings") String l) {
        Settings.init();
        success(sender, "Reloaded settings from database");
    }

    @CommandAnnotation
    public void reloaddataCrates(CorePlayer sender,
                                 @LiteralArg("crates") String l) {
        Core.getInstance().getCrateManager().init();
        success(sender, "Reloaded crates from database");
    }

    @CommandAnnotation
    public void reloaddataArenas(CorePlayer sender,
                                 @LiteralArg("arenas") String l) {
        Arenas.init();
        success(sender, "Reloaded arenas from database");
    }

    @CommandAnnotation
    public void reloaddataStructures(CorePlayer sender,
                                 @LiteralArg("structures") String l) {
        BuildStructures.init();
        success(sender, "Reloaded structures from database");
    }

    @CommandAnnotation
    public void reloaddataCollectibles(CorePlayer sender,
                                       @LiteralArg("collectibles") String l) {
        Collectible.clear();
        for (CorePlugin plugin : CorePlugin.getAllPlugins()) {
            plugin.reloadCollectibles();
        }
        success(sender, "Reloaded collectibles from database");
    }

}
