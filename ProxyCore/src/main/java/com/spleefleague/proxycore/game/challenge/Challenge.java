package com.spleefleague.proxycore.game.challenge;

/**
 * @author NickM13
 * @since 2/11/2021
 */
public abstract class Challenge {

    private final String mode;
    private final String query;

    private final long timeout;

    public Challenge(String mode, String query) {
        this.mode = mode;
        this.query = query;
        this.timeout = System.currentTimeMillis() + 30 * 1000L;
    }

    public String getMode() {
        return mode;
    }

    public String getQuery() {
        return query;
    }

    public boolean isTimedOut() {
        return System.currentTimeMillis() > timeout;
    }

}
