package soup587.exturaddon.mixin.render;

//? if > 1.20.1
import net.minecraft.client.DeltaTracker;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.PlayerRideableJumping;
import org.figuramc.figura.avatar.Avatar;
import org.figuramc.figura.avatar.AvatarManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import soup587.exturaddon.ducks.RendererAPIAccessor;

@Mixin(Gui.class)
public class GuiMixin {

	@Shadow @Final private Minecraft minecraft;

	//? if < 1.20.2 {
	/*@Inject(at = @At("HEAD"), method = "renderHotbar", cancellable = true)
	private void renderHotbar(float tickDelta, GuiGraphics graphics, CallbackInfo ci) {
	*///?} else {
	@Inject(at = @At("HEAD"), method = "renderItemHotbar", cancellable = true)
	private void renderHotbar(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {
	//?}
		Entity entity = this.minecraft.getCameraEntity(); Avatar avatar;
		if (entity == null || (avatar = AvatarManager.getAvatar(entity)) == null || avatar.luaRuntime == null || ((RendererAPIAccessor) avatar.luaRuntime.renderer).shouldRenderHotbar())
			return;
		ci.cancel();
	}
	@Inject(at = @At("HEAD"), method = "renderEffects", cancellable = true)
	//? if < 1.20.2 {
	/*private void setRenderEffects(GuiGraphics graphics, CallbackInfo ci) {
	*///?} else
	private void setRenderEffects(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {
		Entity entity = this.minecraft.getCameraEntity(); Avatar avatar;
		if (entity == null || (avatar = AvatarManager.getAvatar(entity)) == null || avatar.luaRuntime == null || ((RendererAPIAccessor) avatar.luaRuntime.renderer).shouldRenderEffects())
			return;
		ci.cancel();
	}
	//? if < 1.21.2 {
	@Inject(at = @At("HEAD"), method = "renderJumpMeter", cancellable = true)
	private void setRenderJumpMeter(PlayerRideableJumping mount, GuiGraphics graphics, int x, CallbackInfo ci) {
		Entity entity = this.minecraft.getCameraEntity(); Avatar avatar;
		if (entity == null || (avatar = AvatarManager.getAvatar(entity)) == null || avatar.luaRuntime == null || ((RendererAPIAccessor) avatar.luaRuntime.renderer).shouldRenderJumpMeter())
			return;
		ci.cancel();
	}
	@Inject(at = @At("HEAD"), method = "renderExperienceBar", cancellable = true)
	private void setRenderExperienceBar(GuiGraphics graphics, int x, CallbackInfo ci) {
		Entity entity = this.minecraft.getCameraEntity(); Avatar avatar;
		if (entity == null || (avatar = AvatarManager.getAvatar(entity)) == null || avatar.luaRuntime == null || ((RendererAPIAccessor) avatar.luaRuntime.renderer).shouldRenderExperienceBar())
			return;
		ci.cancel();
	}
	//? } else {
	/*@ModifyExpressionValue(method = "renderHotbarAndDecorations", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/MultiPlayerGameMode;hasExperience()Z"))
	private boolean renderExperienceBar(boolean bool) {
		if(!bool) return false;
		Entity entity = this.minecraft.getCameraEntity(); Avatar avatar;
		if (entity == null || (avatar = AvatarManager.getAvatar(entity)) == null || avatar.luaRuntime == null || ((RendererAPIAccessor) avatar.luaRuntime.renderer).shouldRenderExperienceBar())
			return true;
		return false;
	}
	*///? }

	@Inject(at = @At("HEAD"), method = "renderSelectedItemName", cancellable = true)
	private void setRenderSelectedItemName(GuiGraphics graphics, CallbackInfo ci) {
		Entity entity = this.minecraft.getCameraEntity(); Avatar avatar;
		if (entity == null || (avatar = AvatarManager.getAvatar(entity)) == null || avatar.luaRuntime == null ||
				((RendererAPIAccessor) avatar.luaRuntime.renderer).shouldRenderSelectedItemName())
			return;
		ci.cancel();
	}
	@Inject(at = @At("HEAD"), method = "renderPlayerHealth", cancellable = true)
	private void setRenderPlayerHealth(GuiGraphics graphics, CallbackInfo ci) {
		Entity entity = this.minecraft.getCameraEntity(); Avatar avatar;
		if (entity == null || (avatar = AvatarManager.getAvatar(entity)) == null || avatar.luaRuntime == null ||
				((RendererAPIAccessor) avatar.luaRuntime.renderer).shouldRenderPlayerHealth())
			return;
		ci.cancel();
	}

}
