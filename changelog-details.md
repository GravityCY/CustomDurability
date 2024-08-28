## Command changes, updates, and improvements!
### Updated `/cd list`, now shows text you can click to either edit, or remove an entry! 
![Image](https://i.ibb.co/khtPdpz/p-Iu-Azf-HLHm.png)
- Clicking `[Edit]` will just automatically type the command `/cd set item <item_name> <previous_durability>`
- Clicking `[Remove]` will automatically run the command `/cd clear <item_name>`

---

- *Each time you click remove, (or run the remove command) it lists all items again, so make sure you're always scrolled down. (I feel slightly unsure about this behaviour, so if you think this is bad let me know!)*
- Also shows the real durability of an armor if the armor multiplier config is on.
- Also, now shows all the contents of an item tag, whenever possible.

---
### Updated `/cd set <item_name> <durability>`
Now you have to specify either:
  - `item`, to specify an actual minecraft item or tag.
  - `wildcard`, you can now try to add things in bulk using wildcards! _(I do want to say I implemented my own wildcard parser and in all my tests it works perfectly, but if it seems like it's not co-operating, let me know!)_

---

**`item` Examples**:
- `/cd set item minecraft:diamond_pickaxe 100`
- `/cd set item #cd:tools/diamond 100`

---

**`wildcard` Examples**:
1. `/cd set wildcard minecraft:*diamond* 100`
2. `/cd set wildcard minecraft:*wooden* 100`
3. `/cd set wildcard minecraft:diamond* 100`
4. `/cd set wildcard minecraft:*diamond 100`

- What **1**, and **2**  will do is add anything that starts with `minecraft:` and has `diamond` or `wooden` anywhere in its id!
- **3** adds anything that starts with `minecraft:diamond` and can end with whatever so `minecraft:diamond_pickaxe` will match.
- **4** adds anything that starts with `minecraft:` and ends in `diamond` so `minecraft:pickaxe_diamond` would match, although that's not a real item.

## Context
When you run `/cd set wildcard <string> <durability>` it won't immediately add the matched items into your config, it will add them to a temporary context, which you can clean up any unwanted matches first.

Say you run `/cd set wildcard minecraft:*diamond* 100` that will add all diamond armors and diamond tools to your context, so you can selectively remove or edit which ones you want to add to your config!

---

This also introduces the `/cd context` command, this pretty much has exactly the same commands as inside of `/cd`, basically `list`, `clear`, `set`, except for 3 unique ones, `confirm`, `cancel` and `filter`

- `confirm` will add all the items you have in your current context to your config.
- `cancel` will just clear the context.
- `filter` will filter all of your items in your current context based off of 4 inputs `TOOL` `WEAPON` `ARMOUR` `OTHER`
  - Make sure you have something in your context and then run for example: `/cd context filter TOOL`, this will keep only tools in your context.

- `set` also adds `all` command, which sets all the durabilities in your context list to a given durability