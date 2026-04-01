package net.uku3lig.hitrange;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.KeyMapping;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.uku3lig.hitrange.config.HitRangeConfig;
import net.uku3lig.ukulib.config.ConfigManager;
import net.uku3lig.ukulib.utils.Ukutils;
import org.lwjgl.glfw.GLFW;

public class HitRange  {
    @Getter
    private static final ConfigManager<HitRangeConfig> manager = ConfigManager.createDefault(HitRangeConfig.class, "hitrange");

    @Getter @Setter
    private static Player nearest;

    public static void onInitialize() {
        Ukutils.registerToggleBind(new KeyMapping("hitrange.keybind.toggle", GLFW.GLFW_KEY_UNKNOWN, KeyMapping.Category.register(Identifier.fromNamespaceAndPath("hitrange", "key"))),
                () -> manager.getConfig().isEnabled(), b -> manager.getConfig().setEnabled(b), Component.translatable("hitrange.keybind.toggle.msg"));
    }
}
