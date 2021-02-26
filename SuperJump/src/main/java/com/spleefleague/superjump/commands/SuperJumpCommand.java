/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.superjump.commands;

import com.google.common.collect.Sets;
import com.spleefleague.core.command.CoreCommand;
import com.spleefleague.core.command.annotation.*;
import com.spleefleague.core.game.Arena;
import com.spleefleague.core.game.arena.Arenas;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.rank.CoreRank;
import com.spleefleague.superjump.SuperJump;
import com.spleefleague.superjump.game.SJMode;
import com.spleefleague.superjump.game.conquest.ConquestPack;
import javax.annotation.Nullable;
import java.util.Set;

/**
 * @author NickM13
 */
public class SuperJumpCommand extends CoreCommand {
    
    public SuperJumpCommand() {
        super("superjump", CoreRank.DEFAULT);
        addAlias("sj");
        setUsage("/sj <mode>");
        this.setOptions("gamemodes", pi -> enabledModes);
        setOptions("conquestPacks", pi -> ConquestPack.getPackNames());
        setOptions("shuffleArenas", pi -> Arenas.getAll(SJMode.SHUFFLE.getBattleMode()).keySet());
        setOptions("proArenas", pi -> Arenas.getAll(SJMode.PRO.getBattleMode()).keySet());
    }

    private static final Set<String> enabledModes = Sets.newHashSet(SJMode.CLASSIC.name().toLowerCase());
    
    @CommandAnnotation
    public void sj(CorePlayer sender) {
        sender.getMenu().setInventoryMenuItem(SuperJump.getInstance().getSJMenuItem());
    }

    @CommandAnnotation
    public void sjClassic(CorePlayer sender, @LiteralArg(value="classic") String l, @Nullable @OptionArg(listName="shuffleArenas") String arenaName) {
        Arena arena = Arenas.get(arenaName, SJMode.CLASSIC.getBattleMode());
        SuperJump.getInstance().queuePlayer(SJMode.CLASSIC.getBattleMode(), sender, arena);
    }

    @CommandAnnotation
    public void sjChallenge(CorePlayer sender,
                            @LiteralArg("challenge") String l,
                            @LiteralArg("classic") String mode,
                            @OptionArg(listName = "arenas") String arenaName,
                            @CorePlayerArg(allowCrossServer = true, allowSelf = false) CorePlayer target) {
        SuperJump.getInstance().challengePlayer(sender, target, SJMode.CLASSIC.getBattleMode(), arenaName);
    }

    @CommandAnnotation(disabled = true)
    public void sjShuffle(CorePlayer sender, @LiteralArg(value="shuffle") String l, @Nullable @OptionArg(listName="shuffleArenas") String arenaName) {
        Arena arena = Arenas.get(arenaName, SJMode.SHUFFLE.getBattleMode());
        SuperJump.getInstance().queuePlayer(SJMode.SHUFFLE.getBattleMode(), sender, arena);
    }

    @CommandAnnotation(disabled = true)
    public void sjPro(CorePlayer sender, @LiteralArg(value="pro") String l, @Nullable @OptionArg(listName="proArenas") String arenaName) {
        Arena arena = Arenas.get(arenaName, SJMode.PRO.getBattleMode());
        SuperJump.getInstance().queuePlayer(SJMode.PRO.getBattleMode(), sender, arena);
    }

    @CommandAnnotation(disabled = true)
    public void sjConquest(CorePlayer sender, @LiteralArg(value="conquest") String l, @OptionArg(listName="conquestPacks") String packName) {
        ConquestPack pack = ConquestPack.getPack(packName);
        if (pack != null) {
            sender.getMenu().setInventoryMenuItem(pack.createMenu());
        }
    }

    @CommandAnnotation(disabled = true)
    public void sjEndless(CorePlayer sender, @LiteralArg(value="endless") String l) {
        SuperJump.getInstance().queuePlayer(SJMode.ENDLESS.getBattleMode(), sender);
    }

    @CommandAnnotation(disabled = true, minRank = "DEVELOPER")
    public void sjEndless(CorePlayer sender, @LiteralArg(value="endless") String l, @HelperArg("[level]") Integer level) {
        sender.getStatistics().setHigher("superjump", "endless:level", level);
        SuperJump.getInstance().queuePlayer(SJMode.ENDLESS.getBattleMode(), sender);
    }
    
}
