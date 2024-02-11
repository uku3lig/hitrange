package net.uku3lig.hitrange.config;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tab.Tab;
import net.uku3lig.hitrange.CircleRenderer;
import net.uku3lig.hitrange.HitRange;
import net.uku3lig.ukulib.config.option.*;
import net.uku3lig.ukulib.config.option.widget.ButtonTab;
import net.uku3lig.ukulib.config.screen.TabbedConfigScreen;

public class HitRangeConfigScreen extends TabbedConfigScreen<HitRangeConfig> {
    public HitRangeConfigScreen(Screen parent) {
        super("HitRange Config", parent, HitRange.getManager());
    }

    @Override
    protected Tab[] getTabs(HitRangeConfig config) {
        return new Tab[]{new GeneralTab(), new ColorsTab(), new AdvancedTab()};
    }

    @Override
    public void removed() {
        super.removed();
        CircleRenderer.computeAngles();
    }

    private class GeneralTab extends ButtonTab<HitRangeConfig> {
        protected GeneralTab() {
            super("General", HitRangeConfigScreen.this.manager);
        }

        @Override
        protected WidgetCreator[] getWidgets(HitRangeConfig config) {
            return new WidgetCreator[]{
                    CyclingOption.ofBoolean("hitrange.enabled", config.isEnabled(), config::setEnabled),
                    new SliderOption("hitrange.radius", config.getRadius(), d -> config.setRadius((float) d), SliderOption.DEFAULT_VALUE_TO_TEXT, 0.05, 5, 0.05),
                    CyclingOption.ofTranslatableEnum("hitrange.renderMode", HitRangeConfig.RenderMode.class, config.getRenderMode(), config::setRenderMode),
                    new SliderOption("hitrange.thickness", config.getThickness(), d -> config.setThickness((float) d), SliderOption.DEFAULT_VALUE_TO_TEXT, 0.01, 2, 0.01),
                    new SliderOption("hitrange.height", config.getHeight(), d -> config.setHeight((float) d), SliderOption.DEFAULT_VALUE_TO_TEXT, 0, 5, 0.01),
                    CyclingOption.ofBoolean("hitrange.nearestOnly", config.isNearestOnly(), config::setNearestOnly),
                    CyclingOption.ofBoolean("hitrange.showSelf", config.isShowSelf(), config::setShowSelf),
            };
        }
    }

    private class ColorsTab extends ButtonTab<HitRangeConfig> {
        protected ColorsTab() {
            super("Colors", HitRangeConfigScreen.this.manager);
        }

        @Override
        protected WidgetCreator[] getWidgets(HitRangeConfig config) {
            return new WidgetCreator[]{
                    new ColorOption("hitrange.color", config.getColor(), config::setColor, true),
                    new ColorOption("hitrange.inRangeColor", config.getInRangeColor(), config::setInRangeColor, true),
                    CyclingOption.ofBoolean("hitrange.randomColors", config.isRandomColors(), config::setRandomColors),
            };
        }
    }

    private class AdvancedTab extends ButtonTab<HitRangeConfig> {
        protected AdvancedTab() {
            super("Advanced", HitRangeConfigScreen.this.manager);
        }

        @Override
        protected WidgetCreator[] getWidgets(HitRangeConfig config) {
            return new WidgetCreator[]{
                    new TextOption("Machine, turn back §onow§r."),
                    new TextOption("This config tab is §onot§r for your kind."),
                    new IntSliderOption("hitrange.circleSegments", config.getCircleSegments(), config::setCircleSegments, IntSliderOption.DEFAULT_INT_TO_TEXT, 3, 180),
                    new IntSliderOption("hitrange.maxSearchDistance", config.getMaxSearchDistance(), config::setMaxSearchDistance, IntSliderOption.DEFAULT_INT_TO_TEXT, 1, 100),
                    new IntSliderOption("hitrange.maxDistance", config.getMaxDistance(), config::setMaxDistance, IntSliderOption.DEFAULT_INT_TO_TEXT, 1, 200),
                    new SimpleButton("hitrange.computeAngles", b -> CircleRenderer.computeAngles()),
            };
        }
    }
}
