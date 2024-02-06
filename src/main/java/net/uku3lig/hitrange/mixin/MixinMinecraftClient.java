package net.uku3lig.hitrange.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.uku3lig.hitrange.HitRange;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class MixinMinecraftClient {
    @Inject(method = "tick", at = @At("TAIL"))
    public void getNearestPlayer(CallbackInfo ci) {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (HitRange.getManager().getConfig().isNearestOnly() && player != null) {
            HitRange.setNearest(player.getWorld().getClosestPlayer(player, 30));
        }
    }
}
