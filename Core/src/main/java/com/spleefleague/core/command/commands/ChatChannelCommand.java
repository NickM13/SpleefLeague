/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.command.commands;

import com.spleefleague.core.command.CoreCommand;
import com.spleefleague.core.Core;
import com.spleefleague.core.command.annotation.CommandAnnotation;
import com.spleefleague.core.chat.ChatChannel;
import com.spleefleague.core.chat.ChatChannel.Channel;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.rank.Rank;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import com.spleefleague.core.command.annotation.OptionArg;

/**
 * @author NickM13
 */
public class ChatChannelCommand extends CoreCommand {

    private class QuickChat {
        ChatChannel cc;
        String name;
        String desc;
        
        public QuickChat(ChatChannel cc, String name, String desc) {
            this.cc = cc;
            this.name = name;
            this.desc = desc;
        }
        
        public String createChatDescription() {
            String formatted = desc + ": " + name;
            return formatted;
        }
    }
    private final List<QuickChat> quickChats = new ArrayList<>();
    
    public ChatChannelCommand() {
        super("chatchannels", Rank.DEFAULT);
        this.addAlias("cc");
        newQuickChat(ChatChannel.getChannel(Channel.GLOBAL), "global", "Global Chat");
        newQuickChat(ChatChannel.getChannel(Channel.PARTY), "party", "Party Chat");
        newQuickChat(ChatChannel.getChannel(Channel.VIP), "vip", "VIP Chat");
        newQuickChat(ChatChannel.getChannel(Channel.BUILD), "builder", "Builder Chat");
        newQuickChat(ChatChannel.getChannel(Channel.STAFF), "staff", "Staff Chat");
        newQuickChat(ChatChannel.getChannel(Channel.ADMIN), "admin", "Admin Chat");
        setUsage("/cc <channel> | /chatchannels");
        setDescription("Set your current chat channel");
        setOptions("chatChannels", pi -> getAvailableChatNames(pi));
    }
    
    private Set<String> getAvailableChatNames(PriorInfo pi) {
        Set<String> names = new HashSet<>();
        for (QuickChat qc : quickChats) {
            if (qc.cc.isAvailable(pi.getCorePlayer())) {
                names.add(qc.name);
            }
        }
        return names;
    }
    
    private void newQuickChat(ChatChannel cc, String name, String desc) {
        quickChats.add(new QuickChat(cc, name, desc));
    }
    
    private void printChatPerm(CorePlayer cp, QuickChat qc) {
        if (qc.cc.isAvailable(cp)) {
            Core.getInstance().sendMessage(cp, qc.createChatDescription());
        }
    }
    
    private boolean checkChatPerm(CorePlayer cp, QuickChat qc) {
        return (qc.cc.isAvailable(cp));
    }
    
    @CommandAnnotation
    public void chatchannels(CorePlayer sender) {
        Core.getInstance().sendMessage(sender, "Current Channel: " + sender.getChatChannel().getName());
        Core.getInstance().sendMessage(sender, "Available channels: ");
        for (QuickChat qc : quickChats) {
            printChatPerm(sender, qc);
        }
    }
    
    @CommandAnnotation
    public void chatchannels(CorePlayer sender, @OptionArg(listName="chatChannels") String channel) {
        for (QuickChat qc : quickChats) {
            if (channel.equalsIgnoreCase(qc.name)) {
                if (checkChatPerm(sender, qc)) {
                    sender.setChatChannel(qc.cc);
                    return;
                }
                break;
            }
        }
        error(sender, "Channel not found");
        error(sender, "Find available channels with /cc");
    }

}
