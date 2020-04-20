/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.command;

import com.spleefleague.core.Core;
import com.spleefleague.core.chat.Chat;
import com.spleefleague.core.command.annotation.*;
import com.spleefleague.core.command.error.CoreError;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.rank.Rank;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;
import com.spleefleague.core.util.variable.TpCoord;
import com.spleefleague.core.database.variable.DBPlayer;
import net.minecraft.server.v1_15_R1.CommandListenerWrapper;
import net.minecraft.server.v1_15_R1.IChatBaseComponent;
import org.bukkit.command.Command;
import org.bukkit.craftbukkit.v1_15_R1.command.CraftBlockCommandSender;

/**
 * @author NickM13
 */
public class CommandTemplate extends Command {
    
    protected Class<? extends CommandTemplate> commandClass;
    private final Map<String, Function<CorePlayer, Set<String>>> optionsMap;
    
    protected static Set<String> allPermissions = new HashSet<>();
    
    public static Set<String> getAllPermissions() {
        return allPermissions;
    }
    
    protected CommandTemplate(Class<? extends CommandTemplate> commandClass, String name, Rank requiredRank, Rank... additionalRanks) {
        super(name);
        this.optionsMap = new HashMap<>();
        this.commandClass = commandClass;
        String perm = "spleefleague." + name.toLowerCase();
        allPermissions.add(perm);
        this.setPermission(perm);
        requiredRank.addPermission(perm);
        for (Rank ar : additionalRanks) {
            ar.addExclusivePermission(perm);
        }
        Core.getInstance().addCommand(this);
    }
    
    protected void setOptions(String name, Function<CorePlayer, Set<String>> options) {
        optionsMap.put(name, options);
    }
    
    protected Set<String> getOptions(String name, CorePlayer cp) {
        return optionsMap.get(name).apply(cp);
    }
    
    private Integer toInt(String str) {
        try {
            return Integer.parseInt(str);
        } catch(NumberFormatException e) {
            return null;
        }
    }
    private Double toDouble(String str) {
        try {
            return Double.parseDouble(str);
        } catch (NumberFormatException e) {
            return null;
        }
    }
    
    private boolean strEquals(String arg, String ... com) {
        for (String com1 : com) {
            if (arg.equalsIgnoreCase(com1)) {
                return true;
            }
        }
        return false;
    }
    
    private class Boundary {
        double min = 0, max = -1;
    }
    private Boundary getBounds(String str) {
        Boundary b = new Boundary();
        
        if (str.contains("..")) {
            if (str.substring(0, 2).equals("..")) {
                b.max = Double.parseDouble(str.substring(2));
            } else if (str.substring(str.length() - 2, str.length()).equals("..")) {
                b.min = Double.parseDouble(str.substring(0, str.length() - 2));
            } else {
                String[] vs = str.split("[.][.]");
                b.min = Double.parseDouble(vs[0]);
                b.max = Double.parseDouble(vs[1]);
            }
        } else {
            b.min = 0;
            b.max = Double.parseDouble(str);
        }
        
        return b;
    }
    
    private Vector modifyLocation(Vector loc, String c, String arg) {
        Vector location = loc.clone();
        switch (arg.charAt(0)) {
            case '~':
                switch (c) {
                    case "x": location.add(new Vector(Double.parseDouble(arg.substring(1)), 0D, 0D)); break;
                    case "y": location.add(new Vector(0D, Double.parseDouble(arg.substring(1)), 0D)); break;
                    case "z": location.add(new Vector(0D, 0D, Double.parseDouble(arg.substring(1)))); break;
                }
                break;
            case '^':
                //Vector v1 = loc.getDirection();
                //Vector v2 = v1.normalize().add(new Vector());
                //Vector r = v1.crossProduct(v2);
                //vector.add(vector);
                break;
            default:
                switch (c) {
                    case "x": location.setX(Double.parseDouble(arg)); break;
                    case "y": location.setY(Double.parseDouble(arg)); break;
                    case "z": location.setZ(Double.parseDouble(arg)); break;
                }
                break;
        }
        return location;
    }
    
    private Vector modifyDLocation(Vector loc, String c, String arg) {
        Vector location = loc.clone();
        switch (c) {
            case "dx": location.setX(Double.parseDouble(arg)); break;
            case "dy": location.setY(Double.parseDouble(arg)); break;
            case "dz": location.setZ(Double.parseDouble(arg)); break;
        }
        return location;
    }
    
    private boolean isValidCorePlayer(CommandSender cs, CorePlayer sender, CorePlayer cp, CorePlayerArg cpa) {
        if (cp == null) return false;
        if (cpa == null) return true;
        if ((!cpa.allowOffline() && !cp.isOnline())
                || (!cpa.allowSelf() && sender != null && sender.equals(cp))) {
            cs.sendMessage(Chat.ERROR + "Invalid player");
            return false;
        }
        return true;
    }
    
    private boolean isNumInbounds(CommandSender cs, Double num, NumberArg na) {
        if (num == null) return false;
        if (na == null) return true;
        if (num < na.minValue() || num > na.maxValue()) {
            cs.sendMessage(Chat.ERROR + "Expected number between " + na.minValue() + " and " + na.maxValue());
            return false;
        }
        return true;
    }
    
    private enum SortType {
        NEAREST,
        FURTHEST,
        RANDOM,
        ARBITRARY
    }
    
    private List<Entity> getEntitiesByArgs(CommandSender sender, Vector loc, List<Entity> entities, String selectorArgs) {
        if (selectorArgs.equals("")) return entities;
        
        Vector dloc = loc.clone();
        
        Integer limit = null;
        SortType sort = SortType.ARBITRARY;

        HashSet<Entity> entitySet = new HashSet<>(entities);
        
        if (selectorArgs.charAt(0) == '[' &&
                selectorArgs.charAt(selectorArgs.length() - 1) == ']') {
            selectorArgs = selectorArgs.substring(1, selectorArgs.length() - 1).trim();
            String[] args = selectorArgs.split(","), arg;
            String argument, value;
            
            for (String arg1 : args) {
                arg = arg1.split("=");
                argument = arg[0];
                value = arg[1];
                if (strEquals(argument, "distance", "r")) {
                    Boundary bound = getBounds(value);
                    Iterator<Entity> it = entitySet.iterator();
                    while (it.hasNext()) {
                        Entity e = it.next();
                        if ((bound.max >= 0 &&
                                e.getLocation().toVector().distance(loc) > bound.max) ||
                                e.getLocation().toVector().distance(loc) < bound.min) {
                            it.remove();
                        }
                    }
                } else if (strEquals(argument, "x", "y", "z")) {
                    loc = modifyLocation(loc, argument, value);
                } else if (strEquals(argument, "dx", "dy", "dz")) {
                    dloc = modifyDLocation(dloc, argument, value);
                    
                    if (strEquals(argument, "dz")) {
                        Iterator<Entity> it = entitySet.iterator();
                        while (it.hasNext()) {
                            Entity e = it.next();
                            if (!e.getBoundingBox().overlaps(new BoundingBox(loc.getX(),
                                    loc.getY(),
                                    loc.getZ(),
                                    loc.getX() + dloc.getX(),
                                    loc.getY() + dloc.getY(),
                                    loc.getZ() + dloc.getZ()))) {
                                it.remove();
                            }
                        }
                    }
                } else if (strEquals(argument, "scores")) {
                    sender.sendMessage("scores argument not set up yet");
                } else if (strEquals(argument, "tag")) {
                    sender.sendMessage("tag argument not set up yet");
                } else if (strEquals(argument, "team")) {
                    sender.sendMessage("team argument not set up yet");
                } else if (strEquals(argument, "limit")) {
                    limit = Integer.valueOf(value);
                } else if (strEquals(argument, "sort")) {
                    sort = SortType.valueOf(value.toUpperCase());
                } else if (strEquals(argument, "level", "l", "lm")) {
                    Boundary bound = getBounds(value);
                    Iterator<Entity> it = entitySet.iterator();
                    while (it.hasNext()) {
                        Entity e = it.next();
                        if (e instanceof Player) {
                            Player p = (Player) e;
                            if ((bound.max >= 0 &&
                                    p.getLevel() > bound.max) ||
                                    p.getLevel() < bound.min) {
                                it.remove();
                            }
                        } else {
                            it.remove();
                        }
                    }
                } else if (strEquals(argument, "gamemode", "m")) {
                    GameMode gm = null;
                    String gmStr = "";
                    if (value.charAt(0) == '!')
                        gmStr = value.substring(1);
                    else
                        gmStr = value;
                    switch (gmStr.toLowerCase()) {
                        case "0": case "s": case "survival":
                            gm = GameMode.SURVIVAL;
                            break;
                        case "1": case "c": case "creative":
                            gm = GameMode.CREATIVE;
                            break;
                        case "2": case "a": case "adventure":
                            gm = GameMode.ADVENTURE;
                            break;
                        case "3": case "spectator":
                            gm = GameMode.SPECTATOR;
                            break;
                    }
                    if (gm == null) continue;
                    Iterator<Entity> it = entitySet.iterator();
                    while(it.hasNext()) {
                        Entity e = it.next();
                        Player p = null;
                        if (e instanceof Player) {
                            p = (Player) e;
                        } else {
                            it.remove();
                        }
                        if (p == null
                                || (value.charAt(0) == '!' && p.getGameMode().equals(gm))
                                || (value.charAt(0) != '!' && !p.getGameMode().equals(gm))) {
                            it.remove();
                        }
                    }
                } else if (strEquals(argument, "name")) {
                    Iterator<Entity> it = entitySet.iterator();
                    while(it.hasNext()) {
                        Entity e = it.next();
                        if ((value.charAt(0) == '!' && e.getName().equalsIgnoreCase(value.substring(1))) ||
                                (value.charAt(0) != '!' && !e.getName().equalsIgnoreCase(value))) {
                            it.remove();
                        }
                    }
                } else if (strEquals(argument, "x_rotation", "rx", "rxm")) {
                    sender.sendMessage("x_rotation argument not set up yet");
                } else if (strEquals(argument, "y_rotation", "ry", "rym")) {
                    sender.sendMessage("y_rotation argument not set up yet");
                } else if (strEquals(argument, "type")) {
                    sender.sendMessage("type argument not set up yet");
                } else if (strEquals(argument, "nbt")) {
                    sender.sendMessage("nbt argument not set up yet");
                } else if (strEquals(argument, "advancements")) {
                    sender.sendMessage("advancements argument not set up yet");
                } else if (strEquals(argument, "predicate")) {
                    sender.sendMessage("predicate argument not set up yet");
                }
            }
        } else {
            return new ArrayList<>();
        }
        
        class EntityDistanced {
            final Entity entity;
            final Integer distance;
            
            public EntityDistanced(Entity entity, Integer distance) {
                this.entity = entity;
                this.distance = distance;
            }
        }
        
        ArrayList<EntityDistanced> entityDistancedList = new ArrayList<>();
        for (Entity e : entitySet) {
            entityDistancedList.add(new EntityDistanced(e, (int)loc.distance(e.getLocation().toVector())));
        }
        
        if (sort != SortType.ARBITRARY) {
            switch (sort) {
                case NEAREST:
                    entityDistancedList.sort(Comparator.comparingInt(e -> e.distance));
                    break;
                case FURTHEST:
                    entityDistancedList.sort((e1, e2) -> e2.distance-e1.distance);
                    break;
                case RANDOM:
                    Random random = new Random();
                    entityDistancedList.sort((e1, e2) -> random.nextInt());
                    break;
                default: break;
            }
        }
        List<Entity> entityList = new ArrayList<>();
        for (EntityDistanced ed : entityDistancedList) {
            entityList.add(ed.entity);
        }
        
        if (limit != null) {
            entityList = entityList.subList(0, Math.min(limit, entityList.size()));
        }
        
        return entityList;
    }
    
    @SuppressWarnings("deprecation")
    @Override
    public boolean execute(CommandSender cs, String command, String[] args) {
        CorePlayer cp = null;
        boolean success = false;
        BlockCommandSender bcs = null;
        CraftBlockCommandSender cbcs = null;
        CommandListenerWrapper listener = null;
        Location loc = null;
        if (cs instanceof Player) {
            cp = Core.getInstance().getPlayers().get(cs.getName());
            loc = cp.getPlayer().getLocation();
        } else if (cs instanceof BlockCommandSender) {
            bcs = (BlockCommandSender) cs;
            cbcs = (CraftBlockCommandSender) bcs;
            listener = cbcs.getWrapper();
            loc = bcs.getBlock().getLocation().clone().add(0.5, 0.5, 0.5);
        } else {
            loc = new Location(Core.DEFAULT_WORLD, 0, 0, 0);
        }
        if (cp == null
                || this.getPermission() == null
                || cp.getPlayer().hasPermission(this.getPermission())) {
            Random random = new Random();
            for (Method method : commandClass.getDeclaredMethods()) {
                if (!method.isAnnotationPresent(CommandAnnotation.class)) {
                    continue;
                }
                ArrayList<Object> params = new ArrayList<>();
                int paramSize = 0;
                int paramCount = 0;
                
                for (Parameter p : method.getParameters()) {
                    if (p.isAnnotationPresent(Nullable.class)) {
                        if (paramSize - 1 == args.length) {
                            break;
                        }
                    }
                    paramSize++;
                    paramCount++;
                }
                
                if ((paramSize - 1 != args.length &&
                        (args.length == 0 ||
                        !method.getParameters()[method.getParameterCount() - 1].getType().equals(String.class)))) {
                    continue;
                }
                if (paramSize - 1 > args.length) {
                    continue;
                }
                
                if (cp != null &&
                        method.getParameters()[0].getType().equals(CorePlayer.class)) {
                    if (!cp.getRank().hasPermission(Rank.getRank(method.getAnnotation(CommandAnnotation.class).minRank()))) {
                        continue;
                    }
                    params.add(cp);
                } else if (method.getParameters()[0].getType().equals(CommandSender.class)) {
                    params.add(cs);
                } else {
                    continue;
                }

                List<Object> objList;
                Object obj = null;

                boolean invalidArg = false;

                int ai = 0, pi = 1;
                for (; pi < paramCount && !invalidArg; pi++) {
                    // Check for "vararg"
                    String varArgStr = "";
                    if (pi == paramCount - 1 &&
                            paramSize - 1 != args.length) {
                        for (; ai < args.length; ai++) {
                            varArgStr = varArgStr.concat(args[ai]);
                            if (ai < args.length - 1) varArgStr += " ";
                        }
                    }

                    Parameter param = method.getParameters()[pi];
                    Class<?> paramClass = param.getType();
                    String arg = varArgStr.length() == 0 ? args[ai] : varArgStr;

                    if (varArgStr.length() == 0) {
                        // Target selectors
                        if (arg.charAt(0) == '@') {
                            arg = arg.substring(1);

                            // Target selector arguments

                            List<Entity> entities;
                            switch (arg.charAt(0)) {
                                case 'p': // Nearest player
                                    if (!paramClass.equals(CorePlayer.class)) {
                                        invalidArg = true;
                                        break;
                                    }
                                    obj = null;
                                    if (cp != null) {
                                        obj = cp;
                                    } else if (bcs != null) {
                                        for (DBPlayer dbp : Core.getInstance().getPlayers().getOnline()) {
                                            cp = (CorePlayer) dbp;
                                            if (obj == null
                                                    || cp.getLocation().distance(loc) < ((CorePlayer) obj).getLocation().distance(loc)) {
                                                obj = cp;
                                            }
                                        }
                                    }
                                    if (obj == null) {
                                        invalidArg = true;
                                        continue;
                                    }
                                    params.add(obj);
                                    break;
                                case 'r': // Random player
                                    if (!paramClass.equals(CorePlayer.class)) {
                                        invalidArg = true;
                                        break;
                                    }
                                    obj = (Player) Bukkit.getOnlinePlayers().toArray()[random.nextInt(Bukkit.getOnlinePlayers().size())];
                                    params.add(Core.getInstance().getPlayers().get((Player) obj));
                                    break;
                                case 'a': // All online players
                                    if (!paramClass.equals(List.class)) {
                                        invalidArg = true;
                                        break;
                                    }
                                    entities = new ArrayList<>();
                                    for (CorePlayer cp1 : Core.getInstance().getPlayers().getOnline()) {
                                        entities.add(cp1.getPlayer());
                                    }
                                    entities = getEntitiesByArgs(cs, loc.toVector(), entities, arg.substring(1));
                                    objList = new ArrayList<>();
                                    for (Entity e : entities) {
                                        objList.add(Core.getInstance().getPlayers().get(e.getName()));
                                    }
                                    params.add(objList);
                                    break;
                                case 'e': // All entities (in world of command sender)
                                    if (!paramClass.equals(List.class)) {
                                        invalidArg = true;
                                        break;
                                    }
                                    entities = loc.getWorld().getEntities();
                                    entities = getEntitiesByArgs(cs, loc.toVector(), entities, arg.substring(1));
                                    params.add(entities);
                                    break;
                                case 's': // The entity executing this command
                                    if (cp != null &&
                                            paramClass.equals(CorePlayer.class)) {
                                        params.add(cp);
                                    } else if (cs instanceof BlockCommandSender &&
                                            paramClass.equals(BlockCommandSender.class)) {
                                        params.add((BlockCommandSender)cs);
                                    } else if (paramClass.equals(CorePlayer.class)) {
                                        params.add(cs);
                                    } else {
                                        invalidArg = true;
                                        break;
                                    }
                                    break;
                                default: break;
                            }
                            ai++;
                            continue;
                        }
                        if (paramClass.equals(TpCoord.class) &&
                                (obj = TpCoord.create(arg)) != null) {
                            params.add(obj);
                            ai++;
                            continue;
                        }
                        if (paramClass.equals(CorePlayer.class) &&
                                (obj = Core.getInstance().getPlayers().getOffline(arg)) != null) {
                            invalidArg = !isValidCorePlayer(cs, cp, (CorePlayer) obj, param.getAnnotation(CorePlayerArg.class));
                            params.add(obj);
                            ai++;
                            continue;
                        }
                        if (paramClass.equals(Player.class) &&
                                (obj = Bukkit.getPlayer(arg)) != null) {
                            params.add(obj);
                            ai++;
                            continue;
                        }
                        if (paramClass.equals(OfflinePlayer.class)) {
                            obj = Bukkit.getOfflinePlayer(arg);
                            params.add(obj);
                            ai++;
                            continue;
                        }
                        if (paramClass.equals(Integer.class) &&
                                (obj = toInt(arg)) != null) {
                            invalidArg = !isNumInbounds(cs, ((Integer) obj).doubleValue(), param.getAnnotation(NumberArg.class));
                            params.add(obj);
                            ai++;
                            continue;
                        }
                        if (paramClass.equals(Double.class)
                                && (obj = toDouble(arg)) != null) {
                            invalidArg = !isNumInbounds(cs, (Double) obj, param.getAnnotation(NumberArg.class));
                            params.add(obj);
                            ai++;
                            continue;
                        }
                    }
                    // Last resort, just pass string
                    if (paramClass.equals(String.class)) {
                        if (param.isAnnotationPresent(LiteralArg.class)) {
                            if (!((LiteralArg) param.getAnnotation(LiteralArg.class)).value().equalsIgnoreCase(arg)) {
                                invalidArg = true;
                                continue;
                            }
                        } else if (param.isAnnotationPresent(OptionArg.class)) {
                            if (((OptionArg) param.getAnnotation(OptionArg.class)).force()) {
                                invalidArg = true;
                                Set<String> options2 = this.getOptions(((OptionArg) param.getAnnotation(OptionArg.class)).listName(), cp);
                                for (String o : options2) {
                                    if (o.equalsIgnoreCase(arg)
                                            || o.contains(":") && o.split(":")[1].equalsIgnoreCase(arg)) {
                                        arg = o;
                                        invalidArg = false;
                                        break;
                                    }
                                }
                                if (invalidArg) continue;
                            }
                        }
                        params.add(arg);
                        ai++;
                        continue;
                    }
                    invalidArg = true;
                }
                for (int r = paramCount; r < method.getParameterCount(); r++) {
                    if (method.getParameters()[r].isAnnotationPresent(Nullable.class)) {
                        params.add(null);
                    } else {
                        invalidArg = false;
                        break;
                    }
                }
                if (!invalidArg) {
                    try {
                        if (boolean.class.isAssignableFrom(method.getReturnType())
                                && bcs != null) {
                            success = (Boolean) method.invoke(this, params.toArray(new Object[0]));
                            if (success) {
                                listener.a(3);
                            } else {
                                listener.sendFailureMessage(IChatBaseComponent.ChatSerializer.a("Failure"));
                            }
                            return success;
                        } else {
                            method.invoke(this, params.toArray(new Object[0]));
                            return true;
                        }
                    } catch (SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                        //error(cp, "Command error");
                        Logger.getLogger(CommandTemplate.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
            if (!success) {
                if (cp != null) usage(cp, this.getUsage());
                else            usage(cs, this.getUsage());
            }
        } else {
            error(cp, CoreError.UNABLE);
        }
        if (bcs != null) {
            listener.sendFailureMessage(IChatBaseComponent.ChatSerializer.a("Failure"));
            //listener.a(3);
        }
        return true;
    }
    
    @SuppressWarnings("deprecation")
    @Override
    public List<String> tabComplete(CommandSender cs, String alias, String[] args) {
        List<String> options = new ArrayList<>();
        
        optionSelected = false;
        
        CorePlayer cp = null;
        if (cs instanceof Player) {
            cp = Core.getInstance().getPlayers().get(cs.getName());
        }
        
        for (Method method : commandClass.getDeclaredMethods()) {
            if (!method.isAnnotationPresent(CommandAnnotation.class) ||
                    ((CommandAnnotation) method.getAnnotation(CommandAnnotation.class)).hidden()) continue;
            
            int paramSize = 0;
            
            for (Parameter p : method.getParameters()) {
                paramSize++;
            }
            
            if (paramSize - 1 < args.length) {
                continue;
            }
            
            if (method.getParameters()[0].getType().equals(CorePlayer.class) && cp != null) {
                if (!cp.getRank().hasPermission(Rank.getRank(method.getAnnotation(CommandAnnotation.class).minRank()))) {
                    continue;
                }
            } else if (!method.getParameters()[0].getType().equals(CommandSender.class)) {
                continue;
            }

            Object obj = null;

            boolean invalidArg = false;

            int ai = 0, pi = 1;
            for (; pi < args.length && !invalidArg; pi++) {
                // Check for "vararg"

                Parameter param = method.getParameters()[pi];
                Class<?> paramClass = param.getType();
                String arg = args[ai];
                if (arg.length() == 0) {
                    invalidArg = true;
                    break;
                }
                
                // Target selectors
                if (arg.charAt(0) == '@') {
                    arg = arg.substring(1);

                    // Target selector arguments

                    List<Entity> entities;
                    switch (arg.charAt(0)) {
                        case 'p': case 'r': // One player
                            if (!paramClass.equals(CorePlayer.class)) {
                                invalidArg = true;
                                break;
                            }
                            break;
                        case 'a': case 'e': // Lots of entities
                            if (!paramClass.equals(List.class)) {
                                invalidArg = true;
                                break;
                            }
                            break;
                        case 's': case 'S': // The entity executing this command
                            if (!paramClass.equals(CorePlayer.class) &&
                                    !(cs instanceof BlockCommandSender &&
                                    paramClass.equals(BlockCommandSender.class))) {
                                invalidArg = true;
                                break;
                            }
                            break;
                        default: break;
                    }
                    ai++;
                    continue;
                }
                if (paramClass.equals(TpCoord.class)) {
                    invalidArg = ((obj = TpCoord.create(arg)) == null);
                    ai++;
                    continue;
                }
                if (paramClass.equals(CorePlayer.class)) {
                    invalidArg = ((obj = Core.getInstance().getPlayers().getOffline(arg)) == null);
                    if (!invalidArg) {
                        invalidArg = !isValidCorePlayer(cs, cp, (CorePlayer) obj, param.getAnnotation(CorePlayerArg.class));
                    }
                    ai++;
                    continue;
                }
                if (paramClass.equals(Player.class)) {
                    invalidArg = ((obj = Bukkit.getPlayer(arg)) == null);
                    ai++;
                    continue;
                }
                if (paramClass.equals(OfflinePlayer.class)) {
                    ai++;
                    continue;
                }
                if (paramClass.equals(Integer.class)) {
                    invalidArg = ((obj = toInt(arg)) == null);
                    if (!invalidArg)
                        invalidArg = !isNumInbounds(cs, ((Integer) obj).doubleValue(), param.getAnnotation(NumberArg.class));
                    ai++;
                    continue;
                }
                if (paramClass.equals(Double.class)) {
                    invalidArg = ((obj = toDouble(arg)) == null);
                    if (!invalidArg)
                        invalidArg = !isNumInbounds(cs, (Double) obj, param.getAnnotation(NumberArg.class));
                    ai++;
                    continue;
                }
                // Last resort, just pass string
                if (paramClass.equals(String.class)) {
                    if (param.isAnnotationPresent(LiteralArg.class)) {
                        if (!((LiteralArg) param.getAnnotation(LiteralArg.class)).value().equalsIgnoreCase(arg)) {
                            invalidArg = true;
                            continue;
                        }
                    } else if (param.isAnnotationPresent(OptionArg.class)) {
                        if (((OptionArg) param.getAnnotation(OptionArg.class)).force()) {
                            invalidArg = true;
                            Set<String> optionSet = getOptions(((OptionArg) param.getAnnotation(OptionArg.class)).listName(), cp);
                            for (String o : optionSet) {
                                if (/*o.toUpperCase().startsWith(arg.toUpperCase()) || */o.equalsIgnoreCase(arg)) {
                                    invalidArg = false;
                                    break;
                                }
                            }
                            if (invalidArg) continue;
                        }
                    }
                    ai++;
                    continue;
                }
                invalidArg = true;
            }
            if (!invalidArg) {
                String lastArg = args[args.length - 1];
                Parameter currParam = method.getParameters()[args.length];
                if (currParam.isAnnotationPresent(HelperArg.class) &&
                        lastArg.isEmpty()) {
                    options.add(((HelperArg) currParam.getAnnotation(HelperArg.class)).value());
                }
                if (currParam.getType().equals(String.class)) {
                    if (currParam.isAnnotationPresent(LiteralArg.class)) {
                        String literal = ((LiteralArg) currParam.getAnnotation(LiteralArg.class)).value();
                        addOption(options, literal, lastArg);
                    } else if (currParam.isAnnotationPresent(OptionArg.class)) {
                        for (String option : getOptions(((OptionArg) currParam.getAnnotation(OptionArg.class)).listName(), cp)) {
                            addOption(options, option, lastArg);
                        }
                    }
                } else if (currParam.getType().equals(CorePlayer.class) ||
                        currParam.getType().equals(Player.class) ||
                        currParam.getType().equals(OfflinePlayer.class)) {
                    CorePlayerArg cpa = (CorePlayerArg) currParam.getType().getAnnotation(CorePlayerArg.class);
                    for (CorePlayer cp2 : Core.getInstance().getPlayers().getOnline()) {
                        if (cp != null && cpa != null
                                && !cpa.allowSelf()
                                && cp2.getName().equalsIgnoreCase(cp.getName())) {
                            
                        }
                        addOption(options, cp2.getName(), lastArg);
                    }
                    if (cpa == null || cpa.allowSelf()) {
                        addOption(options, "@p", lastArg);
                        addOption(options, "@s", lastArg);
                    }
                } else if (currParam.getType().equals(List.class)) {
                    addOption(options, "@a", lastArg);
                    addOption(options, "@e", lastArg);
                }
                if (optionSelected) {
                    String[] args2 = new String[args.length + 1];
                    System.arraycopy(args, 0, args2, 0, args.length);
                    args2[args.length] = "";
                    options.clear();
                    for (String o : tabComplete(cs, alias, args2)) {
                        options.add(lastArg + " " + o);
                    }
                }
            }
        }
        
        return options;
    }
    
    private boolean optionSelected;
    
    private void addOption(List<String> options, String option, String arg) {
        if (option.equalsIgnoreCase(arg)
                || (option.contains(":") && option.split(":")[1].equalsIgnoreCase(arg))) {
            options.clear();
            optionSelected = true;
        } else if (option.toUpperCase().startsWith(arg.toUpperCase())
                || (option.contains(":") && option.toUpperCase().split(":")[1].startsWith(arg.toUpperCase()))) {
            options.add(option);
        }
    }

    public void addAlias(String alias) {
        List<String> aliases = this.getAliases();
        aliases.add(alias);
        this.setAliases(aliases);
    }
    
    protected void success(CommandSender cs, String msg) {
        Core.getInstance().sendMessage(cs, msg);
    }
    protected void success(CorePlayer cp, String msg) {
        Core.getInstance().sendMessage(cp, msg);
    }
    
    protected void error(CommandSender cs, String msg) {
        Core.getInstance().sendMessage(cs, Chat.ERROR + msg);
    }
    protected void error(CorePlayer cp, String msg) {
        Core.getInstance().sendMessage(cp, Chat.ERROR + msg);
    }
    protected void error(CommandSender cs, CoreError error) {
        Core.getInstance().sendMessage(cs, Chat.ERROR + error.getMessage());
    }
    protected void error(CorePlayer cp, CoreError error) {
        Core.getInstance().sendMessage(cp, Chat.ERROR + error.getMessage());
    }
    
    protected void usage(CommandSender cs, String msg) {
        Core.getInstance().sendMessage(cs, msg);
    }
    protected void usage(CorePlayer cp, String msg) {
        Core.getInstance().sendMessage(cp, msg);
    }
}
