package net.uku3lig.hitrange.config;

import net.minecraft.client.gui.screen.Screen;
import net.uku3lig.hitrange.CircleRenderer;
import net.uku3lig.hitrange.HitRange;
import net.uku3lig.ukulib.config.option.*;
import net.uku3lig.ukulib.config.screen.AbstractConfigScreen;

public class HitRangeConfigScreen extends AbstractConfigScreen<HitRangeConfig> {

    public HitRangeConfigScreen(Screen parent) {
        super("HitRange Config", parent, HitRange.getManager());
    }

    @Override
    protected WidgetCreator[] getWidgets(HitRangeConfig config) {
        return new WidgetCreator[]{
                CyclingOption.ofBoolean("hitrange.enabled", config.isEnabled(), config::setEnabled),
                new IntSliderOption("hitrange.circleSegments", config.getCircleSegments(), config::setCircleSegments, IntSliderOption.DEFAULT_INT_TO_TEXT, 3, 180),
                new SliderOption("hitrange.radius", config.getRadius(), d -> config.setRadius((float) d), SliderOption.DEFAULT_VALUE_TO_TEXT, 0.05, 5, 0.05),
                CyclingOption.ofTranslatableEnum("hitrange.renderMode", HitRangeConfig.RenderMode.class, config.getRenderMode(), config::setRenderMode),
                new SliderOption("hitrange.thickness", config.getThickness(), d -> config.setThickness((float) d), SliderOption.DEFAULT_VALUE_TO_TEXT, 0.01, 2, 0.01),
                new ColorOption("hitrange.color", config.getColor(), config::setColor, true),
                new ColorOption("hitrange.inRangeColor", config.getInRangeColor(), config::setInRangeColor, true),
                CyclingOption.ofBoolean("hitrange.randomColors", config.isRandomColors(), config::setRandomColors),
                new SliderOption("hitrange.height", config.getHeight(), d -> config.setHeight((float) d), SliderOption.DEFAULT_VALUE_TO_TEXT, 0, 5, 0.01),
                CyclingOption.ofBoolean("hitrange.nearestOnly", config.isNearestOnly(), config::setNearestOnly),
        };
    }

    @Override
    public void removed() {
        super.removed();

        HitRangeConfig config = manager.getConfig();
        CircleRenderer.computeAngles(config.getRenderMode(), config.getCircleSegments(), config.getRadius(), config.getThickness());
    }
}
