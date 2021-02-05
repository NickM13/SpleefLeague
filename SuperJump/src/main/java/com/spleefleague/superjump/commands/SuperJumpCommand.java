/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.superjump.commands;

import com.spleefleague.core.command.CoreCommand;
import com.spleefleague.core.command.annotation.CommandAnnotation;
import com.spleefleague.core.command.annotation.HelperArg;
import com.spleefleague.core.command.annotation.LiteralArg;
import com.spleefleague.core.command.annotation.OptionArg;
import com.spleefleague.core.game.Arena;
import com.spleefleague.core.game.arena.Arenas;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.rank.CoreRank;
import com.spleefleague.superjump.SuperJump;
import com.spleefleague.superjump.game.SJMode;
import com.spleefleague.superjump.game.conquest.ConquestPack;
import javax.annotation.Nullable;

/**
 * @author NickM13
 */
public class SuperJumpCommand extends CoreCommand {
    
    public SuperJumpCommand() {
        super("superjump", CoreRank.DEFAULT);
        addAlias("sj");
        setUsage("/sj <mode>");
        setOptions("conquestPacks", (cp) -> ConquestPack.getPackNames());
        setOptions("shuffleArenas", (cp) -> Arenas.getAll(SJMode.SHUFFLE.getBattleMode()).keySet());
        setOptions("proArenas", (cp) -> Arenas.getAll(SJMode.PRO.getBattleMode()).keySet());
    }
    
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
    public void sjShuffle(CorePlayer sender, @LiteralArg(value="shuffle") String l, @Nullable @OptionArg(listName="shuffleArenas") String arenaName) {
        Arena arena = Arenas.get(arenaName, SJMode.SHUFFLE.getBattleMode());
        SuperJump.getInstance().queuePlayer(SJMode.SHUFFLE.getBattleMode(), sender, arena);
    }
    
    @CommandAnnotation
    public void sjPro(CorePlayer sender, @LiteralArg(value="pro") String l, @Nullable @OptionArg(listName="proArenas") String arenaName) {
        Arena arena = Arenas.get(arenaName, SJMode.PRO.getBattleMode());
        SuperJump.getInstance().queuePlayer(SJMode.PRO.getBattleMode(), sender, arena);
    }
    
    @CommandAnnotation
    public void sjConquest(CorePlayer sender, @LiteralArg(value="conquest") String l, @OptionArg(listName="conquestPacks") String packName) {
        ConquestPack pack = ConquestPack.getPack(packName);
        if (pack != null) {
            sender.getMenu().setInventoryMenuItem(pack.createMenu());
        }
    }

    @CommandAnnotation
    public void sjEndless(CorePlayer sender, @LiteralArg(value="endless") String l) {
        SuperJump.getInstance().queuePlayer(SJMode.ENDLESS.getBattleMode(), sender);
    }

    @CommandAnnotation(minRank = "DEVELOPER")
    public void sjEndless(CorePlayer sender, @LiteralArg(value="endless") String l, @HelperArg("[level]") Integer level) {
        sender.getStatistics().get("superjump").set("endless:level", level);
        SuperJump.getInstance().queuePlayer(SJMode.ENDLESS.getBattleMode(), sender);
    }
    
}
