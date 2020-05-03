package com.spleefleague.spleef.commands;

import com.spleefleague.core.command.CommandTemplate;
import com.spleefleague.core.command.annotation.LiteralArg;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.rank.Rank;

/**
 * @author NickM13
 * @since 4/29/2020
 */
public class SpleefArenaCommand extends CommandTemplate {
    
    public SpleefArenaCommand() {
        super(SpleefArenaCommand.class, "spleefarena", Rank.DEVELOPER);
        setContainer("spleef");
    }
    
    public void spleefarenaCreate(CorePlayer sender,
            @LiteralArg("create") String l,
            String name) {
        
    }
    
}
