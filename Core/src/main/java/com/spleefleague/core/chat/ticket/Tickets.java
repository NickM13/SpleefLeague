/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.chat.ticket;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.spleefleague.core.Core;
import com.spleefleague.core.player.CorePlayer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bson.Document;
import org.bukkit.Bukkit;

/**
 * @author NickM13
 */
public class Tickets {

    protected static Map<UUID, Ticket> openTickets = new HashMap<>();
    protected static List<Ticket> allTickets = new ArrayList<>();
    protected static Map<UUID, List<Ticket>> playerTickets = new HashMap<>();
    protected static MongoCollection<Document> ticketCollection;
    protected static Set<Ticket> newTickets;
    
    public static void init() {
        ticketCollection = Core.getInstance().getPluginDB().getCollection("Tickets");
        newTickets = new HashSet<>();
        
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
        
        Bukkit.getScheduler().scheduleSyncRepeatingTask(Core.getInstance(), () -> {
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
        }, 0L, 100L);
    }
    
    public static void save() {
        List<Document> docs = new ArrayList<>();
        for (Ticket ticket : newTickets) {
            docs.add(ticket.save());
        }
        if (!docs.isEmpty()) ticketCollection.insertMany(docs);
        newTickets.clear();
    }
    
    public static void close() {
        save();
    }
    
    protected static void archiveTicket(UUID uuid) {
        Ticket ticket = openTickets.get(uuid);
        if (ticket != null) {
            openTickets.remove(uuid);
        }
    }
    
    protected static void addTicket(Ticket ticket) {
        allTickets.add(ticket);
        if (!playerTickets.containsKey(ticket.getSender())) {
            playerTickets.put(ticket.getSender(), new ArrayList<>());
        }
        playerTickets.get(ticket.getSender()).add(ticket);
        newTickets.add(ticket);
    }
    
    public static void openTicket(CorePlayer cp, String issue) {
        Ticket ticket = new Ticket(allTickets.size(), cp.getUniqueId(), issue);
        openTickets.put(cp.getUniqueId(), ticket);
        addTicket(ticket);
    }
    
    public static void respondTicket(CorePlayer sender, CorePlayer ticketOwner, String message) {
        Ticket ticket = openTickets.get(ticketOwner.getUniqueId());
        if (ticket != null) {
            ticket.sendMessageToSender(sender, message);
        }
    }
    
    public static Ticket getOpenTicket(CorePlayer cp) {
        return openTickets.get(cp.getUniqueId());
    }
    
    public static List<Ticket> getAllTickets(CorePlayer cp) {
        return playerTickets.get(cp.getUniqueId());
    }
    
}
