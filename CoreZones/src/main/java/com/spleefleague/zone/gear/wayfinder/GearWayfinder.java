package com.spleefleague.zone.gear.wayfinder;

import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.zone.gear.Gear;

/**
 * @author NickM13
 * @since 3/3/2021
 */
public class GearWayfinder extends Gear {

    public GearWayfinder() {
        super(GearType.WAYFINDER);
    }

    public GearWayfinder(String identifier, String name) {
        super(GearType.WAYFINDER, identifier, name);
    }

    @Override
    protected boolean onActivate(CorePlayer corePlayer) {
        return false;
    }

}
