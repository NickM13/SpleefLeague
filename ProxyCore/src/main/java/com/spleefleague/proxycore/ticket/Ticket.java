package com.spleefleague.proxycore.ticket;

import com.spleefleague.coreapi.chat.Chat;
import com.spleefleague.coreapi.chat.ChatColor;
import com.spleefleague.coreapi.database.annotation.DBField;
import com.spleefleague.coreapi.database.variable.DBEntity;
import com.spleefleague.proxycore.ProxyCore;
import com.spleefleague.proxycore.chat.ChatChannel;
import com.spleefleague.proxycore.player.ProxyCorePlayer;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author NickM13
 * @since 2/18/2021
 */
public class Ticket extends DBEntity {

    @DBField private UUID uuid;
    @DBField private UUID sender;
    @DBField private List<String> messages = new ArrayList<>();

    // Timeout if ticket is not replied to
    protected long timeout;
    // Prevent multiple responses
    protected long responseTimeout;
    protected boolean open;

    public Ticket() {

    }

    public Ticket(UUID uuid, UUID sender, String issue) {
        this.uuid = uuid;
        this.sender = sender;
        this.messages = new ArrayList<>();

        this.responseTimeout = 0;
        this.open = true;

        sendMessageStaff(null, issue);
    }

    public void setOpen(boolean state) {
        open = state;
    }

    public UUID getSender() {
        return sender;
    }

    public ProxyCorePlayer getSenderPlayer() {
        return ProxyCore.getInstance().getPlayers().getOffline(sender);
    }

    public void resetTimeout() {
        // Timeout timer set to current time plus 5 minutes
        timeout = System.currentTimeMillis() + 1000 * 60 * 5;
    }

    public void checkTimeout() {
        if (open && System.currentTimeMillis() > timeout) {
            open = false;
            ProxyCore.getInstance().sendMessage(getSenderPlayer(), Chat.TICKET_PREFIX + "[Ticket]" + Chat.TICKET_ISSUE + " Your ticket has timed out.");
        }
    }

    protected TextComponent formatSender(String issue) {
        TextComponent msg = new TextComponent(Chat.TICKET_PREFIX + "[Ticket: " + ChatColor.YELLOW + getSenderPlayer().getName() + Chat.TICKET_PREFIX + "] ");

        msg.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/ticketreply " + ProxyCore.getInstance().getPlayers().getOffline(sender).getName() + " "));
        msg.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder().append("Click to respond").create()));
        msg.addExtra(Chat.TICKET_ISSUE + issue);

        return msg;
    }

    // Ticket sender sees this one
    protected TextComponent formatStaff1(ProxyCorePlayer player, String issue) {
        TextComponent msg = new TextComponent();

        msg.addExtra(Chat.TICKET_PREFIX + "[Ticket");
        if (player != null) {
            msg.addExtra(Chat.TICKET_PREFIX + ":");
            msg.addExtra(player.getChatName());
        }
        msg.addExtra(Chat.TICKET_PREFIX + "] ");
        msg.addExtra(Chat.TICKET_ISSUE + issue);

        return msg;
    }

    // Staff sees this one
    protected TextComponent formatStaff2(ProxyCorePlayer player, String issue) {
        TextComponent msg = new TextComponent();

        msg.addExtra(Chat.TICKET_PREFIX + "[Ticket: ");
        msg.addExtra(getSenderPlayer().getChatName());
        if (player != null) {
            msg.addExtra(Chat.TICKET_PREFIX + ":");
            msg.addExtra(player.getChatName());
        }
        msg.addExtra(Chat.TICKET_PREFIX + "] ");
        msg.addExtra(Chat.TICKET_ISSUE + issue);

        return msg;
    }

    public void sendResponses(ProxyCorePlayer cp) {
        for (String msg : messages) {
            ProxyCore.getInstance().sendMessage(cp, msg);
        }
    }

    public void sendMessageStaff(UUID staff, String msg) {
        ProxyCorePlayer pcp = ProxyCore.getInstance().getPlayers().get(staff);
        if (responseTimeout < System.currentTimeMillis() || pcp == null) {
            TextComponent formatted = formatStaff2(pcp, msg);
            ProxyCore.getInstance().sendMessage(getSenderPlayer(), formatted);
            ProxyCore.getInstance().sendMessage(ChatChannel.TICKET, formatted);
            responseTimeout = System.currentTimeMillis() + 10000;
            resetTimeout();
            messages.add(formatted.toPlainText());
        } else {
            ProxyCore.getInstance().sendMessage(pcp, "Try again in " + (int) ((responseTimeout - System.currentTimeMillis()) / 1000) + " seconds");
        }
    }

    public void sendMessageSender(String msg) {
        if (responseTimeout < System.currentTimeMillis()) {
            TextComponent formatted = formatSender(msg);
            resetTimeout();
            messages.add(formatted.toPlainText());
            ProxyCore.getInstance().sendMessage(getSenderPlayer(), formatted);
            ProxyCore.getInstance().sendMessage(ChatChannel.TICKET, formatted);
        } else {
            ProxyCore.getInstance().sendMessage(getSenderPlayer(), "Try again in " + (int) ((responseTimeout - System.currentTimeMillis()) / 1000) + " seconds");
        }
    }

    public boolean isOpen() {
        return open;
    }

    public void close(ProxyCorePlayer staff) {
        if (!open) return;
        open = false;
        ProxyCore.getInstance().sendMessage(getSenderPlayer(), Chat.TICKET_PREFIX + "[Ticket]" + Chat.TICKET_ISSUE + " Your ticket has been closed.");

        TextComponent text = new TextComponent(Chat.TICKET_PREFIX + "[Ticket: ");
        text.addExtra(getSenderPlayer().getChatName());
        text.addExtra(Chat.TICKET_PREFIX + "]" + Chat.TICKET_ISSUE + " Ticket closed.");
    }

}
