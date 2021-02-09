package com.spleefleague.proxycore.infraction;

import com.mongodb.client.MongoCollection;
import com.spleefleague.coreapi.infraction.Infraction;
import com.spleefleague.coreapi.infraction.InfractionManager;
import com.spleefleague.coreapi.infraction.InfractionType;
import com.spleefleague.coreapi.utils.TimeUtils;
import com.spleefleague.coreapi.utils.packet.bungee.player.PacketBungeePlayerKick;
import com.spleefleague.proxycore.ProxyCore;
import com.spleefleague.proxycore.player.ProxyCorePlayer;
import net.md_5.bungee.api.chat.TextComponent;
import org.bson.Document;

import java.util.UUID;

/**
 * @author NickM13
 */
public class ProxyInfractionManager extends InfractionManager {

    private MongoCollection<Document> infractionCollection;

    public void init() {
        infractionCollection = ProxyCore.getInstance().getDatabase().getCollection("Infractions");
    }

    public void close() {

    }

    public Infraction isBanned(UUID uuid) {
        Document findQuery = new Document("identifier", uuid.toString());
        Document doc = infractionCollection.find(findQuery).first();
        if (doc == null) return null;

        Document latestDoc = doc.get("latest", Document.class);

        if (latestDoc == null) return null;

        Document recentDoc = latestDoc.get(InfractionType.BAN.getLatestId(), Document.class);

        if (recentDoc == null) {
            return null;
        }

        Infraction infraction = new Infraction();
        infraction.load(recentDoc);
        return infraction;
    }

    public void push(Infraction infraction) {
        infraction.setTime(System.currentTimeMillis());
        Document findQuery = new Document("identifier", infraction.getTarget().toString());
        if (infractionCollection.find(findQuery).first() == null) {
            infractionCollection.insertOne(new Document("identifier", infraction.getTarget().toString()));
        }
        Document infractionDoc = new Document("infractions", infraction.toDocument());
        Document updateQuery = new Document("$push", infractionDoc);
        if (infraction.getType().getLatestId() != null) {
            Document latestDoc = new Document("latest." + infraction.getType().getLatestId(), infraction.toDocument());
            updateQuery.append(infraction.getType().getLatestDocOperator(), latestDoc);
        }
        infractionCollection.updateOne(findQuery, updateQuery);

        ProxyCorePlayer pcp = ProxyCore.getInstance().getPlayers().get(infraction.getTarget());
        if (pcp != null) {
            TextComponent kickMessage = new TextComponent();
            switch (infraction.getType()) {
                case WARNING:
                    ProxyCore.getInstance().sendMessage(pcp, "Warning: " + infraction.getReason());
                    return;
                case MUTE_PUBLIC:
                    ProxyCore.getInstance().sendMessage(pcp, "You have been muted: " + infraction.getReason());
                    ProxyCore.getInstance().sendMessage(pcp, "Remaining time: " + TimeUtils.timeToString(infraction.getDuration()));
                    return;
                case MUTE_SECRET:
                case UNBAN:

                    return;
                case BAN:
                    ProxyCore.getInstance().getPacketManager().sendPacket(
                            pcp.getUniqueId(),
                            new PacketBungeePlayerKick(pcp.getUniqueId(), "You have been banned: " + infraction.getReason()));
                    kickMessage.addExtra("You have been banned: ");
                    break;
                case KICK:
                    ProxyCore.getInstance().getPacketManager().sendPacket(
                            pcp.getUniqueId(),
                            new PacketBungeePlayerKick(pcp.getUniqueId(), "You have been kicked: " + infraction.getReason()));
                    kickMessage.addExtra("You have been kicked: ");
                    break;
                case TEMPBAN:
                    ProxyCore.getInstance().getPacketManager().sendPacket(
                            pcp.getUniqueId(),
                            new PacketBungeePlayerKick(pcp.getUniqueId(),
                                    "You have been banned: " +
                                            infraction.getReason() + "\n" +
                                            TimeUtils.timeToString(infraction.getDuration())));
                    kickMessage.addExtra("You have been temp-banned: ");
                    break;
            }
            kickMessage.addExtra(infraction.getReason());
            if (infraction.getDuration() > 0) {
                kickMessage.addExtra("\n" + TimeUtils.timeToString(infraction.getDuration()));
            }
            ProxyCore.getInstance().getPacketManager().sendPacket(
                    pcp.getUniqueId(),
                    new PacketBungeePlayerKick(pcp.getUniqueId(), ""));
            pcp.getPlayer().disconnect(kickMessage);
        }
    }

}
