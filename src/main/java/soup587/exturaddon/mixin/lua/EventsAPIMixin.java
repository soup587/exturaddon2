package soup587.exturaddon.mixin.lua;

import org.figuramc.figura.lua.LuaWhitelist;
import org.figuramc.figura.lua.api.event.EventsAPI;
import org.figuramc.figura.lua.api.event.LuaEvent;
import org.figuramc.figura.lua.docs.LuaFieldDoc;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import soup587.exturaddon.ducks.EventsAPIAccessor;

import java.util.Map;

@Mixin(EventsAPI.class)
public class EventsAPIMixin implements EventsAPIAccessor {

	@Shadow @Final private Map<String, LuaEvent> events;

	@LuaWhitelist
	@LuaFieldDoc("events.pre_render")
	public LuaEvent PRE_RENDER;

	@Inject(method = "<init>", at = @At("RETURN"))
	void addEvents(CallbackInfo ci) {
		PRE_RENDER = new LuaEvent();

		events.put("PRE_RENDER", PRE_RENDER);
	}

	public LuaEvent extura$getPreRenderEvent() {
		return PRE_RENDER;
	}

}
