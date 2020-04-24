<p align="center"> 
<img src="https://i.ibb.co/0KWhPY8/Top-Shop-Banner.png">
</p>

[![Discord](https://img.shields.io/discord/703255127347429386?color=%23738ADB&label=discord)](https://discord.gg/jPJ9Ugs)
[![GitHub release (latest by date)](https://img.shields.io/github/v/release/Sciirof/TopShop)](https://github.com/Sciirof/TopShop/releases/latest/)
# TopShop
TopShop is a GUI AdminShop plugin that allows server admins to add items to a global GUI shop. 
Users with the correct permissions are able to view, buy and sell in this shop.

# Installation
**TopShop uses [Vault](https://www.spigotmc.org/resources/vault.34315/) make sure you have that installed as well as an economy plugin! (if you have Essentials installed this comes with an economy)**

Download the latest [Release](https://github.com/Sciirof/TopShop/releases/) (or the one you need) and place it in your server's plugin folder.

```
YourServer\
    \plugins
        \TopShop.jar
```

# Usefull Links
TODO: Add wiki

# Config
#### config.yml
Currently the config doesn't offer much I'm still looking through stuff that might be useful to customize to give a unique experience to your server.
```
#####################################
#                                   #
#        TopShop - By Sciirof       # 
#            version: 1.0           #
#                                   #
#####################################

#Shop GUI title you can use color codes
shopTitle: "&d&lTopShop&r&7 - Weclome"
```

# Shop
#### shops.yml
**Unless you know what you're doing I suggest letting the plugin add items via the `/ts add` command**
```
shop:
  item-0:
    item: COBBLESTONE
    buy: 10
    sell: 5
  item-1:
    item: CRAFTING_TABLE
    buy: 10
    sell: 0
```

# Important
This plugin was made during some free time in a night. The code is currently messy and being cleaned up.
