package soup587.exturaddon.mixin.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import org.figuramc.figura.avatar.Avatar;
import org.figuramc.figura.avatar.AvatarManager;
import org.figuramc.figura.permissions.Permissions;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class GameRendererMixin {

	@Shadow @Final private Minecraft minecraft;

	@Inject(method = "render", at = @At("HEAD"))
	private void preRender(float tickDelta, long startTime, boolean tick, CallbackInfo ci) {
		Avatar avatar = AvatarManager.getAvatar(this.minecraft.getCameraEntity());
		if (avatar == null)
			return;
		avatar.customInstructions.get("preRender").reset(avatar.permissions.get(Permissions.RENDER_INST));

		AvatarManager.executeAll("preRender", renderedAvatar -> renderedAvatar.preRenderEvent(tickDelta));
	}

}
