package com.spleefleague.core.crate;

import com.spleefleague.core.player.collectible.Collectible;
import com.spleefleague.core.player.collectible.CollectibleSkin;
import com.spleefleague.core.player.purse.CoreCurrency;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author NickM13
 * @since 2/2/2021
 */
public class CrateLoot {

    public List<Collectible> collectibles = new ArrayList<>();
    public List<CollectibleSkin> collectibleSkins = new ArrayList<>();
    public Map<CoreCurrency, Integer> currencies = new HashMap<>();

}
