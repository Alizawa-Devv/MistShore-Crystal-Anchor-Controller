package net.mistshore.smpca;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.CrafterCraftEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;

final class CraftingListener implements Listener {

    private final MistShoreSMPCAPlugin plugin;

    CraftingListener(MistShoreSMPCAPlugin plugin) {
        this.plugin = plugin;
    }

    // 摆好配方的时候就把结果格清掉，玩家看不到成品
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPrepare(PrepareItemCraftEvent e) {
        ItemStack result = e.getInventory().getResult();
        if (result != null && isBlocked(result.getType())) {
            e.getInventory().setResult(null);
        }
    }

    // 再拦一道，免得别的插件又把结果塞回去
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onCraft(CraftItemEvent e) {
        if (isBlocked(e.getRecipe().getResult().getType())) {
            e.setCancelled(true);
        }
    }

    // 自动合成器
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onCrafter(CrafterCraftEvent e) {
        if (isBlocked(e.getResult().getType())) {
            e.setCancelled(true);
        }
    }

    private boolean isBlocked(Material type) {
        PluginSettings s = plugin.getSettings();
        if (type == Material.RESPAWN_ANCHOR) return s.blockAnchorCraft;
        if (type == Material.END_CRYSTAL) return s.blockCrystalCraft;
        return false;
    }
}
