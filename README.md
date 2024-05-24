#  Custom Durability
A Fabric Mod that allows for changing the durabilities of all items.

# Overview
Change any item's durability with a Command!

Should work on Integrated Servers and Dedicated Servers.\
Requires [FAPI](${fabric_api_url})\
Need on Client: ✅  Need on Server: ✅

**Haven't tested modded items, I'm mainly just targetting this mod for Vanilla, since it's what I wanted... it probably? should? work with modded items?**

![Sample](https://i.imgur.com/EcObE6G.png)

## Command
A Command to set the durability, using the Item ID or a Tag
+ `/cd armorMultiplier <True or False>`
+ `/cd clear <Optional Item ID>`
+ `/cd list`
+ `/cd set <Item ID or Item Tag> <Durability>`

Setting a diamond pickaxe's durability to 5000:\
`/cd set diamond_pickaxe 5000`
Setting all diamond tools durability to 5000:\
`/cd set #customdurability:tools/diamond 5000`

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

* `#cd:tools`
  * `wood`
  * `stone`
  * `iron`
  * `gold`
  * `diamond`
  * `netherite`
* `#cd:armor`
  * `leather`
  * `chainmail`
  * `iron`
  * `gold`
  * `diamond`
  * `netherite`

You'd use these by for example doing:
* For Wooden Tools: `#cd:tools/wood`
* For Leather Armor: `#cd:armor/leather`