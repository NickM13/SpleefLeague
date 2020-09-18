package com.spleefleague.spleef.game.battle.power;

import com.comphenix.protocol.wrappers.BlockPosition;
import com.spleefleague.core.game.battle.Battle;
import com.spleefleague.core.game.battle.BattlePlayer;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.vendor.Vendorable;
import com.spleefleague.core.world.FakeBlock;
import com.spleefleague.core.world.FakeUtils;
import com.spleefleague.core.world.build.BuildStructure;
import com.spleefleague.core.world.build.BuildStructures;
import com.spleefleague.spleef.Spleef;
import com.spleefleague.spleef.game.battle.SpleefBattlePlayer;
import com.spleefleague.spleef.game.battle.power.ability.Abilities;
import com.spleefleague.spleef.game.battle.power.ability.Ability;
import com.spleefleague.spleef.game.battle.power.ability.abilities.AbilityMobility;
import com.spleefleague.spleef.game.battle.power.ability.abilities.AbilityOffensive;
import com.spleefleague.spleef.game.battle.power.ability.abilities.AbilityUtility;
import com.spleefleague.spleef.game.battle.power.ability.abilities.mobility.MobilityEnderRift;
import com.spleefleague.spleef.player.SpleefPlayer;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

/**
 * @author NickM
 * @since 4/15/2020
 */
public class PowerSpleefPlayer extends SpleefBattlePlayer {

    private Map<String, Object> powerValueMap = new HashMap<>();

    private AbilityUtility utility;
    private AbilityOffensive offensive;
    private AbilityMobility mobility;

    // Cooldown is the seconds into the current round that the ability will have all of its charges back.
    private Map<Ability.Type, Double> cooldowns = new HashMap<>();

    public PowerSpleefPlayer(CorePlayer cp, Battle<?> battle) {
        super(cp, battle);
        updatePowers();
    }

    public void updatePowers() {
        SpleefPlayer sp = Spleef.getInstance().getPlayers().get(getCorePlayer());
        chooseOffensive();
        chooseUtility();
        chooseMobililty();
        for (Ability.Type type : Ability.Type.values()) {
            cooldowns.put(type, 0D);
        }
    }

    public void chooseOffensive() {
        SpleefPlayer sp = Spleef.getInstance().getPlayers().get(getCorePlayer());
        if (sp.getActiveOffensive() != null) {
            offensive = sp.getActiveOffensive();
        } else {
            offensive = (AbilityOffensive) Abilities.getAbilityRandom(Ability.Type.OFFENSIVE);
            Spleef.getInstance().sendMessage(getCorePlayer(), "You've been assigned a random &cOffensive &7power: &c" + offensive.getDisplayName());
        }
        offensive.reset(this);
        getPlayer().setCooldown(Ability.Type.OFFENSIVE.getMaterial(), 0);
    }

    public void chooseUtility() {
        SpleefPlayer sp = Spleef.getInstance().getPlayers().get(getCorePlayer());
        if (sp.getActiveUtility() != null) {
            utility = sp.getActiveUtility();
        } else {
            utility = (AbilityUtility) Abilities.getAbilityRandom(Ability.Type.UTILITY);
            Spleef.getInstance().sendMessage(getCorePlayer(), "You've been assigned a random &9Utility &7power: &9" + utility.getDisplayName());
        }
        utility.reset(this);
        getPlayer().setCooldown(Ability.Type.UTILITY.getMaterial(), 0);
    }

    public void chooseMobililty() {
        SpleefPlayer sp = Spleef.getInstance().getPlayers().get(getCorePlayer());
        if (sp.getActiveMobility() != null) {
            mobility = sp.getActiveMobility();
        } else {
            mobility = (AbilityMobility) Abilities.getAbilityRandom(Ability.Type.MOBILITY);
            Spleef.getInstance().sendMessage(getCorePlayer(), "You've been assigned a random &aMobility &7power: &a" + mobility.getDisplayName());
        }
        mobility.reset(this);
        getPlayer().setCooldown(Ability.Type.MOBILITY.getMaterial(), 0);
    }

    @Override
    public void respawn() {
        resetCooldowns();
        BlockPosition pos = new BlockPosition(getSpawn().getBlockX(), getSpawn().getBlockY() + 15, getSpawn().getBlockZ());
        BuildStructure platform = BuildStructures.get("power:respawn");
        Map<BlockPosition, FakeBlock> transformed = FakeUtils.translateBlocks(platform.getFakeBlocks(), pos);
        getBattle().getGameWorld().setBlocks(transformed);
        for (Map.Entry<BlockPosition, FakeBlock> entry : transformed.entrySet()) {
            getBattle().getGameWorld().setBlockDelayed(entry.getKey(), Material.AIR.createBlockData(), 6 * 20);
        }
        Location spawn = pos.toLocation(getPlayer().getWorld()).add(0.5, 0, 0.5);
        spawn.setYaw(getSpawn().getYaw());
        spawn.setPitch(getSpawn().getPitch());
        getPlayer().teleport(spawn);
        knockoutStreak = 0;
        blocksBrokenRound = 0;
    }

    //@Deprecated
    public Map<String, Object> getPowerValueMap() {
        return powerValueMap;
    }

    public <T extends Object> T getPowerValue(Class<T> clazz, String key) {
        return (T) powerValueMap.get(key);
    }

    public <T extends Object> T getPowerValue(Class<T> clazz, String key, T def) {
        return (T) powerValueMap.getOrDefault(key, def);
    }

    public void setPowerValue(String key, Object value) {
        powerValueMap.put(key, value);
    }

    public AbilityUtility getUtility() {
        return utility;
    }

    public String getUtilityName() {
        return utility != null ? utility.getDisplayName() : "";
    }

    public AbilityOffensive getOffensive() {
        return offensive;
    }

    public String getOffensiveName() {
        return offensive != null ? offensive.getDisplayName() : "";
    }

    public AbilityMobility getMobility() {
        return mobility;
    }

    public String getMobilityName() {
        return mobility != null ? mobility.getDisplayName() : "";
    }

    public void resetCooldowns() {
        for (Ability.Type type : Ability.Type.values()) {
            cooldowns.put(type, 0D);
            getPlayer().setCooldown(type.getMaterial(), 40);
        }
        if (utility != null) {
            utility.reset(this);
        }
        if (offensive != null) {
            offensive.reset(this);
        }
        if (mobility != null) {
            mobility.reset(this);
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
        ItemStack newItem = getPlayer().getInventory().getItem(ability.getType().getSlot());
        if (newItem != null) {
            int charges = ability.getCharges(this);
            if (newItem.getAmount() != charges) {
                newItem.setAmount(charges);
                getPlayer().getInventory().setItem(ability.getType().getSlot(), newItem);
            }
        }
        ability.update(this);
    }

    private static final String CD_OUTER = ChatColor.GRAY + "" + ChatColor.BOLD + "" + ChatColor.ITALIC;
    private static final String CD_COLOR = ChatColor.GRAY + "" + ChatColor.BOLD + "" + ChatColor.ITALIC;

    private String toHotbarString(String color, Ability ability) {
        StringBuilder stringBuilder = new StringBuilder(CD_OUTER + "[");
        if (ability != null) {
            stringBuilder.append(ability.getHotbarString(color, CD_COLOR, this));
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
                toHotbarString(ChatColor.RED + "" + ChatColor.BOLD + "" + ChatColor.ITALIC, offensive) + "  " +
                        toHotbarString(ChatColor.BLUE + "" + ChatColor.BOLD + "" + ChatColor.ITALIC, utility) + "  " +
                        toHotbarString(ChatColor.GREEN + "" + ChatColor.BOLD + "" + ChatColor.ITALIC, mobility));
    }

    @Override
    public void onRightClick() {
        if (mobility != null && getBattle().isRoundStarted()) {
            mobility.activate(this);
        }
    }

    @Override
    public void onSwapItem() {
        if (utility != null && getBattle().isRoundStarted()) {
            utility.activate(this);
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
                            offensive.activate(this);
                        }
                        break;
                    case UTILITY:
                        if (utility != null && getBattle().isRoundStarted()) {
                            utility.activate(this);
                        }
                        break;
                    case MOBILITY:
                        if (mobility != null && getBattle().isRoundStarted()) {
                            mobility.activate(this);
                        }
                        break;
                }
            }
        }
    }

    @Override
    public void onDropItem() {
        if (offensive != null && getBattle().isRoundStarted()) {
            offensive.activate(this);
        }
    }

    @Override
    public void onStartSneak() {
        super.onStartSneak();
        if (offensive != null && getBattle().isRoundStarted()) {
            offensive.onStartSneak(this);
        }
        if (utility != null && getBattle().isRoundStarted()) {
            utility.onStartSneak(this);
        }
        if (mobility != null && getBattle().isRoundStarted()) {
            mobility.onStartSneak(this);
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
            offensive.onBlockBreak(this);
        }
        if (utility != null && getBattle().isRoundStarted()) {
            utility.onBlockBreak(this);
        }
        if (mobility != null && getBattle().isRoundStarted()) {
            mobility.onBlockBreak(this);
        }
    }

    @Override
    public void onPlayerPunch(BattlePlayer target) {
        if (offensive != null && getBattle().isRoundStarted()) {
            offensive.onPlayerPunch(this, (PowerSpleefPlayer) target);
        }
        if (utility != null && getBattle().isRoundStarted()) {
            utility.onPlayerPunch(this, (PowerSpleefPlayer) target);
        }
        if (mobility != null && getBattle().isRoundStarted()) {
            mobility.onPlayerPunch(this, (PowerSpleefPlayer) target);
        }
    }

    @Override
    public void onPlayerHit(BattlePlayer attacker) {
        if (offensive != null && getBattle().isRoundStarted()) {
            offensive.onHit(this);
        }
        if (utility != null && getBattle().isRoundStarted()) {
            utility.onHit(this);
        }
        if (mobility != null && getBattle().isRoundStarted()) {
            mobility.onHit(this);
        }
    }

}
