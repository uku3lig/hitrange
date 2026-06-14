package net.uku3lig.hitrange.mixin;

import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.extract.LevelExtractor;
import net.minecraft.client.renderer.state.level.LevelRenderState;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.uku3lig.hitrange.HitRange;
import net.uku3lig.hitrange.config.HitRangeConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelExtractor.class)
public class MixinLevelExtractor {
    @Inject(method = "extractVisibleEntities", at = @At("TAIL"))
    public void storeClientPlayerPos(Camera camera, Frustum frustum, DeltaTracker tickCounter, LevelRenderState renderStates, CallbackInfo ci) {
        LocalPlayer player = Minecraft.getInstance().player;
        HitRangeConfig config = HitRange.getManager().getConfig();
        if (!config.isEnabled() || player == null || !config.isShowSelf()) return;

        float tickDelta = tickCounter.getGameTimeDeltaPartialTick(false);
        double px = Mth.lerp(tickDelta, player.xOld, player.getX());
        double py = Mth.lerp(tickDelta, player.yOld, player.getY());
        double pz = Mth.lerp(tickDelta, player.zOld, player.getZ());
        Vec3 playerPos = new Vec3(px, py, pz);

        Vec3 cameraPos = renderStates.cameraRenderState.pos;
        HitRange.playerPos = playerPos.subtract(cameraPos);
    }
}
