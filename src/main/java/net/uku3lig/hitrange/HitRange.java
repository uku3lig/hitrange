package net.uku3lig.hitrange;

import lombok.Getter;
import net.uku3lig.hitrange.config.HitRangeConfig;
import net.uku3lig.ukulib.config.ConfigManager;

public class HitRange {
    @Getter
    private static final ConfigManager<HitRangeConfig> manager = ConfigManager.createDefault(HitRangeConfig.class, "hitrange");

    private HitRange() {}
}
