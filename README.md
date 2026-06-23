# AE2 Search Autofill

A lightweight, **client-side** [Applied Energistics 2](https://www.curseforge.com/minecraft/mc-mods/applied-energistics-2) addon that brings back NEI-style **click-to-fill** for the ME terminal search bar — so you never type the name of an item you already have in hand or under the mouse.

**Minecraft 26.1.2 · NeoForge**

## Features

- **Click to fill** — hold an item on your cursor and **left-click the search bar** of any ME terminal. The bar fills with that item's name and the grid filters live. (Right-click still clears the search as usual.)
- **Fill from hovered item** — a keybind (default **Right Shift**) that fills the search from whatever item is under your mouse:
  - an item in the ME grid,
  - an item in your inventory,
  - or an item in **JEI**'s ingredient list / bookmarks (when JEI is installed).
- After filling, the text is **selected with the caret at the end**, so a single Backspace clears it and typing replaces it immediately.

Works on every ME terminal variant (storage, crafting, pattern encoding, ...).

## Requirements

- **Applied Energistics 2** `26.1.10-beta` or newer — **required**.
- **JEI (Just Enough Items)** — *optional*. The mod works fine without it; installing it simply enables filling the search from JEI's overlay.

## Usage

Open any ME terminal, then:

- **Hold an item + left-click the search bar** → the search fills with its name.
- **Hover an item + press the keybind** (Right Shift by default) → same thing, from the hovered item.

The keybind can be rebound or unbound under **Options → Controls → AE2 Search Autofill**. Use a non-text key (a modifier or function key): a printable key would be typed into the search field instead of triggering the fill.

## Client-side only

This addon runs entirely on the client. It adds no registries, packets, or server-side logic — you can install it on the client alone, and a client running it can still join servers that don't have it.

## Building from source

```
git clone https://github.com/am0rphe/AE2SearchAutofill.git
cd AE2SearchAutofill
./gradlew build
```

The built jar lands in `build/libs/`. Requires JDK 25.

## License

Licensed under the **GNU Lesser General Public License v3.0 or later** (LGPL-3.0-or-later), matching Applied Energistics 2's own license. See [LICENSE](LICENSE).

## Credits

Created by **am0rphe**. Built on the work of the Applied Energistics 2 team.