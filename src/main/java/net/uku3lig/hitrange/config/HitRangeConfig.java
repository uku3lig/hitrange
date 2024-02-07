package net.uku3lig.hitrange.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.minecraft.util.TranslatableOption;
import net.uku3lig.hitrange.HitRange;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HitRangeConfig implements Serializable {
    // general
    private boolean enabled = true;
    private float radius = 3.0f;
    private RenderMode renderMode = RenderMode.THICK;
    private float thickness = 0.15f;
    private float height = 0.0f;
    private boolean nearestOnly = false;
    // colors
    private int color = 0x80FF0000;
    private int inRangeColor = 0x8000FF00;
    private boolean randomColors = false;
    // advanced
    private int circleSegments = 60;
    private boolean preventZFight = true;
    private int maxSearchDistance = 30;

    @Getter
    @AllArgsConstructor
    public enum RenderMode implements TranslatableOption {
        LINE(0, "hitrange.mode.line"),
        THICK(1, "hitrange.mode.thick"),
        FILLED(2, "hitrange.mode.filled"),
        ;

        private final int id;
        private final String translationKey;
    }
}
