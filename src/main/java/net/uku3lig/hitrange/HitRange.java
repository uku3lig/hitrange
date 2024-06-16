package net.uku3lig.hitrange;

import lombok.Getter;
import lombok.Setter;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.uku3lig.hitrange.config.HitRangeConfig;
import net.uku3lig.ukulib.config.ConfigManager;
import net.uku3lig.ukulib.utils.Ukutils;
import org.lwjgl.glfw.GLFW;

public class HitRange implements ClientModInitializer {
    @Getter
    private static final ConfigManager<HitRangeConfig> manager = ConfigManager.createDefault(HitRangeConfig.class, "hitrange");

    @Getter @Setter
    private static PlayerEntity nearest;

    @Override
    public void onInitializeClient() {
        Ukutils.registerToggleBind(new KeyBinding("hitrange.keybind.toggle", GLFW.GLFW_KEY_UNKNOWN, "hitrange.name"),
                () -> manager.getConfig().isEnabled(), b -> manager.getConfig().setEnabled(b), Text.translatable("hitrange.keybind.toggle.msg"));
    }
}
