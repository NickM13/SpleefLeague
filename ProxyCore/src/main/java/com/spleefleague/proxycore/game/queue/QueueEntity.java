package com.spleefleague.proxycore.game.queue;

/**
 * @author NickM13
 * @since 2/7/2021
 */
public abstract class QueueEntity {

    protected int rating;
    protected int ratingDiff;
    long joinTime;
    String query;
    int size;

    public QueueEntity(String query, int rating) {
        if (query == null || query.equals("")) this.query = "*";
        else this.query = query;
        this.rating = rating;
        this.joinTime = System.currentTimeMillis();
    }

    public long getJoinTime() {
        return joinTime;
    }

    public int getRating() {
        return rating;
    }

    // x is millis since join time
    // f(x) = 0.1(x / 1000)^2 + 50
    public void calcRatings() {
        ratingDiff = (int) (0.1 * Math.pow((System.currentTimeMillis() - joinTime) / 1000f, 2) + 50);
    }

    public int getRatingMin() {
        return rating - ratingDiff;
    }

    public int getRatingMax() {
        return rating + ratingDiff;
    }

}
