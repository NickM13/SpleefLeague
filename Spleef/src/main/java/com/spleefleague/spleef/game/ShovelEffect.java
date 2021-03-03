package com.spleefleague.spleef.game;

import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.world.game.GameUtils;
import com.spleefleague.core.world.global.GlobalWorld;
import com.spleefleague.coreapi.database.annotation.DBField;
import com.spleefleague.coreapi.database.variable.DBEntity;
import com.spleefleague.spleef.game.effects.*;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.util.Vector;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author NickM13
 * @since 2/26/2021
 */
@SuppressWarnings("unused")
public class ShovelEffect extends DBEntity {

    public enum ShovelEffectType {

        NONE,
        FLAME_WHEEL(new Particle.DustOptions(Color.WHITE, 0.5f)),
        FLAME_PHOENIX(new Particle.DustOptions(Color.WHITE, 0.5f)),
        WATER(new Particle.DustOptions(Color.WHITE, 0.5f)),
        AIR_TORNADO(new Particle.DustOptions(Color.WHITE, 0.5f)),
        LIGHTNING(new Particle.DustOptions(Color.WHITE, 0.5f)),
        SNOW_BLIZZARD(new Particle.DustOptions(Color.WHITE, 0.5f));

        public final Particle.DustOptions dustOptions;

        ShovelEffectType() {
            this(new Particle.DustOptions(Color.WHITE, 0.5f));
        }

        ShovelEffectType(Particle.DustOptions dustOptions) {
            this.dustOptions = dustOptions;
        }

    }

    @DBField private ShovelEffectType effectType = ShovelEffectType.LIGHTNING;
    @DBField private Particle particle = Particle.BUBBLE_POP;
    @DBField private Integer tickSpace = 4;
    @DBField private Integer delayIn = 10;
    @DBField private Integer remain = 20;
    @DBField private Double radius = 5D;
    @DBField private Integer count = 10;

    public ShovelEffect() {

    }

    public ShovelEffect setType(ShovelEffectType effectType) {
        this.effectType = effectType;
        return this;
    }

    public ShovelEffectType getType() {
        return effectType;
    }

    public Particle getParticle() {
        return particle;
    }

    public ShovelEffect setParticle(Particle particle) {
        this.particle = particle;
        return this;
    }

    public Integer getTickSpace() {
        return tickSpace;
    }

    public ShovelEffect setTickSpace(Integer tickSpace) {
        this.tickSpace = tickSpace;
        return this;
    }

    public Integer getDelayIn() {
        return delayIn;
    }

    public ShovelEffect setDelayIn(Integer delayIn) {
        this.delayIn = delayIn;
        return this;
    }

    public Integer getRemain() {
        return remain;
    }

    public ShovelEffect setRemain(Integer remain) {
        this.remain = remain;
        return this;
    }

    public Double getRadius() {
        return radius;
    }

    public ShovelEffect setRadius(Double radius) {
        this.radius = radius;
        return this;
    }

    public Integer getCount() {
        return count;
    }

    public ShovelEffect setCount(Integer count) {
        this.count = count;
        return this;
    }

    private void performAirTornado(CorePlayer corePlayer) {
        GlobalWorld globalWorld = corePlayer.getGlobalWorld();
        EffectAirTornado effect = new EffectAirTornado(globalWorld, corePlayer.getLocation());
        globalWorld.addRepeatingTask(() -> {

        }, 60, 1);
    }

    private void performLightning(CorePlayer corePlayer) {
        GlobalWorld globalWorld = corePlayer.getGlobalWorld();
        globalWorld.addRepeatingTask(() -> {
            EffectLightning effect = new EffectLightning(globalWorld, corePlayer.getHand(), corePlayer.getPlayer().getLocation().getDirection(), 40);
            effect.play();
        }, 3, 5);
    }

    private void performFlameWheel(CorePlayer corePlayer) {
        GlobalWorld globalWorld = corePlayer.getGlobalWorld();
        EffectFlameWheel effect = new EffectFlameWheel(globalWorld.getWorld(),
                corePlayer.getHand(),
                1, 0.5);
        AtomicInteger i = new AtomicInteger();
        globalWorld.addRepeatingTask(() -> {
            if (effect.isAlive()) {
                if (effect.forward()) {
                    GameUtils.spawnCircleParticles(globalWorld,
                            effect.getCenter(),
                            effect.getRight(),
                            Particle.FLAME,
                            1D, i.getAndIncrement() * (Math.PI / 16), Math.PI * 2, 8);
                }
            }
        }, 100, 1);
    }

    private void performFlamePhoenix(CorePlayer corePlayer) {
        GlobalWorld globalWorld = corePlayer.getGlobalWorld();
        EffectFlamePhoenix effect = new EffectFlamePhoenix(globalWorld,
                corePlayer.getHand(),
                0.5);
        globalWorld.addRepeatingTask(effect::forward, 100, 1);
    }

    private void performSnowBlizzard(CorePlayer corePlayer) {
        GlobalWorld globalWorld = corePlayer.getGlobalWorld();
        //EffectSnowBlizzard effect = new EffectSnowBlizzard(globalWorld);
        //globalWorld.addRepeatingTask(effect::tick, 100, 1);
    }

    public void activate(CorePlayer corePlayer) {
        if (corePlayer.getPlayer().getCooldown(Material.DIAMOND_SHOVEL) > 0) return;
        switch (effectType) {
            case AIR_TORNADO:
                performAirTornado(corePlayer);
                break;
            case FLAME_WHEEL:
                performFlameWheel(corePlayer);
                break;
            case FLAME_PHOENIX:
                performFlamePhoenix(corePlayer);
                break;
            case LIGHTNING:
                performLightning(corePlayer);
                break;
            case SNOW_BLIZZARD:
                performSnowBlizzard(corePlayer);
                break;
            default:
                return;
        }
        corePlayer.getPlayer().setCooldown(Material.DIAMOND_SHOVEL, 60);
    }

}
