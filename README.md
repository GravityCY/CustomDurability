# 💪 Custom Durability
A Fabric Mod that allows for changing the durabilities of all items.

# 🌐 Overview
Change any item's durability using the Config Screen or with a Command!\

Should work on Integrated Servers and Dedicated Servers.\
Requires [YACL](https://modrinth.com/mod/yacl), [FAPI](https://modrinth.com/mod/fabric-api), and optionally [Mod Menu](https://modrinth.com/mod/modmenu) (for the config screen)\
Need on Client: ✅  Need on Server: ✅

**Haven't tested modded items, I'm mainly just targetting this mod for Vanilla, since it's what I wanted... it probably? should? work with modded items?**

![Sample](https://i.imgur.com/EcObE6G.png)

## 🔧 Config Screen
Currently the way you declare an item's durability through the config screen is by
typing it's id (or item tag) and then the durability with a semicolon in between them. \
Eg. `minecraft:wooden_pickaxe;100` or `wooden_pickaxe;100`

![Config Screen](https://i.imgur.com/3evlmk8.png)

## Command
A Command to set the Durability, using the Item ID or a Tag
+ `/customdurability armorMultiplier <True or False>`
+ `/customdurability clear <Optional Item ID>`
+ `/customdurability list`
+ `/customdurability set <Item ID or Item Tag> <Durability>`

Setting a diamond pickaxe's durability to 5000:\
`/customdurability set diamond_pickaxe 5000`
Setting all diamond tools durability to 5000:\
`/customdurability set #customdurability:tools/diamond 5000`

***To see all the tags this mod adds scroll down***

![Command](https://i.imgur.com/MDsbjTj.png)

## ✖ Armor Multiplier
The Armor Multiplier option is so that you can use a base vanilla value that will multiply
your inputted durability, so if you put 5 for boot armor, it would get multiplied by 13, so you'd get what, 65?
(this is how vanilla does it, and is why helmets, chestplates, leggings and boots don't share the same durability)

**Default Multipliers**:
+ <span style="color:gray;">**Helmet**</span>: 11
+ <span style="color:gray;">**Chestplate**</span>: 16
+ <span style="color:gray;">**Leggings**</span>: 15
+ <span style="color:gray;">**Boots**</span>: 13

## 🕮 Helpful Tags
I added some tags that should help with some of the more common items:

* `#customdurability:tools`
  * `wood`
  * `stone`
  * `iron`
  * `gold`
  * `diamond`
  * `netherite`
* `#customdurability:armor`
  * `leather`
  * `chainmail`
  * `iron`
  * `gold`
  * `diamond`
  * `netherite`

You'd use these by for example doing:
* For Wooden Tools: `#customdurability:tools/wood`
* For Leather Armor: `#customdurability:armor/leather`