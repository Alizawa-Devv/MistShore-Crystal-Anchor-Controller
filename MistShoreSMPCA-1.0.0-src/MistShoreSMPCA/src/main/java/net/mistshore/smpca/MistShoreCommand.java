package net.mistshore.smpca;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;

final class MistShoreCommand implements CommandExecutor, TabCompleter {

    private static final String PERM_RELOAD = "mistshoresmpca.reload";
    private static final Component PREFIX = Component.text("[MistShoreSMPCA] ", NamedTextColor.DARK_AQUA);

    private final MistShoreSMPCAPlugin plugin;

    MistShoreCommand(MistShoreSMPCAPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String sub = args.length == 0 ? "status" : args[0].toLowerCase(Locale.ROOT);

        switch (sub) {
            case "status" -> showStatus(sender);
            case "reload" -> {
                if (!sender.hasPermission(PERM_RELOAD)) {
                    msg(sender, "你没有权限执行此命令。", NamedTextColor.RED);
                    return true;
                }
                String err = plugin.reload();
                if (err == null) {
                    msg(sender, "配置已重载。", NamedTextColor.GREEN);
                    showStatus(sender);
                } else {
                    msg(sender, "重载失败，还在用旧配置：" + err, NamedTextColor.RED);
                }
            }
            default -> msg(sender, "用法：/" + label + " [reload|status]", NamedTextColor.YELLOW);
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length != 1) {
            return List.of();
        }
        List<String> subs = sender.hasPermission(PERM_RELOAD) ? List.of("status", "reload") : List.of("status");
        return StringUtil.copyPartialMatches(args[0], subs, new ArrayList<>());
    }

    private void showStatus(CommandSender sender) {
        PluginSettings s = plugin.getSettings();
        String damage = s.damageEnabled ? s.modeName() + " / " + s.damageValue : "关闭";

        msg(sender, "当前配置", NamedTextColor.AQUA);
        line(sender, "重生锚伤害", Component.text(damage, NamedTextColor.WHITE));
        line(sender, "破坏地形", yesNo(s.breakTerrain));
        line(sender, "禁止合成重生锚", yesNo(s.blockAnchorCraft));
        line(sender, "禁止合成末影水晶", yesNo(s.blockCrystalCraft));
    }

    private static void msg(CommandSender sender, String text, NamedTextColor color) {
        sender.sendMessage(PREFIX.append(Component.text(text, color)));
    }

    private static void line(CommandSender sender, String name, Component value) {
        sender.sendMessage(Component.text("- " + name + "：", NamedTextColor.GRAY).append(value));
    }

    private static Component yesNo(boolean b) {
        return b ? Component.text("是", NamedTextColor.GREEN) : Component.text("否", NamedTextColor.RED);
    }
}
