package com.spleefleague.core.logger;

import com.spleefleague.core.Core;

import java.util.logging.Level;

/**
 * @author NickM13
 * @since 4/24/2020
 */
public class CoreLogger {
    
    public static void logError(String msg) {
        Core.getInstance().getLogger().log(Level.SEVERE, msg);
    }
    
    public static void logWarning(String msg) {
        Core.getInstance().getLogger().log(Level.WARNING, msg);
    }
    
    public static void logInfo(String msg) {
        Core.getInstance().getLogger().log(Level.INFO, msg);
    }
    
}
