package com.spleefleague.spleef.game.battle.power;

import com.comphenix.protocol.wrappers.BlockPosition;
import com.spleefleague.core.game.battle.Battle;
import com.spleefleague.core.game.battle.BattlePlayer;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.world.FakeBlock;
import com.spleefleague.core.world.FakeUtils;
import com.spleefleague.core.world.FakeWorld;
import com.spleefleague.core.world.build.BuildStructure;
import com.spleefleague.core.world.build.BuildStructures;
import com.spleefleague.spleef.Spleef;
import com.spleefleague.spleef.game.battle.SpleefBattlePlayer;
import com.spleefleague.spleef.game.battle.power.ability.Abilities;
import com.spleefleague.spleef.game.battle.power.ability.Ability;
import com.spleefleague.spleef.game.battle.power.ability.AbilityStats;
import com.spleefleague.spleef.game.battle.power.ability.abilities.AbilityMobility;
import com.spleefleague.spleef.game.battle.power.ability.abilities.AbilityOffensive;
import com.spleefleague.spleef.game.battle.power.ability.abilities.AbilityUtility;
import com.spleefleague.spleef.player.SpleefPlayer;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * @author NickM
 * @since 4/15/2020
 */
public class PowerSpleefPlayer extends SpleefBattlePlayer {

    private AbilityStats utilityStats = null, offensiveStats = null, mobilityStats = null;

    private AbilityUtility utility;
    private AbilityOffensive offensive;
    private AbilityMobility mobility;

    private List<PowerSpleefPlayer> opponents = new ArrayList<>();

    // Cooldown is the seconds into the current round that the ability will have all of its charges back.
    private final Map<Ability.Type, Double> cooldowns = new HashMap<>();

    public PowerSpleefPlayer(CorePlayer cp, Battle<?> battle) {
        super(cp, battle);
        selectPowers();
    }

    public void addOpponent(PowerSpleefPlayer opponent) {
        this.opponents.add(opponent);
    }

    public void setOpponents(PowerSpleefPlayer ... opponents) {
        this.opponents.clear();
        this.opponents.addAll(Arrays.asList(opponents));
    }

    public List<PowerSpleefPlayer> getOpponents() {
        return opponents;
    }

    public void selectPowers() {
        chooseOffensive();
        chooseUtility();
        chooseMobililty();
        for (Ability.Type type : Ability.Type.values()) {
            cooldowns.put(type, 0D);
        }
    }

    public void chooseOffensive() {
        String offensiveOption = getCorePlayer().getOptions().getString(Ability.Type.OFFENSIVE.getOptionName());
        if (!offensiveOption.isEmpty()) {
            offensiveStats = Abilities.getAbility(Ability.Type.OFFENSIVE, offensiveOption);
        }
        if (offensiveStats == null) {
            offensiveStats = Abilities.getRandomAbilityStats(Ability.Type.OFFENSIVE);
            Spleef.getInstance().sendMessage(getCorePlayer(), "You've been assigned a random &cOffensive &7power: &c" + offensiveStats.getName());
        }
        offensive = (AbilityOffensive) offensiveStats.create(this);
        getPlayer().setCooldown(Ability.Type.OFFENSIVE.getMaterial(), 0);
    }

    public void chooseUtility() {
        String utilityOption = getCorePlayer().getOptions().getString(Ability.Type.UTILITY.getOptionName());
        if (!utilityOption.isEmpty()) {
            utilityStats = Abilities.getAbility(Ability.Type.UTILITY, utilityOption);
        }
        if (utilityStats == null) {
            utilityStats = Abilities.getRandomAbilityStats(Ability.Type.UTILITY);
            Spleef.getInstance().sendMessage(getCorePlayer(), "You've been assigned a random &9Utility &7power: &9" + utilityStats.getName());
        }
        utility = (AbilityUtility) utilityStats.create(this);
        getPlayer().setCooldown(Ability.Type.UTILITY.getMaterial(), 0);
    }

    public void chooseMobililty() {
        String mobilityOption = getCorePlayer().getOptions().getString(Ability.Type.MOBILITY.getOptionName());
        if (!mobilityOption.isEmpty()) {
            mobilityStats = Abilities.getAbility(Ability.Type.MOBILITY, mobilityOption);
        }
        if (mobilityStats == null) {
            mobilityStats = Abilities.getRandomAbilityStats(Ability.Type.MOBILITY);
            Spleef.getInstance().sendMessage(getCorePlayer(), "You've been assigned a random &aMobility &7power: &a" + mobilityStats.getName());
        }
        mobility = (AbilityMobility) mobilityStats.create(this);
        getPlayer().setCooldown(Ability.Type.MOBILITY.getMaterial(), 0);
    }

    @Override
    public void respawn() {
        setChanneling(false);
        setFallen(false);
        resetCooldowns();
        BlockPosition pos = new BlockPosition(getSpawn().getBlockX(), getSpawn().getBlockY() + 15, getSpawn().getBlockZ());
        BuildStructure platform = BuildStructures.get("power:respawn");
        Map<BlockPosition, FakeBlock> transformed = FakeUtils.translateBlocks(platform.getFakeBlocks(), pos);
        getBattle().getGameWorld().setBlocks(transformed);
        for (Map.Entry<BlockPosition, FakeBlock> entry : transformed.entrySet()) {
            getBattle().getGameWorld().setBlockDelayed(entry.getKey(), FakeWorld.AIR, 6 * 20);
        }
        Location spawn = pos.toLocation(getPlayer().getWorld()).add(0.5, 0, 0.5);
        spawn.setYaw(getSpawn().getYaw());
        spawn.setPitch(getSpawn().getPitch());
        getPlayer().teleport(spawn);
        knockoutStreak = 0;
        blocksBrokenRound = 0;
    }

    public AbilityStats getAbilityStats(Ability.Type type) {
        switch (type) {
            case OFFENSIVE: return offensiveStats;
            case UTILITY: return utilityStats;
            case MOBILITY: return mobilityStats;
            default: return null;
        }
    }

    public Ability getAbility(Ability.Type type) {
        switch (type) {
            case OFFENSIVE: return offensive;
            case UTILITY: return utility;
            case MOBILITY: return mobility;
            default: return null;
        }
    }

    public AbilityUtility getUtility() {
        return utility;
    }

    public String getUtilityName() {
        return utilityStats != null ? utilityStats.getName() : "";
    }

    public AbilityOffensive getOffensive() {
        return offensive;
    }

    public String getOffensiveName() {
        return offensiveStats != null ? offensiveStats.getName() : "";
    }

    public AbilityMobility getMobility() {
        return mobility;
    }

    public String getMobilityName() {
        return mobilityStats != null ? mobilityStats.getName() : "";
    }

    public void resetCooldowns() {
        for (Ability.Type type : Ability.Type.values()) {
            cooldowns.put(type, 0D);
            getPlayer().setCooldown(type.getMaterial(), 40);
        }
        if (utility != null) {
            utility.reset();
        }
        if (offensive != null) {
            offensive.reset();
        }
        if (mobility != null) {
            mobility.reset();
        }
    }

    public void setCooldown(Ability.Type type, double cooldown) {
        cooldowns.put(type, cooldown);
    }

    public double getCooldown(Ability.Type type) {
        return cooldowns.get(type);
    }

    protected void updateAbilities(Ability.Type type) {
        Ability ability;
        switch (type) {
            case UTILITY:
                ability = utility;
                break;
            case MOBILITY:
                ability = mobility;
                break;
            case OFFENSIVE:
                ability = offensive;
                break;
            default: return;
        }
        if (ability == null) return;
        ItemStack newItem = getPlayer().getInventory().getItem(ability.getStats().getType().getSlot());
        if (newItem != null) {
            int charges = ability.getCharges();
            if (newItem.getAmount() != charges) {
                newItem.setAmount(charges);
                getPlayer().getInventory().setItem(ability.getStats().getType().getSlot(), newItem);
            }
        }
        ability.update();
    }

    private static final String CD_OUTER = ChatColor.GRAY + "" + ChatColor.BOLD + "" + ChatColor.ITALIC;
    private static final String CD_COLOR = ChatColor.GRAY + "" + ChatColor.BOLD + "" + ChatColor.ITALIC;

    private String toHotbarString(String color, Ability ability) {
        StringBuilder stringBuilder = new StringBuilder(CD_OUTER + "[");
        if (ability != null) {
            stringBuilder.append(ability.getHotbarString(color, CD_COLOR));
        } else {
            stringBuilder.append("─────");
        }
        stringBuilder.append(CD_OUTER + "]");
        return stringBuilder.toString();
    }

    public void updateAbilities() {
        updateAbilities(Ability.Type.MOBILITY);
        updateAbilities(Ability.Type.OFFENSIVE);
        updateAbilities(Ability.Type.UTILITY);

        getCorePlayer().sendHotbarText(
                        toHotbarString(ChatColor.RED   + "" + ChatColor.BOLD + "" + ChatColor.ITALIC, offensive) + "  " +
                        toHotbarString(ChatColor.BLUE  + "" + ChatColor.BOLD + "" + ChatColor.ITALIC, utility)   + "  " +
                        toHotbarString(ChatColor.GREEN + "" + ChatColor.BOLD + "" + ChatColor.ITALIC, mobility));
    }

    @Override
    public void onRightClick() {
        if (mobility != null && getBattle().isRoundStarted()) {
            mobility.activate();
        }
    }

    @Override
    public void onSwapItem() {
        if (utility != null && getBattle().isRoundStarted()) {
            utility.activate();
        }
    }

    @Override
    public void onSlotChange(int newSlot) {
        getPlayer().getInventory().setHeldItemSlot(0);
        for (Ability.Type type : Ability.Type.values()) {
            if (type.getSlot() == newSlot) {
                switch (type) {
                    case OFFENSIVE:
                        if (offensive != null && getBattle().isRoundStarted()) {
                            offensive.activate();
                        }
                        break;
                    case UTILITY:
                        if (utility != null && getBattle().isRoundStarted()) {
                            utility.activate();
                        }
                        break;
                    case MOBILITY:
                        if (mobility != null && getBattle().isRoundStarted()) {
                            mobility.activate();
                        }
                        break;
                }
            }
        }
    }

    @Override
    public void onDropItem() {
        if (offensive != null && getBattle().isRoundStarted()) {
            offensive.activate();
        }
    }

    @Override
    public void onStartSneak() {
        super.onStartSneak();
        if (offensive != null && getBattle().isRoundStarted()) {
            offensive.onStartSneak();
        }
        if (utility != null && getBattle().isRoundStarted()) {
            utility.onStartSneak();
        }
        if (mobility != null && getBattle().isRoundStarted()) {
            mobility.onStartSneak();
        }
    }

    @Override
    public void onStopSneak() {
        super.onStopSneak();
    }

    @Override
    public void onBlockBreak() {
        super.onBlockBreak();
        if (offensive != null && getBattle().isRoundStarted()) {
            offensive.onBlockBreak();
        }
        if (utility != null && getBattle().isRoundStarted()) {
            utility.onBlockBreak();
        }
        if (mobility != null && getBattle().isRoundStarted()) {
            mobility.onBlockBreak();
        }
    }

    @Override
    public void onPlayerPunch(BattlePlayer target) {
        if (offensive != null && getBattle().isRoundStarted()) {
            offensive.onPlayerPunch((PowerSpleefPlayer) target);
        }
        if (utility != null && getBattle().isRoundStarted()) {
            utility.onPlayerPunch((PowerSpleefPlayer) target);
        }
        if (mobility != null && getBattle().isRoundStarted()) {
            mobility.onPlayerPunch((PowerSpleefPlayer) target);
        }
    }

    @Override
    public void onPlayerHit(BattlePlayer attacker) {
        if (offensive != null && getBattle().isRoundStarted()) {
            offensive.onHit();
        }
        if (utility != null && getBattle().isRoundStarted()) {
            utility.onHit();
        }
        if (mobility != null && getBattle().isRoundStarted()) {
            mobility.onHit();
        }
    }

}
