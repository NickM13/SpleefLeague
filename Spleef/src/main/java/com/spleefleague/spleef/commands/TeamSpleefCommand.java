package com.spleefleague.spleef.commands;

import com.google.common.collect.Lists;
import com.spleefleague.core.chat.Chat;
import com.spleefleague.core.command.CoreCommand;
import com.spleefleague.core.command.annotation.CommandAnnotation;
import com.spleefleague.core.command.annotation.CorePlayerArg;
import com.spleefleague.core.command.annotation.LiteralArg;
import com.spleefleague.core.command.annotation.OptionArg;
import com.spleefleague.core.game.Arena;
import com.spleefleague.core.game.arena.Arenas;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.rank.Rank;
import com.spleefleague.spleef.Spleef;
import com.spleefleague.spleef.game.SpleefMode;

import javax.annotation.Nullable;

/**
 * @author NickM13
 * @since 5/14/2020
 */
public class TeamSpleefCommand extends CoreCommand {

    public TeamSpleefCommand() {
        super("teamspleef", Rank.DEFAULT);
        this.addAlias("ts");
        this.setOptions("arenas", cp -> Arenas.getUnpaused(SpleefMode.TEAM.getBattleMode()).keySet());
    }

    @CommandAnnotation
    public void teamChallenge(CorePlayer sender,
                                 @LiteralArg("challenge") String l,
                                 @OptionArg(listName = "arenas") String arenaName,
                                 @CorePlayerArg(allowSelf = false, allowCrossServer = true) CorePlayer target) {
        Spleef.getInstance().challengePlayer(sender, target, SpleefMode.TEAM.getBattleMode(), arenaName);
    }

    @CommandAnnotation
    public void team(CorePlayer sender,
                        @Nullable @OptionArg(listName = "arenas") String arenaName) {
        Spleef.getInstance().queuePlayer(SpleefMode.TEAM.getBattleMode(), sender, Arenas.get(arenaName, SpleefMode.TEAM.getBattleMode()));
    }

}
