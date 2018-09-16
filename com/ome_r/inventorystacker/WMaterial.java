package com.ome_r.inventorystacker;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public enum WMaterial {

    SPAWNER("MOB_SPAWNER");

    private final String legacy;

    WMaterial(String legacy){
        this.legacy = legacy;
    }

    public Material parseMaterial(){
        Material material = Material.matchMaterial(toString());
        return material != null ?  material : Material.matchMaterial(legacy);
    }

    public static boolean isValidAndSpawnEgg(ItemStack itemStack){
        return itemStack.getType().name().contains(Bukkit.getBukkitVersion().contains("1.13") ? "SPAWN_EGG" : "MONSTER_EGG");
    }

}
