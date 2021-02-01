package com.spleefleague.proxycore.player;

import com.spleefleague.proxycore.ProxyCore;
import net.md_5.bungee.api.scheduler.ScheduledTask;

/**
 * @author NickM13
 * @since 1/31/2021
 */
public class PlayerUpdateManager {

    public enum Priority {
        LOW,
        HIGH
    }

    private ScheduledTask direct, generic;

    public void init() {

    }

    public void close() {

    }

    public void pushPlayerUpdate(Priority priority) {

    }

}
