package net.uku3lig.hitrange.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.uku3lig.ukulib.config.IConfig;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HitRangeConfig implements IConfig<HitRangeConfig> {
    private boolean enabled = true;
    private int circleSegments = 180;
    private float radius = 3.0f;
    private int color = 0xFFFF0000;
    private boolean randomColors = false;
    private float height = 0.0f;

    // todo maybe add colors for being in range, etc?

    @Override
    public HitRangeConfig defaultConfig() {
        return new HitRangeConfig();
    }
}
