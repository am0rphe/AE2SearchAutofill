package com.am0rphe.ae2searchautofill;

import net.minecraft.world.item.ItemStack;

/**
 * Contract implemented (via mixin) by AE2's {@code MEStorageScreen}, the common base of every
 * ME terminal screen (storage, crafting, pattern encoding, ...).
 *
 * <p>It lets the client-side keybind handler ask the currently open terminal to autofill its
 * search field without reaching into AE2 internals directly.
 */
public interface SearchAutofillScreen {
    /**
     * Writes the display name of the item currently under the mouse into the terminal's search
     * field, falling back to the item held on the cursor when nothing is hovered. Does nothing
     * when neither an item is hovered nor carried.
     */
    void ae2searchautofill$autofillFromHovered();

    /**
     * Writes the given stack's display name into the terminal's search field. Used for sources the
     * screen can't see on its own (e.g. an item hovered in JEI's overlay). No-op when the stack is
     * empty or AE2 has hidden its search field (the "Use External Search" option).
     */
    void ae2searchautofill$applySearch(ItemStack stack);
}
