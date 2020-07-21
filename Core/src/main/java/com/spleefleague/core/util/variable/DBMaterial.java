package com.spleefleague.core.util.variable;

import com.spleefleague.coreapi.database.variable.DBVariable;
import org.bukkit.Material;

public class DBMaterial extends DBVariable<String> {

    private Material material;

    public DBMaterial() {

    }

    @Override
    public void load(String name) {
        material = Material.valueOf(name);
    }

    @Override
    public String save() {
        return material.name();
    }

}
