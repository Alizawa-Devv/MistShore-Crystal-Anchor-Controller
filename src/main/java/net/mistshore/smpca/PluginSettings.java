package net.mistshore.smpca;

import java.util.Locale;
import org.bukkit.configuration.ConfigurationSection;

final class PluginSettings {

    static final PluginSettings DEFAULT = new PluginSettings(true, false, 0.5, false, false, false);

    // 伤害上限，防止配置里填个天文数字
    private static final double MAX_DAMAGE = 1_000_000;

    final boolean damageEnabled;
    final boolean fixedDamage; // false 就是倍率模式
    final double damageValue;
    final boolean breakTerrain;
    final boolean blockAnchorCraft;
    final boolean blockCrystalCraft;

    private PluginSettings(boolean damageEnabled, boolean fixedDamage, double damageValue,
                           boolean breakTerrain, boolean blockAnchorCraft, boolean blockCrystalCraft) {
        this.damageEnabled = damageEnabled;
        this.fixedDamage = fixedDamage;
        this.damageValue = damageValue;
        this.breakTerrain = breakTerrain;
        this.blockAnchorCraft = blockAnchorCraft;
        this.blockCrystalCraft = blockCrystalCraft;
    }

    static PluginSettings load(ConfigurationSection cfg) {
        String mode = cfg.getString("damage.mode", "MULTIPLIER").trim().toUpperCase(Locale.ROOT);
        boolean fixed;
        switch (mode) {
            case "MULTIPLIER" -> fixed = false;
            case "FIXED", "SET" -> fixed = true; // 顺手兼容一下 SET 的写法
            default -> throw new IllegalArgumentException("damage.mode 只能填 MULTIPLIER 或 FIXED，当前是 " + mode);
        }

        // 不直接用 getDouble，填成字符串的话它会悄悄返回 0
        Object raw = cfg.get("damage.value", DEFAULT.damageValue);
        if (!(raw instanceof Number)) {
            throw new IllegalArgumentException("damage.value 要填数字");
        }
        double value = ((Number) raw).doubleValue();
        if (Double.isNaN(value) || value < 0 || value > MAX_DAMAGE) {
            throw new IllegalArgumentException("damage.value 超出范围了，只能是 0 ~ " + (long) MAX_DAMAGE);
        }

        return new PluginSettings(
                cfg.getBoolean("damage.enabled", true),
                fixed,
                value,
                cfg.getBoolean("explosion.break-terrain", false),
                cfg.getBoolean("crafting.block-respawn-anchor", false),
                cfg.getBoolean("crafting.block-end-crystal", false)
        );
    }

    double apply(double damage) {
        double result = fixedDamage ? damageValue : damage * damageValue;
        // 原伤害本身就离谱的话乘出来可能爆掉，统一压到上限
        if (Double.isNaN(result) || result > MAX_DAMAGE) {
            return MAX_DAMAGE;
        }
        return result;
    }

    String modeName() {
        return fixedDamage ? "FIXED" : "MULTIPLIER";
    }
}
