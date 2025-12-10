package soup587.exturaddon.mixin;

import net.minecraft.world.entity.EntityType;
import org.figuramc.figura.avatar.Avatar;
import org.figuramc.figura.lua.FiguraLuaRuntime;
import org.figuramc.figura.model.rendering.EntityRenderMode;
import org.figuramc.figura.permissions.PermissionPack;
import org.figuramc.figura.permissions.Permissions;
import org.jetbrains.annotations.Nullable;
import org.luaj.vm2.Varargs;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import soup587.exturaddon.ducks.AvatarAccessor;

import java.util.Map;
import java.util.UUID;

@Mixin(Avatar.class)
public abstract class AvatarMixin implements AvatarAccessor {

	@Shadow public boolean loaded;

	@Shadow public FiguraLuaRuntime luaRuntime;

	@Shadow @Final public PermissionPack.PlayerPermissionPack permissions;

	@Shadow public EntityRenderMode renderMode;

	@Shadow
	@Final
	public Map<String, Avatar.Instructions> customInstructions;

	@Invoker("run")
	abstract Varargs exturaddon$invokeRun(Object toRun, Avatar.Instructions limit, Object... args);

	public Avatar.Instructions preRender;

	@Inject(method = "<init>(Ljava/util/UUID;)V", at = @At("TAIL"))
	private void constructor(UUID owner, CallbackInfo ci) {
		this.preRender = new Avatar.Instructions(permissions.get(Permissions.RENDER_INST));
		customInstructions.putIfAbsent("preRender", this.preRender);
	}

	@Unique
	public void extura$preRenderEvent(float delta) {
		if (loaded && luaRuntime != null && luaRuntime.getUser() != null)
			exturaddon$invokeRun("PRE_RENDER", preRender, delta, renderMode.name());
	}
}
