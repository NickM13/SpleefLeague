package com.spleefleague.splegg.game;

import com.spleefleague.core.game.battle.Battle;
import com.spleefleague.core.game.battle.BattlePlayer;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.world.game.projectile.ProjectileStats;
import com.spleefleague.splegg.Splegg;
import joptsimple.internal.Strings;
import org.bukkit.ChatColor;
import org.bukkit.potion.PotionEffectType;

/**
 * @author NickM13
 * @since 4/25/2020
 */
public class SpleggBattlePlayer extends BattlePlayer {
    
    private int knockouts;
    private int knockoutStreak;
    private final SpleggGun[] spleggGun = new SpleggGun[2];
    private final double[] cooldown = new double[2];
    private final double[] charge = {-1D, -1D};
    private int lastTick = -1;
    private final boolean[] fireOffCd = new boolean[2];

    public SpleggBattlePlayer(CorePlayer cp, Battle<?> battle, SpleggGun gun1, SpleggGun gun2) {
        super(cp, battle);
        this.knockouts = 0;
        this.knockoutStreak = 0;
        if (gun1 == null) {
            this.spleggGun[0] = SpleggGun.getRandom(gun2);
            Splegg.getInstance().sendMessage(cp, "Random Primary Splegg Gun selected: " + spleggGun[0].getDisplayName());
        } else {
            this.spleggGun[0] = gun1;
        }
        if (gun2 == null || gun2.equals(this.spleggGun[0])) {
            this.spleggGun[1] = SpleggGun.getRandom(this.spleggGun[0]);
            Splegg.getInstance().sendMessage(cp, "Random Secondary Splegg Gun selected: " + spleggGun[1].getDisplayName());
        } else {
            this.spleggGun[1] = gun2;
        }
    }

    public SpleggGun getGun1() {
        return spleggGun[0];
    }

    public SpleggGun getGun2() {
        return spleggGun[1];
    }
    
    @Override
    public void respawn() {
        super.respawn();
        knockoutStreak = 0;
    }

    @Override
    public void onSlotChange(int newSlot) {
        getBattle().getGameWorld().stopFutureShots(getCorePlayer());
        charge[0] = charge[1] = -1;
        getPlayer().removePotionEffect(PotionEffectType.SLOW);
    }

    @Override
    public void onDropItem() {
        if (getPlayer().getInventory().getHeldItemSlot() == 0) {
            getPlayer().getInventory().setHeldItemSlot(1);
        } else {
            getPlayer().getInventory().setHeldItemSlot(0);
        }
        getBattle().getGameWorld().stopFutureShots(getCorePlayer());
    }

    private final double GLOBAL_BUFFER = 0.25D;

    private void shoot(int gun) {
        if (spleggGun[gun] == null) {
            Thread.dumpStack();
            return;
        }
        if (cooldown[gun] <= getBattle().getRoundTime()) {
            if (spleggGun[gun].getProjectileStats().fireSystem == ProjectileStats.FireSystem.DEFAULT) {
                cooldown[gun] = getBattle().getRoundTime() + spleggGun[gun].getProjectileStats().fireCooldown / 20.;
                getBattle().getGameWorld().shootProjectile(getCorePlayer(), spleggGun[gun].getProjectileStats());
            } else if (spleggGun[gun].getProjectileStats().fireSystem == ProjectileStats.FireSystem.CHARGE) {
                if (charge[gun] >= 0) {
                    double chargePercent = Math.min(1, Math.floor(5 * (getBattle().getRoundTime() - charge[gun]) / (spleggGun[gun].getProjectileStats().chargeTime / 20.)) / 5.);
                    if (chargePercent >= 0.2) {
                        cooldown[gun] = getBattle().getRoundTime() + spleggGun[gun].getProjectileStats().fireCooldown / 20.;
                        getPlayer().removePotionEffect(PotionEffectType.SLOW);
                        getBattle().getGameWorld().shootProjectileCharged(getCorePlayer(),
                                spleggGun[gun].getProjectileStats(),
                                chargePercent);
                        charge[gun] = -1;
                    }
                } else {
                    charge[gun] = getBattle().getRoundTime();
                }
            }
        } else if (cooldown[gun] <= getBattle().getRoundTime() + GLOBAL_BUFFER) {
            fireOffCd[gun] = true;
            return;
        }
        fireOffCd[gun] = false;
    }

    @Override
    public void onRightClick() {
        if (getBattle().isRoundStarted() && !isFallen()) {
            shoot(getPlayer().getInventory().getHeldItemSlot());
        }
    }

    private static final int HOTBAR_LEN = 5;
    private static final char[] CD_ANIM = {'─', '═', '╪', '▓', '█'};

    private static String getHotbarString(double percent) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = HOTBAR_LEN * CD_ANIM.length - 1; i >= 0; i-=CD_ANIM.length) {
            int max = (int) ((percent % 1) * HOTBAR_LEN * CD_ANIM.length) + 1;
            if (i >= max) {
                stringBuilder.append(CD_ANIM[Math.max(0, Math.min(CD_ANIM.length - 1, i - max))]);
            } else {
                stringBuilder.append("  ");
            }
        }
        return stringBuilder.toString();
    }

    private static final String CD_OUTER = ChatColor.GRAY + "" + ChatColor.BOLD + "" + ChatColor.ITALIC;
    private static final String FIRST_COLOR = ChatColor.RED + "" + ChatColor.BOLD + "" + ChatColor.ITALIC;
    private static final String SECOND_COLOR = ChatColor.BLUE + "" + ChatColor.BOLD + "" + ChatColor.ITALIC;

    public String getHotbarString() {
        StringBuilder stringBuilder = new StringBuilder();
        if (spleggGun[0] != null) {
            stringBuilder.append(CD_OUTER).append(" [");
            double percent1;
            if (spleggGun[0].getProjectileStats().fireSystem == ProjectileStats.FireSystem.DEFAULT) {
                percent1 = Math.max(0, (cooldown[0] - getBattle().getRoundTime()) / (spleggGun[0].getProjectileStats().fireCooldown / 20.));
                if (percent1 > 0) {
                    stringBuilder.append(getHotbarString(percent1));
                } else {
                    stringBuilder.append(FIRST_COLOR).append(Strings.repeat('█', HOTBAR_LEN));
                }
            } else {
                percent1 = Math.max(0, (cooldown[0] - getBattle().getRoundTime()) / (spleggGun[0].getProjectileStats().fireCooldown / 20.));
                if (percent1 <= 0) {
                    if (charge[0] < 0) stringBuilder.append(Strings.repeat(' ', HOTBAR_LEN * 2));
                    else {
                        percent1 = 1.f - Math.min(1, (getBattle().getRoundTime() - charge[0]) / (spleggGun[0].getProjectileStats().chargeTime / 20.));
                        stringBuilder.append(FIRST_COLOR).append(percent1 > 0 ? getHotbarString(percent1) : Strings.repeat('█', HOTBAR_LEN));
                    }
                } else {
                    stringBuilder.append(getHotbarString(1. - percent1));
                }
            }
            stringBuilder.append(CD_OUTER).append("] ");
        }
        if (spleggGun[1] != null) {
            stringBuilder.append(CD_OUTER).append(" [");
            double percent2;
            if (spleggGun[1].getProjectileStats().fireSystem == ProjectileStats.FireSystem.DEFAULT) {
                percent2 = Math.max(0, (cooldown[1] - getBattle().getRoundTime()) / (spleggGun[1].getProjectileStats().fireCooldown / 20.));
                if (percent2 > 0) {
                    stringBuilder.append(getHotbarString(percent2));
                } else {
                    stringBuilder.append(SECOND_COLOR).append(Strings.repeat('█', HOTBAR_LEN));
                }
            } else {
                percent2 = Math.max(0, (cooldown[1] - getBattle().getRoundTime()) / (spleggGun[1].getProjectileStats().fireCooldown / 20.));
                if (percent2 <= 0) {
                    if (charge[1] < 0) stringBuilder.append(Strings.repeat(' ', HOTBAR_LEN * 2));
                    else {
                        percent2 = 1.f - Math.min(1, (getBattle().getRoundTime() - charge[1]) / (spleggGun[1].getProjectileStats().chargeTime / 20.));
                        stringBuilder.append(SECOND_COLOR).append(percent2 > 0 ? getHotbarString(percent2) : Strings.repeat('█', HOTBAR_LEN));
                    }
                } else {
                    stringBuilder.append(getHotbarString(1. - percent2));
                }
            }
            stringBuilder.append(CD_OUTER).append("] ");
        }
        return stringBuilder.toString();
    }

    @SuppressWarnings("unused")
    public int getKnockouts() {
        return knockouts;
    }

    @SuppressWarnings("unused")
    public void addKnockouts(int knockouts) {
        this.knockouts += knockouts;
        knockoutStreak += knockouts;
    }

    @SuppressWarnings("unused")
    public int getKnockoutStreak() {
        return knockoutStreak;
    }

    public void updateAbilities() {
        getCorePlayer().sendHotbarText(getHotbarString());
        int higherCharge = (int) (Math.min(1, Math.max(
                charge[0] >= 0 ? (getBattle().getRoundTime() - charge[0]) / (spleggGun[0].getProjectileStats().chargeTime / 20.) : -1,
                charge[1] >= 0 ? (getBattle().getRoundTime() - charge[1]) / (spleggGun[1].getProjectileStats().chargeTime / 20.) : -1)) * 5) - 1;
        if (higherCharge >= 0) {
            if (lastTick != higherCharge) {
                lastTick = higherCharge;
                if (charge[0] > charge[1]) {
                    if (lastTick == 4) {
                        getBattle().getGameWorld().playSound(getPlayer().getLocation(),
                                spleggGun[0].getProjectileStats().chargedSoundEffect,
                                spleggGun[0].getProjectileStats().chargedSoundVolume.floatValue(),
                                spleggGun[0].getProjectileStats().chargedSoundPitch.floatValue());
                    } else {
                        getBattle().getGameWorld().playSound(getPlayer().getLocation(),
                                spleggGun[0].getProjectileStats().chargingSoundEffect,
                                spleggGun[0].getProjectileStats().chargingSoundVolume.floatValue(),
                                spleggGun[0].getProjectileStats().chargingSoundPitch.floatValue());
                    }
                } else {
                    if (lastTick == 4) {
                        getBattle().getGameWorld().playSound(getPlayer().getLocation(),
                                spleggGun[1].getProjectileStats().chargedSoundEffect,
                                spleggGun[1].getProjectileStats().chargedSoundVolume.floatValue(),
                                spleggGun[1].getProjectileStats().chargedSoundPitch.floatValue());
                    } else {
                        getBattle().getGameWorld().playSound(getPlayer().getLocation(),
                                spleggGun[1].getProjectileStats().chargingSoundEffect,
                                spleggGun[1].getProjectileStats().chargingSoundVolume.floatValue(),
                                spleggGun[1].getProjectileStats().chargingSoundPitch.floatValue());
                    }
                }
            }
            getPlayer().addPotionEffect(PotionEffectType.SLOW.createEffect(10, higherCharge));
        } else {
            lastTick = -1;
        }
        if (fireOffCd[0]) shoot(0);
        if (fireOffCd[1]) shoot(1);
    }

    public void resetAbilities() {
        cooldown[0] = cooldown[1] = 0;
        charge[0] = charge[1] = -1;
        getPlayer().removePotionEffect(PotionEffectType.SLOW);
    }
    
}
