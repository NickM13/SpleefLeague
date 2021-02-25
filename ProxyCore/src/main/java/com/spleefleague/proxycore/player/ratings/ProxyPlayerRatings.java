package com.spleefleague.proxycore.player.ratings;

import com.google.common.collect.Sets;
import com.spleefleague.coreapi.player.PlayerRatings;
import com.spleefleague.coreapi.player.statistics.Rating;
import com.spleefleague.coreapi.player.statistics.Ratings;
import com.spleefleague.proxycore.ProxyCore;
import com.spleefleague.proxycore.chat.ChatChannel;
import com.spleefleague.proxycore.game.queue.QueueManager;
import com.spleefleague.proxycore.player.ProxyCorePlayer;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;

/**
 * @author NickM13
 * @since 4/28/2020
 */
public class ProxyPlayerRatings extends PlayerRatings {

    protected final ProxyCorePlayer owner;

    public ProxyPlayerRatings(ProxyCorePlayer owner) {
        super();
        this.owner = owner;
    }

    private void updateRating(String mode, String season) {
        Rating rating = modeRatingsMap.get(mode).get(season);
        int result = rating.updateDivision();
        if (result != 0) {
            /*
            if (rating.getDivision() == Rating.Division.MASTER) {
                // Store masters players in another list and when a new master is promoted, drop someone
            }
             */

            TextComponent component = new TextComponent();

            component.setColor(ChatColor.GRAY);
            component.addExtra(owner.getChatName());
            component.addExtra(" has been ");
            component.addExtra(result == 1 ? "promoted" : "demoted");
            component.addExtra(" to ");
            component.addExtra(rating.getDivision().getDisplayName());
            component.addExtra(" in ");
            System.out.println(mode);
            System.out.println(ProxyCore.getInstance().getQueueManager().getQueue(mode));
            component.addExtra(ProxyCore.getInstance().getQueueManager().getQueue(mode).getDisplayName());

            ProxyCore.getInstance().getChat().sendNotificationFriends(Sets.newHashSet(owner.getUniqueId()), component);

            component = new TextComponent();

            component.setColor(ChatColor.GRAY);
            component.addExtra("You have been ");
            component.addExtra(result == 1 ? "promoted" : "demoted");
            component.addExtra(" to ");
            component.addExtra(rating.getDivision().getDisplayName());
            component.addExtra(" in ");
            component.addExtra(ProxyCore.getInstance().getQueueManager().getQueue(mode).getDisplayName());

            ProxyCore.getInstance().sendMessage(owner, component);
        }
    }

    public void setRating(String mode, String season, int elo) {
        super.setRating(mode, season, elo);

        updateRating(mode, season);

        ProxyCore.getInstance().getLeaderboards().get(mode).setPlayerScore(owner.getUniqueId(), owner.getName(), modeRatingsMap.get(mode).get(season));
    }

    /**
     * Add elo to a season's rating, adding a win if the amount is positive and loss if negative
     *
     * @param mode Mode
     * @param season Season
     * @param amt Amount
     */
    @Override
    public int addRating(String mode, String season, int amt) {
        if (!modeRatingsMap.containsKey(mode)) {
            modeRatingsMap.put(mode, new Ratings(mode));
        }

        int elo = modeRatingsMap.get(mode).get(season).addElo(amt);

        updateRating(mode, season);

        ProxyCore.getInstance().getLeaderboards().get(mode).setPlayerScore(owner.getUniqueId(), owner.getName(), modeRatingsMap.get(mode).get(season));

        return elo;
    }

    public int getElo(String mode, String season) {
        return super.getElo(mode, season);
    }

}
