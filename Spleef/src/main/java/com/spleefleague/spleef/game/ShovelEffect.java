package com.spleefleague.spleef.game;

import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.world.global.GlobalWorld;
import com.spleefleague.coreapi.database.annotation.DBField;
import com.spleefleague.coreapi.database.variable.DBEntity;
import org.bukkit.Particle;
import org.bukkit.util.Vector;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author NickM13
 * @since 2/26/2021
 */
@SuppressWarnings("unused")
public class ShovelEffect extends DBEntity {

    public enum ShovelEffectCastType {
        SELF,
        LAUNCH,
        SIGHT
    }

    public enum ShovelEffectDesign {
        BOMB,
        TORNADO,
        SPRINKLER
    }

    @DBField private ShovelEffectCastType castType = ShovelEffectCastType.SELF;
    @DBField private ShovelEffectDesign design = ShovelEffectDesign.BOMB;
    @DBField private Particle particle = Particle.WATER_BUBBLE;
    @DBField private Integer delayIn = 10;
    @DBField private Integer remain = 20;
    @DBField private Double radius = 5D;
    @DBField private Integer count = 10;

    public ShovelEffect() {

    }

    public ShovelEffect setCastType(ShovelEffectCastType castType) {
        this.castType = castType;
        return this;
    }

    public ShovelEffectCastType getCastType() {
        return castType;
    }

    public ShovelEffectDesign getDesign() {
        return design;
    }

    public ShovelEffect setDesign(ShovelEffectDesign design) {
        this.design = design;
        return this;
    }

    public Particle getParticle() {
        return particle;
    }

    public ShovelEffect setParticle(Particle particle) {
        this.particle = particle;
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

    private void performSelf(CorePlayer corePlayer) {
        AtomicInteger i = new AtomicInteger();
        GlobalWorld globalWorld = corePlayer.getGlobalWorld();
        globalWorld.addRepeatingTask(() -> {
            if (i.getAndIncrement() >= delayIn) {
                Vector vec = corePlayer.getLocation().toVector();
                globalWorld.spawnParticles(particle, vec.getX(), vec.getY(), vec.getZ(), count);
            }
        }, remain, 4);
    }

    private void performLaunch(CorePlayer corePlayer) {

    }

    private void performSight(CorePlayer corePlayer) {

    }

    public void activate(CorePlayer corePlayer) {
        switch(castType) {
            case SELF:
                performSelf(corePlayer);
                break;
            case SIGHT:
                performSight(corePlayer);
                break;
            case LAUNCH:
                performLaunch(corePlayer);
                break;
        }
    }

}
