package net.uku3lig.hitrange.mixin;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.uku3lig.hitrange.CircleRenderer;
import net.uku3lig.hitrange.HitRange;
import net.uku3lig.hitrange.config.HitRangeConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntityRenderer.class)
public abstract class MixinPlayerEntityRenderer  {
    @Inject(method = "render(Lnet/minecraft/client/network/AbstractClientPlayerEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At("TAIL"))
    private void render(AbstractClientPlayerEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        HitRangeConfig config = HitRange.getManager().getConfig();
        if (!config.isEnabled()) return;

        int color = config.isRandomColors() ? entity.getNameForScoreboard().hashCode() | 0xFF000000 : config.getColor();
        float dy = (entity.isInSneakingPose() ? 0.125f : 0) + config.getHeight() + 0.0001f;

        CircleRenderer.drawCircle(matrices, vertexConsumers, config.getRenderMode(), dy, color);
    }
}
