package net.uku3lig.hitrange.config;

import com.mojang.serialization.Codec;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tab.TabManager;
import net.minecraft.client.gui.widget.TabNavigationWidget;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import net.uku3lig.hitrange.CircleRenderer;
import net.uku3lig.hitrange.HitRange;
import net.uku3lig.ukulib.config.screen.CloseableScreen;
import net.uku3lig.ukulib.config.screen.ColorSelectScreen;
import net.uku3lig.ukulib.utils.Ukutils;

import java.util.Arrays;

import static net.minecraft.client.gui.screen.world.CreateWorldScreen.FOOTER_SEPARATOR_TEXTURE;

public class HitRangeConfigScreen extends CloseableScreen {
    private TabNavigationWidget tabWidget;
    private final TabManager tabManager = new TabManager(this::addDrawableChild, this::remove);

    public HitRangeConfigScreen(Screen parent) {
        super(Text.of("HitRange Config"), parent);
    }

    @Override
    public void tick() {
        super.tick();
        this.tabManager.tick();
    }

    @Override
    protected void init() {
        this.tabWidget = TabNavigationWidget.builder(this.tabManager, this.width)
                .tabs(new GeneralTab(), new ColorsTab(), new AdvancedTab())
                .build();
        this.addDrawableChild(this.tabWidget);
        this.tabWidget.selectTab(0, false);
        this.initTabNavigation();

        this.addDrawableChild(Ukutils.doneButton(this.width, this.height, this.parent));
    }

    @Override
    protected void initTabNavigation() {
        if (this.tabWidget != null) {
            this.tabWidget.setWidth(this.width);
            this.tabWidget.init();
            int i = this.tabWidget.getNavigationFocus().getBottom();
            ScreenRect screenRect = new ScreenRect(0, i, this.width, this.height - 36 - i);
            this.tabManager.setTabArea(screenRect);
        }
    }

    @Override
    public void render(DrawContext drawContext, int mouseX, int mouseY, float delta) {
        this.renderBackground(drawContext);
        drawContext.drawTexture(FOOTER_SEPARATOR_TEXTURE, 0, MathHelper.roundUpToMultiple(this.height - 36 - 2, 2), 0.0F, 0.0F, this.width, 2, 32, 2);
        super.render(drawContext, mouseX, mouseY, delta);
    }

    @Override
    public void removed() {
        super.removed();
        CircleRenderer.computeAngles();
    }

    private static class GeneralTab extends OptionTab<HitRangeConfig> {
        protected GeneralTab() {
            super("General", HitRange.getManager());
        }

        @Override
        public SimpleOption<?>[] getOptions(HitRangeConfig config) {
            return new SimpleOption[]{
                    SimpleOption.ofBoolean("hitrange.enabled", config.isEnabled(), config::setEnabled),
                    new SimpleOption<>("hitrange.radius", SimpleOption.emptyTooltip(), HitRangeConfigScreen::doubleValue, new SimpleOption.ValidatingIntSliderCallbacks(1, 100).withModifier(i -> i / 20.0, d -> (int) (d * 20)), Codec.doubleRange(0.05, 5), (double) config.getRadius(), d -> config.setRadius(d.floatValue())),
                    new SimpleOption<>("hitrange.renderMode", SimpleOption.emptyTooltip(), (t, v) -> GameOptions.getGenericValueText(t, Text.translatable(v.getTranslationKey())), new SimpleOption.PotentialValuesBasedCallbacks<>(Arrays.asList(HitRangeConfig.RenderMode.values()), Codec.INT.xmap(HitRangeConfig.RenderMode::byId, HitRangeConfig.RenderMode::getId)), config.getRenderMode(), config::setRenderMode),
                    new SimpleOption<>("hitrange.thickness", SimpleOption.emptyTooltip(), HitRangeConfigScreen::doubleValue, new SimpleOption.ValidatingIntSliderCallbacks(1, 200).withModifier(i -> i / 100.0, d -> (int) (d * 100)), Codec.doubleRange(0.01, 2), (double) config.getThickness(), d -> config.setThickness(d.floatValue())),
                    new SimpleOption<>("hitrange.height", SimpleOption.emptyTooltip(), HitRangeConfigScreen::doubleValue, new SimpleOption.ValidatingIntSliderCallbacks(0, 500).withModifier(i -> i / 100.0, d -> (int) (d * 100)), Codec.doubleRange(0, 5), (double) config.getHeight(), d -> config.setHeight(d.floatValue())),
                    SimpleOption.ofBoolean("hitrange.nearestOnly", config.isNearestOnly(), config::setNearestOnly),
                    SimpleOption.ofBoolean("hitrange.showSelf", config.isShowSelf(), config::setShowSelf),
            };
        }
    }

    private static class ColorsTab extends OptionTab<HitRangeConfig> {
        protected ColorsTab() {
            super("Colors", HitRange.getManager());
        }

        @Override
        public SimpleOption<?>[] getOptions(HitRangeConfig config) {
            return new SimpleOption[]{
                    Ukutils.createOpenButton("hitrange.color", p -> new ColorSelectScreen(Text.translatable("hitrange.color"), p, config::setColor, config.getColor(), HitRange.getManager())),
                    Ukutils.createOpenButton("hitrange.inRangeColor", p -> new ColorSelectScreen(Text.translatable("hitrange.inRangeColor"), p, config::setInRangeColor, config.getInRangeColor(), HitRange.getManager())),
                    SimpleOption.ofBoolean("hitrange.randomColors", config.isRandomColors(), config::setRandomColors),
                    SimpleOption.ofBoolean("hitrange.colorWhenInRange", config.isColorWhenInRange(), config::setColorWhenInRange)
            };
        }
    }

    private static class AdvancedTab extends OptionTab<HitRangeConfig> {
        protected AdvancedTab() {
            super("Advanced", HitRange.getManager());
        }

        @Override
        public SimpleOption<?>[] getOptions(HitRangeConfig config) {
            return new SimpleOption[]{
                    new SimpleOption<>("hitrange.circleSegments", SimpleOption.emptyTooltip(), GameOptions::getGenericValueText, new SimpleOption.ValidatingIntSliderCallbacks(3, 180), config.getCircleSegments(), config::setCircleSegments),
                    new SimpleOption<>("hitrange.maxSearchDistance", SimpleOption.emptyTooltip(), GameOptions::getGenericValueText, new SimpleOption.ValidatingIntSliderCallbacks(1, 100), config.getMaxSearchDistance(), config::setMaxSearchDistance),
                    new SimpleOption<>("hitrange.maxDistance", SimpleOption.emptyTooltip(), GameOptions::getGenericValueText, new SimpleOption.ValidatingIntSliderCallbacks(1, 200), config.getMaxDistance(), config::setMaxDistance),
                    Ukutils.createButton("hitrange.computeAngles", s -> CircleRenderer.computeAngles()),
            };
        }
    }

    private static Text doubleValue(Text text, double value) {
        return GameOptions.getGenericValueText(text, Text.literal(String.format("%.2f", value)));
    }
}
