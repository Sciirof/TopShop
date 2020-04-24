package com.pigeoncraft.topshop;

import java.io.File;
import java.io.IOException;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

//import net.milkbowl.vault.economy.Economy;

public class CommandAdd implements CommandExecutor {
	
	private Main plugin;
	
	public CommandAdd(Main plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		//Economy economy = Main.getEconomy();
		if(command.getName().equalsIgnoreCase("ts")) {
			if(args.length == 3) {
				if(args[0].equalsIgnoreCase("add")) {
					if(sender instanceof Player) {
						Player player = (Player) sender;
						if(!player.getInventory().getItemInMainHand().equals(null) || !player.getInventory().getItemInMainHand().equals(Material.AIR)) {
							//has item in hand
							if(player.hasPermission("topshop.add")) {
								if(args.length == 3) {
									File shopFile = Main.getShopConfig();
									FileConfiguration config = YamlConfiguration.loadConfiguration(shopFile);
									ConfigurationSection sec = config.getConfigurationSection("shop");
									int itemCount = 0;
									for(String key : sec.getKeys(false)) {
										itemCount++;
									}
									sec.createSection("item-" + itemCount);
									ConfigurationSection itemSec = sec.getConfigurationSection("item-" + itemCount);
									itemSec.set("item", player.getInventory().getItemInMainHand().getType().toString());
									itemSec.set("buy", Integer.parseInt(args[1]));
									itemSec.set("sell", Integer.parseInt(args[2]));
									try {
										config.save(shopFile);
										plugin.setupInventories();
									} catch (IOException e) {

									}
									sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&2Item has been added to shop."));
									return true;
								} else {
									sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cUsage: /ts add <buyPrice> <sellPrice>"));
									sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cExample: /ts add 100 85"));
									return true;
								}
							} else {
								sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&4You do not have permission!"));
								return true;
							}
						} else {
							//hand is empty
							sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou must have something in your hand!"));
							return true;
						}
					} else {
						//add by item id check if arguments length = 3
						return true;
					}
				} else {
					return false;
				}
				}
			if(args.length == 1) {
				if(args[0].equalsIgnoreCase("reload")) {
					plugin.reloadConfig();
					plugin.setupInventories();
					sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aTopShop has been reloaded!"));
				}
			}
			}
		return false;
	}
}
