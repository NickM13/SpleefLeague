package com.spleefleague.spleef.commands;

import com.spleefleague.core.command.CoreCommand;
import com.spleefleague.core.command.annotation.CommandAnnotation;
import com.spleefleague.core.command.annotation.CorePlayerArg;
import com.spleefleague.core.command.annotation.LiteralArg;
import com.spleefleague.core.command.annotation.OptionArg;
import com.spleefleague.core.game.arena.Arenas;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.rank.CoreRank;
import com.spleefleague.spleef.Spleef;
import com.spleefleague.spleef.game.SpleefMode;

import javax.annotation.Nullable;

/**
 * @author NickM13
 * @since 5/14/2020
 */
public class PowerSpleefCommand extends CoreCommand {

    public PowerSpleefCommand() {
        super("powerspleef", CoreRank.DEFAULT);
        this.addAlias("ps");
        this.setOptions("arenas", cp -> Arenas.getUnpaused(SpleefMode.POWER.getBattleMode()).keySet());
    }

    @CommandAnnotation
    public void powerChallenge(CorePlayer sender,
                                 @LiteralArg("challenge") String l,
                                 @OptionArg(listName = "arenas") String arenaName,
                                 @CorePlayerArg(allowSelf = false, allowCrossServer = true) CorePlayer target) {
        Spleef.getInstance().challengePlayer(sender, target, SpleefMode.POWER.getBattleMode(), arenaName);
    }

    @CommandAnnotation
    public void power(CorePlayer sender,
                        @Nullable @OptionArg(listName = "arenas") String arenaName) {
        Spleef.getInstance().queuePlayer(SpleefMode.POWER.getBattleMode(), sender, Arenas.get(arenaName, SpleefMode.POWER.getBattleMode()));
    }

}
