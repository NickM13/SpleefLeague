package com.spleefleague.spleef.commands;

import com.spleefleague.core.command.CoreCommand;
import com.spleefleague.core.command.annotation.CommandAnnotation;
import com.spleefleague.core.command.annotation.LiteralArg;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.rank.CoreRank;
import com.spleefleague.spleef.game.battle.classic.affix.ClassicSpleefAffixes;

/**
 * @author NickM13
 * @since 5/15/2020
 */
public class AffixCommand extends CoreCommand {

    public AffixCommand() {
        super("affix", CoreRank.DEVELOPER);
    }

    @CommandAnnotation
    public void affixReload(CorePlayer sender,
                               @LiteralArg("reload") String l1) {
        ClassicSpleefAffixes.refresh();
    }

}
