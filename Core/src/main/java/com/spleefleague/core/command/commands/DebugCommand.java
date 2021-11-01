/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.command.commands;

import com.spleefleague.core.Core;
import com.spleefleague.core.chat.Chat;
import com.spleefleague.core.command.annotation.*;
import com.spleefleague.core.command.CoreCommand;
import com.spleefleague.core.menu.InventoryMenuUtils;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.rank.CoreRank;

import com.spleefleague.core.util.CoreUtils;
import com.spleefleague.core.world.FakeBlock;
import com.spleefleague.core.world.FakeWorld;
import com.spleefleague.core.world.global.GlobalWorld;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.inventory.Inventory;

import javax.annotation.Nullable;
import java.util.Random;

/**
 * @author NickM13
 */
public class DebugCommand extends CoreCommand {

    public DebugCommand() {
        super("sldebug", CoreRank.DEVELOPER);
        setUsage("/sldebug " + ChatColor.MAGIC + "[hope u no read]");
        setDescription("debu" + ChatColor.MAGIC + "g more read?");
        setOptions("sounds", pi -> CoreUtils.enumToStrSet(Sound.class, true));
    }

    @CommandAnnotation
    public void debugRank(CorePlayer sender,
                          @LiteralArg("blocktypes") String l,
                          @EnumArg Material material) {
        success(sender, "Material: " + material);
        success(sender, "Block: " + material.isBlock());
        success(sender, "Solid: " + material.isSolid());
        success(sender, "Air: " + material.isAir());
        success(sender, "Interactable: " + material.isInteractable());
        success(sender, "Occluding: " + material.isOccluding());
    }

    @CommandAnnotation
    public void debugRank(CorePlayer sender,
                          @LiteralArg("rank") String l,
                          String mode,
                          String season,
                          Integer elo) {
        sender.getRatings().addRating(mode, season, elo);
    }

    @CommandAnnotation
    public void debugBlockStep(CorePlayer sender,
                           @LiteralArg("block") String l1,
                           @LiteralArg("step") String l2,
                           @EnumArg Material material) {
        sender.getPlayer().playSound(sender.getLocation(), new FakeBlock(material.createBlockData()).getStepSound(), 1, 1);
    }

    @CommandAnnotation
    public void debugBlockBreak(CorePlayer sender,
                           @LiteralArg("block") String l1,
                           @LiteralArg("break") String l2,
                           @EnumArg Material material) {
        sender.getPlayer().playSound(sender.getLocation(), new FakeBlock(material.createBlockData()).getBreakSound(), 1, 1);
    }

    @CommandAnnotation
    public void debugBlockPlace(CorePlayer sender,
                                @LiteralArg("block") String l1,
                                @LiteralArg("place") String l2,
                                @EnumArg Material material) {
        sender.getPlayer().playSound(sender.getLocation(), new FakeBlock(material.createBlockData()).getPlaceSound(), 1, 1);
    }

    @CommandAnnotation
    public void debugBlockHit(CorePlayer sender,
                                @LiteralArg("block") String l1,
                                @LiteralArg("hit") String l2,
                                @EnumArg Material material) {
        sender.getPlayer().playSound(sender.getLocation(), new FakeBlock(material.createBlockData()).getHitSound(), 1, 1);
    }

    @CommandAnnotation
    public void debugBlockFall(CorePlayer sender,
                                @LiteralArg("block") String l1,
                                @LiteralArg("fall") String l2,
                                @EnumArg Material material) {
        sender.getPlayer().playSound(sender.getLocation(), new FakeBlock(material.createBlockData()).getFallSound(), 1, 1);
    }

    @CommandAnnotation
    public void debugItem(CorePlayer sender,
                          @LiteralArg("item") String l) {
        Random random = new Random();
        sender.getGlobalWorld().addRotationItem(
                sender,
                InventoryMenuUtils.createCustomItem(Material.DIAMOND_SHOVEL, random.nextInt(50) * 100));
    }

    @CommandAnnotation
    public void debugChest(CorePlayer sender,
                           @LiteralArg("chest") String l,
                           @HelperArg("<title>") String title) {
        Inventory inventory = Bukkit.createInventory(null, 9 * 6, Chat.colorize(title));
        sender.getPlayer().openInventory(inventory);
    }

    @CommandAnnotation
    public void debugSound(CorePlayer sender,
                           @LiteralArg("sound") String l,
                           @HelperArg("<pitch>") Double pitch,
                           @Nullable @OptionArg(listName = "sounds", force = false) String startsWith) {
        TextComponent message = new TextComponent("");
        TextComponent soundStr;

        int i = 0;
        int split = 20;
        for (Sound sound : Sound.values()) {
            if (startsWith != null && sound.toString().toLowerCase().startsWith(startsWith.toLowerCase())) {
                if (i > 0) {
                    message.addExtra(", ");
                }
                soundStr = new TextComponent(sound.toString().toLowerCase().replaceFirst("entity_", "").replaceFirst("block_", "").replaceFirst("item_", "").replaceFirst("ui_", "").replaceFirst("music_", "").replaceAll("_", "."));
                soundStr.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to play sound '" + sound.toString() + "' at pitch " + pitch).create()));
                soundStr.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/sldebug play " + pitch + " " + sound.toString()));
                soundStr.setColor(org.bukkit.ChatColor.valueOf(Chat.getColor("DEFAULT").name()).asBungee());
                message.addExtra(soundStr);
                i++;
                if (i > split) {
                    sender.sendMessage(message);
                    message = new TextComponent("");
                    i = 0;
                }
            }
        }
        if (i > 0) {
            sender.sendMessage(message);
        }
    }

    @CommandAnnotation(hidden = true)
    public void debugPlay(CorePlayer sender,
                          @LiteralArg("play") String l,
                          Double pitch,
                          String name) {
        Bukkit.getOnlinePlayers().forEach(p -> p.playSound(sender.getPlayer().getLocation(), Sound.valueOf(name), 1, pitch.floatValue()));
    }

}
