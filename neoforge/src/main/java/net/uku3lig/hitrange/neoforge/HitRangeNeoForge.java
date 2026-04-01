package net.uku3lig.hitrange.neoforge;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.uku3lig.hitrange.HitRange;
import net.uku3lig.hitrange.UkulibHook;
import net.uku3lig.ukulib.neoforge.UkulibNFProvider;

@Mod(value = "hitrange", dist = Dist.CLIENT)
public class HitRangeNeoForge {
    public HitRangeNeoForge(ModContainer container) {
        HitRange.onInitialize();
        container.registerExtensionPoint(UkulibNFProvider.class, UkulibHook::new);
    }
}
