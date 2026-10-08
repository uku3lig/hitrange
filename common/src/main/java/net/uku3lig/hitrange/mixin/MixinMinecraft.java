package net.uku3lig.hitrange.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.component.AttackRange;
import net.uku3lig.hitrange.CircleRenderer;
import net.uku3lig.hitrange.HitRange;
import net.uku3lig.hitrange.config.HitRangeConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MixinMinecraft {
    @Inject(method = "tick", at = @At("TAIL"))
    public void getNearestPlayer(CallbackInfo ci) {
        LocalPlayer player = Minecraft.getInstance().player;
        HitRangeConfig config = HitRange.getManager().getConfig();
        if (player == null) return;

        if (config.isNearestOnly()) {
            Player nearest = player.level().getNearestPlayer(player.getX(), player.getY(), player.getZ(), config.getMaxSearchDistance(), e -> !e.equals(player));
            HitRange.setNearest(nearest);
        }

        if (config.isRadiusFromReach()) {
            AttackRange attackRange = player.getActiveItem().get(DataComponents.ATTACK_RANGE);
            // if the item changes we recompute the circle based on attack range
            if (attackRange != HitRange.attackRange) {
                CircleRenderer.computeAngles(attackRange);
            }

            HitRange.attackRange = attackRange;
        }
    }
}
