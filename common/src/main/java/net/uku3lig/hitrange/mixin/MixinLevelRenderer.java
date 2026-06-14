package net.uku3lig.hitrange.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.state.level.LevelRenderState;
import net.uku3lig.hitrange.CircleRenderer;
import net.uku3lig.hitrange.HitRange;
import net.uku3lig.hitrange.config.HitRangeConfig;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public class MixinLevelRenderer {
    @Shadow
    @Final
    private EntityRenderDispatcher entityRenderDispatcher;


    @Inject(method = "submitEntities", at = @At(value = "TAIL"))
    public void renderFirstPersonCircle(PoseStack poseStack, LevelRenderState levelRenderState, SubmitNodeCollector output, CallbackInfo ci) {
        LocalPlayer player = Minecraft.getInstance().player;
        HitRangeConfig config = HitRange.getManager().getConfig();
        if (!config.isEnabled() || !config.isShowSelf()) return;

        var state = (AvatarRenderState) this.entityRenderDispatcher.extractEntity(player, 0);

        poseStack.pushPose();
        poseStack.translate(HitRange.playerPos);
        output.submitCustomGeometry(poseStack, CircleRenderer.getCurrentType(state), (entry, vertices) -> CircleRenderer.drawCircle(entry, vertices, state));
        poseStack.popPose();
    }
}
