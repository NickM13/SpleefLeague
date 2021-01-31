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
    @DBField private UUID target;
    @DBField private UUID punisher;
    @DBField private String reason;
    @DBField private Long time;
    @DBField private Long duration;

    public Infraction() {

    }

    public Infraction(@Nonnull InfractionType type, @Nonnull UUID target, @Nullable UUID punisher, @Nonnull String reason, @Nonnull Long time, @Nonnull Long duration) {
        this.type = type;
        this.target = target;
        this.punisher = punisher;
        this.reason = reason;
        this.time = time;
        this.duration = duration;
    }

    public InfractionType getType() {
        return type;
    }

    public void setType(InfractionType type) {
        this.type = type;
    }

    public UUID getTarget() {
        return target;
    }

    public void setTarget(UUID target) {
        this.target = target;
    }

    public UUID getPunisher() {
        return punisher;
    }

    public void setPunisher(UUID punisher) {
        this.punisher = punisher;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

}
