package net.uku3lig.hitrange.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.world.phys.Vec3;
import net.uku3lig.hitrange.CircleRenderer;
import net.uku3lig.hitrange.HitRange;
import net.uku3lig.hitrange.config.HitRangeConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntityRenderer.class)
public abstract class MixinLivingEntityRenderer {
    @Inject(method = "submit(Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/level/CameraRenderState;)V", at = @At("TAIL"))
    private void render(LivingEntityRenderState state, PoseStack matrices, SubmitNodeCollector submitNodeCollector, CameraRenderState cameraRenderState, CallbackInfo ci) {
        HitRangeConfig config = HitRange.getManager().getConfig();
        LocalPlayer player = Minecraft.getInstance().player;

        Vec3 pos = new Vec3(state.x, state.y, state.z);

        if (!(state instanceof AvatarRenderState playerState)) return;
        if (!config.isEnabled() || player == null || player.getId() == playerState.id) return;
        if (!pos.closerThan(player.position(), config.getMaxDistance())) return;
        if (config.isNearestOnly() && (HitRange.getNearest() == null || HitRange.getNearest().getId() != playerState.id))
            return;
        if (playerState.deathTime > 0.0f || playerState.isInvisibleToPlayer || playerState.bedOrientation != null)
            return;

        submitNodeCollector.submitCustomGeometry(matrices, CircleRenderer.getCurrentType(playerState),
                (entry, vertices) -> CircleRenderer.drawCircle(entry, vertices, playerState));
    }
}
