package net.uku3lig.hitrange;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import net.uku3lig.hitrange.config.HitRangeConfig;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CircleRenderer extends RenderPhase {
    private static final RenderLayer.MultiPhase DEBUG_LINE_STRIP = makeLayer(VertexFormat.DrawMode.DEBUG_LINE_STRIP);
    private static final RenderLayer.MultiPhase DEBUG_QUADS = makeLayer(VertexFormat.DrawMode.QUADS);
    private static final RenderLayer.MultiPhase TRIANGLE_FAN = makeLayer(VertexFormat.DrawMode.TRIANGLE_FAN);

    private static final List<Angle> angles = new ArrayList<>();

    static {
        computeAngles();
    }

    public static void drawCircle(MatrixStack matrices, VertexConsumerProvider vertexConsumers, LivingEntity entity) {
        HitRangeConfig config = HitRange.getManager().getConfig();
        ClientPlayerEntity player = MinecraftClient.getInstance().player;

        int color = config.getColor();
        if (config.isRandomColors()) {
            color = entity.getEntityName().hashCode() | 0xFF000000;
        } else if (config.isColorWhenInRange() && !entity.equals(player) && entity.isInRange(player, config.getRadius())) {
            color = config.getInRangeColor();
        }

        float dy = (entity.isInSneakingPose() ? 0.125f : 0) + config.getHeight();

        RenderLayer layer = switch (config.getRenderMode()) {
            case LINE -> DEBUG_LINE_STRIP;
            case THICK -> DEBUG_QUADS;
            case FILLED -> TRIANGLE_FAN;
        };

        VertexConsumer vertices = vertexConsumers.getBuffer(layer);

        matrices.push();
        switch (config.getRenderMode()) {
            case LINE -> drawCircleLineStrip(matrices, vertices, dy, color);
            case THICK -> drawCircleQuad(matrices, vertices, dy, color);
            case FILLED -> drawCircleTriangleFan(matrices, vertices, dy, color);
        }
        matrices.pop();
    }

    private static void drawCircleLineStrip(MatrixStack matrices, VertexConsumer vertices, float dy, int argb) {
        Matrix4f positionMatrix = matrices.peek().getPositionMatrix();
        Matrix3f normalMatrix = matrices.peek().getNormalMatrix();

        for (Angle angle : angles) {
            vertices.vertex(positionMatrix, angle.dx, dy, angle.dz).color(argb).normal(normalMatrix, 0.0f, 0.0f, 0.0f).next();
        }

        Angle first = angles.get(0); // closes the circle
        vertices.vertex(positionMatrix, first.dx, dy, first.dz).color(argb).normal(normalMatrix, 0.0f, 0.0f, 0.0f).next();
    }

    private static void drawCircleQuad(MatrixStack matrices, VertexConsumer vertices, float dy, int argb) {
        Matrix4f positionMatrix = matrices.peek().getPositionMatrix();
        Matrix3f normalMatrix = matrices.peek().getNormalMatrix();

        for (int i = 1; i < angles.size() + 1; i++) {
            Angle angle = angles.get(i % angles.size());
            Angle prevAngle = angles.get(i - 1);

            vertices.vertex(positionMatrix, prevAngle.dx, dy, prevAngle.dz).color(argb).normal(normalMatrix, 0.0f, 0.0f, 0.0f).next();
            vertices.vertex(positionMatrix, prevAngle.farDx, dy, prevAngle.farDz).color(argb).normal(normalMatrix, 0.0f, 0.0f, 0.0f).next();
            vertices.vertex(positionMatrix, angle.farDx, dy, angle.farDz).color(argb).normal(normalMatrix, 0.0f, 0.0f, 0.0f).next();
            vertices.vertex(positionMatrix, angle.dx, dy, angle.dz).color(argb).normal(normalMatrix, 0.0f, 0.0f, 0.0f).next();
        }
    }

    private static void drawCircleTriangleFan(MatrixStack matrices, VertexConsumer vertices, float dy, int argb) {
        Matrix4f positionMatrix = matrices.peek().getPositionMatrix();
        Matrix3f normalMatrix = matrices.peek().getNormalMatrix();

        vertices.vertex(positionMatrix, 0, dy, 0).color(argb).normal(normalMatrix, 0.0f, 0.0f, 0.0f).next();
        drawCircleLineStrip(matrices, vertices, dy, argb);
    }

    public static void computeAngles() {
        angles.clear();
        HitRangeConfig config = HitRange.getManager().getConfig();

        if (config.getRenderMode() == HitRangeConfig.RenderMode.THICK) {
            for (int i = 0; i < config.getCircleSegments(); i++) {
                float angle = 2.0f * MathHelper.PI * ((float) i / config.getCircleSegments());
                float dst = config.getRadius() - (config.getThickness() / 2);

                float dx = dst * MathHelper.sin(angle);
                float dz = dst * MathHelper.cos(angle);

                float farDx = (dst + config.getThickness()) * MathHelper.sin(angle);
                float farDz = (dst + config.getThickness()) * MathHelper.cos(angle);

                angles.add(new Angle(dx, dz, farDx, farDz));
            }
        } else {
            for (int i = 0; i < config.getCircleSegments(); i++) {
                float angle = 2.0f * MathHelper.PI * ((float) i / config.getCircleSegments());
                float dx = config.getRadius() * MathHelper.sin(angle);
                float dz = config.getRadius() * MathHelper.cos(angle);

                angles.add(new Angle(dx, dz));
            }
        }
    }

    private static RenderLayer.MultiPhase makeLayer(VertexFormat.DrawMode mode) {
        String name = "hitrange_" + mode.name().toLowerCase(Locale.ROOT);

        return RenderLayer.of(name, VertexFormats.POSITION_COLOR, mode, 1536, false, true,
                RenderLayer.MultiPhaseParameters.builder()
                        .program(COLOR_PROGRAM)
                        .transparency(TRANSLUCENT_TRANSPARENCY)
                        .cull(ENABLE_CULLING)
                        .lightmap(ENABLE_LIGHTMAP)
                        .overlay(ENABLE_OVERLAY_COLOR)
                        .writeMaskState(COLOR_MASK)
                        .depthTest(LEQUAL_DEPTH_TEST)
                        .layering(VIEW_OFFSET_Z_LAYERING)
                        .build(false)
        );
    }

    // trolling
    private CircleRenderer(String name, Runnable beginAction, Runnable endAction) {
        super(name, beginAction, endAction);
    }

    private record Angle(float dx, float dz, float farDx, float farDz) {
        public Angle(float dx, float dz) {
            this(dx, dz, 0, 0);
        }
    }
}
