package net.uku3lig.hitrange;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.rendertype.LayeringTransform;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.uku3lig.hitrange.config.HitRangeConfig;
import net.uku3lig.hitrange.mixin.RenderTypeAccessor;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CircleRenderer {
    private static final RenderType LINES = makeType(RenderPipelines.LINES, null);
    private static final RenderType QUADS = makeType(RenderPipelines.DEBUG_QUADS, null);
    private static final Int2ObjectMap<RenderType> PLAYER_TRIANGLE_FANS = new Int2ObjectOpenHashMap<>();

    private static final List<Angle> angles = new ArrayList<>();

    static {
        computeAngles();
    }

    public static RenderType getCurrentType(AvatarRenderState state) {
        return switch (HitRange.getManager().getConfig().getRenderMode()) {
            case LINE -> LINES;
            case THICK -> QUADS;
            case FILLED ->
                    PLAYER_TRIANGLE_FANS.computeIfAbsent(state.id, i -> makeType(RenderPipelines.DEBUG_TRIANGLE_FAN, String.valueOf(i)));
        };
    }

    public static void drawCircle(PoseStack.Pose entry, VertexConsumer vertices, AvatarRenderState state) {
        HitRangeConfig config = HitRange.getManager().getConfig();
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return;

        Vec3 entityPos = new Vec3(state.x, state.y, state.z);

        int color = config.getColor();
        if (config.isRandomColors()) {
            String name = state.scoreText == null ? "" : state.scoreText.getString();
            color = name.hashCode() | 0xFF000000;
        } else if (config.isColorWhenInRange() && state.id != player.getId() && entityPos.closerThan(player.position(), config.getRadius())) {
            color = config.getInRangeColor();
        }

        float dy = (state.isDiscrete ? 0.125f : 0) + config.getHeight();

        switch (config.getRenderMode()) {
            case LINE -> drawCircleLines(entry, vertices, dy, color);
            case THICK -> drawCircleQuad(entry, vertices, dy, color);
            case FILLED -> drawCircleTriangleFan(entry, vertices, dy, color);
        }
    }

    private static void drawCircleLines(PoseStack.Pose entry, VertexConsumer vertices, float dy, int argb) {
        Matrix4f positionMatrix = entry.pose();

        for (int i = 1; i < angles.size() + 1; i++) {
            Angle angle = angles.get(i % angles.size());
            Angle prevAngle = angles.get(i - 1);

            vertices.addVertex(positionMatrix, prevAngle.dx, dy, prevAngle.dz).setColor(argb).setNormal(entry, 0.0f, 0.0f, 0.0f).setLineWidth(3);
            vertices.addVertex(positionMatrix, angle.dx, dy, angle.dz).setColor(argb).setNormal(entry, 0.0f, 0.0f, 0.0f).setLineWidth(3);
        }
    }

    private static void drawCircleQuad(PoseStack.Pose entry, VertexConsumer vertices, float dy, int argb) {
        Matrix4f positionMatrix = entry.pose();

        for (int i = 1; i < angles.size() + 1; i++) {
            Angle angle = angles.get(i % angles.size());
            Angle prevAngle = angles.get(i - 1);

            vertices.addVertex(positionMatrix, prevAngle.dx, dy, prevAngle.dz).setColor(argb).setNormal(entry, 0.0f, 0.0f, 0.0f);
            vertices.addVertex(positionMatrix, prevAngle.farDx, dy, prevAngle.farDz).setColor(argb).setNormal(entry, 0.0f, 0.0f, 0.0f);
            vertices.addVertex(positionMatrix, angle.farDx, dy, angle.farDz).setColor(argb).setNormal(entry, 0.0f, 0.0f, 0.0f);
            vertices.addVertex(positionMatrix, angle.dx, dy, angle.dz).setColor(argb).setNormal(entry, 0.0f, 0.0f, 0.0f);
        }
    }

    private static void drawCircleTriangleFan(PoseStack.Pose entry, VertexConsumer vertices, float dy, int argb) {
        Matrix4f positionMatrix = entry.pose();

        // center of triangle fan
        vertices.addVertex(positionMatrix, 0, dy, 0).setColor(argb).setNormal(entry, 0.0f, 0.0f, 0.0f);

        for (Angle angle : angles) {
            vertices.addVertex(positionMatrix, angle.dx, dy, angle.dz).setColor(argb).setNormal(entry, 0.0f, 0.0f, 0.0f);
        }

        Angle first = angles.getFirst(); // closes the circle
        vertices.addVertex(positionMatrix, first.dx, dy, first.dz).setColor(argb).setNormal(entry, 0.0f, 0.0f, 0.0f);
    }

    public static void computeAngles() {
        angles.clear();
        HitRangeConfig config = HitRange.getManager().getConfig();

        if (config.getRenderMode() == HitRangeConfig.RenderMode.THICK) {
            for (int i = 0; i < config.getCircleSegments(); i++) {
                float angle = 2.0f * Mth.PI * ((float) i / config.getCircleSegments());
                float dst = config.getRadius() - (config.getThickness() / 2);

                float dx = dst * Mth.sin(angle);
                float dz = dst * Mth.cos(angle);

                float farDx = (dst + config.getThickness()) * Mth.sin(angle);
                float farDz = (dst + config.getThickness()) * Mth.cos(angle);

                angles.add(new Angle(dx, dz, farDx, farDz));
            }
        } else {
            for (int i = 0; i < config.getCircleSegments(); i++) {
                float angle = 2.0f * Mth.PI * ((float) i / config.getCircleSegments());
                float dx = config.getRadius() * Mth.sin(angle);
                float dz = config.getRadius() * Mth.cos(angle);

                angles.add(new Angle(dx, dz));
            }
        }
    }

    private static RenderType makeType(RenderPipeline pipeline, @Nullable String suffix) {
        String name = "hitrange_" + pipeline.getClass().getSimpleName().toLowerCase(Locale.ROOT);
        if (suffix != null) name += "_" + suffix;

        RenderSetup setup = RenderSetup.builder(pipeline)
                .useLightmap()
                .useOverlay()
                .setLayeringTransform(LayeringTransform.VIEW_OFFSET_Z_LAYERING)
                .createRenderSetup();

        return RenderTypeAccessor.of(name, setup);
    }

    private record Angle(float dx, float dz, float farDx, float farDz) {
        public Angle(float dx, float dz) {
            this(dx, dz, 0, 0);
        }
    }
}
