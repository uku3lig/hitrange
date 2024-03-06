package net.uku3lig.hitrange;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.text.Text;
import net.uku3lig.hitrange.config.HitRangeConfig;
import net.uku3lig.hitrange.config.HitRangeConfigScreen;
import net.uku3lig.ukulib.api.UkulibAPI;
import net.uku3lig.ukulib.config.screen.AbstractConfigScreen;

import java.util.function.Function;

public class UkulibHook implements UkulibAPI {

    // pure beauty
    // i hate ukulib <1.0
    @Override
    public Function<Screen, AbstractConfigScreen<?>> supplyConfigScreen() {
        return parent -> new AbstractConfigScreen<>(parent, Text.empty(), HitRange.getManager()) {
            @Override
            protected void init() {
                MinecraftClient.getInstance().setScreen(new HitRangeConfigScreen(this.parent));
            }

            @Override
            protected SimpleOption<?>[] getOptions(HitRangeConfig config) {
                return new SimpleOption[0];
            }
        };
    }
}
