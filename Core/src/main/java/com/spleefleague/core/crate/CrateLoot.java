package com.spleefleague.core.crate;

import com.spleefleague.core.player.collectible.Collectible;
import com.spleefleague.core.player.collectible.CollectibleSkin;
import com.spleefleague.core.player.purse.CoreCurrency;

import java.util.ArrayList;
import java.util.List;

/**
 * @author NickM13
 * @since 2/2/2021
 */
public class CrateLoot {

    public static class CrateLootItem {

        public CoreCurrency replacement = null;

        public enum LootType {
            COLLECTIBLE,
            SKIN,
            CURRENCY
        }

        public final LootType lootType;

        public Collectible collectible = null;
        public CollectibleSkin skin = null;
        public CoreCurrency currency = null;
        public int amount;

        public CrateLootItem(Collectible collectible, CoreCurrency replacement) {
            lootType = LootType.COLLECTIBLE;
            this.collectible = collectible;
            this.replacement = replacement;
        }

        public CrateLootItem(CollectibleSkin skin, CoreCurrency replacement) {
            lootType = LootType.SKIN;
            this.skin = skin;
            this.replacement = replacement;
        }

        public CrateLootItem(CoreCurrency currency, int amount) {
            lootType = LootType.CURRENCY;
            this.currency = currency;
            this.amount = amount;
        }

    }

    public List<CrateLootItem> items = new ArrayList<>();

}
