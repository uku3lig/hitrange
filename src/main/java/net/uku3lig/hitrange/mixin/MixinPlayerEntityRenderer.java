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
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntityRenderer.class)
public abstract class MixinPlayerEntityRenderer extends LivingEntityRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> {
    private static final int CIRCLE_SEGMENTS = 180;
    private static final float RADIUS = 3.0f;

    protected MixinPlayerEntityRenderer(EntityRendererFactory.Context ctx, PlayerEntityModel<AbstractClientPlayerEntity> model, float shadowRadius) {
        super(ctx, model, shadowRadius);
    }

    @Inject(method = "render(Lnet/minecraft/client/network/AbstractClientPlayerEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At("TAIL"))
    private void render(AbstractClientPlayerEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        matrices.push();

        VertexConsumer vertices = vertexConsumers.getBuffer(RenderLayer.getLines());
        Matrix4f positionMatrix = matrices.peek().getPositionMatrix();
        Matrix3f normalMatrix = matrices.peek().getNormalMatrix();

        vertices.vertex(positionMatrix, 0.0f, 0.0f, 0.0f).color(1.0f, 0.0f, 0.0f, 1.0f).normal(normalMatrix, 0.0f, 0.0f, 0.0f).next();
        vertices.vertex(positionMatrix, RADIUS, 0.0f, 0.0f).color(1.0f, 0.0f, 0.0f, 1.0f).normal(normalMatrix, 0.0f, 0.0f, 0.0f).next();

        for (int i = 0; i <= CIRCLE_SEGMENTS; i++) {
            float angle = (float) (2.0f * Math.PI * ((float) i /  CIRCLE_SEGMENTS));
            float dx = RADIUS * MathHelper.sin(angle);
            float dz = RADIUS * MathHelper.cos(angle);

            float prevAngle = (float) (2.0f * Math.PI * ((float) (i - 1) / CIRCLE_SEGMENTS));
            float prevDx = RADIUS * MathHelper.sin(prevAngle);
            float prevDz = RADIUS * MathHelper.cos(prevAngle);

            vertices.vertex(positionMatrix, prevDx, 0.0f, prevDz).color(1.0f, 0.0f, 0.0f, 1.0f).normal(normalMatrix, 0.0f, 0.0f, 0.0f).next();
            vertices.vertex(positionMatrix, dx, 0.0f, dz).color(1.0f, 0.0f, 0.0f, 1.0f).normal(normalMatrix, 0.0f, 0.0f, 0.0f).next();
        }

        matrices.pop();
    }
}
