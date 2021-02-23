package com.spleefleague.core.command;

import com.spleefleague.core.command.annotation.*;
import com.spleefleague.core.player.CoreOfflinePlayer;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.collectible.Holdable;
import com.spleefleague.core.player.rank.CoreRank;
import com.spleefleague.core.vendor.Vendorable;
import com.spleefleague.core.vendor.Vendorables;
import org.bukkit.command.CommandSender;

import javax.annotation.Nullable;
import java.util.List;

/**
 * @author NickM13
 * @since 5/6/2020
 */
public class HoldableCommand extends CollectibleCommand {

    private Class<? extends Holdable> holdableClazz;

    protected HoldableCommand(Class<? extends Holdable> holdableClazz, String name, CoreRank requiredRank, CoreRank... additionalRanks) {
        super(holdableClazz, name, requiredRank, additionalRanks);
        this.holdableClazz = holdableClazz;
    }

    /**
     * Returns whether a player in the list is holding a key
     *
     * @param sender     Command Sender
     * @param l          holding
     * @param targets    Target List
     * @param identifier Key Identifier
     * @return Success
     */
    @CommandAnnotation
    public boolean collectibleHolding(CommandSender sender,
                                      @LiteralArg("holding") String l,
                                      List<CorePlayer> targets,
                                      @OptionArg(listName = "collectibles") String identifier,
                                      @HelperArg("playerCount") @NumberArg(minValue = 1, defaultValue = 1) @Nullable Integer playerCount) {
        Holdable holdable = Vendorables.get(holdableClazz, identifier);
        if (holdable == null) {
            sender.sendMessage("Holdable not valid " + identifier);
            return false;
        }
        int required = playerCount != null ? playerCount : 1;
        for (CorePlayer target : targets) {
            Vendorable vendorable = Vendorables.get(target.getHeldItem());
            if (vendorable != null && vendorable.equalsSoft(holdable)) {
                required--;
                if (required <= 0) {
                    return true;
                }
            }
        }
        return false;
    }

}
