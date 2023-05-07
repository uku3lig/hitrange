package net.uku3lig.hitrange.config;

import com.mojang.serialization.Codec;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.text.Text;
import net.uku3lig.hitrange.HitRange;
import net.uku3lig.ukulib.config.screen.AbstractConfigScreen;
import net.uku3lig.ukulib.config.screen.ColorSelectScreen;
import net.uku3lig.ukulib.utils.Ukutils;

public class HitRangeConfigScreen extends AbstractConfigScreen<HitRangeConfig> {

    public HitRangeConfigScreen(Screen parent) {
        super(parent, Text.of("HitRange Config"), HitRange.getManager());
    }

    @Override
    protected SimpleOption<?>[] getOptions(HitRangeConfig config) {
        return new SimpleOption[] {
                SimpleOption.ofBoolean("hitrange.enabled", config.isEnabled(), config::setEnabled),
                new SimpleOption<>("hitrange.circleSegments", SimpleOption.emptyTooltip(), GameOptions::getGenericValueText,
                        new SimpleOption.ValidatingIntSliderCallbacks(3, 360),
                        config.getCircleSegments(), config::setCircleSegments),
                new SimpleOption<>("hitrange.radius", SimpleOption.emptyTooltip(), this::getGenericValueText,
                        new SimpleOption.ValidatingIntSliderCallbacks(1, 100).withModifier(i -> i / 20.0f, f -> (int)(f * 20)),
                        Codec.floatRange(0.05f, 5), config.getRadius(), config::setRadius),
                Ukutils.createOpenButton("hitrange.color", colorValue(config.getColor()),
                        parent -> new ColorSelectScreen(Text.of("color select"), parent, config::setColor, config.getColor(), manager)),
                SimpleOption.ofBoolean("hitrange.randomColors", config.isRandomColors(), config::setRandomColors),
                new SimpleOption<>("hitrange.height", SimpleOption.emptyTooltip(), this::getGenericValueText,
                        new SimpleOption.ValidatingIntSliderCallbacks(0, 500).withModifier(i -> i / 100.0f, f -> (int)(f * 100)),
                        Codec.floatRange(0, 5), config.getHeight(), config::setHeight),
        };
    }

    private Text getGenericValueText(Text prefix, float value) {
		return Text.translatable("options.generic_value", prefix, "%.2f".formatted(value));
	}

    private String colorValue(int color) {
        return "#" + Integer.toHexString(color);
    }
}
