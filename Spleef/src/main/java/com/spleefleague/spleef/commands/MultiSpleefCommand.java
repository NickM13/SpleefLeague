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
public class MultiSpleefCommand extends CoreCommand {

    public MultiSpleefCommand() {
        super("multispleef", Rank.DEFAULT);
        this.addAlias("ms");
        this.setOptions("arenas", cp -> Arenas.getUnpaused(SpleefMode.MULTI.getBattleMode()).keySet());
    }

    @CommandAnnotation
    public void multiChallenge(CorePlayer sender,
                                 @LiteralArg("challenge") String l,
                                 @OptionArg(listName = "arenas") String arenaName,
                                 @CorePlayerArg(allowSelf = false) CorePlayer target) {
        if (!target.canJoinBattle()) {
            error(sender, "That player is in a battle!");
            return;
        }
        Arena arena = Arenas.get(arenaName);
        success(sender, "You have challenged " + target.getDisplayName() + " to a game of " + Chat.GAMEMODE + SpleefMode.MULTI.getBattleMode().getDisplayName() + Chat.DEFAULT + " on " + Chat.GAMEMAP + arena.getName());
        Chat.sendRequest(sender.getDisplayName() + " has challenged you to a game of " + Chat.GAMEMODE + SpleefMode.MULTI.getBattleMode().getDisplayName() + Chat.DEFAULT + " on " + Chat.GAMEMAP + arena.getName(), target, sender, (r, s) -> {
            Spleef.getInstance().getBattleManager(SpleefMode.MULTI.getBattleMode()).startMatch(Lists.newArrayList(r, s), arenaName);
        });
    }

    @CommandAnnotation
    public void multi(CorePlayer sender,
                        @Nullable @OptionArg(listName = "arenas") String arenaName) {
        Spleef.getInstance().queuePlayer(SpleefMode.MULTI.getBattleMode(), sender, Arenas.get(arenaName, SpleefMode.MULTI.getBattleMode()));
    }

}
