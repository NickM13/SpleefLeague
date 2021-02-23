package com.spleefleague.proxycore.game.challenge;

import com.google.common.collect.Lists;
import com.spleefleague.proxycore.ProxyCore;
import com.spleefleague.proxycore.chat.ProxyChat;
import com.spleefleague.proxycore.game.queue.QueueContainerDynamic;
import com.spleefleague.proxycore.party.ProxyParty;
import com.spleefleague.proxycore.player.ProxyCorePlayer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.scheduler.ScheduledTask;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author NickM13
 * @since 2/11/2021
 */
public class ChallengeManager {

    private final Map<UUID, Map<UUID, Challenge>> challengeMap = new HashMap<>();

    private ScheduledTask timeoutTask;

    public void init() {
        timeoutTask = ProxyCore.getInstance().getProxy().getScheduler().schedule(ProxyCore.getInstance(), this::checkTimeouts, 1L, 1L, TimeUnit.SECONDS);
    }

    public void close() {
        timeoutTask.cancel();
    }

    private void checkTimeouts() {
        for (Map.Entry<UUID, Map<UUID, Challenge>> receiver : challengeMap.entrySet()) {
            Iterator<Map.Entry<UUID, Challenge>> it = receiver.getValue().entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<UUID, Challenge> sender = it.next();
                if (sender.getValue().isTimedOut()) {
                    it.remove();

                    ProxyCorePlayer pcpReceiver = ProxyCore.getInstance().getPlayers().get(receiver.getKey());
                    ProxyCorePlayer pcpSender = ProxyCore.getInstance().getPlayers().get(sender.getKey());
                    TextComponent component = new TextComponent();
                    component.addExtra("Challenge from ");
                    component.addExtra(pcpSender.getChatName());
                    component.addExtra(" has expired.");
                    ProxyCore.getInstance().sendMessage(pcpReceiver, component);
                }
            }
        }
    }

    public void onChallenge(UUID sender, UUID receiver, String mode, String query) {
        ProxyCorePlayer pcpSender = ProxyCore.getInstance().getPlayers().get(sender);
        ProxyCorePlayer pcpReceiver = ProxyCore.getInstance().getPlayers().get(receiver);

        QueueContainerDynamic queue = ProxyCore.getInstance().getQueueManager().getQueue(mode);

        if (pcpSender == null || pcpReceiver == null) {
            return;
        }

        ProxyParty senderParty = pcpSender.getParty();
        ProxyParty receiverParty = pcpReceiver.getParty();

        TextComponent component;
        if (senderParty == null && receiverParty == null) {
            if (!ProxyCore.getInstance().getQueueManager().isSizeValid(1, mode, query)) {
                component = new TextComponent();
                component.addExtra("You are not in a party");
                ProxyCore.getInstance().sendMessageError(pcpReceiver, component);
                return;
            }
            if (!challengeMap.containsKey(receiver)) {
                challengeMap.put(receiver, new HashMap<>());
            }
            challengeMap.get(receiver).putIfAbsent(sender, new ChallengeSolo(mode, query));
            String arenaText = ProxyCore.getInstance().getQueueManager().formatArenaQuery(mode, query);

            component = new TextComponent();
            component.addExtra(pcpSender.getChatName());
            component.addExtra(" has challenged you to ");
            component.addExtra(queue.getDisplayName() + arenaText);
            ProxyCore.getInstance().sendMessage(pcpReceiver, component);

            ProxyChat.sendConfirmationButtons(pcpReceiver, "/challenge accept " + pcpSender.getName(),  "/challenge decline " + pcpSender.getName());

            component = new TextComponent();
            component.addExtra("You have challenged ");
            component.addExtra(pcpReceiver.getChatName());
            component.addExtra(" to ");
            component.addExtra(queue.getDisplayName() + arenaText);
            ProxyCore.getInstance().sendMessage(pcpSender, component);
        } else if (senderParty != null && receiverParty != null) {
            if (senderParty == receiverParty) {
                component = new TextComponent();
                component.addExtra("You are in the same party");
                ProxyCore.getInstance().sendMessageError(pcpSender, component);
            }
            if (!senderParty.getOwner().equals(sender)) {
                component = new TextComponent();
                component.addExtra("You are not the party leader");
                ProxyCore.getInstance().sendMessageError(pcpReceiver, component);
                return;
            }
            if (senderParty.getPlayerCount() != receiverParty.getPlayerCount()) {
                component = new TextComponent();
                component.addExtra("Party sizes are not the same");
                ProxyCore.getInstance().sendMessageError(pcpReceiver, component);
                return;
            }
            if (!ProxyCore.getInstance().getQueueManager().isSizeValid(senderParty.getPlayerCount(), mode, query)) {
                component = new TextComponent();
                component.addExtra("You are in a party");
                ProxyCore.getInstance().sendMessageError(pcpReceiver, component);
                return;
            }
            receiver = receiverParty.getOwner();
            pcpReceiver = ProxyCore.getInstance().getPlayers().get(receiver);
            if (pcpReceiver == null) {
                return;
            }
            if (!challengeMap.containsKey(receiver)) {
                challengeMap.put(receiver, new HashMap<>());
            }
            challengeMap.get(receiver).put(sender, new ChallengeParty(mode, query, senderParty.getPlayerCount()));
            String arenaText = ProxyCore.getInstance().getQueueManager().formatArenaQuery(mode, query);

            component = new TextComponent();
            component.addExtra(pcpSender.getChatName());
            component.addExtra(" has challenged your party to ");
            component.addExtra(queue.getDisplayName() + arenaText);
            ProxyCore.getInstance().sendMessage(pcpReceiver, component);

            ProxyChat.sendConfirmationButtons(pcpReceiver, "/challenge accept " + pcpSender.getName(),  "/challenge decline " + pcpSender.getName());

            component = new TextComponent();
            component.addExtra("You have challenged ");
            component.addExtra(pcpReceiver.getChatNamePossessive());
            component.addExtra(" party to ");
            component.addExtra(queue.getDisplayName() + arenaText);
            ProxyCore.getInstance().sendMessage(pcpSender, component);
        } else {
            component = new TextComponent();
            if (senderParty == null) {
                component.addExtra("They are in a party");
            } else {
                component.addExtra("They are not in a party");
            }
            ProxyCore.getInstance().sendMessageError(pcpSender, component);
        }
    }

    public void onAccept(UUID sender, UUID receiver) {
        ProxyCorePlayer pcpSender = ProxyCore.getInstance().getPlayers().get(sender);
        ProxyCorePlayer pcpReceiver = ProxyCore.getInstance().getPlayers().getOffline(receiver);
        if (!pcpReceiver.isOnline()) {
            TextComponent component = new TextComponent();
            component.addExtra(pcpReceiver.getChatName());
            component.addExtra(" is not online");
            ProxyCore.getInstance().sendMessageError(pcpSender, component);
            return;
        }
        if (challengeMap.containsKey(sender) &&
                challengeMap.get(sender).containsKey(receiver)) {
            Challenge challenge = challengeMap.get(sender).get(receiver);
            ProxyParty partySender = pcpSender.getParty();
            ProxyParty partyReceiver = pcpReceiver.getParty();
            TextComponent component;
            if (challenge instanceof ChallengeParty) {
                if (partySender == null) {
                    component = new TextComponent();
                    component.addExtra("You are not in a party");
                    ProxyCore.getInstance().sendMessageError(pcpSender, component);
                } else if (!partySender.getOwner().equals(sender)) {
                    component = new TextComponent();
                    component.addExtra("You are not party leader");
                    ProxyCore.getInstance().sendMessageError(pcpSender, component);
                } else if (partyReceiver == null) {
                    component = new TextComponent();
                    component.addExtra(pcpReceiver.getChatName());
                    component.addExtra(" is not in a party");
                    ProxyCore.getInstance().sendMessageError(pcpSender, component);
                } else if (!partyReceiver.getOwner().equals(receiver)) {
                    component = new TextComponent();
                    component.addExtra(pcpReceiver.getChatName());
                    component.addExtra(" is not party leader");
                    ProxyCore.getInstance().sendMessageError(pcpSender, component);
                } else {
                    if (partySender.getPlayerCount() != ((ChallengeParty) challenge).getSize() || partyReceiver.getPlayerCount() != ((ChallengeParty) challenge).getSize()) {
                        component = new TextComponent();
                        component.addExtra("Challenge is no longer valid");
                        ProxyCore.getInstance().sendMessageError(pcpSender, component);
                        challengeMap.get(sender).remove(receiver);
                    } else if (!partySender.isQueueReady()) {
                        component = new TextComponent();
                        component.addExtra("Someone in your party is not ready");
                        ProxyCore.getInstance().sendMessageError(pcpSender, component);
                    } else if (!partyReceiver.isQueueReady()) {
                        component = new TextComponent();
                        component.addExtra("Someone in ");
                        component.addExtra(pcpReceiver.getChatNamePossessive());
                        component.addExtra(" party is not ready");
                        ProxyCore.getInstance().sendMessageError(pcpSender, component);
                    } else {
                        challengeMap.get(sender).remove(receiver);
                        List<UUID> players = new ArrayList<>();
                        players.addAll(partyReceiver.getPlayerList());
                        players.addAll(partySender.getPlayerList());
                        ProxyCore.getInstance().getQueueManager().forceStart(challenge.getMode(), challenge.getQuery(), players);
                    }
                }
            } else if (challenge instanceof ChallengeSolo) {
                if (partySender != null) {
                    component = new TextComponent();
                    component.addExtra("You are in a party");
                    ProxyCore.getInstance().sendMessageError(pcpSender, component);
                } else if (partyReceiver != null) {
                    component = new TextComponent();
                    component.addExtra(pcpReceiver.getChatName());
                    component.addExtra(" is in a party");
                    ProxyCore.getInstance().sendMessageError(pcpSender, component);
                } else {
                    if (pcpSender.isBattling()) {
                        ProxyCore.getInstance().sendMessageError(pcpSender, new TextComponent("You are already in a battle"));
                        return;
                    }
                    if (pcpReceiver.isBattling()) {
                        component = new TextComponent();
                        component.addExtra(pcpReceiver.getChatName());
                        component.addExtra(" is already in a battle");
                        ProxyCore.getInstance().sendMessageError(pcpSender, component);
                        return;
                    }

                    challengeMap.get(sender).remove(receiver);
                    ProxyCore.getInstance().getQueueManager().forceStart(challenge.getMode(), challenge.getQuery(), Lists.newArrayList(receiver, sender));
                }
            }
        } else {
            TextComponent component = new TextComponent("You don't have a challenge from ");
            component.addExtra(pcpReceiver.getChatName());
            ProxyCore.getInstance().sendMessageError(pcpSender, component);
        }
    }

    public void onDecline(UUID sender, UUID receiver) {
        ProxyCorePlayer pcpSender = ProxyCore.getInstance().getPlayers().get(sender);
        ProxyCorePlayer pcpReceiver = ProxyCore.getInstance().getPlayers().getOffline(receiver);
        if (challengeMap.containsKey(sender) &&
                challengeMap.get(sender).remove(receiver) != null) {
            TextComponent component;
            component = new TextComponent("You have declined ");
            component.addExtra(pcpReceiver.getChatNamePossessive());
            component.addExtra(" challenge");
            ProxyCore.getInstance().sendMessage(pcpSender, new TextComponent(""));
            if (pcpReceiver.isOnline()) {
                component = new TextComponent();
                component.addExtra(pcpSender.getChatName());
                component.addExtra(" declined your challenge");
                ProxyCore.getInstance().sendMessage(pcpReceiver, new TextComponent(""));
            }
        } else {
            TextComponent component = new TextComponent("No challenge request from ");
            component.addExtra(pcpReceiver.getChatName());
            ProxyCore.getInstance().sendMessageError(pcpSender, component);
        }
    }

    public void cancelAllParty(UUID sender) {

    }

}
