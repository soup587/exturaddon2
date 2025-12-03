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
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;
import java.util.UUID;

@Mixin(Avatar.class)
public class AvatarMixin {

	@Shadow public boolean loaded;

	@Shadow public FiguraLuaRuntime luaRuntime;

	@Shadow @Final public PermissionPack.PlayerPermissionPack permissions;

	@Shadow public EntityRenderMode renderMode;

	@Shadow
	@Final
	public Map<String, Avatar.Instructions> customInstructions;

	@Invoker("run")
	Varargs exturaddon$invokeRun(Object toRun, Avatar.Instructions limit, Object... args) {
		throw new AssertionError();
	};

	public Avatar.Instructions preRender;

	@Inject(method = "<init>(Ljava/util/UUID;)V", at = @At("HEAD"))
	public void constructor(UUID owner, CallbackInfo ci) {
		customInstructions.putIfAbsent("preRender", new Avatar.Instructions(permissions.get(Permissions.RENDER_INST)));
	}

	public void preRenderEvent(float delta) {
		if (loaded && luaRuntime != null && luaRuntime.getUser() != null)
			exturaddon$invokeRun("PRE_RENDER", preRender, delta, renderMode.name());
	}
}
