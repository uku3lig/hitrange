package net.uku3lig.hitrange.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.uku3lig.hitrange.CircleRenderer;
import net.uku3lig.hitrange.HitRange;
import net.uku3lig.hitrange.config.HitRangeConfig;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(WorldRenderer.class)
public class MixinWorldRenderer {
    @Shadow @Final private BufferBuilderStorage bufferBuilders;

    @Shadow @Final private EntityRenderDispatcher entityRenderDispatcher;

    @Inject(method = "renderEntities", at = @At(value = "TAIL"))
    public void renderFirstPersonCircle(MatrixStack matrices, VertexConsumerProvider.Immediate immediate, Camera camera, RenderTickCounter tickCounter, List<Entity> entities, CallbackInfo ci) {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        HitRangeConfig config = HitRange.getManager().getConfig();
        if (!config.isEnabled() || player == null || !config.isShowSelf()) return;

        float tickDelta = tickCounter.getTickProgress(false);
        double px = MathHelper.lerp(tickDelta, player.lastRenderX, player.getX());
        double py = MathHelper.lerp(tickDelta, player.lastRenderY, player.getY());
        double pz = MathHelper.lerp(tickDelta, player.lastRenderZ, player.getZ());
        Vec3d playerPos = new Vec3d(px, py, pz);

        Vec3d cameraPos = camera.getPos();
        playerPos = playerPos.subtract(cameraPos);

        EntityRenderer<? super ClientPlayerEntity, ?> renderer = this.entityRenderDispatcher.getRenderer(player);
        PlayerEntityRenderState state = (PlayerEntityRenderState) renderer.getAndUpdateRenderState(player, tickDelta);

        matrices.push();
        matrices.translate(playerPos.x, playerPos.y, playerPos.z);

        VertexConsumerProvider vertexConsumers = this.bufferBuilders.getEntityVertexConsumers();
        CircleRenderer.drawCircle(matrices, vertexConsumers, state);

        matrices.pop();
    }
}
