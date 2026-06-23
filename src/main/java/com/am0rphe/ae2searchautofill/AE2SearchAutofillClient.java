package com.am0rphe.ae2searchautofill;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.Mod;

/**
 * Client-only entrypoint. Loaded only on {@link Dist#CLIENT}, never on a dedicated server.
 *
 * <p>There is nothing to wire up imperatively here: the keybind and its screen listener are
 * registered declaratively by {@code client.AutofillKeyBindings} (via {@code @EventBusSubscriber}),
 * and the search-bar click behaviour is applied by {@code mixin.MEStorageScreenMixin}. This class
 * stays as the client entrypoint so the intent ("this jar carries client-side code") is explicit.
 */
@Mod(value = AE2SearchAutofill.MODID, dist = Dist.CLIENT)
public final class AE2SearchAutofillClient {
    public AE2SearchAutofillClient() {
    }
}
