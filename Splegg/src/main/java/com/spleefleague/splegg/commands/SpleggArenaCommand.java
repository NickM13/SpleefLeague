package com.spleefleague.splegg.commands;

import com.spleefleague.core.command.CommandTemplate;
import com.spleefleague.core.command.annotation.CommandAnnotation;
import com.spleefleague.core.command.annotation.HelperArg;
import com.spleefleague.core.command.annotation.LiteralArg;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.rank.Rank;

/**
 * @author NickM13
 * @since 4/29/2020
 */
public class SpleggArenaCommand extends CommandTemplate {
    
    public SpleggArenaCommand() {
        super(SpleggArenaCommand.class, "spleggarena", Rank.DEVELOPER);
    }
    
    @CommandAnnotation
    public void SpleggArenaCreate(CorePlayer sender,
            @LiteralArg("create") String l,
            @HelperArg("<arenaName>") String name) {
        
    }
    
}
