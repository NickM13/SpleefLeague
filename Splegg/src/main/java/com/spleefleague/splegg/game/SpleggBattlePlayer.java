package com.spleefleague.splegg.game;

import com.spleefleague.core.game.battle.Battle;
import com.spleefleague.core.game.battle.BattlePlayer;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.util.SoundUtils;
import com.spleefleague.core.world.game.projectile.ProjectileStats;
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
    protected SpleggGun spleggGun1, spleggGun2;
    private double cooldown1 = 0., cooldown2 = 0.;
    private double charge1 = -1, charge2 = -1;
    private int lastTick = -1;

    public SpleggBattlePlayer(CorePlayer cp, Battle<?> battle) {
        super(cp, battle);
        this.knockouts = 0;
        this.knockoutStreak = 0;
    }

    public SpleggGun getGun1() {
        return spleggGun1;
    }

    public SpleggGun getGun2() {
        return spleggGun2;
    }
    
    @Override
    public void respawn() {
        super.respawn();
        knockoutStreak = 0;
    }

    @Override
    public void onSlotChange(int newSlot) {
        getBattle().getGameWorld().stopFutureShots(getCorePlayer());
        charge1 = charge2 = -1;
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

    private void shoot1() {
        if (spleggGun1 == null) return;
        if (spleggGun1.getProjectileStats().fireSystem == ProjectileStats.FireSystem.DEFAULT) {
            if (cooldown1 <= getBattle().getRoundTime()) {
                cooldown1 = getBattle().getRoundTime() + spleggGun1.getProjectileStats().fireCooldown / 20.;
                getBattle().getGameWorld().shootProjectile(getCorePlayer(), spleggGun1.getProjectileStats());
            }
        } else if (spleggGun1.getProjectileStats().fireSystem == ProjectileStats.FireSystem.CHARGE) {
            if (cooldown1 <= getBattle().getRoundTime()) {
                if (charge1 >= 0) {
                    double chargePercent = Math.min(1, Math.floor(5 * (getBattle().getRoundTime() - charge1) / (spleggGun1.getProjectileStats().chargeTime / 20.)) / 5.);
                    if (chargePercent >= 0.2) {
                        cooldown1 = getBattle().getRoundTime() + spleggGun1.getProjectileStats().fireCooldown / 20.;
                        getPlayer().removePotionEffect(PotionEffectType.SLOW);
                        getBattle().getGameWorld().shootProjectileCharged(getCorePlayer(),
                                spleggGun1.getProjectileStats(),
                                chargePercent);
                        charge1 = -1;
                    }
                } else {
                    charge1 = getBattle().getRoundTime();
                }
            }
        }
    }

    private void shoot2() {
        if (spleggGun2 == null) return;
        if (spleggGun2.getProjectileStats().fireSystem == ProjectileStats.FireSystem.DEFAULT) {
            if (cooldown2 <= getBattle().getRoundTime()) {
                cooldown2 = getBattle().getRoundTime() + spleggGun2.getProjectileStats().fireCooldown / 20.;
                getBattle().getGameWorld().shootProjectile(getCorePlayer(), spleggGun2.getProjectileStats());
            }
        } else if (spleggGun2.getProjectileStats().fireSystem == ProjectileStats.FireSystem.CHARGE) {
            if (charge2 >= 0) {
                if (charge2 >= 0.2) {
                    cooldown2 = getBattle().getRoundTime() + spleggGun2.getProjectileStats().fireCooldown / 20.;
                    getPlayer().removePotionEffect(PotionEffectType.SLOW);
                    getBattle().getGameWorld().shootProjectileCharged(getCorePlayer(),
                            spleggGun2.getProjectileStats(),
                            Math.min(1, Math.round(5 * (getBattle().getRoundTime() - charge2) / (spleggGun2.getProjectileStats().chargeTime / 20.)) / 5));
                    charge2 = -1;
                }
            } else {
                charge2 = getBattle().getRoundTime();
            }
        }
    }

    @Override
    public void onRightClick() {
        if (getBattle().isRoundStarted() && !isFallen()) {
            if (getPlayer().getInventory().getHeldItemSlot() == 0) {
                shoot1();
            } else if (getPlayer().getInventory().getHeldItemSlot() == 1) {
                shoot2();
            }
        }
    }

    private static final int HOTBAR_LEN = 5;
    private static char[] CD_ANIM = {'─', '═', '╪', '▓', '█'};

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

    public String getHotbarString() {
        StringBuilder stringBuilder = new StringBuilder();
        if (spleggGun1 != null) {
            stringBuilder.append(CD_OUTER + " [");
            double percent1;
            if (spleggGun1.getProjectileStats().fireSystem == ProjectileStats.FireSystem.DEFAULT) {
                percent1 = Math.max(0, (cooldown1 - getBattle().getRoundTime()) / (spleggGun1.getProjectileStats().fireCooldown / 20.));
                if (percent1 > 0) {
                    stringBuilder.append(getHotbarString(percent1));
                } else {
                    stringBuilder.append(ChatColor.RED + "" + ChatColor.BOLD + "" + ChatColor.ITALIC + Strings.repeat('█', HOTBAR_LEN));
                }
            } else {
                percent1 = Math.max(0, (cooldown1 - getBattle().getRoundTime()) / (spleggGun1.getProjectileStats().fireCooldown / 20.));
                if (percent1 <= 0) {
                    if (charge1 < 0) stringBuilder.append(Strings.repeat(' ', HOTBAR_LEN * 2));
                    else {
                        percent1 = 1.f - Math.min(1, (getBattle().getRoundTime() - charge1) / (spleggGun1.getProjectileStats().chargeTime / 20.));
                        stringBuilder.append(ChatColor.RED + "" + ChatColor.BOLD + "" + ChatColor.ITALIC
                                + (percent1 > 0 ? getHotbarString(percent1) : Strings.repeat('█', HOTBAR_LEN)));
                    }
                } else {
                    stringBuilder.append(getHotbarString(1. - percent1));
                }
            }
            stringBuilder.append(CD_OUTER + "] ");
        }
        if (spleggGun2 != null) {
            stringBuilder.append(CD_OUTER + " [");
            double percent2;
            if (spleggGun2.getProjectileStats().fireSystem == ProjectileStats.FireSystem.DEFAULT) {
                percent2 = Math.max(0, (cooldown2 - getBattle().getRoundTime()) / (spleggGun2.getProjectileStats().fireCooldown / 20.));
                if (percent2 > 0) {
                    stringBuilder.append(getHotbarString(percent2));
                } else {
                    stringBuilder.append(ChatColor.BLUE + "" + ChatColor.BOLD + "" + ChatColor.ITALIC + Strings.repeat('█', HOTBAR_LEN));
                }
            } else {
                percent2 = Math.max(0, (cooldown2 - getBattle().getRoundTime()) / (spleggGun2.getProjectileStats().fireCooldown / 20.));
                if (percent2 <= 0) {
                    if (charge2 < 0) stringBuilder.append(Strings.repeat(' ', HOTBAR_LEN * 2));
                    else {
                        percent2 = 1.f - Math.min(1, (getBattle().getRoundTime() - charge2) / (spleggGun2.getProjectileStats().chargeTime / 20.));
                        stringBuilder.append(ChatColor.RED + "" + ChatColor.BOLD + "" + ChatColor.ITALIC
                                + (percent2 > 0 ? getHotbarString(percent2) : Strings.repeat('█', HOTBAR_LEN)));
                    }
                } else {
                    stringBuilder.append(getHotbarString(1. - percent2));
                }
            }
            stringBuilder.append(CD_OUTER + "] ");
        }
        return stringBuilder.toString();
    }

    public int getKnockouts() {
        return knockouts;
    }
    
    public void addKnockouts(int knockouts) {
        this.knockouts += knockouts;
        knockoutStreak += knockouts;
    }
    
    public int getKnockoutStreak() {
        return knockoutStreak;
    }

    public void updateAbilities() {
        getCorePlayer().sendHotbarText(getHotbarString());
        int higherCharge = (int) (Math.min(1, Math.max(
                charge1 >= 0 ? (getBattle().getRoundTime() - charge1) / (spleggGun1.getProjectileStats().chargeTime / 20.) : -1,
                charge2 >= 0 ? (getBattle().getRoundTime() - charge2) / (spleggGun2.getProjectileStats().chargeTime / 20.) : -1)) * 5) - 1;
        if (higherCharge >= 0) {
            if (lastTick != higherCharge) {
                lastTick = higherCharge;
                if (charge1 > charge2) {
                    if (lastTick == 4) {
                        getBattle().getGameWorld().playSound(getPlayer().getLocation(),
                                spleggGun1.getProjectileStats().chargedSoundEffect,
                                spleggGun1.getProjectileStats().chargedSoundVolume.floatValue(),
                                spleggGun1.getProjectileStats().chargedSoundPitch.floatValue());
                    } else {
                        getBattle().getGameWorld().playSound(getPlayer().getLocation(),
                                spleggGun1.getProjectileStats().chargingSoundEffect,
                                spleggGun1.getProjectileStats().chargingSoundVolume.floatValue(),
                                spleggGun1.getProjectileStats().chargingSoundPitch.floatValue());
                    }
                } else {
                    if (lastTick == 4) {
                        getBattle().getGameWorld().playSound(getPlayer().getLocation(),
                                spleggGun2.getProjectileStats().chargedSoundEffect,
                                spleggGun2.getProjectileStats().chargedSoundVolume.floatValue(),
                                spleggGun2.getProjectileStats().chargedSoundPitch.floatValue());
                    } else {
                        getBattle().getGameWorld().playSound(getPlayer().getLocation(),
                                spleggGun2.getProjectileStats().chargingSoundEffect,
                                spleggGun2.getProjectileStats().chargingSoundVolume.floatValue(),
                                spleggGun2.getProjectileStats().chargingSoundPitch.floatValue());
                    }
                }
            }
            getPlayer().addPotionEffect(PotionEffectType.SLOW.createEffect(10, higherCharge));
        } else {
            lastTick = -1;
        }
    }

    public void resetAbilities() {
        cooldown1 = cooldown2 = 0;
        charge1 = charge2 = -1;
        getPlayer().removePotionEffect(PotionEffectType.SLOW);
    }
    
}
