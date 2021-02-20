package com.spleefleague.core.player.collectible;

import com.mongodb.client.MongoCollection;
import com.spleefleague.core.Core;
import com.spleefleague.core.logger.CoreLogger;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.collectible.background.MenuBackground;
import com.spleefleague.core.player.collectible.field.FieldGeneration;
import com.spleefleague.core.player.collectible.hat.Hat;
import com.spleefleague.core.player.collectible.key.Key;
import com.spleefleague.core.player.collectible.music.Song;
import com.spleefleague.core.player.collectible.particles.Particles;
import com.spleefleague.core.player.collectible.pet.Pet;
import com.spleefleague.core.player.collectible.victory.VictoryMessage;
import com.spleefleague.core.vendor.Vendorable;
import com.spleefleague.core.vendor.Vendorables;
import com.spleefleague.coreapi.database.annotation.DBLoad;
import com.spleefleague.coreapi.database.annotation.DBSave;
import org.bson.Document;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Collectible items are vendorable items that when
 * purchased (or are default available) are added to a
 * collectibles menu where you can activate/deactivate
 * them for different effects
 *
 * @author NickM13
 * @since 4/18/2020
 */
public abstract class Collectible extends Vendorable {

    private static MongoCollection<Document> collectiblesCol;

    public Collectible() {
        super();
    }

    public static void init() {
        collectiblesCol = Core.getInstance().getPluginDB().getCollection("Collectibles");

        Vendorables.init();

        Hat.init();
        Key.init();
        //Pet.init();

        MenuBackground.init();
        FieldGeneration.init();
        Song.init();
        Particles.init();
        VictoryMessage.init();
    }

    public static void clear() {
        Vendorables.clear();
    }

    public static void loadDatabase() {
        for (Document doc : collectiblesCol.find()) {
            try {
                Class<? extends Vendorable> clazz = Vendorable.getClassFromExactType(doc.get("type", String.class));
                if (clazz == null) continue;
                Vendorable vendorable = clazz.getConstructor().newInstance();
                vendorable.load(doc);
                Vendorables.register(vendorable);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException exception) {
                CoreLogger.logError(null, exception);
            }
        }
    }

    public static int loadCollectibles(Class<? extends Vendorable> parentClazz) {
        int count = 0;
        for (Document doc : collectiblesCol.find(new Document("parentType", Vendorable.getExactTypeName(parentClazz)))) {
            try {
                Class<? extends Vendorable> typeClazz = Vendorable.getClassFromExactType(doc.get("type", String.class));
                if (typeClazz == null) continue;
                Vendorable vendorable = typeClazz.getConstructor().newInstance();
                vendorable.load(doc);
                Vendorables.register(vendorable);
                count++;
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
        return count;
    }

    public static void close() {
        Hat.close();
        Key.close();
        Pet.close();
        MenuBackground.close();
        FieldGeneration.close();
        Song.close();
        Particles.close();
        VictoryMessage.close();
    }

    public static <T extends Collectible> T create(Class<T> collectibleClass, String identifier, String displayName) {
        try {
            T collectible = collectibleClass.getConstructor(String.class, String.class).newInstance(identifier, displayName);
            collectible.updateDisplayItem();
            collectible.saveChanges();
            Vendorables.register(collectible);
            return collectible;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException exception) {
            CoreLogger.logError(exception);
            return null;
        }
    }

    public static <T extends Collectible> T create(T collectible) {
        collectible.updateDisplayItem();
        collectible.saveChanges();
        Vendorables.register(collectible);
        return collectible;
    }

    public static void destroy(Class<? extends Collectible> collectibleClass, String identifier) {
        Vendorables.get(collectibleClass, identifier).unsave();
        Vendorables.unregister(collectibleClass, identifier);
    }

    public static <T extends Collectible> T clone(Class<T> collectibleClass, String identifier, String cloneTo) {
        try {
            if (Vendorables.contains(collectibleClass, cloneTo)) {
                return null;
            } else {
                T collectible = (T) Vendorables.get(collectibleClass, identifier).clone();
                collectible.setIdentifier(cloneTo);
                collectible.updateDisplayItem();
                collectible.saveChanges();
                Vendorables.register(collectible);
                return collectible;
            }
        } catch (CloneNotSupportedException exception) {
            CoreLogger.logError(exception);
        }
        return null;
    }

    protected Map<String, CollectibleSkin> skins = new HashMap<>();

    @Override
    public void afterLoad() {
        super.afterLoad();
        for (CollectibleSkin skin : skins.values()) {
            skin.updateDisplayItem();
        }
    }

    /**
     * Called when a player clicks on this collectible on
     * their collections menu
     *
     * @param cp Core Player
     */
    public abstract void onEnable(CorePlayer cp);

    /**
     * Called when another collectible of the same type has
     * been enabled
     *
     * @param cp Core Player
     */
    public abstract void onDisable(CorePlayer cp);

    /**
     * Whether an item is available for purchasing for things
     * such as requiring prerequisites, levels or achievements
     *
     * @param cp Core Player
     * @return Availability
     */
    @Override
    public abstract boolean isAvailableToPurchase(CorePlayer cp);

    /**
     * Called when a player successfully purchases this item from the vendor
     *
     * @param cp Core Player
     */
    @Override
    public void purchase(CorePlayer cp) {

    }

    @Override
    public void saveChanges() {
        save(collectiblesCol);
    }


    public void unsave() {
        unsave(collectiblesCol);
    }

    @DBLoad(fieldName = "skins")
    private void loadSkins(Document doc) {
        skins.clear();
        for (Map.Entry<String, Object> entry : doc.entrySet()) {
            CollectibleSkin skin = new CollectibleSkin(this);
            skin.load((Document) entry.getValue());
            skins.put(entry.getKey(), skin);
        }
    }

    @DBSave(fieldName = "skins")
    private Document saveSkins() {
        Document doc = new Document();

        for (Map.Entry<String, CollectibleSkin> skin : skins.entrySet()) {
            doc.append(skin.getKey(), skin.getValue().toDocument());
        }

        return doc;
    }

    @Override
    public Collectible clone() throws CloneNotSupportedException {
        return (Collectible) super.clone();
    }

    public boolean createSkin(String identifier, int cmd, String name) {
        if (skins.containsKey(identifier)) {
            return false;
        }
        skins.put(identifier, new CollectibleSkin(this, cmd, name));
        saveChanges();
        return true;
    }

    public void destroySkin(String identifier) {
        if (skins.remove(identifier) != null) {
            saveChanges();
        }
    }

    public boolean isUnlocked(CorePlayer corePlayer) {
        return corePlayer.getCollectibles().contains(this) || isDefault(corePlayer);
    }

    public CollectibleSkin getSkin(String identifier) {
        return skins.get(identifier);
    }

    public Set<String> getSkinIds() {
        return skins.keySet();
    }

    public final ItemStack getDisplayItem(String skin) {
        if (skin == null || skin.isEmpty() || !skins.containsKey(skin)) {
            return getDisplayItem();
        }
        return skins.get(skin).getDisplayItem();
    }

    public boolean hasSkins() {
        return skins.size() > 0;
    }

}
