package com.spleefleague.core.command.commands;

import com.spleefleague.core.Core;
import com.spleefleague.core.command.CoreCommand;
import com.spleefleague.core.command.annotation.*;
import com.spleefleague.core.menu.hotbars.main.FriendsMenu;
import com.spleefleague.core.menu.hotbars.main.friends.FriendPendingMenu;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.rank.CoreRank;

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
        sender.getMenu().setInventoryMenuItem(FriendPendingMenu.getItem());
    }

}
