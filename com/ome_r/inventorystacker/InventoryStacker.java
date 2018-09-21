package com.ome_r.inventorystacker;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public class InventoryStacker extends JavaPlugin implements CommandExecutor {

    @Override
    public void onEnable() {
        getCommand("stack").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)){
            sender.sendMessage(ChatColor.RED + "Only players can use this command.");
            return false;
        }

        if(args.length != 0){
            sender.sendMessage(ChatColor.RED + "Usage: /stack");
            return false;
        }

        Player player = (Player) sender;

        Map<EntityType, Integer> spawnerAmounts = new HashMap<>();

        for(int slot = 0; slot < player.getInventory().getSize(); slot++){
            ItemStack itemStack = player.getInventory().getItem(slot);
            if(itemStack != null && itemStack.getType() == WMaterial.SPAWNER.parseMaterial()){
                EntityType entityType = getSpawnerType(itemStack);
                spawnerAmounts.put(entityType, spawnerAmounts.getOrDefault(entityType, 0) + Utils.getSpawnerItemAmount(itemStack, itemStack.getAmount()));
                player.getInventory().setItem(slot, new ItemStack(Material.AIR));
            }
        }

        for(EntityType type : spawnerAmounts.keySet()){
            HashMap<Integer, ItemStack> additionalItems = player.getInventory().addItem(createItemSpawner(type, spawnerAmounts.get(type)));
            if (!additionalItems.isEmpty()) {
                for (ItemStack itemStack : additionalItems.values()) {
                    player.getWorld().dropItemNaturally(player.getLocation(), itemStack);
                }
            }
        }

        player.sendMessage(ChatColor.YELLOW + "Successfully stacked all spawner-items!");

        return true;
    }

    private ItemStack createItemSpawner(EntityType type, int amount){
        ItemStack itemStack = Utils.setSpawnerItemAmount(new ItemStack(WMaterial.SPAWNER.parseMaterial()), amount);

        BlockStateMeta blockStateMeta = (BlockStateMeta) itemStack.getItemMeta();
        CreatureSpawner creatureSpawner = (CreatureSpawner) blockStateMeta.getBlockState();

        creatureSpawner.setSpawnedType(type);

        blockStateMeta.setBlockState(creatureSpawner);

        blockStateMeta.setDisplayName(getName(type, amount));

        itemStack.setItemMeta(blockStateMeta);

        return itemStack;
    }

    private EntityType getSpawnerType(ItemStack itemStack){
        BlockStateMeta blockStateMeta = (BlockStateMeta) itemStack.getItemMeta();
        CreatureSpawner creatureSpawner = (CreatureSpawner) blockStateMeta.getBlockState();
        return creatureSpawner.getSpawnedType();
    }

    private String getName(EntityType type, int amount){
        String name = ChatColor.YELLOW + "x{0} {1} Spawner";
        return name.replace("{0}", amount + "").replace("{1}", Utils.getFormattedType(type.name()));
    }

}
