/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.chat.ticket;

import com.spleefleague.core.Core;
import com.spleefleague.core.annotation.DBField;
import com.spleefleague.core.chat.Chat;
import com.spleefleague.core.chat.ChatChannel;
import com.spleefleague.core.chat.ChatChannel.Channel;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.util.database.DBEntity;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author NickM13
 */
public class Ticket extends DBEntity {

    @DBField
    protected Long ticketId;
    @DBField
    protected UUID sender;
    @DBField
    protected List<String> messages;
    
    // Timeout if ticket is not replied to
    protected long timeout;
    // Prevent multiple responses
    protected long responseTimeout;
    protected boolean open;
    
    public Ticket() {
        this.responseTimeout = 0;
        this.open = false;
    }
    
    public Ticket(long ticketId, UUID sender, String issue) {
        this.ticketId = ticketId;
        this.sender = sender;
        this.messages = new ArrayList<>();
        
        this.responseTimeout = 0;
        this.open = true;
        
        sendMessageToSender(null, issue);
    }
    
    public void setOpen(boolean state) {
        open = state;
    }
    
    public UUID getSender() {
        return sender;
    }
    
    public CorePlayer getSenderPlayer() {
        return Core.getInstance().getPlayers().getOffline(sender);
    }
    
    public void resetTimeout() {
        // Timeout timer set to current time plus 5 minutes
        timeout = System.currentTimeMillis() + 1000 * 60 * 5;
    }
    
    public void checkTimeout() {
        if (open && System.currentTimeMillis() > timeout) {
            open = false;
            Chat.sendMessageToPlayer(getSenderPlayer(), Chat.TICKET_PREFIX + "[Ticket]" + Chat.TICKET_ISSUE + "Your ticket has timed out.");
        }
    }
    
    protected String formatSender(String issue) {
        String msg = "";
        
        msg += Chat.TICKET_PREFIX + "[Ticket: " + getSenderPlayer().getDisplayName() + Chat.TICKET_PREFIX + "] ";
        msg += Chat.TICKET_ISSUE + issue;
        
        return msg;
    }
    
    // Ticket sender sees this one
    protected String formatStaff1(CorePlayer player, String issue) {
        String msg = "";
        
        msg += Chat.TICKET_PREFIX + "[Ticket";
        if (player != null)
            msg += Chat.TICKET_PREFIX + ": " + player.getDisplayName();
        msg += Chat.TICKET_PREFIX + "] ";
        msg += Chat.TICKET_ISSUE + issue;
        
        return msg;
    }
    
    // Staff sees this one
    protected String formatStaff2(CorePlayer player, String issue) {
        String msg = "";
        
        msg += Chat.TICKET_PREFIX + "[Ticket: " + getSenderPlayer().getDisplayName();
        if (player != null)
            msg += Chat.TICKET_PREFIX + ":" + player.getDisplayName();
        msg += Chat.TICKET_PREFIX + "] ";
        msg += Chat.TICKET_ISSUE + issue;
        
        return msg;
    }
    
    public void sendResponses(CorePlayer cp) {
        for (String msg : messages) {
            Chat.sendMessageToPlayer(cp, msg);
        }
    }
    
    public void sendMessageToSender(CorePlayer staff, String msg) {
        if (responseTimeout < System.currentTimeMillis() || staff == null) {
            String formatted = formatStaff1(staff, msg);
            Chat.sendMessageToPlayer(getSenderPlayer(), formatted);
            Chat.sendMessage(ChatChannel.getChannel(Channel.TICKET), formatStaff2(staff, msg));
            // 10 second response timeout
            if (staff != null) responseTimeout = System.currentTimeMillis() + 10000;
            resetTimeout();
            messages.add(formatted);
        } else {
            Chat.sendMessageToPlayer(staff, "Try again in " + (int)((responseTimeout - System.currentTimeMillis()) / 1000) + " seconds");
        }
    }
    
    public void sendMessageToStaff(String msg) {
        if (responseTimeout < System.currentTimeMillis()) {
            String formatted = formatSender(msg);
            Chat.sendMessage(ChatChannel.getChannel(Channel.TICKET), formatted);
            resetTimeout();
            messages.add(formatted);
        } else {
            Chat.sendMessageToPlayer(getSenderPlayer(), "Try again in " + (int)((responseTimeout - System.currentTimeMillis()) / 1000) + " seconds");
        }
    }
    
    public boolean isOpen() {
        return open;
    }
    
    public void close(CorePlayer staff) {
        if (!open) return;
        open = false;
        Chat.sendMessageToPlayer(getSenderPlayer(), Chat.TICKET_PREFIX + "[Ticket]" + Chat.TICKET_ISSUE + " Your ticket has been closed.");
        Chat.sendMessage(ChatChannel.getChannel(Channel.TICKET), Chat.TICKET_PREFIX + "[Ticket: " + getSenderPlayer().getDisplayName() + Chat.TICKET_PREFIX + "]" + Chat.TICKET_ISSUE + " Ticket closed.");
    }
    
}
