package net.uku3lig.hitrange.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;
import net.uku3lig.hitrange.CircleRenderer;
import net.uku3lig.hitrange.HitRange;
import net.uku3lig.hitrange.config.HitRangeConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntityRenderer.class)
public abstract class MixinLivingEntityRenderer {
    @Inject(method = "render(Lnet/minecraft/client/render/entity/state/LivingEntityRenderState;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;Lnet/minecraft/client/render/state/CameraRenderState;)V", at = @At("TAIL"))
    private void render(LivingEntityRenderState state, MatrixStack matrices, OrderedRenderCommandQueue orderedRenderCommandQueue, CameraRenderState cameraRenderState, CallbackInfo ci) {
        HitRangeConfig config = HitRange.getManager().getConfig();
        ClientPlayerEntity player = MinecraftClient.getInstance().player;

        Vec3d pos = new Vec3d(state.x, state.y, state.z);

        if (!(state instanceof PlayerEntityRenderState playerState)) return;
        if (!config.isEnabled() || player == null || player.getId() == playerState.id) return;
        if (!pos.isInRange(player.getEntityPos(), config.getMaxDistance())) return;
        if (config.isNearestOnly() && (HitRange.getNearest() == null || HitRange.getNearest().getId() != playerState.id)) return;
        if (playerState.deathTime > 0.0f || playerState.invisibleToPlayer || playerState.sleepingDirection != null) return;

        orderedRenderCommandQueue.submitCustom(matrices, CircleRenderer.getCurrentLayer(),
                (entry, vertices) -> CircleRenderer.drawCircle(entry, vertices, playerState));
    }
}
