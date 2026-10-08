package net.uku3lig.hitrange.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.component.AttackRange;
import net.uku3lig.ukulib.config.option.StringTranslatable;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HitRangeConfig implements Serializable {
    // general
    private boolean enabled = true;
    private float radius = 3.0f;
    private boolean radiusFromReach = true;
    private RenderMode renderMode = RenderMode.THICK;
    private float thickness = 0.15f;
    private float height = 0.0f;
    private boolean nearestOnly = false;
    private boolean showSelf = true;
    // colors
    private int color = 0x80FF0000;
    private int inRangeColor = 0x8000FF00;
    private boolean randomColors = false;
    private boolean colorWhenInRange = true;
    // advanced
    private int circleSegments = 60;
    private int maxDistance = 100;
    private int maxSearchDistance = 50;

    public float getEffectiveRadius(AttackRange range) {
        return this.isRadiusFromReach() && range != null
                ? range.effectiveMaxRange(Minecraft.getInstance().player)
                : this.getRadius();
    }

    @Getter
    @AllArgsConstructor
    public enum RenderMode implements StringTranslatable {
        LINE("line", "hitrange.mode.line"),
        THICK("thick", "hitrange.mode.thick"),
        FILLED("filled", "hitrange.mode.filled"),
        ;

        private final String name;
        private final String translationKey;
    }
}
