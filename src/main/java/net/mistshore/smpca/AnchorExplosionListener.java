package net.mistshore.smpca;

import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

final class AnchorExplosionListener implements Listener {

    private final MistShoreSMPCAPlugin plugin;

    AnchorExplosionListener(MistShoreSMPCAPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onDamage(EntityDamageByBlockEvent e) {
        if (e.getCause() != DamageCause.BLOCK_EXPLOSION) {
            return;
        }
        // 伤害结算的时候锚已经变成空气了，getDamager() 拿不到类型，得看爆炸前的快照
        if (!isAnchor(e.getDamagerBlockState())) {
            return;
        }

        PluginSettings s = plugin.getSettings();
        if (s.damageEnabled) {
            e.setDamage(s.apply(e.getDamage()));
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onExplode(BlockExplodeEvent e) {
        if (!plugin.getSettings().breakTerrain && isAnchor(e.getExplodedBlockState())) {
            // 只清空要被炸掉的方块，伤害和击退照常
            e.blockList().clear();
        }
    }

    private static boolean isAnchor(BlockState state) {
        return state != null && state.getType() == Material.RESPAWN_ANCHOR;
    }
}
