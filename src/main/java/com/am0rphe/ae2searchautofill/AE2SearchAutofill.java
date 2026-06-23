package com.am0rphe.ae2searchautofill;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.fml.common.Mod;

/**
 * Common mod entrypoint.
 *
 * <p>This mod is a <em>client-only</em> Applied Energistics 2 addon. It registers no blocks,
 * items, registries, network packets or any other server-side content, so a dedicated server
 * that happens to ship the jar simply does nothing with it, and a client that has it installed
 * can still join servers that don't. All behaviour lives in client-gated classes
 * ({@link AE2SearchAutofillClient}, {@code client.AutofillKeyBindings} and the
 * {@code mixin.MEStorageScreenMixin}).
 *
 * <p>This class only exists to own the shared {@link #MODID}/{@link #LOGGER} constants and to
 * give the mod a no-op entrypoint that loads cleanly on every physical side.
 */
@Mod(AE2SearchAutofill.MODID)
public final class AE2SearchAutofill {
    public static final String MODID = "ae2searchautofill";
    public static final Logger LOGGER = LogUtils.getLogger();

    public AE2SearchAutofill() {
        // Nothing to set up on the common side: this addon is purely client-side.
    }
}
