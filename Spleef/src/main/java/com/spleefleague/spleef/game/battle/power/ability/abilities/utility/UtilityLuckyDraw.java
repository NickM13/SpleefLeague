package com.spleefleague.spleef.game.battle.power.ability.abilities.utility;

import com.spleefleague.spleef.Spleef;
import com.spleefleague.spleef.game.battle.power.PowerSpleefPlayer;
import com.spleefleague.spleef.game.battle.power.ability.Abilities;
import com.spleefleague.spleef.game.battle.power.ability.Ability;
import com.spleefleague.spleef.game.battle.power.ability.AbilityStats;
import com.spleefleague.spleef.game.battle.power.ability.abilities.AbilityUtility;
import com.spleefleague.spleef.game.battle.power.ability.abilities.mobility.MobilityAirDash;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * @author NickM13
 * @since 9/21/2020
 */
public class UtilityLuckyDraw extends AbilityUtility {

    public static AbilityStats init() {
        return init(UtilityLuckyDraw.class)
                .setCustomModelData(4)
                .setName("Luck of the Draw")
                .setDescription("Generate a random one time use utility power.")
                .setUsage(15);
    }
    private static final List<AbilityStats> randomAbilities = new ArrayList<>();

    static {
        randomAbilities.add(MobilityAirDash.init());
    }

    private Ability lastAbility = null;

    @Override
    public AbilityStats getStats() {
        if (lastAbility != null)
            return lastAbility.getStats();
        return super.getStats();
    }

    @Override
    public String getName() {
        if (lastAbility != null)
            return lastAbility.getName();
        return super.getName();
    }

    @Override
    public String getDescription() {
        if (lastAbility != null)
            return lastAbility.getDescription();
        return super.getDescription();
    }

    @Override
    public ItemStack getDisplayItem() {
        if (lastAbility != null)
            return lastAbility.getDisplayItem();
        return super.getDisplayItem();
    }

    @Override
    public int getCharges() {
        if (lastAbility != null)
            return lastAbility.getCharges();
        return super.getCharges();
    }

    @Override
    public String getHotbarString(String readyColor, String cdColor) {
        if (lastAbility != null)
            return lastAbility.getHotbarString(readyColor, cdColor);
        return super.getHotbarString(readyColor, cdColor);
    }

    @Override
    public boolean isReady() {
        if (lastAbility != null)
            return lastAbility.isReady();
        return super.isReady();
    }

    @Override
    public void onHit() {
        if (lastAbility != null)
            lastAbility.onHit();
    }

    /**
     * This is called when a player breaks a block.
     */
    @Override
    public void onBlockBreak() {
        if (lastAbility != null)
            lastAbility.onBlockBreak();
    }

    /**
     * This is called when a  player starts sneaking
     */
    @Override
    public void onStartSneak() {
        if (lastAbility != null)
            lastAbility.onStartSneak();
    }

    /**
     * This is called when a  player starts sneaking
     */
    @Override
    public void onStopSneak() {
        if (lastAbility != null)
            lastAbility.onStopSneak();
    }

    /**
     * This is called when a  player starts sneaking
     *
     * @param target Punched Player
     */
    @Override
    public void onPlayerPunch(PowerSpleefPlayer target) {
        if (lastAbility != null)
            lastAbility.onPlayerPunch(target);
    }

    /**
     * Called every 0.1 seconds (2 ticks)
     */
    @Override
    public void update() {
        if (lastAbility != null)
            lastAbility.update();
    }

    /**
     * This is called when a player uses an ability that isn't on cooldown.
     */
    @Override
    public boolean onUse() {
        if (lastAbility == null) {
            lastAbility = Abilities.getRandomAbilityStats().create(getUser());
            Spleef.getInstance().sendMessage(getUser().getCorePlayer(), "Random power selected: " + lastAbility.getName());
            lastAbility.setOnCooldownConsumer((psp) -> {
                lastAbility = null;
                this.applyCooldown();
                getUser().getCorePlayer().refreshHotbar();
            });
            getUser().getCorePlayer().refreshHotbar();
        } else {
            lastAbility.onUse();
            lastAbility.applyCooldown();
        }
        return false;
    }

    /**
     * Called at the start of a round
     */
    @Override
    public void reset() {
        if (lastAbility != null) {
            lastAbility.reset();
            lastAbility = null;
        }
    }

}
