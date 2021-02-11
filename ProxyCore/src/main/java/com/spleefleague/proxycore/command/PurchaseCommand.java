package com.spleefleague.proxycore.command;

import com.spleefleague.proxycore.ProxyCore;
import com.spleefleague.proxycore.player.ProxyCorePlayer;
import net.md_5.bungee.api.CommandSender;
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
        pcp.setPermRank(ProxyCore.getInstance().getRankManager().getRank(rankName));
    }

    private static long MILLIS_TO_DAYS = 1000 * 60 * 60 * 24;

    private void purchaseTempRank(ProxyCorePlayer pcp, String rankName, Integer time) {
        pcp.addTempRank(rankName, time * MILLIS_TO_DAYS);
    }

    private void purchaseCrate(ProxyCorePlayer pcp, String crateName, Integer count) {
        pcp.getCrates().changeCrateCount(crateName, count);
    }

    private void purchaseBooster(ProxyCorePlayer pcp, String boosterName) {
        System.out.println("Boosters not set up yet");
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

        ProxyCorePlayer pcp = ProxyCore.getInstance().getPlayers().get(uuid);

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
                purchaseBooster(pcp, strings[2]);
                break;
        }
    }

}
