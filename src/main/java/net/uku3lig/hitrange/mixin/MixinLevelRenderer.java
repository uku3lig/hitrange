package net.uku3lig.hitrange.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.state.level.LevelRenderState;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.uku3lig.hitrange.CircleRenderer;
import net.uku3lig.hitrange.HitRange;
import net.uku3lig.hitrange.config.HitRangeConfig;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public class MixinLevelRenderer {
    @Shadow
    @Final
    private EntityRenderDispatcher entityRenderDispatcher;

    @Unique
    private Vec3 playerPos = null;

    @Inject(method = "extractVisibleEntities", at = @At("TAIL"))
    public void storeClientPlayerPos(Camera camera, Frustum frustum, DeltaTracker tickCounter, LevelRenderState renderStates, CallbackInfo ci) {
        LocalPlayer player = Minecraft.getInstance().player;
        HitRangeConfig config = HitRange.getManager().getConfig();
        if (!config.isEnabled() || player == null || !config.isShowSelf()) return;

        float tickDelta = tickCounter.getGameTimeDeltaPartialTick(false);
        double px = Mth.lerp(tickDelta, player.xOld, player.getX());
        double py = Mth.lerp(tickDelta, player.yOld, player.getY());
        double pz = Mth.lerp(tickDelta, player.zOld, player.getZ());
        Vec3 playerPos = new Vec3(px, py, pz);

        Vec3 cameraPos = renderStates.cameraRenderState.pos;
        this.playerPos = playerPos.subtract(cameraPos);
    }

    @Inject(method = "submitEntities", at = @At(value = "TAIL"))
    public void renderFirstPersonCircle(PoseStack matrices, LevelRenderState renderStates, SubmitNodeCollector queue, CallbackInfo ci) {
        LocalPlayer player = Minecraft.getInstance().player;
        HitRangeConfig config = HitRange.getManager().getConfig();
        if (!config.isEnabled() || !config.isShowSelf()) return;

        var state = (AvatarRenderState) this.entityRenderDispatcher.extractEntity(player, 0);

        matrices.pushPose();
        matrices.translate(this.playerPos);
        queue.submitCustomGeometry(matrices, CircleRenderer.getCurrentType(state), (entry, vertices) -> CircleRenderer.drawCircle(entry, vertices, state));
        matrices.popPose();
    }
}
