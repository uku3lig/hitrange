package net.uku3lig.hitrange;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.uku3lig.hitrange.config.HitRangeConfig;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CircleRenderer extends RenderPhase {
    private static final RenderLayer.MultiPhase DEBUG_LINE_STRIP = makeLayer(RenderPipelines.DEBUG_LINE_STRIP);
    private static final RenderLayer.MultiPhase DEBUG_QUADS = makeLayer(RenderPipelines.DEBUG_QUADS);
    private static final RenderLayer.MultiPhase TRIANGLE_FAN = makeLayer(RenderPipelines.DEBUG_TRIANGLE_FAN);

    private static final List<Angle> angles = new ArrayList<>();

    static {
        computeAngles();
    }

    public static RenderLayer getCurrentLayer() {
        return switch (HitRange.getManager().getConfig().getRenderMode()) {
            case LINE -> DEBUG_LINE_STRIP;
            case THICK -> DEBUG_QUADS;
            case FILLED -> TRIANGLE_FAN;
        };
    }

    public static void drawCircle(MatrixStack.Entry entry, VertexConsumer vertices, PlayerEntityRenderState state) {
        HitRangeConfig config = HitRange.getManager().getConfig();
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) return;

        Vec3d entityPos = new Vec3d(state.x, state.y, state.z);

        int color = config.getColor();
        if (config.isRandomColors()) {
            String name = state.playerName == null ? "" : state.playerName.getString();
            color = name.hashCode() | 0xFF000000;
        } else if (config.isColorWhenInRange() && state.id != player.getId() && entityPos.isInRange(player.getEntityPos(), config.getRadius())) {
            color = config.getInRangeColor();
        }

        float dy = (state.sneaking ? 0.125f : 0) + config.getHeight();

        switch (config.getRenderMode()) {
            case LINE -> drawCircleLineStrip(entry, vertices, dy, color);
            case THICK -> drawCircleQuad(entry, vertices, dy, color);
            case FILLED -> drawCircleTriangleFan(entry, vertices, dy, color);
        }
    }

    private static void drawCircleLineStrip(MatrixStack.Entry entry, VertexConsumer vertices, float dy, int argb) {
        Matrix4f positionMatrix = entry.getPositionMatrix();

        for (Angle angle : angles) {
            vertices.vertex(positionMatrix, angle.dx, dy, angle.dz).color(argb).normal(entry, 0.0f, 0.0f, 0.0f);
        }

        Angle first = angles.getFirst(); // closes the circle
        vertices.vertex(positionMatrix, first.dx, dy, first.dz).color(argb).normal(entry, 0.0f, 0.0f, 0.0f);
    }

    private static void drawCircleQuad(MatrixStack.Entry entry, VertexConsumer vertices, float dy, int argb) {
        Matrix4f positionMatrix = entry.getPositionMatrix();

        for (int i = 1; i < angles.size() + 1; i++) {
            Angle angle = angles.get(i % angles.size());
            Angle prevAngle = angles.get(i - 1);

            vertices.vertex(positionMatrix, prevAngle.dx, dy, prevAngle.dz).color(argb).normal(entry, 0.0f, 0.0f, 0.0f);
            vertices.vertex(positionMatrix, prevAngle.farDx, dy, prevAngle.farDz).color(argb).normal(entry, 0.0f, 0.0f, 0.0f);
            vertices.vertex(positionMatrix, angle.farDx, dy, angle.farDz).color(argb).normal(entry, 0.0f, 0.0f, 0.0f);
            vertices.vertex(positionMatrix, angle.dx, dy, angle.dz).color(argb).normal(entry, 0.0f, 0.0f, 0.0f);
        }
    }

    private static void drawCircleTriangleFan(MatrixStack.Entry entry, VertexConsumer vertices, float dy, int argb) {
        Matrix4f positionMatrix = entry.getPositionMatrix();

        vertices.vertex(positionMatrix, 0, dy, 0).color(argb).normal(entry, 0.0f, 0.0f, 0.0f);
        drawCircleLineStrip(entry, vertices, dy, argb);
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

    private static RenderLayer.MultiPhase makeLayer(RenderPipeline pipeline) {
        String name = "hitrange_" + pipeline.getClass().getSimpleName().toLowerCase(Locale.ROOT);

        return RenderLayer.of(name, 1536, false, true, pipeline,
                RenderLayer.MultiPhaseParameters.builder()
                        .lightmap(ENABLE_LIGHTMAP)
                        .overlay(ENABLE_OVERLAY_COLOR)
                        .layering(VIEW_OFFSET_Z_LAYERING)
                        .build(false)
        );
    }

    // required for COLOR_PROGRAM, etc.
    // see #makeLayer
    private CircleRenderer(String name, Runnable beginAction, Runnable endAction) {
        super(name, beginAction, endAction);
    }

    private record Angle(float dx, float dz, float farDx, float farDz) {
        public Angle(float dx, float dz) {
            this(dx, dz, 0, 0);
        }
    }
}
