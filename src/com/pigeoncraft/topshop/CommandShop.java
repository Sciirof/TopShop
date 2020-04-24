package com.pigeoncraft.topshop;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatColor;

public class CommandShop implements CommandExecutor {
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(command.getName().equalsIgnoreCase("shop")) {
			if(sender instanceof Player) {
				Player player = (Player) sender;
				if(player.hasPermission("topshop.shop")) {
					ShopGUI shop = Main.getShopGUI();
					shop.openInventory(player);
					return true;
				} else {
					sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&4You do not have permission!"));
					return true;
				}
				
			} else {
				sender.sendMessage("You can not do this from your console.");
				return true;
			}
		}
		return false;
	}
}
