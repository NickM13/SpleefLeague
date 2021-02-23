package com.spleefleague.core.player.crates;

import com.spleefleague.core.Core;
import com.spleefleague.core.crate.Crate;
import com.spleefleague.core.crate.CrateLoot;
import com.spleefleague.core.logger.CoreLogger;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.collectible.Collectible;
import com.spleefleague.core.player.purse.CoreCurrency;
import com.spleefleague.core.vendor.Vendorable;
import com.spleefleague.core.vendor.Vendorables;
import com.spleefleague.coreapi.player.crate.PlayerCrates;
import com.spleefleague.coreapi.utils.packet.spigot.player.PacketSpigotPlayerCrate;

import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * @author NickM13
 * @since 2/2/2021
 */
public class CorePlayerCrates extends PlayerCrates {

    private final CorePlayer owner;

    public CorePlayerCrates(CorePlayer owner) {
        this.owner = owner;
    }

    @Override
    public void setCrateCount(String crate, int count) {
        int change = count - getCrateCount(crate);
        super.setCrateCount(crate, count);
        Core.getInstance().sendPacket(new PacketSpigotPlayerCrate(owner.getUniqueId(), crate, change, false));
    }

    @Override
    public void changeCrateCount(String crate, int amount) {
        super.changeCrateCount(crate, amount);
        PacketSpigotPlayerCrate packet = new PacketSpigotPlayerCrate(owner.getUniqueId(), crate, amount, false);
        Core.getInstance().sendPacket(packet);
    }

    public CrateLoot openCrate(String crateName) {
        CrateLoot crateLoot = new CrateLoot();

        Crate crate = Core.getInstance().getCrateManager().get(crateName);

        Random random = new Random();
        int collectibleCount = (int) Math.round(random.nextDouble() * (crate.getCollectibleMax() - crate.getCollectibleMin()) + crate.getCollectibleMin());
        int currencyCount = (int) Math.round(random.nextDouble() * (crate.getCurrencyMax() - crate.getCurrencyMin()) + crate.getCurrencyMin());

        Map<Vendorable.Rarity, List<String>> available = owner.getCollectibles().getAvailableCollectibles();

        double roll;
        for (int i = 0; i < collectibleCount; i++) {
            roll = Math.random() * crate.getTotalCollectibleWeight();
            for (Map.Entry<String, Double> entry : crate.getCollectibleWeightMap().entrySet()) {
                roll -= entry.getValue();
                if (roll < 0) {
                    Vendorable.Rarity rarity = Vendorable.Rarity.valueOf(entry.getKey());
                    if (available.get(rarity).isEmpty()) {
                        Core.getInstance().sendMessage(owner, "Uh oh, something went wrong with your roll!");
                    } else {
                        int roll2 = random.nextInt(available.get(rarity).size());
                        String str = available.get(rarity).remove(roll2);
                        String[] parts = str.split(":");
                        String parent = parts[0];
                        String collectibleId = parts[1];
                        Collectible collectible = (Collectible) Vendorables.get(parent, collectibleId);
                        if (collectible == null) {
                            CoreLogger.logError("Null collectible was rolled for " + owner.getName() + ": " + str);
                            continue;
                        }
                        if (parts.length > 2) {
                            String skin = parts[2];
                            if (owner.getCollectibles().getInfo(collectible).getOwnedSkins().containsKey(skin)) {
                                // Roll fragment
                            } else {
                                crateLoot.collectibleSkins.add(collectible.getSkin(skin));
                                owner.getCollectibles().addSkin(collectible, skin);
                            }
                        } else {
                            if (owner.getCollectibles().contains(collectible)) {
                                // Roll fragment
                            } else {
                                crateLoot.collectibles.add(collectible);
                                owner.getCollectibles().add(collectible);
                            }
                        }
                    }
                }
            }
        }

        for (int i = 0; i < currencyCount; i++) {
            roll = Math.random() * crate.getTotalCurrencyWeight();
            for (Map.Entry<String, Double> entry : crate.getCurrencyWeightMap().entrySet()) {
                roll -= entry.getValue();
                if (roll < 0) {
                    CoreCurrency coreCurrency = CoreCurrency.valueOf(entry.getKey());
                    int newVal = crateLoot.currencies.getOrDefault(coreCurrency, 0) + 1;
                    crateLoot.currencies.put(CoreCurrency.valueOf(entry.getKey()), newVal);
                }
            }
        }

        for (Map.Entry<CoreCurrency, Integer> entry : crateLoot.currencies.entrySet()) {
            owner.getPurse().addCurrency(entry.getKey(), entry.getValue());
        }

        super.changeCrateCount(crateName, -1);
        Core.getInstance().sendPacket(new PacketSpigotPlayerCrate(owner.getUniqueId(), crateName, -1, true));
        return crateLoot;
    }

}
