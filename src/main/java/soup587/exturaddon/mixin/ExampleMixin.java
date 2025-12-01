package soup587.exturaddon.mixin;

import net.minecraft.client.Minecraft;
import soup587.exturaddon.Exturaddon;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class ExampleMixin {

	@Inject(method = "onGameLoadFinished", at = @At("RETURN"))
	private void afterLoadLevel(CallbackInfo ci) {
		Exturaddon.LOGGER.info("Game Loaded!");
	}

}
