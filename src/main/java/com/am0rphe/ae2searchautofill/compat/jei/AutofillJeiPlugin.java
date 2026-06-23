package com.am0rphe.ae2searchautofill.compat.jei;

import org.jetbrains.annotations.Nullable;

import com.am0rphe.ae2searchautofill.AE2SearchAutofill;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.runtime.IBookmarkOverlay;
import mezz.jei.api.runtime.IIngredientListOverlay;
import mezz.jei.api.runtime.IJeiRuntime;

import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

/**
 * JEI integration. This class is the only one in the mod that touches the JEI API, so it must never
 * be loaded when JEI is absent — and it isn't: JEI instantiates it via the {@link JeiPlugin}
 * annotation, and the only other reference to it ({@link #hoveredStack()}) is called behind a
 * {@code ModList.isLoaded("jei")} guard, so the JVM only links this class when JEI is present.
 *
 * <p>It captures the {@link IJeiRuntime} while JEI is running and exposes the item the mouse is
 * hovering over JEI's ingredient list or bookmarks, which the autofill keybind uses as a source.
 */
@JeiPlugin
public class AutofillJeiPlugin implements IModPlugin {
    private static final Identifier PLUGIN_UID =
            Identifier.fromNamespaceAndPath(AE2SearchAutofill.MODID, "jei_plugin");

    /** The live JEI runtime, or {@code null} while JEI isn't running (e.g. before the world loads). */
    @Nullable
    private static IJeiRuntime runtime;

    @Override
    public Identifier getPluginUid() {
        return PLUGIN_UID;
    }

    @Override
    public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
        runtime = jeiRuntime;
    }

    @Override
    public void onRuntimeUnavailable() {
        runtime = null;
    }

    /**
     * @return the {@link ItemStack} currently hovered in JEI's ingredient list or bookmark overlay,
     *         or {@link ItemStack#EMPTY} when JEI has no runtime yet, nothing is hovered there, or a
     *         non-item ingredient (fluid, ...) is hovered. The ingredient list takes priority over
     *         bookmarks; the two never overlap on screen so order only matters in theory.
     */
    public static ItemStack hoveredStack() {
        IJeiRuntime current = runtime;
        if (current == null) {
            return ItemStack.EMPTY;
        }

        // getIngredientUnderMouse(type) returns the hovered ingredient cast to the requested type,
        // or null when nothing (or a different ingredient type) is under the mouse.
        IIngredientListOverlay listOverlay = current.getIngredientListOverlay();
        ItemStack fromList = listOverlay.getIngredientUnderMouse(VanillaTypes.ITEM_STACK);
        if (fromList != null && !fromList.isEmpty()) {
            return fromList;
        }

        IBookmarkOverlay bookmarkOverlay = current.getBookmarkOverlay();
        ItemStack fromBookmark = bookmarkOverlay.getIngredientUnderMouse(VanillaTypes.ITEM_STACK);
        if (fromBookmark != null && !fromBookmark.isEmpty()) {
            return fromBookmark;
        }

        return ItemStack.EMPTY;
    }
}
