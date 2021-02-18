package com.spleefleague.proxycore.player;

import com.google.common.collect.Lists;
import com.spleefleague.coreapi.database.annotation.DBField;
import com.spleefleague.coreapi.database.variable.DBPlayer;
import com.spleefleague.coreapi.player.PlayerStatistics;
import com.spleefleague.coreapi.player.collectibles.PlayerCollectibles;
import com.spleefleague.coreapi.player.options.PlayerOptions;
import com.spleefleague.coreapi.player.purse.PlayerPurse;
import com.spleefleague.coreapi.utils.packet.bungee.player.PacketBungeePlayerResync;
import com.spleefleague.coreapi.utils.packet.spigot.queue.PacketSpigotQueueJoin;
import com.spleefleague.proxycore.ProxyCore;
import com.spleefleague.proxycore.chat.ChatChannel;
import com.spleefleague.proxycore.droplet.Droplet;
import com.spleefleague.proxycore.game.queue.QueueContainer;
import com.spleefleague.proxycore.party.ProxyParty;
import com.spleefleague.proxycore.player.crates.ProxyPlayerCrates;
import com.spleefleague.proxycore.player.friends.ProxyFriendsList;
import com.spleefleague.proxycore.player.purse.ProxyPlayerPurse;
import com.spleefleague.proxycore.player.ranks.ProxyPermanentRank;
import com.spleefleague.proxycore.player.ranks.ProxyRank;
import com.spleefleague.proxycore.player.ranks.ProxyTempRank;
import com.spleefleague.proxycore.player.ratings.ProxyPlayerRatings;
import com.spleefleague.proxycore.player.statistics.ProxyPlayerStatistics;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author NickM13
 * @since 6/6/2020
 */
public class ProxyCorePlayer extends DBPlayer {

    private Droplet droplet = null;
    private ServerInfo currentServer = null;
    private PacketSpigotQueueJoin lastQueueRequest = null;
    private boolean battling = false;
    private boolean spectating = false;

    @DBField private String nickname = null;
    @DBField private UUID disguise = null;

    @DBField private ProxyPermanentRank permRank = new ProxyPermanentRank();
    @DBField private List<ProxyTempRank> tempRanks = new ArrayList<>();

    @DBField private Boolean vanished = false;

    @DBField private final ProxyPlayerPurse purse = new ProxyPlayerPurse(this);
    @DBField private final PlayerOptions options = new PlayerOptions();
    @DBField private final PlayerCollectibles collectibles = new PlayerCollectibles();

    @DBField private Long lastOnline = -1L;

    @DBField private final ProxyPlayerRatings ratings = new ProxyPlayerRatings(this);
    @DBField private final ProxyPlayerStatistics statistics = new ProxyPlayerStatistics(this);
    @DBField private final ProxyPlayerCrates crates = new ProxyPlayerCrates(this);

    @DBField private final ProxyFriendsList friends = new ProxyFriendsList(this);

    @DBField private ChatChannel chatChannel = ChatChannel.GLOBAL;

    @DBField private UUID currentBattle = null;

    private boolean online = false;

    private long url = 0;

    public ProxyCorePlayer() {

    }

    @Override
    public void newPlayer(UUID uuid, String username) {
        super.newPlayer(uuid, username);
        nickname = username;
    }

    @Override
    public void init() {
        online = true;
        updateTempRanks();
    }

    @Override
    public void initOffline() {
        super.initOffline();
        online = false;
        updateTempRanks();
    }

    @Override
    public void close() {
        lastOnline = System.currentTimeMillis();
    }

    public void updateTempRanks() {
        if (tempRanks.removeIf(proxyTempRank -> System.currentTimeMillis() > proxyTempRank.getExpireTime())) {
            ProxyCore.getInstance().getPlayers().save(this);
            ProxyCore.getInstance().getPacketManager().sendPacket(new PacketBungeePlayerResync(getUniqueId(), Lists.newArrayList(PacketBungeePlayerResync.Field.RANK)));
        }
    }

    public void setPermRank(ProxyRank permRank) {
        this.permRank.setRank(permRank);
        ProxyCore.getInstance().getPlayers().save(this);
        ProxyCore.getInstance().getPacketManager().sendPacket(new PacketBungeePlayerResync(getUniqueId(), Lists.newArrayList(PacketBungeePlayerResync.Field.RANK)));
    }

    public void addTempRank(String rankName, long duration) {
        ProxyRank rank = ProxyCore.getInstance().getRankManager().getRank(rankName);
        if (rank != null) {
            ProxyTempRank tempRank = new ProxyTempRank(rank, duration + System.currentTimeMillis());
            // TODO: Add temp rank message here maybe
            tempRanks.add(tempRank);
            ProxyCore.getInstance().getPlayers().save(this);
            ProxyCore.getInstance().getPacketManager().sendPacket(new PacketBungeePlayerResync(getUniqueId(), Lists.newArrayList(PacketBungeePlayerResync.Field.RANK)));
        }
    }

    public void clearTempRanks() {
        tempRanks.clear();
    }

    public String getNickname() {
        return nickname;
    }

    public UUID getDisguise() {
        return disguise;
    }

    public ProxyPermanentRank getPermRank() {
        return permRank;
    }

    public List<ProxyTempRank> getTempRanks() {
        return tempRanks;
    }

    public Boolean getVanished() {
        return vanished;
    }

    public ProxyPlayerPurse getPurse() {
        return purse;
    }

    public PlayerOptions getOptions() {
        return options;
    }

    public PlayerCollectibles getCollectibles() {
        return collectibles;
    }

    public long getLastOnline() {
        return lastOnline;
    }

    public boolean isOnline() {
        return online;
    }

    public void allowUrl() {
        url = System.currentTimeMillis();
        ProxyCore.getInstance().sendMessage(this, "You may send a URL within the next 30 seconds");
    }

    public void disallowUrl() {
        url = 0;
    }

    public boolean canSendUrl() {
        return getRank().hasPermission(ProxyRank.MODERATOR) || url > System.currentTimeMillis();
    }

    /**
     * @return Name of player as TextComponent to allow for quick /tell
     */
    public TextComponent getChatName() {
        TextComponent text = new TextComponent(getRank().getColor() + nickname);

        text.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/tell " + nickname + " "));

        return text;
    }

    /**
     * @return Name of player as TextComponent to allow for quick /tell
     */
    public TextComponent getChatNamePossessive() {
        TextComponent text = new TextComponent(getRank().getColor() + nickname + "'s");

        text.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/tell " + nickname + " "));

        return text;
    }

    /**
     * @return Name of player as TextComponent to allow for quick /tell
     */
    public TextComponent getChatNameRanked() {
        TextComponent text = new TextComponent(getRank().getChatTag() + getRank().getColor() + nickname);

        text.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/tell " + nickname + " "));

        return text;
    }

    /**
     * Returns highest TempRank, if no TempRanks available then Permanent Rank
     * This allows us to set Admins to Default for a set time
     *
     * @return Highest TempRank, if no TempRanks available then Permanent Rank
     */
    public ProxyRank getRank() {
        if (tempRanks.size() > 0) {
            ProxyTempRank highestRank = null;
            for (ProxyTempRank ctr : tempRanks) {
                if (highestRank == null ||
                        ctr.getRank().getLadder() > highestRank.getRank().getLadder()) {
                    highestRank = ctr;
                }
            }
            if (highestRank != null) {
                return highestRank.getRank();
            }
        }
        return permRank.getRank();
    }

    public void setLastOnline() {
        lastOnline = System.currentTimeMillis();
    }

    public PlayerStatistics getStatistics() {
        return statistics;
    }

    public ProxyPlayerCrates getCrates() {
        return crates;
    }

    public void connect(ServerInfo server) {
        getPlayer().connect(server);
    }

    public void connect(Droplet droplet) {
        getPlayer().connect(droplet.getInfo());
    }

    public void setCurrentServer(ServerInfo currentServer) {
        this.currentServer = currentServer;
    }

    public void setCurrentDroplet(Droplet droplet) {
        this.droplet = droplet;
    }

    public ServerInfo getCurrentServer() {
        return currentServer;
    }

    public Droplet getCurrentDroplet() {
        return droplet;
    }

    public ProxiedPlayer getPlayer() {
        return ProxyCore.getInstance().getProxy().getPlayer(uuid);
    }

    public void setBattling(boolean battling) {
        this.battling = battling;
    }

    public boolean isBattling() {
        return battling;
    }

    public void setSpectating(boolean spectating) {
        this.spectating = spectating;
    }

    public boolean isSpectating() {
        return spectating;
    }

    public void setCurrentBattle(UUID battleUuid) {
        this.currentBattle = battleUuid;
    }

    public void leaveBattle(UUID battleId) {
        if (currentBattle != null && currentBattle.equals(battleId)) {
            currentBattle = null;
            battling = false;
        }
    }

    public UUID getCurrentBattle() {
        return currentBattle;
    }

    public ProxyParty getParty() {
        return ProxyCore.getInstance().getPartyManager().getParty(this);
    }

    public ProxyPlayerRatings getRatings() {
        return ratings;
    }

    public ProxyFriendsList getFriends() {
        return friends;
    }

    public void setLastQueueRequest(PacketSpigotQueueJoin packet) {
        lastQueueRequest = packet;
    }

    public PacketSpigotQueueJoin getLastQueueRequest() {
        return lastQueueRequest;
    }

    public ChatChannel getChatChannel() {
        return chatChannel;
    }

    public void setChatChannel(ChatChannel chatChannel) {
        this.chatChannel = chatChannel;
        ProxyCore.getInstance().sendMessage(this, new TextComponent("Chat Channel set to " + chatChannel.getDisplayName()));
    }

}
