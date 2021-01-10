package com.spleefleague.core.command.commands;

import com.spleefleague.core.Core;
import com.spleefleague.core.command.CoreCommand;
import com.spleefleague.core.command.annotation.CommandAnnotation;
import com.spleefleague.core.command.annotation.CorePlayerArg;
import com.spleefleague.core.command.annotation.LiteralArg;
import com.spleefleague.core.command.annotation.OptionArg;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.rank.Rank;
import net.md_5.bungee.api.chat.*;
import org.bukkit.Bukkit;

import java.util.Iterator;
import java.util.UUID;

public class FriendCommand extends CoreCommand {

    public FriendCommand() {
        super("friend", Rank.DEFAULT);
        setOptions("friendList", pi -> pi.getCorePlayer().getFriends().getNames());
    }

    @CommandAnnotation
    public void friendAdd(CorePlayer sender,
                          @LiteralArg("add") String l,
                          @CorePlayerArg(allowSelf = false, allowCrossServer = true) CorePlayer target) {
        sender.getFriends().sendFriendRequest(target);
    }

    @CommandAnnotation
    public void friendList(CorePlayer sender,
                           @LiteralArg("list") String l) {
        for (UUID uuid : sender.getFriends().getAll()) {
            CorePlayer cp = Core.getInstance().getPlayers().getOffline(uuid);
            if (cp.isOnline()) {
                success(sender, cp.getDisplayName() + " - " + "Online");
            } else {
                success(sender, cp.getDisplayName() + " - " + "Offline");
            }
        }
    }

    @CommandAnnotation
    public void friendRemove(CorePlayer sender,
                             @LiteralArg("remove") String l,
                             @OptionArg(listName = "friendList") String name) {
        sender.getFriends().removeFriend(Core.getInstance().getPlayers().getOffline(Bukkit.getOfflinePlayer(name).getUniqueId()));
    }

    @CommandAnnotation
    public void friendPending(CorePlayer sender,
                              @LiteralArg("requests") String l) {
        if (sender.getFriends().getRequesting().isEmpty()) {
            error(sender, "You have no pending friend requests!");
            return;
        }
        BaseComponent component = new TextComponent("Friend requests - ");
        Iterator<UUID> it = sender.getFriends().getRequesting().iterator();
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
