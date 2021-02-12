package com.spleefleague.splegg.commands;

import com.spleefleague.core.command.HoldableCommand;
import com.spleefleague.core.command.annotation.CommandAnnotation;
import com.spleefleague.core.command.annotation.LiteralArg;
import com.spleefleague.core.command.annotation.OptionArg;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.rank.CoreRank;
import com.spleefleague.core.util.CoreUtils;
import com.spleefleague.core.vendor.Vendorables;
import com.spleefleague.core.world.game.projectile.ProjectileStats;
import com.spleefleague.splegg.game.SpleggGun;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author NickM13
 * @since 5/6/2020
 */
public class SpleggGunCommand extends HoldableCommand {

    public SpleggGunCommand() {
        super(SpleggGun.class, "splegggun", CoreRank.DEVELOPER);
        this.addAlias("gun");
        this.setOptions("projectileFields", pi -> getProjectileFields());
        this.setOptions("nextFields", this::getNextFields);
        this.setContainer("splegg");
    }

    public Set<String> getProjectileFields() {
        Set<String> projectileFields = new TreeSet<>();
        for (Field field : ProjectileStats.class.getFields()) {
            if (!field.getAnnotatedType().isAnnotationPresent(Deprecated.class)) {
                projectileFields.add(field.getName());
            }
        }
        return projectileFields;
    }

    public Set<String> getNextFields(PriorInfo pi) {
        Set<String> options = new TreeSet<>();
        try {
            Field field = ProjectileStats.class.getDeclaredField(pi.getArgs().get(pi.getArgs().size() - 1));
            if (Enum.class.isAssignableFrom(field.getType())) {
                options.addAll(CoreUtils.enumToStrSet((Class<? extends Enum<?>>) field.getType(), true));
            } else if (Boolean.class.equals(field.getType())) {
                options.add("true");
                options.add("false");
            }
        } catch (NoSuchFieldException exception) {
            //exception.printStackTrace();
        }
        return options;
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
    public void gunReset(CorePlayer sender,
                         @LiteralArg("reset") String l2) {
        SpleggGun spleggGun = Vendorables.get(SpleggGun.class, sender.getHeldItem());
        if (spleggGun == null) {
            error(sender, "You're not holding a splegg gun!");
            return;
        }
        spleggGun.resetStats();
    }

    @CommandAnnotation
    public void gunProjectileField(CorePlayer sender,
                                            @LiteralArg("projectile") String l2,
                                            @OptionArg(listName = "projectileFields") String fieldName,
                                            @Nullable @OptionArg(listName = "nextFields", force = false) String value) {
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
                        field.set(spleggGun.getProjectileStats(), Enum.valueOf((Class<Enum>) field.getType(), value.toUpperCase()));
                    } catch (IllegalArgumentException | IllegalAccessException exception) {
                        exception.printStackTrace();
                        error(sender, "Expected enum {" + CoreUtils.mergeSetString(CoreUtils.enumToStrSet((Class<? extends Enum<?>>) field.getType(), true)) + "}");
                        return;
                    }
                } else if (List.class.isAssignableFrom(field.getType())) {
                    try {
                        ParameterizedType listType = (ParameterizedType) field.getGenericType();
                        Class<?> listClass = (Class<?>) listType.getActualTypeArguments()[0];
                        if (Integer.class.isAssignableFrom(listClass)) {
                            List<Integer> list = new ArrayList<>();
                            for (String v : value.split(" ")) {
                                list.add(Integer.valueOf(v));
                            }
                            field.set(spleggGun.getProjectileStats(), list);
                        } else {
                            error(sender, "Contact developer Nick!");
                            return;
                        }
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
