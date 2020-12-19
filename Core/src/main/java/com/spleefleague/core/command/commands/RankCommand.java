package com.spleefleague.core.command.commands;

import com.spleefleague.core.command.CoreCommand;
import com.spleefleague.core.command.annotation.*;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.rank.Rank;
import com.spleefleague.core.player.rank.Ranks;
import com.spleefleague.coreapi.chat.ChatColor;

import javax.annotation.Nullable;

/**
 * @author NickM13
 */
public class RankCommand extends CoreCommand {

    public RankCommand() {
        super("rank", Rank.DEVELOPER);
        setOptions("ranks", pi -> Ranks.getRankNames());
    }

    @CommandAnnotation
    public void rankCreate(CorePlayer sender,
                           @LiteralArg("create") String l,
                           @HelperArg("<identifier>") String identifier,
                           @HelperArg("<ladder>") @NumberArg(minValue = -1000, maxValue = 1000) Integer ladder,
                           @EnumArg ChatColor chatColor) {
        if (Ranks.createRank(identifier, ladder, org.bukkit.ChatColor.valueOf(chatColor.name()))) {
            success(sender, "Rank " + identifier + " created.");
        } else {
            error(sender, "Rank already exists!");
        }
    }

    @CommandAnnotation
    public void rankEditName(CorePlayer sender,
                             @LiteralArg("edit") String l1,
                             @OptionArg(listName = "ranks") String rank,
                             @LiteralArg("name") String l2,
                             @Nullable String displayName) {
        if (displayName == null) displayName = "";
        if (Ranks.setRankName(rank, displayName)) {
            success(sender, "Rank display name changed");
        }
    }

    @CommandAnnotation
    public void rankEditLadder(CorePlayer sender,
                               @LiteralArg("edit") String l1,
                               @OptionArg(listName = "ranks") String rank,
                               @LiteralArg("ladder") String l2,
                               @HelperArg("<ladder>") @NumberArg(minValue = -1000, maxValue = 1000) Integer ladder) {
        if (Ranks.setRankLadder(rank, ladder)) {
            success(sender, "Rank ladder value changed");
        }
    }

    @CommandAnnotation
    public void rankEditColor(CorePlayer sender,
                              @LiteralArg("edit") String l1,
                              @OptionArg(listName = "ranks") String rank,
                              @LiteralArg("color") String l2,
                              @EnumArg ChatColor color) {
        if (Ranks.setRankColor(rank, org.bukkit.ChatColor.valueOf(color.name()))) {
            success(sender, "Rank color changed");
        }
    }

    @CommandAnnotation
    public void rankEditHasOp(CorePlayer sender,
                              @LiteralArg("edit") String l1,
                              @OptionArg(listName = "ranks") String rank,
                              @LiteralArg("hasOp") String l2,
                              Boolean hasOp) {
        if (Ranks.setRankOp(rank, hasOp)) {
            success(sender, "Rank color changed");
        }
    }

}