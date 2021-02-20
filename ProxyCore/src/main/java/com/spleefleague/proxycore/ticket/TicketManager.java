package com.spleefleague.proxycore.ticket;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.spleefleague.proxycore.ProxyCore;
import com.spleefleague.proxycore.player.ProxyCorePlayer;
import org.bson.Document;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author NickM13
 * @since 2/18/2021
 */
public class TicketManager {

    protected Map<UUID, Ticket> openTickets = new HashMap<>();
    protected List<Ticket> allTickets = new ArrayList<>();
    protected Map<UUID, List<Ticket>> playerTickets = new HashMap<>();
    protected MongoCollection<Document> ticketCollection;
    protected Set<Ticket> newTickets = new HashSet<>();

    public void init() {
        ticketCollection = ProxyCore.getInstance().getDatabase().getCollection("Tickets");

        MongoCursor<Document> mc = ticketCollection.find().cursor();

        while (mc.hasNext()) {
            Document doc = mc.next();
            Ticket ticket = new Ticket();
            ticket.load(doc);
            allTickets.add(ticket);
            if (!playerTickets.containsKey(ticket.getSender())) {
                playerTickets.put(ticket.getSender(), new ArrayList<>());
            }
            playerTickets.get(ticket.getSender()).add(ticket);
        }

        ProxyCore.getInstance().getProxy().getScheduler().schedule(ProxyCore.getInstance(), () -> {
            Set<UUID> toRemove = new HashSet<>();
            for (Map.Entry<UUID, Ticket> t : openTickets.entrySet()) {
                t.getValue().checkTimeout();
                if (!t.getValue().isOpen()) {
                    toRemove.add(t.getKey());
                }
            }
            for (UUID uuid : toRemove) {
                archiveTicket(uuid);
            }
        }, 0, 5, TimeUnit.SECONDS);
    }

    public void save() {
        List<Document> docs = new ArrayList<>();
        for (Ticket ticket : newTickets) {
            docs.add(ticket.toDocument());
        }
        if (!docs.isEmpty()) ticketCollection.insertMany(docs);
        newTickets.clear();
    }

    public void close() {
        save();
    }

    protected void archiveTicket(UUID uuid) {
        Ticket ticket = openTickets.get(uuid);
        if (ticket != null) {
            openTickets.remove(uuid);
        }
    }

    protected void addTicket(Ticket ticket) {
        allTickets.add(ticket);
        if (!playerTickets.containsKey(ticket.getSender())) {
            playerTickets.put(ticket.getSender(), new ArrayList<>());
        }
        playerTickets.get(ticket.getSender()).add(ticket);
        newTickets.add(ticket);
    }

    public void openTicket(UUID uuid, String issue) {
        if (openTickets.containsKey(uuid)) {
            Ticket ticket = openTickets.get(uuid);
            ticket.sendMessageSender(issue);
        } else {
            Ticket ticket = new Ticket(UUID.randomUUID(), uuid, issue);
            openTickets.put(uuid, ticket);
            addTicket(ticket);
        }
    }

    public void closeTicket(UUID sender, UUID target) {
        if (openTickets.containsKey(target)) {
            openTickets.get(target).close(ProxyCore.getInstance().getPlayers().get(sender));
        }
    }

    public void replyTicket(UUID sender, UUID target, String message) {
        Ticket ticket = openTickets.get(target);
        if (ticket != null) {
            if (sender.equals(target)) {
                ticket.sendMessageSender(message);
            } else {
                ticket.sendMessageStaff(sender, message);
            }
        }
    }

    public Ticket getOpenTicket(ProxyCorePlayer pcp) {
        return openTickets.get(pcp.getUniqueId());
    }

    public List<Ticket> getAllTickets(ProxyCorePlayer pcp) {
        return playerTickets.get(pcp.getUniqueId());
    }

}
