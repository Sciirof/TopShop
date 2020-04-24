package com.pigeoncraft.topshop;

import java.util.List;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.milkbowl.vault.economy.Economy;

public class ShopGUI implements InventoryHolder, Listener{

	private final Inventory inv;
	private Economy economy;
	private Main plugin;
	private int pageCount = 1;
	private int currentPage = 1;
	private int lastItemIndex = 0;
	private List<ItemStack> shopItemList;
	private boolean removeMode = false;
	private FileConfiguration shopConf;
	
	//constructor
	public ShopGUI(Main plugin, boolean removeMode) {
		String title;
		if(!removeMode) {
			title = ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("shopTitle")); //Change to defaultConfig's
		} else {
			title = ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("removeTitle")); //Change to defaultConfig's
		}
			
		inv = Bukkit.createInventory(this, 27, title);
		economy = plugin.getEconomy();
		this.plugin = plugin;
		this.removeMode = removeMode;
		initializeItems();
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}
	
	@Override
    public Inventory getInventory()
    {
        return inv;
    }
	
	public void initializeItems()
	{
		File shopFile = Main.getShopConfig();
		FileConfiguration config = YamlConfiguration.loadConfiguration(shopFile);
		shopConf = config;
		//config.getConfigurationSection("shop").set("Item1")
		ConfigurationSection sec = config.getConfigurationSection("shop");
		shopItemList = new ArrayList<ItemStack>();
		int itemCount = 0;
		for(String key : sec.getKeys(false)) {
			ItemStack shopItem;
			String name = sec.getConfigurationSection(key).getString("item");
			int buy = sec.getConfigurationSection(key).getInt("buy");
			int sell = sec.getConfigurationSection(key).getInt("sell");
			if(sell > 0) {
				
				shopItem = createGuiItem(Material.getMaterial(name), ChatColor.translateAlternateColorCodes('&', "&a&oBuy: $" + buy), ChatColor.translateAlternateColorCodes('&', "&c&oSell: $" + sell));
			} else {
				shopItem = createGuiItem(Material.getMaterial(name), ChatColor.translateAlternateColorCodes('&', "&a&oBuy: $"  + buy), ChatColor.translateAlternateColorCodes('&', "&c&o&mSell: $" + sell));
			}
			shopItemList.add(shopItem);
			itemCount++;
		}
		if(shopItemList.size() > 24) {
			pageCount = (shopItemList.size() / 24) + 1;
			if(pageCount == 0)
				pageCount = 1;
		}
		for(int i = 0; i <= 24; i++) {
			if(i < shopItemList.size()) {
				inv.setItem(i, shopItemList.get(i));
				lastItemIndex = i;
			}
		}
		inv.setItem(26, createGuiItemWithName(Material.GREEN_WOOL, "Next Page", "Navigate to the next page."));
		inv.setItem(25, createGuiItemWithName(Material.RED_WOOL, "Previous Page", "Navigate to the previous page."));
	}
	
	protected ItemStack createGuiItem(final Material material, final String... lore)
    {
        final ItemStack item = new ItemStack(material, 1);
        final ItemMeta meta = item.getItemMeta();

        // Set the lore of the item
        meta.setLore(Arrays.asList(lore));

        item.setItemMeta(meta);

        return item;
    }
	
	protected ItemStack createGuiItemWithName(final Material material, final String name, final String... lore)
    {
        final ItemStack item = new ItemStack(material, 1);
        final ItemMeta meta = item.getItemMeta();

        // Set the lore of the item
        meta.setDisplayName(name);
        meta.setLore(Arrays.asList(lore));

        item.setItemMeta(meta);

        return item;
    }
	
	public void openInventory(final HumanEntity ent)
    {
        ent.openInventory(inv);
    }
	
	@EventHandler
    public void onInventoryClick(final InventoryClickEvent e)
    {
        if (e.getInventory().getHolder() != this) return;

        e.setCancelled(true);

        final ItemStack clickedItem = e.getCurrentItem();

        // verify current item is not null
        if (clickedItem == null || clickedItem.getType() == Material.AIR) return;

        final Player p = (Player) e.getWhoClicked();

        // Using slots click is a best option for your inventory click's
        if(e.getRawSlot() == 25) {
        	//Previous
        	if(!loadPrevPage()) {
        		p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou are on the first page!"));
        	}
        }
        if(e.getRawSlot() == 26) {
        	//next page
        	if(!loadNextPage()) {
        		p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou are on the last page!"));
        	}
        }
        if(e.getRawSlot() < 25) {
        	economy = plugin.getEconomy();
        	OfflinePlayer target = Bukkit.getOfflinePlayer(p.getUniqueId());
        	double money = economy.getBalance(target);
        	double buy = (double) Integer.parseInt(clickedItem.getItemMeta().getLore().get(0).replaceAll("[^0-9]", ""));
        	double sell = (double) Integer.parseInt(clickedItem.getItemMeta().getLore().get(1).replaceAll("[^0-9]", ""));
        	if(e.getClick() == ClickType.LEFT && !removeMode) {
        		if(p.hasPermission("topshop.buy")) {
        			if(money >= buy) {
                		if(freeSlot(p)) {
                			ItemStack item = new ItemStack(clickedItem.getType(), 1);
                			p.getInventory().addItem(item);
                			economy.withdrawPlayer(target, buy);
                		} else {
                			p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou don't have a free slot in your inventory!"));
                		}
                	} else {
                		p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou don't have enough money!"));
                	}
        		} else {
        			p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&4You do not have permission!"));
        		}
        		
        	}
        	if(e.getClick() == ClickType.RIGHT && !removeMode) {
        		if(p.hasPermission("topshop.sell")) {
        			if(sell > 0) {
            			ItemStack[] playerItems = p.getInventory().getContents();
            			boolean hasItem = false;
            			for(ItemStack item : playerItems) {
            				if(item.getType() == clickedItem.getType()) {
            					int stackSize = item.getAmount();
            					item.setAmount(stackSize - 1);
            					economy.depositPlayer(p, sell);
            					p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aThe shop paid you $"+ sell));
            					hasItem = true;
            					break;
            				}
            				if(!hasItem) {
            					p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou don't have that item!"));
            				}
            			}
            		} else {
            			p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cThe shop is currently not buying this item!"));
            		}
        		} else {
        			p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&4You do not have permission!"));
        		}
        	}
        	if(e.getClick() == ClickType.SHIFT_RIGHT && !removeMode) {
        		if(p.hasPermission("topshop.sell")) {
        			if(sell > 0) {
            			ItemStack[] playerItems = p.getInventory().getContents();
            			boolean hasItem = false;
            			for(ItemStack item : playerItems) {
            				int stackSize = item.getAmount();
            				if(item.getType() == clickedItem.getType() && stackSize == clickedItem.getMaxStackSize()) {
            					item.setAmount(0);
            					economy.depositPlayer(p, sell * stackSize);
            					p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aThe shop paid you $"+ sell * stackSize));
            					hasItem = true;
            					break;
            				}
            				if(!hasItem) {
            					p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou don't have a stack!"));
            				}
            			}
            		} else {
            			p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cThe shop is currently not buying this item!"));
            		}
        		} else {
        			p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&4You do not have permission!"));
        		}
        	}
        	if(e.getClick() == ClickType.SHIFT_LEFT && !removeMode) {
        		if(p.hasPermission("topshop.buy")) {
        			int maxStackSize = clickedItem.getMaxStackSize();
            		if(money >= buy * maxStackSize) {
                		if(freeSlot(p)) {
                			ItemStack item = new ItemStack(clickedItem.getType(), maxStackSize);
                			p.getInventory().addItem(item);
                			economy.withdrawPlayer(target, buy * maxStackSize);
                		} else {
                			p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou don't have a free slot in your inventory!"));
                		}
                	} else {
                		p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou don't have enough money!"));
                	}
        		} else {
        			p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&4You do not have permission!"));
        		}
        	}
        	if(e.getClick() == ClickType.LEFT && removeMode) {
        		if(currentPage > 1) {
        			if(e.getRawSlot() + 24 * (currentPage - 1) < shopItemList.size() - 1) {
        				FileConfiguration config = shopConf;
        				ConfigurationSection sec = config.getConfigurationSection("shop");
        				for(String key : sec.getKeys(false)) {
        					int keyVal = Math.abs(Integer.parseInt(key.replaceAll("[^0-9]", "")));
        					if(key == "item-" + (e.getRawSlot() + 24 * (currentPage - 1))){
            					shopConf.set("shop.item-" + (e.getRawSlot() + 24 * (currentPage - 1)), null);
        					}
        					if( keyVal > e.getRawSlot() + 24 * (currentPage - 1)) {
        						String name = sec.getConfigurationSection(key).getString("item");
            					int _buy = sec.getConfigurationSection(key).getInt("buy");
            					int _sell = sec.getConfigurationSection(key).getInt("sell");
            					shopConf.set("shop.item-" + keyVal, null);
            					shopConf.set("shop.item-" + (keyVal - 1) + ".item", name);
            					shopConf.set("shop.item-" + (keyVal - 1) + ".buy", _buy);
            					shopConf.set("shop.item-" + (keyVal - 1) + ".sell", _sell);
        					}
        				}
        			} else {
        				shopConf.set("shop.item-" + (e.getRawSlot() + 24 * (currentPage - 1)), null);
        			}
        			shopItemList.remove(e.getRawSlot() + 24 * (currentPage - 1));
        			inv.setItem(e.getRawSlot(), new ItemStack(Material.AIR));
        			try {
						shopConf.save(Main.getShopConfig());
					} catch (IOException e1) {
						
					}
        			plugin.setupInventories();
        			p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cItem "  + (e.getRawSlot() + 24 * (currentPage - 1)) + " was removed from the shop"));
        			ShopGUI shopgui = new ShopGUI(plugin, true);
        			shopgui.openInventory(p);
        		} else {
        			if(e.getRawSlot() < shopItemList.size() - 1) {
        				FileConfiguration config = shopConf;
        				ConfigurationSection sec = config.getConfigurationSection("shop");
        				for(String key : sec.getKeys(false)) {
        					int keyVal = Math.abs(Integer.parseInt(key.replaceAll("[^0-9]", "")));
        					if(key == "item-" + (e.getRawSlot() + 24 * (currentPage - 1))){
            					shopConf.set("shop.item-" + (e.getRawSlot()), null);
        					}
        					if( keyVal > e.getRawSlot() + 24 * (currentPage - 1)) {
        						String name = sec.getConfigurationSection(key).getString("item");
            					int _buy = sec.getConfigurationSection(key).getInt("buy");
            					int _sell = sec.getConfigurationSection(key).getInt("sell");
            					shopConf.set("shop.item-" + keyVal, null);
            					shopConf.set("shop.item-" + (keyVal - 1) + ".item", name);
            					shopConf.set("shop.item-" + (keyVal - 1) + ".buy", _buy);
            					shopConf.set("shop.item-" + (keyVal - 1) + ".sell", _sell);
        					}
        				}
        			} else {
        				shopConf.set("shop.item-" + (e.getRawSlot()), null);
        			}
        			shopItemList.remove(e.getRawSlot());
        			inv.setItem(e.getRawSlot(), new ItemStack(Material.AIR));
        			try {
						shopConf.save(Main.getShopConfig());
					} catch (IOException e1) {
						
					}
        			plugin.setupInventories();
        			p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cItem was removed from the shop"));
        			ShopGUI shopgui = new ShopGUI(plugin, true);
        			shopgui.openInventory(p);
        		}
        	}
        }
    }
	
	private boolean freeSlot(Player player) {
		if(player.getInventory().firstEmpty() > -1) {
			return true;
		}
		return false;
	}
	
	private boolean loadNextPage() {
		if (currentPage < pageCount) {
			//set items
			inv.clear();
			inv.setItem(26, createGuiItemWithName(Material.GREEN_WOOL, "Next Page", "Navigate to the next page."));
			inv.setItem(25, createGuiItemWithName(Material.RED_WOOL, "Previous Page", "Navigate to the previous page."));
			int y = 0;
			for(int x = lastItemIndex; x <= lastItemIndex + 24; x++) {
				if(x < shopItemList.size() && y <= 24) {
					inv.setItem(y, shopItemList.get(x));
					lastItemIndex = x;
					y++;
				}
			}
			currentPage++;
			return true;
		}
		return false;
	}
	
	private boolean loadPrevPage() {
		if(currentPage > 1) {
			inv.clear();
			inv.setItem(26, createGuiItemWithName(Material.GREEN_WOOL, "Next Page", "Navigate to the next page."));
			inv.setItem(25, createGuiItemWithName(Material.RED_WOOL, "Previous Page", "Navigate to the previous page."));
			//set items
			int y = 0;
			for(int x = lastItemIndex - 26; x <= lastItemIndex + 24; x++) {
				if(x < shopItemList.size() && y <= 24) {
					inv.setItem(y, shopItemList.get(x));
					lastItemIndex = x;
					y++;
				}
			}
			currentPage--;
			return true;
		}
		return false;
	}
}
