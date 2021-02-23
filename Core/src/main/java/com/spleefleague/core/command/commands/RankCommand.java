package com.spleefleague.core.command.commands;

import com.spleefleague.core.Core;
import com.spleefleague.core.chat.Chat;
import com.spleefleague.core.command.CoreCommand;
import com.spleefleague.core.command.annotation.*;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.rank.CoreRank;
import com.spleefleague.coreapi.chat.ChatColor;

import javax.annotation.Nullable;

/**
 * @author NickM13
 */
public class RankCommand extends CoreCommand {

    public RankCommand() {
        super("rank", CoreRank.DEVELOPER);
        setOptions("ranks", pi -> Core.getInstance().getRankManager().getRankNames());
    }

    @CommandAnnotation
    public void rankCreate(CorePlayer sender,
                           @LiteralArg("create") String l,
                           @HelperArg("identifier") String identifier,
                           @HelperArg("ladder") @NumberArg(minValue = -1000, maxValue = 1000) Integer ladder,
                           @EnumArg ChatColor chatColor) {
        if (Core.getInstance().getRankManager().createRank(identifier, ladder, chatColor)) {
            success(sender, "Created rank " + identifier);
        } else {
            error(sender, "Rank already exists");
        }
    }

    @CommandAnnotation
    public void rankEditName(CorePlayer sender,
                             @LiteralArg("edit") String l1,
                             @OptionArg(listName = "ranks") String rank,
                             @LiteralArg("name") String l2,
                             @Nullable String displayName) {
        if (displayName == null) displayName = "";
        String colorized = Chat.colorize(displayName);
        Core.getInstance().getRankManager().setRankName(rank, colorized);
        success(sender, "Rank name set to " + colorized);
    }

    @CommandAnnotation
    public void rankEditLadder(CorePlayer sender,
                               @LiteralArg("edit") String l1,
                               @OptionArg(listName = "ranks") String rank,
                               @LiteralArg("ladder") String l2,
                               @HelperArg("ladder") @NumberArg(minValue = -10000, maxValue = 10000) Integer ladder) {
        Core.getInstance().getRankManager().setRankLadder(rank, ladder);
        success(sender, "Rank ladder set to " + ladder);
    }

    @CommandAnnotation
    public void rankEditMaxFriends(CorePlayer sender,
                                   @LiteralArg("edit") String l1,
                                   @OptionArg(listName = "ranks") String rank,
                                   @LiteralArg("maxfriends") String l2,
                                   @HelperArg("maxfriends") @NumberArg(minValue = -1, maxValue = 10000) Integer maxFriends) {
        Core.getInstance().getRankManager().setRankMaxFriends(rank, maxFriends);
        success(sender, "Rank maxfriends set to " + maxFriends);
    }

    @CommandAnnotation
    public void rankEditColor(CorePlayer sender,
                              @LiteralArg("edit") String l1,
                              @OptionArg(listName = "ranks") String rank,
                              @LiteralArg("color") String l2,
                              @EnumArg ChatColor color) {
        Core.getInstance().getRankManager().setRankColor(rank, color);
        success(sender, "Rank color set to " + color);
    }

    @CommandAnnotation
    public void rankEditHasOp(CorePlayer sender,
                              @LiteralArg("edit") String l1,
                              @OptionArg(listName = "ranks") String rank,
                              @LiteralArg("hasOp") String l2,
                              Boolean hasOp) {
        Core.getInstance().getRankManager().setRankOp(rank, hasOp);
        success(sender, "Rank hasOp set to " + hasOp);
    }

    @CommandAnnotation
    public void rankEditCoinMultiply(CorePlayer sender,
                                     @LiteralArg("edit") String l1,
                                     @OptionArg(listName = "ranks") String rank,
                                     @LiteralArg("coinMultiply") String l2,
                                     @NumberArg(minValue = 0) Double multiplier) {
        Core.getInstance().getRankManager().setCoinMultiplier(rank, multiplier);
        success(sender, "Rank coin multiplier set to " + multiplier);
    }

    @CommandAnnotation
    public void rankEditOreMultiply(CorePlayer sender,
                                     @LiteralArg("edit") String l1,
                                     @OptionArg(listName = "ranks") String rank,
                                     @LiteralArg("oreMultiply") String l2,
                                     @NumberArg(minValue = 0) Double multiplier) {
        Core.getInstance().getRankManager().setOreMultiplier(rank, multiplier);
        success(sender, "Rank ore multiplier set to " + multiplier);
    }

    @CommandAnnotation
    public void rankInfo(CorePlayer sender,
                         @LiteralArg("info") String l1,
                         @OptionArg(listName = "ranks") String rankName) {
        CoreRank rank = Core.getInstance().getRankManager().getRank(rankName);
        String formatted = ChatColor.GRAY + "{ identifier: " + rank.getIdentifier() + ", " +
                "name: " + rank.getDisplayName() + ChatColor.GRAY + ", " +
                "ladder: " + rank.getLadder() + ", " +
                "color: " + rank.getColor().name() + ", " +
                "maxFriends: " + rank.getMaxFriends() + ", " +
                "coinMultiplier: " + rank.getCoinMultiplier() + ", " +
                "oreMultiplier: " + rank.getOreMultiplier() + ", " +
                "hasOp: " + rank.getHasOp() + " }";
        success(sender, formatted);
    }

}
