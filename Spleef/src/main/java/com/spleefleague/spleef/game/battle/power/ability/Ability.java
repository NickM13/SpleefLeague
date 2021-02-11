package com.spleefleague.spleef.game.battle.power.ability;

import com.spleefleague.spleef.game.battle.power.PowerSpleefPlayer;
import com.spleefleague.spleef.game.battle.power.training.PowerTrainingBattle;
import com.spleefleague.spleef.game.battle.power.training.PowerTrainingPlayer;
import joptsimple.internal.Strings;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author NickM13
 * @since 5/17/2020
 */
public abstract class Ability {

    public enum Type {
        MOBILITY(5,
                Material.GOLD_INGOT,
                ChatColor.GREEN + "" + ChatColor.BOLD,
                "Mobility",
                "(Place Block)",
                Color.fromRGB(127, 255, 127),
                "Spleef:PowerMobility"),
        OFFENSIVE(3,
                Material.NETHER_BRICK,
                ChatColor.RED + "" + ChatColor.BOLD,
                "Offensive",
                "(Drop Item)",
                Color.fromRGB(255, 63, 63),
                "Spleef:PowerOffensive"),
        UTILITY(4,
                Material.IRON_INGOT,
                ChatColor.BLUE + "" + ChatColor.BOLD,
                "Utility",
                "(Swap Item)",
                Color.fromRGB(63, 63, 255),
                "Spleef:PowerUtility");

        private final int slot;
        private final Material material;
        private final String chatColor;
        private final String displayName;
        private final String bindName;
        private final Color particleColor;
        private final Particle.DustOptions dustSmall, dustMedium, dustBig;
        private final String optionName;

        Type(int slot, Material material, String chatColor, String displayName, String bindName, Color particleColor, String optionName) {
            this.slot = slot;
            this.material = material;
            this.chatColor = chatColor;
            this.displayName = displayName;
            this.bindName = bindName;
            this.particleColor = particleColor;
            this.dustSmall = new Particle.DustOptions(particleColor, 0.75f);
            this.dustMedium = new Particle.DustOptions(particleColor, 1.5f);
            this.dustBig = new Particle.DustOptions(particleColor, 2.5f);
            this.optionName = optionName;
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

        public String getDisplayName() {
            return displayName;
        }

        public String getBindName() {
            return bindName;
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

        public String getOptionName() {
            return optionName;
        }

    }

    private PowerSpleefPlayer psp;
    private AbilityStats stats;
    private Consumer<PowerSpleefPlayer> onCooldownConsumer;

    public Ability() { }

    public void init(PowerSpleefPlayer psp, AbilityStats stats) {
        this.psp = psp;
        this.stats = stats;
    }

    public PowerSpleefPlayer getUser() {
        return psp;
    }

    public Player getPlayer() {
        return psp.getPlayer();
    }

    public AbilityStats getStats() {
        return stats;
    }

    public String getName() {
        return stats.getName();
    }

    public String getDescription() {
        return stats.getDescription();
    }

    public ItemStack getDisplayItem() {
        return stats.getDisplayItem();
    }

    public void setOnCooldownConsumer(Consumer<PowerSpleefPlayer> consumer) {
        onCooldownConsumer = consumer;
    }

    public int getCharges() {
        return (int) Math.max(1, Math.min(stats.getCharges(), stats.getCharges() - ((psp.getCooldown(stats.getType()) - psp.getBattle().getRoundTime()) / stats.getCooldown())));
    }

    private static final int HOTBAR_LEN = 5;

    /**
     * Returns a percent number of how much is remaining until the value is fully charged
     *
     * @return percent
     */
    protected double getMissingPercent() {
        return Math.max(0, (psp.getCooldown(stats.getType()) - psp.getBattle().getRoundTime()) / stats.getCooldown());
    }

    //private static char[] CD_ANIM = {'─', '░', '▒', '▓', '█'};
    private static char[] CD_ANIM = {'─', '═', '╪', '▓', '█'};

    public String getHotbarString(String readyColor, String cdColor) {
        double percent = getMissingPercent();
        if (percent > 0) {
            int max;
            boolean isReady = psp.getPlayer().getCooldown(stats.getType().getMaterial()) <= 0;
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

    protected boolean isFinishedRandom() {
        return getCharges() <= 1;
    }

    protected void finishRandom() {
        onCooldownConsumer.accept(getUser());
    }

    public void applyCooldown() {
        if (psp instanceof PowerTrainingPlayer && !((PowerTrainingBattle) psp.getBattle()).isCooldownEnabled()) {
            applyCooldown(1);
        } else {
            applyCooldown(stats.getCooldown());
        }
    }

    protected void applyCooldown(double cooldown) {
        double newCooldown = Math.max(psp.getBattle().getRoundTime(), psp.getCooldown(stats.getType())) + cooldown;
        if (onCooldownConsumer != null) {
            if (isFinishedRandom()) {
                finishRandom();
                return;
            }
            psp.setCooldown(Type.UTILITY, newCooldown);
            psp.getPlayer().setCooldown(Type.UTILITY.getMaterial(), (int) (20 * Math.max(stats.getRefresh(), newCooldown - (psp.getBattle().getRoundTime()) - (this.stats.getCooldown() * (stats.getCharges() - 1)))));
        } else {
            psp.setCooldown(stats.getType(), newCooldown);
            psp.getPlayer().setCooldown(stats.getType().getMaterial(), (int) (20 * Math.max(stats.getRefresh(), newCooldown - (psp.getBattle().getRoundTime()) - (this.stats.getCooldown() * (stats.getCharges() - 1)))));
        }
    }

    public boolean isReady() {
        return psp.getPlayer().getCooldown(stats.getType().getMaterial()) <= 0;
    }

    /**
     * Attempt to use the ability.
     */
    public final void activate() {
        if (isReady()) {
            if (onUse()) {
                applyCooldown();
            }
        } else {
            onUseCooling();
        }
    }

    /**
     * This is called when a player uses an ability that isn't on cooldown.
     */
    public abstract boolean onUse();

    /**
     * This is called when a player tried to use an ability while it's on cooldown, used for
     * re-activatable abilities.
     */
    protected void onUseCooling() { }

    public void onHit() { }

    /**
     * This is called when a player breaks a block.
     */
    public void onBlockBreak() { }

    /**
     * This is called when a  player starts sneaking
     */
    public void onStartSneak() { }

    /**
     * This is called when a  player starts sneaking
     */
    public void onStopSneak() { }

    /**
     * This is called when a  player starts sneaking
     */
    public void onPlayerPunch(PowerSpleefPlayer target) { }

    /**
     * Called every 0.1 seconds (2 ticks)
     */
    public void update() {

    }

    /**
     * Called at the start of a round
     */
    public abstract void reset();

}
