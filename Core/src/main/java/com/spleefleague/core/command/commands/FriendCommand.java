package com.spleefleague.core.command.commands;

import com.spleefleague.core.Core;
import com.spleefleague.core.command.CoreCommand;
import com.spleefleague.core.command.annotation.*;
import com.spleefleague.core.menu.hotbars.main.FriendsMenu;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.rank.CoreRank;
import net.md_5.bungee.api.chat.*;

import java.util.Iterator;
import java.util.UUID;

public class FriendCommand extends CoreCommand {

    public FriendCommand() {
        super("friend", CoreRank.DEFAULT);
        setOptions("friendList", pi -> pi.getCorePlayer().getFriends().getAllNames());
    }

    @CommandAnnotation
    public void friendAdd(CorePlayer sender,
                          @LiteralArg("add") String l,
                          @CorePlayerArg(allowSelf = false, allowCrossServer = true, allowOffline = true) CorePlayer target) {
        if (!target.isOnline()) {
            error(sender, "Cannot add offline players as friends!");
        } else {
            sender.getFriends().sendFriendRequest(target);
        }
    }

    @CommandAnnotation
    public void friendRemove(CorePlayer sender,
                             @LiteralArg("remove") String l,
                             @OptionArg(listName = "friendList") String name) {
        sender.getFriends().sendFriendRemove(Core.getInstance().getPlayers().getOffline(name));
    }

    @CommandAnnotation(hidden = true)
    public void friendDecline(CorePlayer sender,
                             @LiteralArg("decline") String l,
                             String username) {
        sender.getFriends().sendFriendDecline(Core.getInstance().getPlayers().getOffline(username));
    }

    @CommandAnnotation
    public void friendList(CorePlayer sender,
                           @LiteralArg("list") String l) {
        sender.getMenu().setInventoryMenuItem(FriendsMenu.getItem());
    }

    @CommandAnnotation
    public void friendPending(CorePlayer sender,
                              @LiteralArg("requests") String l) {
        if (sender.getFriends().getIncoming().isEmpty()) {
            error(sender, "You have no pending friend requests!");
            return;
        }
        BaseComponent component = new TextComponent("Friend requests - ");
        Iterator<UUID> it = sender.getFriends().getIncoming().iterator();
        while (it.hasNext()) {
            CorePlayer cp = Core.getInstance().getPlayers().getOffline(it.next());
            TextComponent extra = new TextComponent(cp.getDisplayName());
            extra.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/friend add " + cp.getName()));
            extra.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(new TextComponent("Accept " + cp.getDisplayNamePossessive() + " friend request")).create()));
            component.addExtra(extra);
            if (it.hasNext()) {
                component.addExtra(new TextComponent(", "));
            }
        }
        sender.sendMessage(component);
    }

}
