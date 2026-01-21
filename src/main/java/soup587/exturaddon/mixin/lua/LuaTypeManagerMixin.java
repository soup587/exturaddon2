package soup587.exturaddon.mixin.lua;

import org.figuramc.figura.lua.LuaTypeManager;
import org.jetbrains.annotations.NotNull;
import org.luaj.vm2.lib.VarArgFunction;
import soup587.exturaddon.lua.LuaTypeFunctions;

import java.lang.reflect.*;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LuaTypeManager.class)
public class LuaTypeManagerMixin {

	@Inject(at=@At("HEAD"), method="getWrapper")
	public void getWrapper(@NotNull Method method, CallbackInfoReturnable<VarArgFunction> cir){
        if(method.getParameterTypes().length == 0) {
            if(Modifier.isStatic(method.getModifiers())){
                cir.setReturnValue(new LuaTypeFunctions.StaticFunctionWithoutArgs((LuaTypeManager) (Object) this, method));
				return;
            }
			cir.setReturnValue( new LuaTypeFunctions.InstanceFunctionWithoutArgs((LuaTypeManager) (Object) this, method));
            return;
        }
        cir.setReturnValue(new LuaTypeFunctions.FunctionWithArgs((LuaTypeManager) (Object) this, method));
	}

}
