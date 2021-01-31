package com.spleefleague.core.command.commands;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.spleefleague.core.Core;
import com.spleefleague.core.command.CoreCommand;
import com.spleefleague.core.command.annotation.CommandAnnotation;
import com.spleefleague.core.menu.InventoryMenuSkullManager;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.rank.Rank;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;

import javax.annotation.Nullable;

public class DisguiseCommand extends CoreCommand {

    public DisguiseCommand() {
        super("disguise", Rank.DEVELOPER);
    }

    @CommandAnnotation
    public void disguise(CorePlayer sender, @Nullable String username) {
        if (username == null) {
            sender.setDisguise(null);
        } else {
            sender.setDisguise(Bukkit.getOfflinePlayer(username).getUniqueId());
        }
    }

}
