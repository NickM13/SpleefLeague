/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.command;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.spleefleague.core.Core;
import com.spleefleague.core.chat.Chat;
import com.spleefleague.core.command.annotation.*;
import com.spleefleague.core.command.error.CoreError;
import com.spleefleague.core.logger.CoreLogger;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.rank.CoreRank;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.function.Function;
import javax.annotation.Nullable;

import com.spleefleague.core.player.rank.CoreRankManager;
import com.spleefleague.coreapi.chat.ChatColor;
import com.spleefleague.coreapi.database.variable.DBPlayer;
import net.md_5.bungee.api.chat.TextComponent;
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
import net.minecraft.server.v1_15_R1.CommandListenerWrapper;
import net.minecraft.server.v1_15_R1.IChatBaseComponent;
import org.bukkit.command.Command;
import org.bukkit.craftbukkit.v1_15_R1.command.CraftBlockCommandSender;

/**
 * @author NickM13
 */
public class CoreCommand extends Command {

    public class PriorInfo {
        CorePlayer cp;
        List<String> args;
        List<String> reverse;

        PriorInfo(CorePlayer cp, List<String> args) {
            this.cp = cp;
            this.args = args;
            this.reverse = new ArrayList<>();
            this.reverse.addAll(args);
            Collections.reverse(this.reverse);
        }

        public CorePlayer getCorePlayer() {
            return cp;
        }

        public List<String> getArgs() {
            return args;
        }

        public List<String> getReverse() {
            return reverse;
        }
    }

    private class MethodInfo {

        boolean cpSender;
        Method method;
        Parameter[] parameters;
        int minParams, maxParams;
        boolean varParam;
        boolean hidden;
        boolean confirmation;
        CoreRank minRank;
        List<CoreRank> additionalRanks = new ArrayList<>();
        String description;

        public MethodInfo(Method method, CommandAnnotation commandAnnotation) {
            this.cpSender = method.getParameters()[0].getType().equals(CorePlayer.class);

            this.method = method;
            parameters = method.getParameters();

            minParams = maxParams = parameters.length - 1;
            for (int i = 1; i < parameters.length; i++) {
                Parameter param = parameters[i];
                if (param.isAnnotationPresent(Nullable.class)) {
                    minParams = i - 1;
                    break;
                }
            }

            varParam = parameters[parameters.length - 1].getType().equals(String.class);

            hidden = commandAnnotation.hidden();
            minRank = Core.getInstance().getRankManager().getRank(commandAnnotation.minRank());
            additionalRanks.addAll(Core.getInstance().getRankManager().getRanks(commandAnnotation.additionalRanks().split(",")));
            description = commandAnnotation.description();
            confirmation = commandAnnotation.confirmation();
        }

    }

    private final Map<String, Function<PriorInfo, Set<String>>> optionsMap;
    
    private final static Set<String> allPermissions = new HashSet<>();
    private String container;

    private final Map<Integer, List<MethodInfo>> executeMap = new TreeMap<>(Comparator.comparingInt(i -> i));
    private final Map<Integer, List<MethodInfo>> tabMap = new TreeMap<>(Comparator.comparingInt(i -> i));
    private final List<MethodInfo> helpMethods = new ArrayList<>();

    public static Set<String> getAllPermissions() {
        return allPermissions;
    }
    
    protected CoreCommand(String name, CoreRank requiredRank, CoreRank... additionalRanks) {
        super(name);
        this.optionsMap = new HashMap<>();
        String perm = "spleefleague." + name.toLowerCase();
        allPermissions.add(perm);
        this.setPermission(perm);
        requiredRank.addPermission(perm);
        for (CoreRank ar : additionalRanks) {
            ar.addExclusivePermission(perm);
        }
        container = "core";
        Core.getInstance().addCommand(this);
        setUsage("Command not recognized! Type /" + name + " help to view available commands.");

        List<Method> methods = Lists.newArrayList(getClass().getMethods());
        methods.sort(Comparator.comparingInt(Method::getParameterCount));
        for (Method method : methods) {
            CommandAnnotation commandAnnotation = method.getAnnotation(CommandAnnotation.class);
            if (commandAnnotation == null ||
                    commandAnnotation.disabled()) {
                continue;
            }
            MethodInfo methodInfo = new MethodInfo(method, commandAnnotation);
            int minParamCount = methodInfo.minParams;
            if (!commandAnnotation.hidden()) {
                if (minParamCount > 0) {
                    helpMethods.add(methodInfo);
                }
                if (!tabMap.containsKey(minParamCount)) {
                    tabMap.put(minParamCount, new ArrayList<>());
                }
                tabMap.get(minParamCount).add(methodInfo);
            }
            if (!executeMap.containsKey(minParamCount)) {
                executeMap.put(minParamCount, new ArrayList<>());
            }
            executeMap.get(minParamCount).add(methodInfo);
        }
    }

    @CommandAnnotation(hidden = true)
    public void commandHelp(CorePlayer sender,
                               @LiteralArg("help") String l) {
        usage(sender, ChatColor.DARK_GRAY + "" + ChatColor.STRIKETHROUGH + "" + ChatColor.BOLD + "------------------------");

        TextComponent component;

        for (MethodInfo methodInfo : helpMethods) {
            boolean hasPerms = false;
            if (!sender.getRank().hasPermission(methodInfo.minRank)) {
                for (CoreRank aRank : methodInfo.additionalRanks) {
                    if (sender.getRank().equals(aRank)) {
                        hasPerms = true;
                        break;
                    }
                }
                if (!hasPerms) continue;
            }

            component = new TextComponent();
            component.setColor(net.md_5.bungee.api.ChatColor.GRAY);
            component.addExtra("/" + getName() + " ");

            StringBuilder builder = new StringBuilder();

            boolean optional;
            Parameter[] parameters = methodInfo.parameters;
            for (int i = 1; i < parameters.length; i++) {
                Parameter p = parameters[i];
                if (p.isAnnotationPresent(LiteralArg.class)) {
                    builder.append(p.getAnnotation(LiteralArg.class).value());
                } else {
                    optional = i > methodInfo.minParams;
                    builder.append(optional ? "[" : "<");
                    if (p.isAnnotationPresent(HelperArg.class)) {
                        builder.append(p.getAnnotation(HelperArg.class).value());
                    } else {
                        if (p.isAnnotationPresent(CorePlayerArg.class)) {
                            builder.append("player");
                        } else if (p.isAnnotationPresent(NumberArg.class)) {
                            NumberArg arg = p.getAnnotation(NumberArg.class);
                            boolean round = p.getType() == Integer.class || p.getType() == Long.class;
                            if (arg.minValue() != Double.MIN_VALUE) {
                                builder.append(round ? (int) arg.minValue() : arg.minValue());
                                builder.append("-");
                                builder.append(arg.maxValue() != Double.MAX_VALUE ? (round ? (int) arg.maxValue() : arg.maxValue()) : "?");
                            } else {
                                if (arg.maxValue() != Double.MAX_VALUE) {
                                    builder.append("?-");
                                    builder.append(round ? (int) arg.maxValue() : arg.maxValue());
                                } else {
                                    builder.append("#");
                                }
                            }
                        } else if (p.isAnnotationPresent(EnumArg.class)) {
                            builder.append(p.getType().getSimpleName().toLowerCase());
                        } else if (p.isAnnotationPresent(OptionArg.class)) {
                            builder.append(p.getAnnotation(OptionArg.class).listName());
                        } else {
                            builder.append("?");
                        }
                    }
                    builder.append(optional ? "]" : ">");
                }
                builder.append(" ");
            }

            component.addExtra(builder.toString());

            component.addExtra(ChatColor.DARK_GRAY + "-> ");

            component.addExtra(methodInfo.description);

            usage(sender, component);
        }

        usage(sender, ChatColor.DARK_GRAY + "" + ChatColor.STRIKETHROUGH + "" + ChatColor.BOLD + "------------------------");
    }
    
    protected void setContainer(String container) {
        this.container = container;
    }
    
    public String getContainer() {
        return container;
    }

    protected void setOptions(String name, Function<PriorInfo, Set<String>> options) {
        optionsMap.put(name, options);
    }
    
    protected TreeSet<String> getOptions(String name, CorePlayer cp, List<String> args) {
        return Sets.newTreeSet(optionsMap.get(name).apply(new PriorInfo(cp, args)));
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
    
    private static class Boundary {
        double min = 0, max = -1;
    }
    
    private Boundary getBounds(String str) {
        Boundary b = new Boundary();
        
        if (str.contains("..")) {
            if (str.startsWith("..")) {
                b.max = Double.parseDouble(str.substring(2));
            } else if (str.startsWith("..", str.length() - 2)) {
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
        if ((!cpa.allowOffline() && !cp.isOnline()) ||
                (!cpa.allowCrossServer() && cp.getOnlineState() == DBPlayer.OnlineState.OTHER) ||
                (!cpa.allowSelf() && sender != null && sender.equals(cp))) {
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
    
    private List<Entity> getEntitiesByArgs(CommandSender sender, Location loc, List<Entity> entities, String selectorArgs) {
        if (selectorArgs.equals("")) return entities;
        
        Vector vec = loc.toVector();
        Vector dvec = vec.clone();
        
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
                        if (!e.getWorld().equals(loc.getWorld())
                                || (bound.max >= 0
                                && e.getLocation().distance(loc) > bound.max)
                                || e.getLocation().distance(loc) < bound.min) {
                            it.remove();
                        }
                    }
                } else if (strEquals(argument, "x", "y", "z")) {
                    vec = modifyLocation(vec, argument, value);
                } else if (strEquals(argument, "dx", "dy", "dz")) {
                    dvec = modifyDLocation(dvec, argument, value);
                    
                    if (strEquals(argument, "dz")) {
                        Iterator<Entity> it = entitySet.iterator();
                        while (it.hasNext()) {
                            Entity e = it.next();
                            if (!e.getBoundingBox().overlaps(new BoundingBox(vec.getX(),
                                    vec.getY(),
                                    vec.getZ(),
                                    vec.getX() + dvec.getX(),
                                    vec.getY() + dvec.getY(),
                                    vec.getZ() + dvec.getZ()))) {
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
            entityDistancedList.add(new EntityDistanced(e, (int) vec.distance(e.getLocation().toVector())));
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
        CraftBlockCommandSender cbcs;
        CommandListenerWrapper listener = null;
        Location loc;
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

            for (Map.Entry<Integer, List<MethodInfo>> methodEntry : executeMap.entrySet()) {
                if (args.length < methodEntry.getKey()) continue;
                for (MethodInfo methodInfo : methodEntry.getValue()) {
                    if (args.length > methodInfo.maxParams && !methodInfo.varParam) continue;
                    List<Object> params = new ArrayList<>();
                    List<String> strParams = new ArrayList<>();

                    if (cp != null && methodInfo.cpSender) {
                        if (!cp.getRank().hasPermission(methodInfo.minRank)) {
                            boolean hasPerms = false;
                            for (CoreRank aRank : methodInfo.additionalRanks) {
                                if (cp.getRank().equals(aRank)) {
                                    hasPerms = true;
                                    break;
                                }
                            }
                            if (!hasPerms) continue;
                        }
                        params.add(cp);
                    } else {
                        params.add(cs);
                    }

                    int paramSize = Math.min(methodInfo.maxParams, args.length) + 1;

                    List<Object> objList;
                    Object obj;

                    boolean invalidArg = false;

                    int ai = 0, pi = 1;
                    boolean useVarArg = args.length > methodInfo.maxParams;
                    for (; pi < paramSize && !invalidArg; pi++) {
                        // Check for "vararg"
                        String varArgStr = "";
                        if (pi == paramSize - 1 && useVarArg) {
                            for (; ai < args.length; ai++) {
                                varArgStr = varArgStr.concat(args[ai]);
                                if (ai < args.length - 1) varArgStr += " ";
                            }
                        }

                        Parameter param = methodInfo.parameters[pi];
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
                                            for (CorePlayer cp2 : Core.getInstance().getPlayers().getAllHere()) {
                                                if (obj == null
                                                        || (cp2.getLocation().getWorld().equals(loc.getWorld())
                                                        && cp2.getLocation().distance(loc) < ((CorePlayer) obj).getLocation().distance(loc))) {
                                                    obj = cp2;
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
                                        obj = Bukkit.getOnlinePlayers().toArray()[random.nextInt(Bukkit.getOnlinePlayers().size())];
                                        params.add(Core.getInstance().getPlayers().get((Player) obj));
                                        break;
                                    case 'a': // All online players
                                        if (!paramClass.equals(List.class)) {
                                            invalidArg = true;
                                            break;
                                        }
                                        entities = new ArrayList<>();
                                        for (CorePlayer cp1 : Core.getInstance().getPlayers().getAllHere()) {
                                            entities.add(cp1.getPlayer());
                                        }
                                        entities = getEntitiesByArgs(cs, loc, entities, arg.substring(1));
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
                                        entities = getEntitiesByArgs(cs, loc, entities, arg.substring(1));
                                        params.add(entities);
                                        break;
                                    case 's': // The entity executing this command
                                        if (cp != null &&
                                                paramClass.equals(CorePlayer.class)) {
                                            params.add(cp);
                                        } else if (cs instanceof BlockCommandSender &&
                                                paramClass.equals(BlockCommandSender.class)) {
                                            params.add(cs);
                                        } else if (paramClass.equals(CorePlayer.class)) {
                                            params.add(cs);
                                        } else {
                                            invalidArg = true;
                                            break;
                                        }
                                        break;
                                    default:
                                        break;
                                }
                                strParams.add(arg);
                                ai++;
                                continue;
                            }
                            if (paramClass.equals(TpCoord.class) &&
                                    (obj = TpCoord.create(arg)) != null) {
                                params.add(obj);
                                strParams.add(arg);
                                ai++;
                                continue;
                            }
                            if (paramClass.equals(CorePlayer.class) &&
                                    (obj = Core.getInstance().getPlayers().getOffline(arg)) != null) {
                                invalidArg = !isValidCorePlayer(cs, cp, (CorePlayer) obj, param.getAnnotation(CorePlayerArg.class));
                                params.add(obj);
                                strParams.add(arg);
                                ai++;
                                continue;
                            }
                            if (paramClass.equals(Player.class) &&
                                    (obj = Bukkit.getPlayer(arg)) != null) {
                                params.add(obj);
                                strParams.add(arg);
                                ai++;
                                continue;
                            }
                            if (paramClass.equals(OfflinePlayer.class)) {
                                obj = Bukkit.getOfflinePlayer(arg);
                                params.add(obj);
                                strParams.add(arg);
                                ai++;
                                continue;
                            }
                            if (paramClass.equals(Integer.class) &&
                                    (obj = toInt(arg)) != null) {
                                invalidArg = !isNumInbounds(cs, ((Integer) obj).doubleValue(), param.getAnnotation(NumberArg.class));
                                params.add(obj);
                                strParams.add(arg);
                                ai++;
                                continue;
                            }
                            if (paramClass.equals(Double.class)
                                    && (obj = toDouble(arg)) != null) {
                                invalidArg = !isNumInbounds(cs, (Double) obj, param.getAnnotation(NumberArg.class));
                                params.add(obj);
                                strParams.add(arg);
                                ai++;
                                continue;
                            }
                            if (paramClass.equals(Boolean.class)) {
                                if ("true".startsWith(arg.toLowerCase())) {
                                    params.add(true);
                                    strParams.add("true");
                                } else if ("false".startsWith(arg.toLowerCase())) {
                                    params.add(false);
                                    strParams.add("false");
                                } else {
                                    invalidArg = true;
                                }
                                ai++;
                                continue;
                            }
                            if (Enum.class.isAssignableFrom(paramClass)) {
                                invalidArg = true;
                                Enum possibleMatch = null;
                                Enum match = null;
                                for (Enum o : ((Class<? extends Enum>) param.getType()).getEnumConstants()) {
                                    String enumStr = o.name();
                                    if (enumStr.equalsIgnoreCase(arg)) {
                                        match = o;
                                        invalidArg = false;
                                        break;
                                    } else if (possibleMatch == null && (enumStr.toLowerCase().startsWith(arg.toLowerCase()))) {
                                        possibleMatch = o;
                                    }
                                }
                                if (invalidArg) {
                                    if (possibleMatch != null) {
                                        match = possibleMatch;
                                        invalidArg = false;
                                    } else {
                                        continue;
                                    }
                                }
                                params.add(match);
                                strParams.add(arg);
                                ai++;
                                continue;
                            }
                        }
                        if (paramClass.equals(String.class)) {
                            if (param.isAnnotationPresent(LiteralArg.class)) {
                                if (!param.getAnnotation(LiteralArg.class).value().toLowerCase().startsWith(arg.toLowerCase())) {
                                    invalidArg = true;
                                    continue;
                                }
                            } else if (param.isAnnotationPresent(OptionArg.class)) {
                                if (param.getAnnotation(OptionArg.class).force()) {
                                    invalidArg = true;
                                    String possibleMatch = null;
                                    Set<String> options2 = this.getOptions(param.getAnnotation(OptionArg.class).listName(), cp, strParams);
                                    for (String o : options2) {
                                        if (o.equalsIgnoreCase(arg)
                                                || o.contains(":") && o.split(":")[1].equalsIgnoreCase(arg)) {
                                            arg = o;
                                            invalidArg = false;
                                            break;
                                        } else if (possibleMatch == null
                                                && (o.toLowerCase().startsWith(arg.toLowerCase())
                                                || o.contains(":") && o.split(":")[1].toLowerCase().startsWith(arg.toLowerCase()))) {
                                            possibleMatch = o;
                                        }
                                    }
                                    if (invalidArg) {
                                        if (possibleMatch != null) {
                                            arg = possibleMatch;
                                            invalidArg = false;
                                        } else {
                                            continue;
                                        }
                                    }
                                }
                            }
                            params.add(arg);
                            strParams.add(arg);
                            ai++;
                            continue;
                        }
                        invalidArg = true;
                    }
                    for (int r = paramSize; r < methodInfo.parameters.length; r++) {
                        if (methodInfo.parameters[r].isAnnotationPresent(Nullable.class)) {
                            params.add(null);
                        } else {
                            invalidArg = false;
                            break;
                        }
                    }
                    if (!invalidArg) {
                        try {
                            if (boolean.class.isAssignableFrom(methodInfo.method.getReturnType())
                                    && bcs != null) {
                                success = (Boolean) methodInfo.method.invoke(this, params.toArray(new Object[0]));
                                if (success) {
                                    listener.a(3);
                                } else {
                                    listener.sendFailureMessage(IChatBaseComponent.ChatSerializer.a("Failure"));
                                }
                                return success;
                            } else {
                                if (methodInfo.confirmation) {
                                    Chat.sendRequest(cp,
                                            "cmd:" + methodInfo.method.getName(),
                                            (r, s) -> {
                                                try {
                                                    methodInfo.method.invoke(this, params.toArray(new Object[0]));
                                                } catch (IllegalAccessException | InvocationTargetException ex) {
                                                    CoreLogger.logError(null, ex);
                                                }
                                            },
                                            "Are you sure you want to run this command?");
                                } else {
                                    methodInfo.method.invoke(this, params.toArray(new Object[0]));
                                }
                                return true;
                            }
                        } catch (SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                            //error(cp, "Command error");
                            CoreLogger.logError(null, ex);
                        }
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

        for (Map.Entry<Integer, List<MethodInfo>> methodEntry : tabMap.entrySet()) {
            for (MethodInfo methodInfo : methodEntry.getValue()) {
                if (args.length > methodInfo.maxParams) continue;
                if (cp != null && methodInfo.cpSender) {
                    if (!cp.getRank().hasPermission(methodInfo.minRank)) {
                        boolean hasPerms = false;
                        for (CoreRank aRank : methodInfo.additionalRanks) {
                            if (cp.getRank().equals(aRank)) {
                                hasPerms = true;
                                break;
                            }
                        }
                        if (!hasPerms) continue;
                    }
                }

                Object obj;
                int paramSize = Math.min(methodInfo.maxParams, args.length) + 1;
                boolean invalidArg = false;
                List<String> strParams = new ArrayList<>();

                int ai = 0, pi = 1;
                for (; pi < args.length && !invalidArg; pi++) {
                    // Check for "vararg"

                    Parameter param = methodInfo.parameters[pi];
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

                        switch (arg.charAt(0)) {
                            case 'p':
                            case 'r': // One player
                                if (!paramClass.equals(CorePlayer.class)) {
                                    invalidArg = true;
                                    break;
                                }
                                break;
                            case 'a':
                            case 'e': // Lots of entities
                                if (!paramClass.equals(List.class)) {
                                    invalidArg = true;
                                    break;
                                }
                                break;
                            case 's':
                            case 'S': // The entity executing this command
                                if (!paramClass.equals(CorePlayer.class) &&
                                        !(cs instanceof BlockCommandSender &&
                                                paramClass.equals(BlockCommandSender.class))) {
                                    invalidArg = true;
                                    break;
                                }
                                break;
                            default:
                                break;
                        }
                        strParams.add(arg);
                        ai++;
                        continue;
                    }
                    if (paramClass.equals(TpCoord.class)) {
                        invalidArg = (TpCoord.create(arg) == null);
                        strParams.add(arg);
                        ai++;
                        continue;
                    }
                    if (paramClass.equals(CorePlayer.class)) {
                        invalidArg = ((obj = Core.getInstance().getPlayers().getOffline(arg)) == null);
                        if (!invalidArg) {
                            if (isValidCorePlayer(cs, cp, (CorePlayer) obj, param.getAnnotation(CorePlayerArg.class))) {
                                invalidArg = false;
                            } else {
                                invalidArg = true;
                            }
                        }
                        strParams.add(arg);
                        ai++;
                        continue;
                    }
                    if (paramClass.equals(Player.class)) {
                        invalidArg = (Bukkit.getPlayer(arg) == null);
                        strParams.add(arg);
                        ai++;
                        continue;
                    }
                    if (paramClass.equals(OfflinePlayer.class)) {
                        strParams.add(arg);
                        ai++;
                        continue;
                    }
                    if (paramClass.equals(Integer.class)) {
                        invalidArg = ((obj = toInt(arg)) == null);
                        if (!invalidArg) {
                            invalidArg = !isNumInbounds(cs, ((Integer) obj).doubleValue(), param.getAnnotation(NumberArg.class));
                        }
                        strParams.add(arg);
                        ai++;
                        continue;
                    }
                    if (paramClass.equals(Double.class)) {
                        invalidArg = ((obj = toDouble(arg)) == null);
                        if (!invalidArg)
                            invalidArg = !isNumInbounds(cs, (Double) obj, param.getAnnotation(NumberArg.class));
                        strParams.add(arg);
                        ai++;
                        continue;
                    }
                    if (paramClass.equals(Boolean.class)) {
                        if ("true".startsWith(arg.toLowerCase())) {
                            strParams.add("true");
                        } else if ("false".startsWith(arg.toLowerCase())) {
                            strParams.add("false");
                        } else {
                            invalidArg = true;
                        }
                        ai++;
                        continue;
                    }
                    if (param.isAnnotationPresent(EnumArg.class)) {
                        invalidArg = true;
                        String possibleMatch = null;
                        for (Enum o : ((Class<? extends Enum>) param.getType()).getEnumConstants()) {
                            String enumStr = o.name();
                            if (enumStr.equalsIgnoreCase(arg)) {
                                arg = enumStr;
                                invalidArg = false;
                                break;
                            } else if (possibleMatch == null && (enumStr.toLowerCase().startsWith(arg.toLowerCase()))) {
                                possibleMatch = enumStr;
                            }
                        }
                        if (invalidArg) {
                            if (possibleMatch != null) {
                                arg = possibleMatch;
                                invalidArg = false;
                            } else {
                                continue;
                            }
                        }
                        strParams.add(arg);
                        ai++;
                    }
                    if (paramClass.equals(String.class)) {
                        if (param.isAnnotationPresent(LiteralArg.class)) {
                            if (!param.getAnnotation(LiteralArg.class).value().toLowerCase().startsWith(arg.toLowerCase())) {
                                invalidArg = true;
                                continue;
                            }
                        } else if (param.isAnnotationPresent(OptionArg.class)) {
                            if (param.getAnnotation(OptionArg.class).force()) {
                                invalidArg = true;
                                TreeSet<String> optionSet = getOptions(param.getAnnotation(OptionArg.class).listName(), cp, strParams);
                                String possibleMatch = null;
                                for (String o : optionSet) {
                                    if (/*o.toUpperCase().startsWith(arg.toUpperCase()) || */o.equalsIgnoreCase(arg)) {
                                        invalidArg = false;
                                        break;
                                    } else if (possibleMatch == null
                                            && (o.toLowerCase().startsWith(arg.toLowerCase())
                                            || o.contains(":") && o.split(":")[1].toLowerCase().startsWith(arg.toLowerCase()))) {
                                        possibleMatch = o;
                                    }
                                }
                                if (invalidArg) {
                                    if (possibleMatch != null) {
                                        strParams.add(possibleMatch);
                                        invalidArg = false;
                                        ai++;
                                    }
                                    continue;
                                }
                            }
                        }
                        strParams.add(arg);
                        ai++;
                        continue;
                    }
                    invalidArg = true;
                }
                if (!invalidArg) {
                    String lastArg = args[args.length - 1];
                    Parameter currParam = methodInfo.parameters[args.length];
                    if (currParam.isAnnotationPresent(HelperArg.class) &&
                            lastArg.isEmpty()) {
                        boolean optional = currParam.isAnnotationPresent(Nullable.class);
                        options.add((optional ? "[" : "<") + currParam.getAnnotation(HelperArg.class).value() + (optional ? "]" : ">"));
                    }
                    if (currParam.getType().equals(String.class)) {
                        if (currParam.isAnnotationPresent(LiteralArg.class)) {
                            String literal = currParam.getAnnotation(LiteralArg.class).value();
                            addOption(options, literal, lastArg);
                        } else if (currParam.isAnnotationPresent(OptionArg.class)) {
                            for (String option : getOptions(currParam.getAnnotation(OptionArg.class).listName(), cp, strParams)) {
                                addOption(options, option, lastArg);
                            }
                        }
                    } else if (currParam.getType().equals(CorePlayer.class) ||
                            currParam.getType().equals(Player.class) ||
                            currParam.getType().equals(OfflinePlayer.class)) {
                        CorePlayerArg cpa = currParam.getAnnotation(CorePlayerArg.class);
                        Collection<CorePlayer> cpCollection;
                        if (cpa == null || !cpa.allowCrossServer()) {
                            cpCollection = Core.getInstance().getPlayers().getAllHere();
                        } else {
                            cpCollection = Core.getInstance().getPlayers().getAll();
                        }
                        for (CorePlayer cp2 : cpCollection) {
                            if (cp != null && cpa != null && !cpa.allowSelf()) {
                                if (!cp.getName().equalsIgnoreCase(cp2.getName())) {
                                    addOption(options, cp2.getName(), lastArg);
                                }
                            } else {
                                addOption(options, cp2.getName(), lastArg);
                            }
                        }
                        if (cpa == null || cpa.allowSelf()) {
                            addOption(options, "@p", lastArg);
                            addOption(options, "@s", lastArg);
                        }
                    } else if (currParam.getType().equals(List.class)) {
                        addOption(options, "@a", lastArg);
                        addOption(options, "@e", lastArg);
                    } else if (currParam.isAnnotationPresent(EnumArg.class)) {
                        for (Enum o : ((Class<? extends Enum>) currParam.getType()).getEnumConstants()) {
                            String enumStr = o.name();
                            addOption(options, enumStr, lastArg);
                        }
                    } else if (currParam.getType().equals(Boolean.class)) {
                        addOption(options, "true", lastArg);
                        addOption(options, "false", lastArg);
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
    protected void success(CorePlayer cp, TextComponent msg) {
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
    protected void usage(CorePlayer cp, TextComponent msg) {
        Core.getInstance().sendMessage(cp, msg);
    }
}
