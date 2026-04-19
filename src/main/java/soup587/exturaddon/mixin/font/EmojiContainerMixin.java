package soup587.exturaddon.mixin.font;

import com.google.gson.JsonObject;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import com.moulberry.mixinconstraints.annotations.IfModAbsent;
import org.figuramc.figura.font.EmojiContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@IfModLoaded(value = "figura", minVersion = "0.1.0", maxVersion = "0.1.5")
@IfModAbsent(value = "extura")
@Mixin(EmojiContainer.class)
public class EmojiContainerMixin {

	@Inject(at= @At(value = "INVOKE", target = "Lorg/figuramc/figura/font/EmojiUnicodeLookup;putMetadata(ILorg/figuramc/figura/font/EmojiMetadata;)V"),method = "Lorg/figuramc/figura/font/EmojiContainer;<init>(Ljava/lang/String;Lcom/google/gson/JsonObject;)V")
	void fromJson(String containerName, JsonObject data, CallbackInfo ci){}

}
