package com.am0rphe.ae2searchautofill.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.am0rphe.ae2searchautofill.SearchAutofillScreen;

import appeng.client.gui.me.common.MEStorageScreen;
import appeng.client.gui.widgets.AETextField;
import appeng.menu.me.common.MEStorageMenu;

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

/**
 * Adds NEI-style click-to-fill to AE2's ME terminal search bar.
 *
 * <p>{@code MEStorageScreen} is the common base of every ME terminal variant (storage, crafting,
 * pattern encoding, ...), so a single mixin here covers them all.
 *
 * <p>The mixin declares {@link AbstractContainerScreen} as its superclass purely so it can reach the
 * inherited {@code getMenu()} and {@code hoveredSlot} members — the real target genuinely extends it.
 * The constructor is never called; it only exists to satisfy the compiler.
 */
@Mixin(MEStorageScreen.class)
public abstract class MEStorageScreenMixin extends AbstractContainerScreen<MEStorageMenu>
        implements SearchAutofillScreen {

    @Shadow
    @Final
    private AETextField searchField;

    private MEStorageScreenMixin(MEStorageMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    /**
     * Feature A — left-clicking the search bar while holding an item fills the bar with that item's
     * name instead of just focusing it. Plain left-clicks with an empty cursor fall through to the
     * vanilla focus logic, and right-clicks (AE2's own "clear search") are left untouched.
     *
     * <p>The {@code isVisible()} check matters because AE2 hides its own search field when the
     * "Use External Search" option is on (the search is delegated to JEI/EMI). Without the guard,
     * a click where the bar used to be would still fill — and consume the click on — a hidden field.
     */
    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    private void ae2searchautofill$fillFromCursorOnClick(MouseButtonEvent event, boolean doubleClick,
                                                         CallbackInfoReturnable<Boolean> cir) {
        if (event.button() != 0 || !this.searchField.isVisible()
                || !this.searchField.isMouseOver(event.x(), event.y())) {
            return;
        }
        ItemStack carried = this.getMenu().getCarried();
        if (!carried.isEmpty()) {
            ae2searchautofill$applySearch(carried);
            // Consume the click so it doesn't merely move the text cursor inside the field.
            cir.setReturnValue(true);
        }
    }

    @Override
    public void ae2searchautofill$autofillFromHovered() {
        // Same rationale as Feature A: never write into the field while AE2 has it hidden behind
        // the external (JEI/EMI) search.
        if (!this.searchField.isVisible()) {
            return;
        }
        ItemStack stack = ItemStack.EMPTY;
        // hoveredSlot is refreshed every frame by AbstractContainerScreen#render, so it is current
        // whenever a key press is dispatched. It resolves both ME grid slots (RepoSlot) and the
        // player inventory slots.
        Slot hovered = this.hoveredSlot;
        if (hovered != null && hovered.hasItem()) {
            stack = hovered.getItem();
        }
        if (stack.isEmpty()) {
            stack = this.getMenu().getCarried();
        }
        ae2searchautofill$applySearch(stack);
    }

    /**
     * Writes the stack's display name into the search field, focuses it through the screen so
     * keyboard input is routed there, and selects everything <em>with the caret left at the end</em>
     * so a single Backspace clears it (and typing immediately replaces it). {@code setValue} fires
     * the field's responder, which re-filters the ME grid live.
     *
     * <p>We deliberately don't call AE2's {@code AETextField#selectAll()}: it parks the caret at
     * position 0 (the start of the selection), which is awkward to erase from. Instead, {@code
     * setValue} already leaves the caret at the end, and {@code setHighlightPos(0)} extends the
     * selection back to the start without moving the caret.
     *
     * <p>Guarded so it's safe to call from anywhere (including the JEI path): it does nothing for an
     * empty stack or while AE2 hides its search field behind the external (JEI/EMI) search.
     */
    @Override
    public void ae2searchautofill$applySearch(ItemStack stack) {
        if (stack.isEmpty() || !this.searchField.isVisible()) {
            return;
        }
        this.searchField.setValue(stack.getHoverName().getString());
        this.setFocused(this.searchField);
        this.searchField.setHighlightPos(0);
    }
}