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

## Commands
A bunch of commands to help you set the durability of an item using its ID or Tag, or bulk setting by using wildcards
- `/cd armorMultiplier <True or False>`
  - _whether to use the vanilla armor multipliers for armor_ (more on this below)
- `/cd clear <Optional Item ID or Tag>`: 
  - _sets all configured items back to their default vanilla durabilities_ 
  - _without the optional argument, clears all configured durabilities._
  - _with the optional argument, clears that specified item or tag._
- `/cd list`
  - *lists all items, tags and their new durabilities.*


- `/cd set item <Item ID or Tag> <Durability>`
  - *sets an item (or tag) to a durability*
- `/cd set wildcard <String with Wildcards> <Durability>` 
  - *adds items using wildcards to a temporary context* (more on this below)


- `/cd context list`
  - *lists all items in your current context.*
- `/cd context clear <Optional Item ID>`
  - *clears all items in your current context.*
- `/cd context set item <Item ID or Tag> <Durability>`
  - *sets an item in your current context to a durability.*
- `/cd context set all <Durability>`
  - *sets all items in your current context to a durability.*
- `/cd context filter <TOOL / WEAPON / ARMOUR / OTHER>`
  - *filters items in your current context list based off of the given input* (**KEEPS** the thing you inputted, look at example below) 
- `/cd context confirm`
  - *applies all the item configurations in your current context into your main config.*
- `/cd context cancel`
  - *cancels the current context.*

---

### Some Examples:

**Setting a diamond pickaxe's durability to 5000:**\
`/cd set item diamond_pickaxe 5000`

**Setting all diamond tools durability to 5000:**\
`/cd set item #cd:tools/diamond 5000`

**An example attempt of setting all minecraft diamond tools to 5000 using wildcards:**

`cd set wildcard minecraft:*diamond* 5000`

This then puts it into your temporary context, which would match with all diamond tools, 
and diamond armor, which you don't want, you'd then have to either:
- run `/cd context filter TOOL` to keep all tools and filter out armour
- or just manually remove all the armour items.

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