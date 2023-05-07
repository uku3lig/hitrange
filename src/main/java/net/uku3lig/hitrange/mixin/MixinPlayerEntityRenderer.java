package net.uku3lig.hitrange.mixin;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import net.uku3lig.hitrange.HitRange;
import net.uku3lig.hitrange.config.HitRangeConfig;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntityRenderer.class)
public abstract class MixinPlayerEntityRenderer extends LivingEntityRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> {
    protected MixinPlayerEntityRenderer(EntityRendererFactory.Context ctx, PlayerEntityModel<AbstractClientPlayerEntity> model, float shadowRadius) {
        super(ctx, model, shadowRadius);
    }

    @Inject(method = "render(Lnet/minecraft/client/network/AbstractClientPlayerEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At("TAIL"))
    private void render(AbstractClientPlayerEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        HitRangeConfig config = HitRange.getManager().getConfig();
        if (!config.isEnabled()) return;

        int color = config.isRandomColors() ? entity.getEntityName().hashCode() % 0xFFFFFF | 0xFF000000 : config.getColor();

        matrices.push();

        VertexConsumer vertices = vertexConsumers.getBuffer(RenderLayer.getLines());
        Matrix4f positionMatrix = matrices.peek().getPositionMatrix();
        Matrix3f normalMatrix = matrices.peek().getNormalMatrix();

        vertices.vertex(positionMatrix, 0.0f, config.getHeight(), 0.0f).color(color).normal(normalMatrix, 0.0f, 0.0f, 0.0f).next();
        vertices.vertex(positionMatrix, config.getRadius(), config.getHeight(), 0.0f).color(color).normal(normalMatrix, 0.0f, 0.0f, 0.0f).next();

        for (int i = 0; i <= config.getCircleSegments(); i++) {
            float angle = (float) (2.0f * Math.PI * ((float) i / config.getCircleSegments()));
            float dx = config.getRadius() * MathHelper.sin(angle);
            float dz = config.getRadius() * MathHelper.cos(angle);

            float prevAngle = (float) (2.0f * Math.PI * ((float) (i - 1) / config.getCircleSegments()));
            float prevDx = config.getRadius() * MathHelper.sin(prevAngle);
            float prevDz = config.getRadius() * MathHelper.cos(prevAngle);

            vertices.vertex(positionMatrix, prevDx, config.getHeight(), prevDz).color(color).normal(normalMatrix, 0.0f, 0.0f, 0.0f).next();
            vertices.vertex(positionMatrix, dx, config.getHeight(), dz).color(color).normal(normalMatrix, 0.0f, 0.0f, 0.0f).next();
        }

        matrices.pop();
    }
}
