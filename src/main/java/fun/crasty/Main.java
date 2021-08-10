package fun.crasty;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    private static Economy economy = null;

    StockAndWare stockAndWare = new StockAndWare(this);
    StockWareEditor stockWareEditor = new StockWareEditor(this);

    @Override
    public void onEnable() {
        if (!setupEconomy() ) {
            System.out.println("Vault not found!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        getConfig().options().copyDefaults();
        saveDefaultConfig();



        Bukkit.getPluginManager().registerEvents(stockAndWare, this);
        Bukkit.getPluginManager().registerEvents(stockWareEditor, this);
        Bukkit.getPluginCommand("job_editor").setExecutor(stockWareEditor);
    }

    private boolean setupEconomy()
    {
        RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (economyProvider != null) {
            economy = economyProvider.getProvider();
        }

        return (economy != null);
    }

    public static Economy getEconomy() {
        return economy;
    }

}
