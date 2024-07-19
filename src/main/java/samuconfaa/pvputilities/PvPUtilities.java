// PvPUtilities.java

package samuconfaa.pvputilities;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class PvPUtilities extends JavaPlugin {

    private static PvPUtilities instance;
    private ConfigurationManager configManager;


    @Override
    public void onEnable() {
        instance = this;
        configManager = new ConfigurationManager(this);
        configManager.loadConfig();
        getCommand("pvpu").setExecutor(new PvPCommand(this));
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
    }


    public static PvPUtilities getInstance() {
        return instance;
    }

    public ConfigurationManager getConfigManager() {
        return configManager;
    }
}
