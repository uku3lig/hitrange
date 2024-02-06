package net.uku3lig.hitrange.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
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
        ClientPlayerEntity player = MinecraftClient.getInstance().player;

        if (!config.isEnabled() || entity.equals(player)) return;

        int color = config.getColor();
        if (config.isRandomColors()) {
            color = entity.getNameForScoreboard().hashCode() | 0xFF000000;
        } else if (entity.isInRange(player, config.getRadius())) {
            color = config.getInRangeColor();
        }

        // z fighting grrrr !!!!
        float dy = (entity.isInSneakingPose() ? 0.125f : 0) + config.getHeight() + ((float) entity.squaredDistanceTo(player) / 10_000.0f);

        CircleRenderer.drawCircle(matrices, vertexConsumers, config.getRenderMode(), dy, color);
    }
}
