package com.spleefleague.core.command.commands;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.google.common.collect.Lists;
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

public class DisguiseCommand extends CoreCommand {

    public DisguiseCommand() {
        super("disguise", Rank.DEVELOPER);
    }

    @CommandAnnotation
    public void disguise(CorePlayer sender, String username) {
        GameProfile playerProfile = ((CraftPlayer) sender.getPlayer()).getHandle().getProfile();

        playerProfile.getProperties().clear();

        InventoryMenuSkullManager.Texture texture = InventoryMenuSkullManager.getTexture(Bukkit.getOfflinePlayer(username).getUniqueId());

        playerProfile.getProperties().put("textures", new Property("textures",
                texture.value,
                texture.signature));

        for (CorePlayer cp2 : Core.getInstance().getPlayers().getOnline()) {
            cp2.getPlayer().hidePlayer(Core.getInstance(), sender.getPlayer());
            cp2.getPlayer().showPlayer(Core.getInstance(), sender.getPlayer());
        }
    }

}
