package com.spleefleague.core.world.global.zone;

import com.google.common.collect.Lists;
import com.spleefleague.core.Core;
import com.spleefleague.core.util.variable.Position;
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

    String name;
    Position pos;

    public ZoneLeaf(Document doc) {
        load(doc);
    }

    public ZoneLeaf(String name, Position pos) {
        this.name = name;
        this.pos = pos;
    }

    @Override
    public void load(Document doc) {
        this.name = doc.get("name", String.class);
        this.pos = new Position(doc.get("pos", List.class));
    }

    @Override
    public Document save() {
        return new Document("name", this.name)
                .append("pos", Lists.newArrayList(this.pos.getX(), this.pos.getY(), this.pos.getZ()));
    }
    
    public String getName() {
        return name;
    }

    public Position getPos() {
        return pos;
    }

    public ItemStack createLeaf(String zoneName) {
        ItemStack itemStack = new ItemStack(Material.HONEYCOMB);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setCustomModelData(1);
        itemMeta.getPersistentDataContainer().set(new NamespacedKey(Core.getInstance(), "leafName"), PersistentDataType.STRING, zoneName + ":" + name);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

}
