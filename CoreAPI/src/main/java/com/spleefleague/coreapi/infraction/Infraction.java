package com.spleefleague.coreapi.infraction;

import com.spleefleague.coreapi.database.annotation.DBField;
import com.spleefleague.coreapi.database.variable.DBEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;

/**
 * @author NickM13
 */
public class Infraction extends DBEntity {

    @DBField private InfractionType type;
    @DBField private UUID target = null;
    @DBField private String punisher = "";
    @DBField private String reason = "";
    @DBField private Long time = 0L;
    @DBField private Long duration = 0L;

    public Infraction() {

    }

    public Infraction(@Nonnull InfractionType type, @Nonnull UUID target, @Nonnull String punisher, @Nonnull String reason, @Nonnull Long time, @Nonnull Long duration) {
        this.type = type;
        this.target = target;
        this.punisher = punisher;
        this.reason = reason;
        this.time = time;
        this.duration = duration;
    }

    public Infraction(InfractionPV infractionPV) {
        this.type = infractionPV.type;
        this.target = infractionPV.target;
        this.punisher = infractionPV.punisher;
        this.reason = infractionPV.reason;
        this.time = infractionPV.time;
        this.duration = infractionPV.duration;
    }

    public InfractionType getType() {
        return type;
    }

    public Infraction setType(InfractionType type) {
        this.type = type;
        return this;
    }

    public UUID getTarget() {
        return target;
    }

    public Infraction setTarget(UUID target) {
        this.target = target;
        return this;
    }

    public String getPunisher() {
        return punisher;
    }

    public Infraction setPunisher(String punisher) {
        this.punisher = punisher;
        return this;
    }

    public String getReason() {
        return reason;
    }

    public Infraction setReason(String reason) {
        this.reason = reason;
        return this;
    }

    public Long getTime() {
        return time;
    }

    public Infraction setTime(Long time) {
        this.time = time;
        return this;
    }

    public Long getDuration() {
        return duration;
    }

    public Infraction setDuration(Long duration) {
        this.duration = duration;
        return this;
    }

    public Long getRemainingTime() {
        return (time + duration) - System.currentTimeMillis();
    }

}
