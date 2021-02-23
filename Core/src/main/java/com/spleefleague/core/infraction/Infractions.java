package com.spleefleague.core.infraction;

import com.mongodb.client.MongoCollection;
import com.spleefleague.core.Core;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.coreapi.infraction.Infraction;
import com.spleefleague.coreapi.infraction.InfractionManager;
import com.spleefleague.coreapi.infraction.InfractionType;
import com.spleefleague.coreapi.utils.TimeUtils;
import com.spleefleague.coreapi.utils.packet.bungee.player.PacketBungeePlayerKick;
import com.spleefleague.coreapi.utils.packet.spigot.player.PacketSpigotPlayerInfraction;
import net.md_5.bungee.api.chat.TextComponent;
import org.bson.Document;
import org.bukkit.OfflinePlayer;

import java.util.UUID;

/**
 * @author NickM13
 */
public class Infractions {

    /**
     * Secretly mute a player
     *
     * @param sender Name of punisher
     * @param target OfflinePlayer
     * @param millis Time in milliseconds
     * @param reason Reason
     */
    public static void muteSecret(CorePlayer sender, OfflinePlayer target, long millis, String reason) {
        Infraction infraction = new Infraction();
        infraction.setTarget(target.getUniqueId())
                .setPunisher(sender == null ? "" : sender.getName())
                .setType(InfractionType.MUTE_SECRET)
                .setDuration(millis)
                .setReason(reason);
        Core.getInstance().sendPacket(new PacketSpigotPlayerInfraction(infraction));

        if (sender != null) {
            TextComponent component = new TextComponent();
            component.addExtra("You have secretly muted " + target.getName() + " for " + TimeUtils.timeToString(millis));
            Core.getInstance().sendMessage(sender, component);
        }
    }

    /**
     * Publicly mute a player
     *
     * @param sender Name of punisher
     * @param target OfflinePlayer
     * @param millis Time in milliseconds
     * @param reason Reason
     */
    public static void mutePublic(CorePlayer sender, OfflinePlayer target, long millis, String reason) {
        Infraction infraction = new Infraction();
        infraction.setTarget(target.getUniqueId())
                .setPunisher(sender == null ? "" : sender.getName())
                .setType(InfractionType.MUTE_PUBLIC)
                .setDuration(millis)
                .setReason(reason);
        Core.getInstance().sendPacket(new PacketSpigotPlayerInfraction(infraction));

        if (sender != null) {
            TextComponent component = new TextComponent();
            component.addExtra("You have muted " + target.getName() + " for " + TimeUtils.timeToString(millis));
            Core.getInstance().sendMessage(sender, component);
        }
    }

    /**
     * Unmute a player
     *
     * @param sender Name of punisher
     * @param target OfflinePlayer
     * @param reason Reason
     */
    public static void unmute(CorePlayer sender, OfflinePlayer target, String reason) {
        Infraction infraction = new Infraction();
        infraction.setTarget(target.getUniqueId())
                .setPunisher(sender == null ? "" : sender.getName())
                .setType(InfractionType.MUTE_SECRET)
                .setDuration(0L)
                .setReason(reason);
        Core.getInstance().sendPacket(new PacketSpigotPlayerInfraction(infraction));

        if (sender != null) {
            TextComponent component = new TextComponent();
            component.addExtra("You have unmuted " + target.getName());
            Core.getInstance().sendMessage(sender, component);
        }
    }

    /**
     * Temporarily ban a player from the server
     *
     * @param sender Name of punisher
     * @param target OfflinePlayer
     * @param millis Time in milliseconds
     * @param reason Reason
     */
    public static void tempban(CorePlayer sender, OfflinePlayer target, long millis, String reason) {
        if (target == null) return;
        Infraction infraction = new Infraction();
        infraction.setTarget(target.getUniqueId())
                .setPunisher(sender == null ? "" : sender.getName())
                .setType(InfractionType.TEMPBAN)
                .setDuration(millis)
                .setReason(reason);
        Core.getInstance().sendPacket(new PacketSpigotPlayerInfraction(infraction));

        if (sender != null) {
            TextComponent component = new TextComponent();
            component.addExtra("You have kicked " + target.getName());
            Core.getInstance().sendMessage(sender, component);
        }
    }

    /**
     * Ban a player from the server
     *
     * @param sender Name of punisher
     * @param target OfflinePlayer
     * @param reason Reason
     */
    public static void ban(CorePlayer sender, OfflinePlayer target, String reason) {
        Infraction infraction = new Infraction();
        infraction.setTarget(target.getUniqueId())
                .setPunisher(sender == null ? "" : sender.getName())
                .setType(InfractionType.BAN)
                .setDuration(0L)
                .setReason(reason);
        Core.getInstance().sendPacket(new PacketSpigotPlayerInfraction(infraction));

        if (sender != null) {
            TextComponent component = new TextComponent();
            component.addExtra("You have banned " + target.getName() + " for " + reason);
            Core.getInstance().sendMessage(sender, component);
        }
    }

    /**
     * Unban a player from the server
     *
     * @param sender Name of punisher
     * @param target OfflinePlayer
     * @param reason Reason
     */
    public static void unban(CorePlayer sender, OfflinePlayer target, String reason) {
        Infraction infraction = new Infraction();
        infraction.setTarget(target.getUniqueId())
                .setPunisher(sender == null ? "" : sender.getName())
                .setType(InfractionType.UNBAN)
                .setDuration(0L)
                .setReason(reason);
        Core.getInstance().sendPacket(new PacketSpigotPlayerInfraction(infraction));

        if (sender != null) {
            TextComponent component = new TextComponent();
            component.addExtra("You have unbanned " + target.getName());
            Core.getInstance().sendMessage(sender, component);
        }
    }

    /**
     * Kick a player from the server
     *
     * @param sender Name of punisher
     * @param target OfflinePlayer
     * @param reason Reason
     */
    public static void kick(CorePlayer sender, OfflinePlayer target, String reason) {
        Infraction infraction = new Infraction();
        infraction.setTarget(target.getUniqueId())
                .setPunisher(sender == null ? "" : sender.getName())
                .setType(InfractionType.KICK)
                .setDuration(0L)
                .setReason(reason);
        Core.getInstance().sendPacket(new PacketSpigotPlayerInfraction(infraction));

        if (sender != null) {
            TextComponent component = new TextComponent();
            component.addExtra("You have kicked " + target.getName());
            Core.getInstance().sendMessage(sender, component);
        }
    }

    /**
     * Send a warning to a player
     * Also used for post-ban information
     *
     * @param sender Name of punisher
     * @param target OfflinePlayer
     * @param reason Reason
     */
    public static void warn(CorePlayer sender, OfflinePlayer target, String reason) {
        Infraction infraction = new Infraction();
        infraction.setTarget(target.getUniqueId())
                .setPunisher(sender == null ? "" : sender.getName())
                .setType(InfractionType.WARNING)
                .setDuration(0L)
                .setReason(reason);
        Core.getInstance().sendPacket(new PacketSpigotPlayerInfraction(infraction));

        if (sender != null) {
            TextComponent component = new TextComponent();
            component.addExtra("You have warned " + target.getName() + " for " +
                    reason);
            Core.getInstance().sendMessage(sender, component);
        }
    }

}
