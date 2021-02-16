/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.command.commands;

import com.spleefleague.core.chat.Chat;
import com.spleefleague.core.command.annotation.*;
import com.spleefleague.core.command.CoreCommand;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.rank.CoreRank;

import com.spleefleague.core.util.CoreUtils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.inventory.Inventory;

import javax.annotation.Nullable;

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
