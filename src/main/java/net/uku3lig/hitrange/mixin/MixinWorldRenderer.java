package net.uku3lig.hitrange.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.*;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.EntityRenderManager;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.render.state.WorldRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
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

@Mixin(WorldRenderer.class)
public class MixinWorldRenderer {
    @Shadow
    @Final
    private EntityRenderManager entityRenderManager;

    @Unique
    private Vec3d playerPos = null;

    @Inject(method = "fillEntityRenderStates", at = @At("TAIL"))
    public void storeClientPlayerPos(Camera camera, Frustum frustum, RenderTickCounter tickCounter, WorldRenderState renderStates, CallbackInfo ci) {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        HitRangeConfig config = HitRange.getManager().getConfig();
        if (!config.isEnabled() || player == null || !config.isShowSelf()) return;

        float tickDelta = tickCounter.getTickProgress(false);
        double px = MathHelper.lerp(tickDelta, player.lastRenderX, player.getX());
        double py = MathHelper.lerp(tickDelta, player.lastRenderY, player.getY());
        double pz = MathHelper.lerp(tickDelta, player.lastRenderZ, player.getZ());
        Vec3d playerPos = new Vec3d(px, py, pz);

        Vec3d cameraPos = renderStates.cameraRenderState.pos;
        this.playerPos = playerPos.subtract(cameraPos);
    }

    @Inject(method = "pushEntityRenders", at = @At(value = "TAIL"))
    public void renderFirstPersonCircle(MatrixStack matrices, WorldRenderState renderStates, OrderedRenderCommandQueue queue, CallbackInfo ci) {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        HitRangeConfig config = HitRange.getManager().getConfig();
        if (!config.isEnabled() || !config.isShowSelf()) return;

        var state = (PlayerEntityRenderState) this.entityRenderManager.getAndUpdateRenderState(player, 0);

        matrices.push();
        matrices.translate(this.playerPos);
        queue.submitCustom(matrices, CircleRenderer.getCurrentLayer(), (entry, vertices) -> CircleRenderer.drawCircle(entry, vertices, state));
        matrices.pop();
    }
}
