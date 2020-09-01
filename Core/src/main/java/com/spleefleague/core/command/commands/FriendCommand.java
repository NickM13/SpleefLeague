package com.spleefleague.core.command.commands;

import com.spleefleague.core.command.CoreCommand;
import com.spleefleague.core.command.annotation.CommandAnnotation;
import com.spleefleague.core.command.annotation.CorePlayerArg;
import com.spleefleague.core.command.annotation.LiteralArg;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.rank.Rank;

public class FriendCommand extends CoreCommand {

    protected FriendCommand() {
        super("friend", Rank.DEFAULT);
    }

    @CommandAnnotation
    public void friendAdd(CorePlayer sender,
                          @LiteralArg("add") String l,
                          @CorePlayerArg(allowSelf = false) CorePlayer target) {

    }

}
