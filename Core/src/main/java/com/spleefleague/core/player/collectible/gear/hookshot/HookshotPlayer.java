package com.spleefleague.core.player.collectible.gear.hookshot;

import com.spleefleague.core.player.CorePlayer;

import java.util.UUID;

/**
 * @author NickM13
 */
public class HookshotPlayer {

    private final CorePlayer owner;
    private HookshotProjectile projectile;

    public HookshotPlayer(CorePlayer owner) {
        this.owner = owner;
    }

    public CorePlayer getOwner() {
        return owner;
    }

    public void update() {
        if (projectile != null) {
            if (projectile.isHooked()) {

            }
        }
    }

    public void setProjectile(HookshotProjectile projectile) {
        this.projectile = projectile;
        owner.refreshHotbar();
    }

    public HookshotProjectile getProjectile() {
        return projectile;
    }

    public boolean isFired() {
        return projectile != null;
    }

}
