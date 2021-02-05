package com.spleefleague.core.command.commands;

import com.spleefleague.core.chat.Chat;
import com.spleefleague.core.command.CollectibleCommand;
import com.spleefleague.core.command.annotation.CommandAnnotation;
import com.spleefleague.core.command.annotation.HelperArg;
import com.spleefleague.core.command.annotation.LiteralArg;
import com.spleefleague.core.command.annotation.OptionArg;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.collectible.victory.VictoryMessage;
import com.spleefleague.core.player.rank.CoreRank;
import com.spleefleague.core.vendor.Vendorables;

/**
 * @author NickM13
 * @since 2/3/2021
 */
public class VictoryCommand extends CollectibleCommand {

    protected VictoryCommand() {
        super(VictoryMessage.class, "victory", CoreRank.DEVELOPER);
    }

    @CommandAnnotation
    public void victorySetMessage(CorePlayer sender,
                                  @LiteralArg("set") String l1,
                                  @LiteralArg("message") String l2,
                                  @OptionArg(listName = "collectibles") String collectible,
                                  @HelperArg("message") String message) {
        VictoryMessage victory = Vendorables.get(VictoryMessage.class, collectible);
        message = Chat.colorize(message);
        if (victory != null) {
            victory.setMessage(message);
            success(sender, "Set victory message to " + message);
        }
    }

}
