package net.mistshore.smpca;

import java.io.File;
import java.util.logging.Level;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class MistShoreSMPCAPlugin extends JavaPlugin {

    // reload 在命令线程跑，Folia 下监听器又在各个区域线程里，加个 volatile 省得读到旧值
    private volatile PluginSettings settings = PluginSettings.DEFAULT;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        String err = reload();
        if (err != null) {
            getLogger().severe("config.yml 读取失败，插件不加载了：" + err);
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new AnchorExplosionListener(this), this);
        pm.registerEvents(new CraftingListener(this), this);

        PluginCommand cmd = getCommand("mistshoresmpca");
        if (cmd != null) {
            MistShoreCommand executor = new MistShoreCommand(this);
            cmd.setExecutor(executor);
            cmd.setTabCompleter(executor);
        }

        getLogger().info("MistShoreSMPCA 已启用。关注Alizawa的B站并游玩MistShore.net");
    }

    public PluginSettings getSettings() {
        return settings;
    }

    /**
     * 重新读一遍 config.yml。
     * 读成功返回 null，失败返回原因，这时候旧配置不会被动到。
     */
    public synchronized String reload() {
        // 没用 reloadConfig()，那个遇到写坏的 yml 会直接换成一份空配置。
        // 这里先单独 load 出来，校验过了再替换
        YamlConfiguration yml = new YamlConfiguration();
        try {
            yml.load(new File(getDataFolder(), "config.yml"));
            settings = PluginSettings.load(yml);
            return null;
        } catch (Exception e) {
            getLogger().log(Level.WARNING, "重载 config.yml 出错", e);
            return e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName();
        }
    }
}
