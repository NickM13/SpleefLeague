/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.superjump.commands;

import com.spleefleague.core.command.CommandTemplate;
import com.spleefleague.core.command.annotation.CommandAnnotation;
import com.spleefleague.core.command.annotation.LiteralArg;
import com.spleefleague.core.command.annotation.OptionArg;
import com.spleefleague.core.game.Arena;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.rank.Rank;
import com.spleefleague.superjump.SuperJump;
import com.spleefleague.superjump.game.SJMode;
import com.spleefleague.superjump.game.conquest.ConquestPack;
import com.spleefleague.superjump.player.SuperJumpPlayer;
import javax.annotation.Nullable;

/**
 * @author NickM13
 */
public class SuperJumpCommand extends CommandTemplate {
    
    public SuperJumpCommand() {
        super(SuperJumpCommand.class, "superjump", Rank.DEFAULT);
        addAlias("sj");
        setUsage("/sj <mode>");
        setOptions("conquestPacks", (cp) -> ConquestPack.getPackNames());
        setOptions("shuffleArenas", (cp) -> Arena.getArenaNames(SJMode.SHUFFLE.getArenaMode()));
        setOptions("proArenas", (cp) -> Arena.getArenaNames(SJMode.PRO.getArenaMode()));
    }
    
    @CommandAnnotation
    public void sj(CorePlayer sender) {
        sender.setInventoryMenuItem(SuperJump.getInstance().getSJMenuItem());
    }
    
    @CommandAnnotation
    public void sjClassic(CorePlayer sender, @LiteralArg(value="classic") String l, @Nullable @OptionArg(listName="shuffleArenas") String arenaName) {
        Arena arena = Arena.getByName(arenaName, SJMode.CLASSIC.getArenaMode());
        SuperJump.getInstance().queuePlayer(SJMode.CLASSIC.getArenaMode(), sender, arena);
    }
    
    @CommandAnnotation
    public void sjShuffle(CorePlayer sender, @LiteralArg(value="shuffle") String l, @Nullable @OptionArg(listName="shuffleArenas") String arenaName) {
        Arena arena = Arena.getByName(arenaName, SJMode.SHUFFLE.getArenaMode());
        SuperJump.getInstance().queuePlayer(SJMode.SHUFFLE.getArenaMode(), sender, arena);
    }
    
    @CommandAnnotation
    public void sjPro(CorePlayer sender, @LiteralArg(value="pro") String l, @Nullable @OptionArg(listName="proArenas") String arenaName) {
        Arena arena = Arena.getByName(arenaName, SJMode.PRO.getArenaMode());
        SuperJump.getInstance().queuePlayer(SJMode.PRO.getArenaMode(), sender, arena);
    }
    
    @CommandAnnotation
    public void sjConquest(CorePlayer sender, @LiteralArg(value="conquest") String l, @OptionArg(listName="conquestPacks") String packName) {
        ConquestPack pack = ConquestPack.getPack(packName);
        if (pack != null) {
            sender.setInventoryMenuItem(pack.createMenu());
        }
    }
    
    @CommandAnnotation
    public void sjEndless(CorePlayer sender, @LiteralArg(value="endless") String l, @Nullable Integer level) {
        if (level != null) {
            SuperJumpPlayer sjp = SuperJump.getInstance().getPlayers().get(sender);
            sjp.getEndlessStats().setLevel(level);
        }
        SuperJump.getInstance().queuePlayer(SJMode.ENDLESS.getArenaMode(), sender);
    }
    
}
