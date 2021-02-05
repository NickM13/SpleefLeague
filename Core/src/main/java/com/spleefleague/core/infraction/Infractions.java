package com.spleefleague.core.infraction;

import com.spleefleague.core.Core;
import com.spleefleague.core.chat.ChatChannel;
import com.spleefleague.core.player.infraction.Infraction;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

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
    public static void muteSecret(String sender, OfflinePlayer target, long millis, String reason) {
        Infraction infraction = new Infraction();
        infraction.setUuid(target.getUniqueId())
                .setPunisher(sender)
                .setType(Infraction.Type.MUTE_SECRET)
                .setDuration(millis)
                .setReason(reason);
        Infraction.create(infraction);
    }

    /**
     * Publicly mute a player
     *
     * @param sender Name of punisher
     * @param target OfflinePlayer
     * @param millis Time in milliseconds
     * @param reason Reason
     */
    public static void mutePublic(String sender, OfflinePlayer target, long millis, String reason) {
        Infraction infraction = new Infraction();
        infraction.setUuid(target.getUniqueId())
                .setPunisher(sender)
                .setType(Infraction.Type.MUTE_PUBLIC)
                .setDuration(millis)
                .setReason(reason);
        Infraction.create(infraction);

        if (target.isOnline()) {
            Core.getInstance().sendMessage(Core.getInstance().getPlayers().get(target.getPlayer()), "Muted by " + sender + " for " + infraction.getRemainingTimeString() + ": " + reason);
        }
    }

    /**
     * Unmute a player
     *
     * @param sender Name of punisher
     * @param target OfflinePlayer
     * @param reason Reason
     */
    public static void unmute(String sender, OfflinePlayer target, String reason) {
        Infraction infraction = new Infraction();
        infraction.setUuid(target.getUniqueId())
                .setPunisher(sender)
                .setType(Infraction.Type.MUTE_SECRET)
                .setDuration(0)
                .setReason(reason);
        Infraction.create(infraction);

        if (target.isOnline()) {
            Core.getInstance().sendMessage(Core.getInstance().getPlayers().get(target.getPlayer()), "You've been unmuted by " + sender);
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
    public static void tempban(String sender, OfflinePlayer target, long millis, String reason) {
        if (target == null) return;
        Infraction infraction = new Infraction();
        infraction.setUuid(target.getUniqueId())
                .setPunisher(sender)
                .setType(Infraction.Type.TEMPBAN)
                .setDuration(millis)
                .setReason(reason);
        Infraction.create(infraction);

        if (target.isOnline()) {
            Player player = (Player) target;
            if (player.getLocation().getWorld() != null) {
                player.getLocation().getWorld().strikeLightning(((Player) target).getLocation());
                player.kickPlayer("TempBan for " + infraction.getRemainingTimeString() + ": " + reason + "!");
            }
        }
    }

    /**
     * Ban a player from the server
     *
     * @param sender Name of punisher
     * @param target OfflinePlayer
     * @param reason Reason
     */
    public static void ban(String sender, OfflinePlayer target, String reason) {
        Infraction infraction = new Infraction();
        infraction.setUuid(target.getUniqueId())
                .setPunisher(sender)
                .setType(Infraction.Type.BAN)
                .setDuration(0)
                .setReason(reason);
        Infraction.create(infraction);

        if (target.isOnline()) {
            Player player = (Player) target;
            if (player.getLocation().getWorld() != null) {
                player.getLocation().getWorld().strikeLightning(((Player) target).getLocation());
                player.kickPlayer("Banned: " + reason + "!");
            }
        }
    }

    /**
     * Unban a player from the server
     *
     * @param sender Name of punisher
     * @param target OfflinePlayer
     * @param reason Reason
     */
    public static void unban(String sender, OfflinePlayer target, String reason) {
        Infraction infraction = new Infraction();
        infraction.setUuid(target.getUniqueId())
                .setPunisher(sender)
                .setType(Infraction.Type.UNBAN)
                .setDuration(0)
                .setReason(reason);
        Infraction.create(infraction);

    }

    /**
     * Kick a player from the server
     *
     * @param sender Name of punisher
     * @param target OfflinePlayer
     * @param reason Reason
     */
    public static void kick(String sender, OfflinePlayer target, String reason) {
        Infraction infraction = new Infraction();
        infraction.setUuid(target.getUniqueId())
                .setPunisher(sender)
                .setType(Infraction.Type.WARNING)
                .setDuration(0)
                .setReason(reason);
        Infraction.create(infraction);

        if (target.isOnline()) {
            Player player = (Player) target;
            if (player.getLocation().getWorld() != null) {
                player.getLocation().getWorld().strikeLightning(((Player) target).getLocation());
                player.kickPlayer("Kicked: " + reason + "!");
            }
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
    public static void warn(String sender, OfflinePlayer target, String reason) {
        Infraction infraction = new Infraction();
        infraction.setUuid(target.getUniqueId())
                .setPunisher(sender)
                .setType(Infraction.Type.WARNING)
                .setDuration(0)
                .setReason(reason);
        Infraction.create(infraction);

        if (target.isOnline()) {
            Core.getInstance().sendMessage(Core.getInstance().getPlayers().get(target.getPlayer()), "Warning from " + sender + ": " + reason);
        }
    }

}
