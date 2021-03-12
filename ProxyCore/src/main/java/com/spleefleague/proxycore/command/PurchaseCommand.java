package com.spleefleague.proxycore.command;

import com.spleefleague.coreapi.chat.ChatColor;
import com.spleefleague.proxycore.ProxyCore;
import com.spleefleague.proxycore.player.ProxyCorePlayer;
import com.spleefleague.proxycore.player.ranks.ProxyRank;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;

import java.util.UUID;

/**
 * @author NickM13
 * @since 2/2/2021
 */
public class PurchaseCommand extends Command {

    private enum PurchaseType {
        RANK,
        TEMPRANK,
        CRATE,
        BOOSTER
    }

    public PurchaseCommand() {
        super("purchase", "proxycore.purchase");
    }

    private void purchaseRank(ProxyCorePlayer pcp, String rankName) {
        ProxyRank rank = ProxyCore.getInstance().getRankManager().getRank(rankName);
        pcp.setPermRank(rank);
        ProxyCore.getInstance().sendMessage(pcp, "You are now rank " + rank.getDisplayName() + ChatColor.GRAY + "!");
    }

    private static final long MILLIS_TO_DAYS = 1000 * 60 * 60 * 24;

    private void purchaseTempRank(ProxyCorePlayer pcp, String rankName, Integer time) {
        ProxyRank rank = ProxyCore.getInstance().getRankManager().getRank(rankName);
        pcp.addTempRank(rankName, time * MILLIS_TO_DAYS);
        ProxyCore.getInstance().sendMessage(pcp, "You are now rank " + rank.getDisplayName() + ChatColor.GRAY + "!");
    }

    private void purchaseCrate(ProxyCorePlayer pcp, String crateName, Integer count) {
        ProxyCore.getInstance().getPlayers().get(pcp.getUniqueId()).getCrates().changeCrateCount(crateName, count);
        ProxyCore.getInstance().sendMessage(pcp, "You received some crates!");
    }

    private void purchaseNetworkBooster(ProxyCorePlayer pcp, String boosterName) {
        TextComponent component = new TextComponent();
        component.addExtra(pcp.getChatName());
        component.addExtra(" has purchased a network booster!");
        ProxyCore.getInstance().sendMessage(pcp, "");
    }

    private void purchasePersonalBooster(ProxyCorePlayer pcp, String boosterName) {
        TextComponent component = new TextComponent();
        component.addExtra(pcp.getChatName());
        component.addExtra(" has purchased a network booster!");
        ProxyCore.getInstance().sendMessage(pcp, "");
    }

    @Override
    public void execute(CommandSender commandSender, String[] strings) {
        for (String str : strings) {
            System.out.println(str);
        }

        UUID uuid;
        if (strings.length >= 1) {
            try {
                if (strings[0].contains("-")) {
                    uuid = UUID.fromString(strings[0]);
                } else {
                    uuid = UUID.fromString(strings[0].replaceFirst("(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)", "$1-$2-$3-$4-$5"));
                }
            } catch (IllegalArgumentException exception) {
                System.out.println("Invalid UUID entered: " + strings[0]);
                return;
            }
        } else {
            System.out.println("No player UUID defined");
            return;
        }

        PurchaseType purchaseType = null;
        if (strings.length >= 2) {
            for (PurchaseType type : PurchaseType.values()) {
                if (strings[1].equalsIgnoreCase(type.name())) {
                    purchaseType = type;
                }
            }
        }
        if (purchaseType == null) {
            System.out.println("No purchase type defined");
            return;
        }

        ProxyCorePlayer pcp = ProxyCore.getInstance().getPlayers().getOffline(uuid);

        switch (purchaseType) {
            case RANK:
                purchaseRank(pcp, strings[2]);
                break;
            case TEMPRANK:
                purchaseTempRank(pcp, strings[2], strings.length >= 4 ? Integer.parseInt(strings[3]) : 31);
                break;
            case CRATE:
                purchaseCrate(pcp, strings[2], Integer.parseInt(strings[3]));
                break;
            case BOOSTER:
                if (strings[2].equalsIgnoreCase("network")) {
                    purchaseNetworkBooster(pcp, strings[3]);
                } else if (strings[2].equalsIgnoreCase("personal")) {
                    purchasePersonalBooster(pcp, strings[3]);
                }
                break;
        }
    }

}
