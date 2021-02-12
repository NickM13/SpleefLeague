package com.spleefleague.core.crate;

import com.spleefleague.core.menu.InventoryMenuUtils;
import com.spleefleague.core.player.purse.CoreCurrency;
import com.spleefleague.core.vendor.Vendorable;
import com.spleefleague.coreapi.database.annotation.DBField;
import com.spleefleague.coreapi.database.variable.DBEntity;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

/**
 * @author NickM13
 * @since 2/2/2021
 */
public class Crate extends DBEntity {

    @DBField
    private String displayName = "";
    @DBField
    private String description = "";
    @DBField
    private Material material = Material.CHEST;
    @DBField
    private Integer closedCmd = 1;
    @DBField
    private Integer openedCmd = 2;
    @DBField
    private Integer priority = -1;
    @DBField
    private String style = "";
    @DBField
    private Boolean hidden = true;
    @DBField
    private Map<String, Double> currencyWeights = new HashMap<>();
    @DBField
    private Map<String, Double> collectibleWeights = new HashMap<>();
    @DBField
    private Double collectibleMin = 1D;
    @DBField
    private Double collectibleMax = 1D;
    @DBField
    private Double currencyMin = 0D;
    @DBField
    private Double currencyMax = 0D;

    private ItemStack closedItem;
    private ItemStack openedItem;
    private double totalCollectibleWeight;
    private double totalCurrencyWeight;

    public Crate() {

    }

    public Crate(String identifier, String displayName) {
        this.identifier = identifier;
        this.displayName = displayName;
        updateItems();
    }

    @Override
    public void afterLoad() {
        super.afterLoad();
        updateItems();
        calculateWeights();
    }

    private void updateItems() {
        closedItem = InventoryMenuUtils.createCustomItem(material, closedCmd);
        openedItem = InventoryMenuUtils.createCustomItem(material, openedCmd);
    }

    private void calculateWeights() {
        totalCollectibleWeight = 0;
        totalCurrencyWeight = 0;

        for (Double d : collectibleWeights.values()) {
            totalCollectibleWeight += d;
        }
        for (Double d : currencyWeights.values()) {
            totalCurrencyWeight += d;
        }
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setMaterial(Material material) {
        this.material = material;
        updateItems();
    }

    public void setClosedCmd(int cmd) {
        this.closedCmd = cmd;
        updateItems();
    }

    public void setOpenedCmd(int cmd) {
        this.openedCmd = cmd;
        updateItems();
    }

    public void setStyle(String style) {
        this.style = style != null ? style : "";
    }

    public String style() {
        return style;
    }

    public ItemStack getClosed() {
        return closedItem;
    }

    public ItemStack getOpened() {
        return openedItem;
    }

    public void setPriority(int priority) {
        this.priority = priority;

    }

    public int getPriority() {
        return priority;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public boolean isHidden() {
        return hidden;
    }

    public void setCollectibleWeight(Vendorable.Rarity rarity, Double weight) {
        if (weight <= 0) collectibleWeights.remove(rarity.name());
        else collectibleWeights.put(rarity.name(), weight);
        calculateWeights();
    }

    public Map<String, Double> getCollectibleWeightMap() {
        return collectibleWeights;
    }

    public Double getTotalCollectibleWeight() {
        return totalCollectibleWeight;
    }

    public void setCurrencyWeight(CoreCurrency currency, Double weight) {
        if (weight <= 0) currencyWeights.remove(currency.name());
        else currencyWeights.put(currency.name(), weight);
        calculateWeights();
    }

    public Map<String, Double> getCurrencyWeightMap() {
        return currencyWeights;
    }

    public Double getTotalCurrencyWeight() {
        return totalCurrencyWeight;
    }

    public void setCollectibleCaps(Double min, Double max) {
        collectibleMin = min;
        collectibleMax = max;
    }

    public void setCurrencyCaps(Double min, Double max) {
        currencyMin = min;
        currencyMax = max;
    }

    public Double getCollectibleMin() {
        return collectibleMin;
    }

    public Double getCollectibleMax() {
        return collectibleMax;
    }

    public Double getCurrencyMin() {
        return currencyMin;
    }

    public Double getCurrencyMax() {
        return currencyMax;
    }

}
