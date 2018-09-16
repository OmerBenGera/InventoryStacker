package com.ome_r.inventorystacker;

import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

public class Utils {

    private static String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];

    public static ItemStack setSpawnerItemAmount(ItemStack itemStack, int amount){
        try{
            Class craftItemStack = getBukkitClass("inventory.CraftItemStack");

            Object nmsStack = craftItemStack.getMethod("asNMSCopy", ItemStack.class).invoke(null, itemStack);
            Object tag = getNMSClass("NBTTagCompound").newInstance();

            if((boolean) nmsStack.getClass().getMethod("hasTag").invoke(nmsStack)){
                tag = nmsStack.getClass().getMethod("getTag").invoke(nmsStack);
            }

            tag.getClass().getMethod("setInt", String.class, int.class).invoke(tag, "spawners-amount", amount);

            nmsStack.getClass().getMethod("setTag", tag.getClass()).invoke(nmsStack, tag);

            return (ItemStack) craftItemStack.getMethod("asBukkitCopy", nmsStack.getClass()).invoke(null, nmsStack);
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return itemStack;
    }

    public static String getFormattedType(String type) {
        StringBuilder name = new StringBuilder();

        type = type.replace(" ", "_");

        for (String section : type.split("_")) {
            name.append(section.substring(0, 1).toUpperCase()).append(section.substring(1).toLowerCase()).append(" ");
        }

        return name.substring(0, name.length() - 1);
    }

    private static Class getBukkitClass(String clazz) throws ClassNotFoundException{
        return Class.forName("org.bukkit.craftbukkit." + version + "." + clazz);
    }

    private static Class getNMSClass(String clazz) throws ClassNotFoundException{
        return Class.forName("net.minecraft.server." + version + "." + clazz);
    }

}
