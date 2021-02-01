package com.spleefleague.core.command.commands;

import com.spleefleague.core.command.CollectibleCommand;
import com.spleefleague.core.player.collectible.hat.Hat;
import com.spleefleague.core.player.rank.CoreRank;

/**
 * @author NickM13
 * @since 4/20/2020
 */
public class HatCommand extends CollectibleCommand {
    
    public HatCommand() {
        super(Hat.class, "hat", CoreRank.DEVELOPER);
    }
    
}
