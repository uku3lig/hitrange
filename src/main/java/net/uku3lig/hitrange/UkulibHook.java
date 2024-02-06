package net.uku3lig.hitrange;

import net.minecraft.client.gui.screen.Screen;
import net.uku3lig.hitrange.config.HitRangeConfigScreen;
import net.uku3lig.ukulib.api.UkulibAPI;

import java.util.function.UnaryOperator;

public class UkulibHook implements UkulibAPI {
    @Override
    public UnaryOperator<Screen> supplyConfigScreen() {
        return HitRangeConfigScreen::new;
    }
}
