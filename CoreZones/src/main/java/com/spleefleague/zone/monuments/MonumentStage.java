package com.spleefleague.zone.monuments;

import com.spleefleague.core.chat.NpcMessage;
import com.spleefleague.core.logger.CoreLogger;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.collectible.Collectible;
import com.spleefleague.core.player.purse.CoreCurrency;
import com.spleefleague.core.vendor.Vendorables;
import com.spleefleague.coreapi.database.annotation.DBField;
import com.spleefleague.coreapi.database.annotation.DBLoad;
import com.spleefleague.coreapi.database.annotation.DBSave;
import com.spleefleague.coreapi.database.variable.DBEntity;
import org.bson.Document;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author NickM13
 * @since 2/16/2021
 */
public class MonumentStage extends DBEntity {

    public enum RewardType {

        CURRENCY(RewardCurrency.class),
        COLLECTIBLE(RewardCollectible.class),
        SKIN(RewardCollectibleSkin.class);

        Class<? extends Reward> clazz;

        RewardType(Class<? extends Reward> clazz) {
            this.clazz = clazz;
        }

        Reward create() {
            try {
                return this.clazz.getDeclaredConstructor().newInstance();
            } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                e.printStackTrace();
            }
            return null;
        }

    }

    public abstract static class Reward extends DBEntity {

        @DBField RewardType rewardType;

        protected Reward(RewardType type) {
            this.rewardType = type;
        }

        public abstract void apply(CorePlayer cp);

        public abstract String getDisplayName(boolean turnedIn, CorePlayer corePlayer);

        public abstract ItemStack getDisplayItem();

    }

    public static class RewardCurrency extends Reward {

        @DBField public CoreCurrency currency;
        @DBField public Integer amount;

        public RewardCurrency() {
            super(RewardType.CURRENCY);
        }

        public RewardCurrency(CoreCurrency currency, int amount) {
            super(RewardType.CURRENCY);
            this.currency = currency;
            this.amount = amount;
        }

        @Override
        public void apply(CorePlayer cp) {
            cp.getPurse().addCurrency(currency, amount);
        }

        @Override
        public String getDisplayName(boolean turnedIn, CorePlayer corePlayer) {
            return currency.color + "" + amount + "x " + currency.displayName + (turnedIn ? " (Claimed)" : " (Unclaimed)");
        }

        @Override
        public ItemStack getDisplayItem() {
            return currency.displayItem;
        }

        @Override
        public String toString() {
            return "RewardCurrency{" +
                    "currency=" + currency +
                    ", amount=" + amount +
                    '}';
        }

    }

    public static class RewardCollectible extends Reward {

        @DBField public String type;
        @DBField public String collectible;

        public RewardCollectible() {
            super(RewardType.COLLECTIBLE);
        }

        public RewardCollectible(String type, String collectible) {
            super(RewardType.COLLECTIBLE);
            this.type = type;
            this.collectible = collectible;
        }

        @Override
        public String getDisplayName(boolean turnedIn, CorePlayer corePlayer) {
            String name = Vendorables.get(type, collectible).getDisplayName();
            if (turnedIn && corePlayer.getCollectibles().getInfo(type, collectible).isBaseUnlocked()) {
                name += " (Claimed)";
            } else {
                name += " (Unclaimed)";
            }
            return name;
        }

        @Override
        public void apply(CorePlayer cp) {
            cp.getCollectibles().add((Collectible) Vendorables.get(type, collectible));
        }

        @Override
        public ItemStack getDisplayItem() {
            return Vendorables.get(type, collectible).getDisplayItem();
        }

        @Override
        public String toString() {
            return "RewardCollectible{" +
                    "type='" + type + '\'' +
                    ", collectible='" + collectible + '\'' +
                    '}';
        }

    }

    public static class RewardCollectibleSkin extends Reward {

        @DBField public String type;
        @DBField public String collectible;
        @DBField public String skin;

        public RewardCollectibleSkin() {
            super(RewardType.SKIN);
        }

        public RewardCollectibleSkin(String type, String collectible, String skin) {
            super(RewardType.SKIN);
            this.type = type;
            this.collectible = collectible;
            this.skin = skin;
        }

        @Override
        public void apply(CorePlayer cp) {
            cp.getCollectibles().addSkin((Collectible) Vendorables.get(type, collectible), skin);
        }

        @Override
        public String getDisplayName(boolean turnedIn, CorePlayer corePlayer) {
            String name = ((Collectible) Vendorables.get(type, collectible)).getSkin(skin).getDisplayName();
            if (turnedIn && corePlayer.getCollectibles().getInfo(type, collectible).getOwnedSkins().containsKey(skin)) {
                name += " (Claimed)";
            } else {
                name += " (Unclaimed)";
            }
            return name;
        }

        @Override
        public ItemStack getDisplayItem() {
            return ((Collectible) Vendorables.get(type, collectible)).getSkin(skin).getDisplayItem();
        }

        @Override
        public String toString() {
            return "RewardCollectibleSkin{" +
                    "type='" + type + '\'' +
                    ", collectible='" + collectible + '\'' +
                    ", skin='" + skin + '\'' +
                    '}';
        }

    }

    @DBField private Integer stage;
    @DBField private NpcMessage message = null;
    private final List<Reward> rewards = new ArrayList<>();

    public MonumentStage() {

    }

    public MonumentStage(int stage) {
        this.stage = stage;
    }

    @DBSave(fieldName = "rewards")
    private List<Document> saveRewards() {
        List<Document> docs = new ArrayList<>();
        for (Reward reward : rewards) {
            docs.add(reward.toDocument());
        }
        return docs;
    }

    @DBLoad(fieldName = "rewards")
    private void loadRewards(List<Document> rewards) {
        this.rewards.clear();
        for (Document doc : rewards) {
            Reward reward = RewardType.valueOf(doc.getString("rewardType")).create();
            if (reward != null) {
                reward.load(doc);
                this.rewards.add(reward);
            } else {
                CoreLogger.logError("Reward type was null");
            }
        }
    }

    public int getStage() {
        return stage;
    }

    public NpcMessage getMessage() {
        return message;
    }

    public void setMessage(NpcMessage message) {
        this.message = message;
    }

    public List<Reward> getRewards() {
        return rewards;
    }

    public void addReward(Reward reward) {
        rewards.add(reward);
    }

    public void clearRewards() {
        rewards.clear();
    }

    @Override
    public String toString() {
        return "MonumentStage{" +
                "stage=" + stage +
                ", message=" + message +
                ", rewards=" + rewards +
                '}';
    }

}
