package net.uku3lig.hitrange;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.entity.player.PlayerEntity;
import net.uku3lig.hitrange.config.HitRangeConfig;
import net.uku3lig.ukulib.config.ConfigManager;

public class HitRange {
    @Getter
    private static final ConfigManager<HitRangeConfig> manager = ConfigManager.create(HitRangeConfig.class, "hitrange");

    @Getter @Setter
    private static PlayerEntity nearest;

    private HitRange() {}
}
