package soup587.exturaddon.mixin.render;

//? if > 1.20.1
//import net.minecraft.client.DeltaTracker;
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
import soup587.exturaddon.ducks.AvatarAccessor;

@Mixin(GameRenderer.class)
public class GameRendererMixin {

	@Shadow @Final private Minecraft minecraft;

	@Inject(method = "render", at = @At("HEAD"))
	//? if < 1.20.2 {
	private void preRender(float tickDelta, long startTime, boolean tick, CallbackInfo ci) {
	//?} else
	//private void preRender(DeltaTracker tickDelta, boolean p_109096_, CallbackInfo ci) {
		Avatar avatar = AvatarManager.getAvatar(this.minecraft.getCameraEntity());
		if (avatar == null)
			return;
		avatar.customInstructions.get("preRender").reset(avatar.permissions.get(Permissions.RENDER_INST));

		AvatarManager.executeAll("preRender", renderedAvatar -> ((AvatarAccessor)renderedAvatar).extura$preRenderEvent(
			//? if < 1.20.2 {
				tickDelta
			//?} else
				//tickDelta.getRealtimeDeltaTicks()
		));
	}

}
