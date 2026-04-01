package net.uku3lig.hitrange.fabric;

import net.fabricmc.api.ClientModInitializer;
import net.uku3lig.hitrange.HitRange;

public class HitRangeFabric implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        HitRange.onInitialize();
    }
}
