package com.pigeoncraft.topshop;

import net.milkbowl.vault.economy.Economy;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
//import org.bukkit.ChatColor;
//import org.bukkit.Material;
//import org.bukkit.command.Command;
//import org.bukkit.command.CommandSender;
//import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
//import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
	
	private Economy econ;
	private static File shopsFile;
	private static ShopGUI shop;
	
	@Override
	public void onEnable() {
		if(!setupEconomy()) {
			this.getLogger().severe("Plugin disabled, Vault does not appear to be active!");
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		}
		getDataFolder().mkdir();
		setupFiles();
		setupInventories();
		this.getCommand("shop").setExecutor(new CommandShop());
		this.getCommand("ts").setExecutor(new CommandAdd(this));
	}
	
	@Override
	public void onDisable() {
		
	}
	
	private boolean setupEconomy() {
        if (Bukkit.getPluginManager().getPlugin("Vault") == null) {
            return false;
        }

        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }
	
    public Economy getEconomy() {
        return econ;
    }
    
    private void setupFiles() {
    	File configFile = new File(getDataFolder()+File.separator+"config.yml");
    	File shopsF = new File(getDataFolder()+File.separator+"shops.yml");
    	shopsFile = shopsF;
    	if(!configFile.exists()) {
    		this.saveDefaultConfig();
    	}
    	if(!shopsF.exists()) {
			try {
				shopsF.createNewFile();
			} catch (IOException e) {
				
			}
    	}
    	FileConfiguration config = YamlConfiguration.loadConfiguration(shopsF);
		config.set("shop.item-0.item", Material.COBBLESTONE.toString());
		config.set("shop.item-0.buy", 10);
		config.set("shop.item-0.sell", 5);
		try {
			config.save(shopsF);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public static File getShopConfig() {
    	return shopsFile;
    }
    
    public void setupInventories() {
    	shop = new ShopGUI(this);
    }
    
    public static ShopGUI getShopGUI() {
    	return shop;
    }
}
