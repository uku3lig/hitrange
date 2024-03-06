package net.uku3lig.hitrange.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.minecraft.util.TranslatableOption;
import net.minecraft.util.function.ValueLists;
import net.uku3lig.ukulib.config.IConfig;

import java.util.function.IntFunction;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HitRangeConfig implements IConfig<HitRangeConfig> {
    // general
    private boolean enabled = true;
    private float radius = 3.0f;
    private RenderMode renderMode = RenderMode.THICK;
    private float thickness = 0.15f;
    private float height = 0.0f;
    private boolean nearestOnly = false;
    private boolean showSelf = true;
    // colors
    private int color = 0x40FF0000;
    private int inRangeColor = 0x4000FF00;
    private boolean randomColors = false;
    private boolean colorWhenInRange = true;
    // advanced
    private int circleSegments = 60;
    private int maxDistance = 100;
    private int maxSearchDistance = 50;

    @Override
    public HitRangeConfig defaultConfig() {
        return new HitRangeConfig();
    }

    @Getter
    @AllArgsConstructor
    public enum RenderMode implements TranslatableOption {
        LINE(0, "hitrange.mode.line"),
        THICK(1, "hitrange.mode.thick"),
        FILLED(2, "hitrange.mode.filled"),
        ;

        private static final IntFunction<RenderMode> BY_ID = ValueLists.createIdToValueFunction(RenderMode::getId, values(), ValueLists.OutOfBoundsHandling.WRAP);

        private final int id;
        private final String translationKey;

        public static RenderMode byId(int id) {
            return BY_ID.apply(id);
        }
    }
}
