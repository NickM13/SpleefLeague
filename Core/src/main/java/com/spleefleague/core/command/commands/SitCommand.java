package com.spleefleague.core.command.commands;

import com.spleefleague.core.command.CoreCommand;
import com.spleefleague.core.command.annotation.CommandAnnotation;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.rank.CoreRank;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Arrow;

/**
 * @author NickM13
 * @since 5/5/2020
 */
public class SitCommand extends CoreCommand {

    public SitCommand() {
        super("sit", CoreRank.DEVELOPER);
    }

    @CommandAnnotation
    public void sit(CorePlayer sender) {
        sender.getPlayer().getWorld().spawn(sender.getPlayer().getEyeLocation(), Arrow.class, (entity) -> {
            entity.setPickupStatus(AbstractArrow.PickupStatus.DISALLOWED);
            entity.setVelocity(sender.getPlayer().getLocation().getDirection().multiply(2));
            entity.addPassenger(sender.getPlayer());
        });
    }

}
