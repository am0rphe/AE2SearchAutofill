package com.am0rphe.ae2searchautofill.client;

import org.lwjgl.glfw.GLFW;

import com.mojang.blaze3d.platform.InputConstants;

import com.am0rphe.ae2searchautofill.AE2SearchAutofill;
import com.am0rphe.ae2searchautofill.SearchAutofillScreen;
import com.am0rphe.ae2searchautofill.compat.jei.AutofillJeiPlugin;

import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;

/**
 * Registers the autofill keybind (feature B) and reacts to it.
 *
 * <p>Both handlers live on the same {@code @EventBusSubscriber}: NeoForge routes
 * {@link RegisterKeyMappingsEvent} (an {@code IModBusEvent}) to the mod bus and
 * {@link ScreenEvent.KeyPressed.Pre} to the game bus automatically.
 *
 * <p>We can't use {@link KeyMapping#consumeClick()} here: vanilla only ticks key mappings while no
 * {@code Screen} is open, and the ME terminal is always a screen. Instead we listen to the screen's
 * own key event and match it against our mapping by hand.
 */
@EventBusSubscriber(modid = AE2SearchAutofill.MODID, value = Dist.CLIENT)
public final class AutofillKeyBindings {
    /** Dedicated controls-screen category so the binding doesn't clutter a vanilla group. */
    public static final KeyMapping.Category CATEGORY =
            new KeyMapping.Category(Identifier.fromNamespaceAndPath(AE2SearchAutofill.MODID, "main"));

    /**
     * The autofill key. Defaults to <em>Right Shift</em>: a non-text key, so it isn't swallowed by
     * the terminal's search field the way a printable key would be (a printable key would type its
     * character into the field instead of — or as well as — triggering the autofill). Binding a
     * sensible non-text key out of the box also means the feature works without the user having to
     * discover that requirement. Users can freely rebind or unbind it from the controls screen.
     */
    public static final KeyMapping AUTOFILL_KEY = new KeyMapping(
            "key.ae2searchautofill.autofill",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_RIGHT_SHIFT,
            CATEGORY);

    private AutofillKeyBindings() {
    }

    @SubscribeEvent
    static void onRegisterKeyMappings(RegisterKeyMappingsEvent event) {
        event.registerCategory(CATEGORY);
        event.register(AUTOFILL_KEY);
    }

    @SubscribeEvent
    static void onScreenKeyPressed(ScreenEvent.KeyPressed.Pre event) {
        // If the user unbinds the key, it can never match a real press — short-circuit cheaply.
        if (AUTOFILL_KEY.isUnbound()) {
            return;
        }
        if (!(event.getScreen() instanceof SearchAutofillScreen screen)
                || !AUTOFILL_KEY.matches(event.getKeyEvent())) {
            return;
        }

        // Prefer an item hovered in JEI's overlay (ingredient list / bookmarks) when JEI is present.
        // The AutofillJeiPlugin reference below is the only link to JEI code; because it sits behind
        // this isLoaded guard, the JVM never loads that class (and never sees a missing JEI API)
        // when JEI is absent.
        if (ModList.get().isLoaded("jei")) {
            ItemStack jeiStack = AutofillJeiPlugin.hoveredStack();
            if (!jeiStack.isEmpty()) {
                screen.ae2searchautofill$applySearch(jeiStack);
                event.setCanceled(true);
                return;
            }
        }

        // Otherwise: AE2 slot under the mouse, falling back to the carried item (existing behaviour).
        screen.ae2searchautofill$autofillFromHovered();
        // Swallow the press so the bound key doesn't also reach the search field as text.
        event.setCanceled(true);
    }
}