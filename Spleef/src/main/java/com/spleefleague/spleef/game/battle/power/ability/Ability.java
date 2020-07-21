package com.spleefleague.spleef.game.battle.power.ability;

import com.spleefleague.core.chat.Chat;
import com.spleefleague.spleef.game.battle.power.PowerSpleefPlayer;
import joptsimple.internal.Strings;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.inventory.ItemStack;

/**
 * @author NickM13
 * @since 5/17/2020
 */
public abstract class Ability {

    public enum Type {
        OFFENSIVE(3, Material.NETHER_BRICK, ChatColor.RED + "" + ChatColor.BOLD, Color.fromRGB(255, 63, 63)),
        UTILITY(4, Material.IRON_INGOT, ChatColor.BLUE + "" + ChatColor.BOLD, Color.fromRGB(63, 63, 255)),
        MOBILITY(5, Material.GOLD_INGOT, ChatColor.GREEN + "" + ChatColor.BOLD, Color.fromRGB(127, 255, 127));

        private final int slot;
        private final Material material;
        private final String chatColor;
        private final Color particleColor;
        private final Particle.DustOptions dustSmall, dustMedium, dustBig;

        Type(int slot, Material material, String chatColor, Color particleColor) {
            this.slot = slot;
            this.material = material;
            this.chatColor = chatColor;
            this.particleColor = particleColor;
            this.dustSmall = new Particle.DustOptions(particleColor, 0.75f);
            this.dustMedium = new Particle.DustOptions(particleColor, 1.5f);
            this.dustBig = new Particle.DustOptions(particleColor, 2.5f);
        }

        public int getSlot() {
            return slot;
        }

        public Material getMaterial() {
            return material;
        }

        public String getColor() {
            return chatColor;
        }

        public Color getParticleColor() {
            return particleColor;
        }

        public Particle.DustOptions getDustSmall() {
            return dustSmall;
        }

        public Particle.DustOptions getDustMedium() {
            return dustMedium;
        }

        public Particle.DustOptions getDustBig() {
            return dustBig;
        }

    }

    protected Type type;
    protected String name;
    protected ItemStack displayItem;
    protected int charges;
    protected double cooldown;
    protected double globalCooldown;

    public Ability(Ability.Type type, ItemStack displayItem, int charges, double cooldown, double refreshCooldown) {
        this.name = getClass().getSimpleName();
        this.type = type;
        this.displayItem = displayItem;
        this.charges = charges;
        this.cooldown = cooldown;
        this.globalCooldown = refreshCooldown;
    }

    public Type getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public abstract String getDisplayName();

    public ItemStack getDisplayItem() {
        return displayItem;
    }

    public final String getFullDescription() {
        String cooldownStr = getCooldownString();
        if (cooldownStr.isEmpty()) return getDescription();
        return getDescription() + "\n\n" + cooldownStr;
    }

    public final String getCooldownString() {
        if (charges == 0) {
            return "";
        } else if (charges == 1) {
            return Chat.DESCRIPTION + "Cooldown: " + Chat.STAT + this.cooldown + Chat.DESCRIPTION + " seconds.";
        } else {
            return Chat.DESCRIPTION + "Cooldown: " + Chat.STAT + this.globalCooldown + Chat.DESCRIPTION + " seconds (" + Chat.STAT + this.cooldown + Chat.DESCRIPTION + " seconds/charge)";
        }
    }

    public abstract String getDescription();

    public int getCharges(PowerSpleefPlayer psp) {
        return (int) Math.max(1, Math.min(charges, charges - ((psp.getCooldown(getType()) - psp.getBattle().getRoundTime()) / cooldown)));
    }

    private static final int HOTBAR_LEN = 5;

    /**
     * Returns a percent number of how much is remaining until the value is fully charged
     *
     * @param psp Casting Player
     * @return
     */
    protected double getMissingPercent(PowerSpleefPlayer psp) {
        return Math.max(0, (psp.getCooldown(getType()) - psp.getBattle().getRoundTime()) / cooldown);
    }

    //private static char[] CD_ANIM = {'─', '░', '▒', '▓', '█'};
    private static char[] CD_ANIM = {'─', '═', '╪', '▓', '█'};

    public String getHotbarString(String readyColor, String cdColor, PowerSpleefPlayer psp) {
        double percent = getMissingPercent(psp);
        if (percent > 0) {
            int max;
            boolean isReady = psp.getPlayer().getCooldown(getType().getMaterial()) <= 0;
            if (percent % 1 == 0 && percent > 0) {
                if (isReady) return Strings.repeat('─', HOTBAR_LEN);
                return readyColor + Strings.repeat(' ', HOTBAR_LEN * 2);
            } else {
                max = (int) ((percent % 1) * HOTBAR_LEN * CD_ANIM.length) + 1;
            }
            StringBuilder stringBuilder = new StringBuilder(isReady ? readyColor : cdColor);
            for (int i = HOTBAR_LEN * CD_ANIM.length - 1; i >= 0; i-=CD_ANIM.length) {
                if (i >= max) {
                    stringBuilder.append(CD_ANIM[Math.max(0, Math.min(CD_ANIM.length - 1, i - max))]);
                } else {
                    stringBuilder.append(isReady ? "─" : "  ");
                }
            }
            return stringBuilder.toString();
        } else {
            return readyColor + Strings.repeat('█', HOTBAR_LEN);
        }
    }

    protected void applyCooldown(PowerSpleefPlayer psp) {
        double newCooldown = Math.max(psp.getBattle().getRoundTime(), psp.getCooldown(type)) + cooldown;
        psp.setCooldown(type, newCooldown);
        psp.getPlayer().setCooldown(type.getMaterial(), (int) (20 * Math.max(globalCooldown, newCooldown - (psp.getBattle().getRoundTime()) - (cooldown * (charges - 1)))));
    }

    protected void applyCooldown(PowerSpleefPlayer psp, double cooldown) {
        double newCooldown = Math.max(psp.getBattle().getRoundTime(), psp.getCooldown(type)) + cooldown;
        psp.setCooldown(type, newCooldown);
        psp.getPlayer().setCooldown(type.getMaterial(), (int) (20 * Math.max(globalCooldown, newCooldown - (psp.getBattle().getRoundTime()) - (this.cooldown * (charges - 1)))));
    }

    public boolean isReady(PowerSpleefPlayer psp) {
        return psp.getPlayer().getCooldown(type.getMaterial()) <= 0;
    }

    /**
     * Attempt to use the ability.
     *
     * @param psp Casting Player
     */
    public final void activate(PowerSpleefPlayer psp) {
        if (isReady(psp)) {
            if (onUse(psp)) {
                applyCooldown(psp);
            }
        } else {
            onUseCooling(psp);
        }
    }

    /**
     * This is called when a player uses an ability that isn't on cooldown.
     *
     * @param psp Casting Player
     */
    public abstract boolean onUse(PowerSpleefPlayer psp);

    /**
     * This is called when a player tried to use an ability while it's on cooldown, used for
     * re-activatable abilities.
     *
     * @param psp Casting Player
     */
    protected void onUseCooling(PowerSpleefPlayer psp) { }

    public void onHit(PowerSpleefPlayer psp) { }

    /**
     * This is called when a player breaks a block.
     *
     * @param psp Casting Player
     */
    public void onBlockBreak(PowerSpleefPlayer psp) { }

    /**
     * This is called when a  player starts sneaking
     *
     * @param psp
     */
    public void onStartSneak(PowerSpleefPlayer psp) { }

    /**
     * This is called when a  player starts sneaking
     *
     * @param psp
     */
    public void onStopSneak(PowerSpleefPlayer psp) { }

    /**
     * This is called when a  player starts sneaking
     *
     * @param psp
     */
    public void onPlayerPunch(PowerSpleefPlayer psp, PowerSpleefPlayer target) { }

    /**
     * Called every 0.1 seconds (2 ticks)
     */
    public void update(PowerSpleefPlayer psp) { }

    /**
     * Called at the start of a round
     */
    public abstract void reset(PowerSpleefPlayer psp);

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Ability ability = (Ability) o;

        if (type != ability.type) return false;
        return name.equals(ability.name);
    }

    @Override
    public int hashCode() {
        int result = type.hashCode();
        result = 31 * result + name.hashCode();
        return result;
    }

}
