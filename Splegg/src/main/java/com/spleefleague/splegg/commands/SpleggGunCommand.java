package com.spleefleague.splegg.commands;

import com.spleefleague.core.command.HoldableCommand;
import com.spleefleague.core.command.annotation.CommandAnnotation;
import com.spleefleague.core.command.annotation.HelperArg;
import com.spleefleague.core.command.annotation.LiteralArg;
import com.spleefleague.core.command.annotation.OptionArg;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.rank.Rank;
import com.spleefleague.core.vendor.Vendorables;
import com.spleefleague.core.world.game.projectile.ProjectileStats;
import com.spleefleague.splegg.game.SpleggGun;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author NickM13
 * @since 5/6/2020
 */
public class SpleggGunCommand extends HoldableCommand {

    public SpleggGunCommand() {
        super(SpleggGun.class, "splegggun", Rank.DEVELOPER);
        this.addAlias("gun");
        this.setOptions("projectileFields", cp -> getProjectileFields());
        this.setContainer("splegg");
    }

    public Set<String> getProjectileFields() {
        Set<String> projectileFields = new TreeSet<>();
        for (Field field : ProjectileStats.class.getFields()) {
            projectileFields.add(field.getName());
        }
        return projectileFields;
    }

    @CommandAnnotation
    public void gunProjectile(CorePlayer sender,
                              @LiteralArg("projectile") String l1) {
        SpleggGun spleggGun = Vendorables.get(SpleggGun.class, sender.getHeldItem());
        if (spleggGun == null) {
            error(sender, "You're not holding a splegg gun!");
            return;
        }
        for (String fieldName : getProjectileFields()) {
            try {
                Field field = ProjectileStats.class.getDeclaredField(fieldName);
                success(sender, fieldName + " is set to " + field.get(spleggGun.getProjectileStats()));
            } catch (NoSuchFieldException | IllegalAccessException exception) {
                exception.printStackTrace();
            }
        }
    }

    @CommandAnnotation
    public void gunProjectileField(CorePlayer sender,
                                            @LiteralArg("projectile") String l2,
                                            @OptionArg(listName = "projectileFields") String fieldName,
                                            @Nullable @HelperArg("[value]") String value) {
        try {
            SpleggGun spleggGun = Vendorables.get(SpleggGun.class, sender.getHeldItem());
            if (spleggGun == null) {
                error(sender, "You're not holding a splegg gun!");
                return;
            }
            Field field = ProjectileStats.class.getDeclaredField(fieldName);
            if (value != null) {
                if (Integer.class.equals(field.getType())) {
                    try {
                        field.set(spleggGun.getProjectileStats(), Integer.valueOf(value));
                    } catch (IllegalAccessException | NumberFormatException exception) {
                        exception.printStackTrace();
                        error(sender, "Excepted integer!");
                        return;
                    }
                } else if (Double.class.equals(field.getType())) {
                    try {
                        field.set(spleggGun.getProjectileStats(), Double.valueOf(value));
                    } catch (IllegalAccessException | NumberFormatException exception) {
                        exception.printStackTrace();
                        error(sender, "Expected double!");
                        return;
                    }
                } else if (Boolean.class.equals(field.getType())) {
                    try {
                        field.set(spleggGun.getProjectileStats(), Boolean.valueOf(value));
                    } catch (IllegalAccessException exception) {
                        exception.printStackTrace();
                        error(sender, "Expected boolean!");
                        return;
                    }
                } else if (Enum.class.isAssignableFrom(field.getType())) {
                    try {
                        field.set(spleggGun.getProjectileStats(), Enum.valueOf((Class<Enum>) field.getType(), value));
                    } catch (IllegalAccessException exception) {
                        exception.printStackTrace();
                        error(sender, "Expected boolean!");
                        return;
                    }
                } else {
                    error(sender, "Invalid projectile field");
                    return;
                }
                success(sender, "Set field " + fieldName + " to " + value);
                spleggGun.updateDisplayItem();
                spleggGun.saveChanges();
                sender.setHeldItem(spleggGun.getDisplayItem());
            } else {
                success(sender, fieldName + " is set to " + field.get(spleggGun.getProjectileStats()));
            }
        } catch (NoSuchFieldException | IllegalAccessException exception) {
            exception.printStackTrace();
        }
    }

}
