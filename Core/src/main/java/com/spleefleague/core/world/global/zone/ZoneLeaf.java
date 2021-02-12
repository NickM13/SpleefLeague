package com.spleefleague.core.world.global.zone;

import com.google.common.collect.Lists;
import com.spleefleague.core.Core;
import com.spleefleague.core.util.variable.Point;
import com.spleefleague.coreapi.database.variable.DBVariable;
import org.bson.Document;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

/**
 * @author NickM13
 * @since 5/11/2020
 */
public class ZoneLeaf extends DBVariable<Document> {

    public int id;
    public Point pos;

    public ZoneLeaf(Document doc) {
        load(doc);
    }

    public ZoneLeaf(int id, Point pos) {
        this.id = id;
        this.pos = pos;
    }

    @Override
    public void load(Document doc) {
        this.id = doc.get("id", Integer.class);
        this.pos = new Point(doc.get("pos", List.class));
    }

    @Override
    public Document save() {
        return new Document("id", this.id)
                .append("pos", Lists.newArrayList(this.pos.x, this.pos.y, this.pos.z));
    }

    public int getId() {
        return id;
    }

    public Point getPos() {
        return pos;
    }

    public ItemStack createLeaf(String zoneName) {
        ItemStack itemStack = new ItemStack(Material.HONEYCOMB);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setCustomModelData(1);
        itemMeta.getPersistentDataContainer().set(new NamespacedKey(Core.getInstance(), "leafName"), PersistentDataType.STRING, zoneName + ":" + id);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

}
