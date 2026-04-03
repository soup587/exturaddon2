package soup587.exturaddon.mixin.render;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import org.figuramc.figura.avatar.Avatar;
import org.figuramc.figura.avatar.AvatarManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import soup587.exturaddon.ducks.RendererAPIAccessor;

@Mixin(LevelRenderer.class)
public class LevelRendererMixin {

	@Shadow @Final private Minecraft minecraft;
//? if > 1.21.9{
 	/*@ModifyExpressionValue(method = "extractVisibleEntities", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;isSleeping()Z"))
*///? } else if > 1.21.1{
 	/*@ModifyExpressionValue(method = "collectVisibleEntities", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;isSleeping()Z"))
*///? } else {

	@ModifyExpressionValue(method = "renderLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;isSleeping()Z"))
//?}
	private boolean forceModelRender(boolean original) {
		if (original) return true;
		Avatar avatar = AvatarManager.getAvatar(this.minecraft.getCameraEntity());
		return avatar != null && avatar.luaRuntime != null && ((RendererAPIAccessor) avatar.luaRuntime.renderer).shouldRenderFirstPerson();
	}
}
