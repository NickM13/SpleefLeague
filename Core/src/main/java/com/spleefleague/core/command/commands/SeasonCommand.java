package com.spleefleague.core.command.commands;

import com.google.common.collect.Sets;
import com.spleefleague.core.Core;
import com.spleefleague.core.chat.Chat;
import com.spleefleague.core.command.CoreCommand;
import com.spleefleague.core.command.annotation.CommandAnnotation;
import com.spleefleague.core.command.annotation.LiteralArg;
import com.spleefleague.core.command.error.CoreError;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.rank.CoreRank;

import java.util.Random;
import java.util.Set;

/**
 * @author NickM13
 * @since 4/29/2020
 */
public class SeasonCommand extends CoreCommand {

    public SeasonCommand() {
        super("season", CoreRank.DEVELOPER);
        setUsage("See Developer!");
        setDescription("Seasons Command for Resetting");
    }

    @CommandAnnotation
    public void season(CorePlayer sender) {
        sender.sendMessage("/season reset: Archives all leaderboards and begins a new ranked season");
    }

    private static Set<String> testPlayers = Sets.newHashSet("PxlPanda", "CommunityMC", "SynHD", "worldcom", "Sylent", "JakeDaaBud", "mike601",
            "NESQUEK", "tjommie", "kaskada99", "Withur", "Herocky", "TorWolf", "Taytale", "AmyTheMudkip", "arstan", "Biscut",
            "flameboy101", "BernieSander", "ChiLynn", "MCVisuals", "Rewind", "Spotifi", "Quack", "PJoke1", "tom396",
            "Hoopless", "minifreddusch", "gamerboy80", "AmEeEr0", "Ketthe", "Hashito", "NoSDaemon", "kongkid05", "MonsterGG",
            "leLitzpaNDA", "Toyless");

    @CommandAnnotation
    public void seasonDebug(CorePlayer sender,
                            @LiteralArg("debug") String l) {
        Random rand = new Random();
        /*
        for (OfflinePlayer op : Bukkit.getOfflinePlayers()) {
            Leaderboards.debug(op, rand.nextInt(3000));
        }
        */
        for (String name : testPlayers) {
            CorePlayer cp = Core.getInstance().getPlayers().createFakePlayer(name);
            //Leaderboards.debug(cp, rand.nextInt(3000));
        }
    }

    @CommandAnnotation
    public void seasonReset(CorePlayer sender,
                            @LiteralArg("reset") String l) {
        Chat.sendRequest(sender,
                "SeasonReset",
                (cp, s) -> {
                    Chat.sendRequest(sender,
                            "SeasonReset2",
                            (cp2, s2) -> {
                                //Leaderboards.startNewSeason();
                                success(sender, "Seasons have been reset!");
                                error(sender, CoreError.SETUP);
                            }, "Are you REALLY sure you want to do this?");
                }, "Are you sure you want to start a new season?");
    }

}
