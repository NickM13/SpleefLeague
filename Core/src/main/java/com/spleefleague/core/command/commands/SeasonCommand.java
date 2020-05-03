package com.spleefleague.core.command.commands;

import com.google.common.collect.Sets;
import com.spleefleague.core.Core;
import com.spleefleague.core.chat.Chat;
import com.spleefleague.core.command.CommandTemplate;
import com.spleefleague.core.command.annotation.CommandAnnotation;
import com.spleefleague.core.command.annotation.LiteralArg;
import com.spleefleague.core.game.leaderboard.Leaderboards;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.rank.Rank;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.Random;
import java.util.Set;

/**
 * @author NickM13
 * @since 4/29/2020
 */
public class SeasonCommand extends CommandTemplate {
    
    public SeasonCommand() {
        super(SeasonCommand.class, "season", Rank.DEVELOPER);
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
            OfflinePlayer op = Bukkit.getOfflinePlayer(name);
            CorePlayer cp = (CorePlayer) Core.getInstance().getPlayers().createFakePlayer(op);
            Leaderboards.debug(cp, rand.nextInt(3000));
        }
    }
    
    @CommandAnnotation
    public void seasonReset(CorePlayer sender,
            @LiteralArg("reset") String l) {
        Chat.sendRequest("Are you sure you want to start a new season?",
                sender,
                "SeasonReset",
                (cp, s) -> {
                    Chat.sendRequest("Are you REALLY sure you want to do this?",
                            sender,
                            "SeasonReset2",
                            (cp2, s2) -> {
                                Leaderboards.startNewSeason();
                                success(sender, "Seasons have been reset!");
                            });
                });
    }
    
}
