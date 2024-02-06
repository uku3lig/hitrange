package net.uku3lig.hitrange.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.minecraft.util.TranslatableOption;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HitRangeConfig implements Serializable {
    private boolean enabled = true;
    private int circleSegments = 60;
    private float radius = 3.0f;
    private RenderMode renderMode = RenderMode.THICK;
    private float thickness = 0.15f;
    private int color = 0xFFFF0000;
    private boolean randomColors = false;
    private float height = 0.0f;

    // todo maybe add colors for being in range, etc?

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
