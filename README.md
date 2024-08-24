#  Custom Durability
A Fabric Mod that allows for changing the durabilities of all items.

# Overview
Change any item's durability with a Command!

**Should work on Integrated Servers and Dedicated Servers.**\
**Requires [FAPI](${fabric})**\
**Need on Client: ✅  Need on Server: ✅**\
**Report any Issues on [GitHub](https://github.com/GravityCY/CustomDurability/issues) or my [Discord Server](https://discord.gg/k6SEKxDbpF)**

**Haven't tested modded items, I'm mainly just targetting this mod for Vanilla, since it's what I wanted... it probably? should? work with modded items?**

![Sample](https://i.imgur.com/EcObE6G.png)

## Command
A Command to set the durability, using the Item ID or a Tag
- `/cd armorMultiplier <True or False>`
  - _Whether to use the vanilla armor multipliers for armor_
- `/cd clear <Optional Item ID or Tag>`: 
  - _Without the optional argument, clears the whole config._
  - _With the optional argument, clears that specified item or tag._
- `/cd list`
  - *lists all items, tags and their durabilities in your config*


- `/cd set item <Item ID or Tag> <Durability>`
  - *sets an item (or tag) to a durability*
- `/cd set wildcard <String with Wildcards> <Durability>` 
  - *adds items using wildcards to a temporary context*


- `/cd context list`
  - *lists all items in your current context.*
- `/cd context clear <Optional Item ID>`
  - *clears all items in your current context.*
- `/cd context set <Item ID or Tag> <Durability>`
  - *sets an item in your current context to a durability.*
- `/cd context confirm`
  - *applies all the item configurations in your context into your main config.*
- `/cd context cancel`
  - *cancels the current context.*

---

### Some Examples:

**Setting a diamond pickaxe's durability to 5000:**\
`/cd set item diamond_pickaxe 5000`

**Setting all diamond tools durability to 5000:**\
`/cd set item #cd:tools/diamond 5000`

**An example attempt of setting all diamond tools to 5000:**

`cd set wildcard minecraft:*diamond* 5000`

This then puts it into your temporary config, which would match with all diamond tools, 
and diamond armor, you'd then have to manually remove all the armor items.

---

## Context
`/cd set wildcard` doesn't immediately put all the items it matches into your main config, 
in order to not add things the wildcard wasn't supposed to get, it puts it in a temporary config that you can remove or edit the entries until you decide to 
apply it to the main config, either through `/cd context confirm` or by clicking the button shown when running the wildcard command.

---

## ✖ Armor Multiplier
The Armor Multiplier option is so that you can use a base vanilla value that will multiply
your inputted durability, so if you put 5 for boot armor, it would get multiplied by 13, so you'd get what, 65?
(this is how vanilla does it, and is why helmets, chestplates, leggings and boots don't share the same durability)

**Default Multipliers**:
- <span style="color:gray;">**Helmet**</span>: 11
- <span style="color:gray;">**Chestplate**</span>: 16
- <span style="color:gray;">**Leggings**</span>: 15
- <span style="color:gray;">**Boots**</span>: 13

---

## 🕮 Helpful Tags
I added some tags that should help with some of the more common items:

- `#cd:tools`
  - `wood`
  - `stone`
  - `iron`
  - `gold`
  - `diamond`
  - `netherite`
- `#cd:armor`
  - `leather`
  - `chainmail`
  - `iron`
  - `gold`
  - `diamond`
  - `netherite`

You'd use these by for example doing:
- For Wooden Tools: `/cd set item #cd:tools/wood <durability>`
- For Leather Armor: `/cd set item #cd:armor/leather <durability>`