package soup587.exturaddon.mixin.lua;

import net.minecraft.client.Minecraft;
import org.figuramc.figura.lua.LuaWhitelist;
import org.figuramc.figura.lua.api.RendererAPI;
import org.figuramc.figura.lua.docs.LuaFieldDoc;
import org.figuramc.figura.lua.docs.LuaMethodDoc;
import org.figuramc.figura.lua.docs.LuaMethodOverload;
import org.luaj.vm2.LuaError;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RendererAPI.class)
public class RendererAPIMixin {

	@LuaFieldDoc("renderer.render_player_health")
	public boolean renderPlayerHealth = true;
	@LuaWhitelist
	@LuaFieldDoc("renderer.render_selected_item_name")
	public boolean renderSelectedItemName = true;
	@LuaWhitelist
	@LuaFieldDoc("renderer.render_hotbar")
	public boolean renderHotbar = true;
	@LuaWhitelist
	@LuaFieldDoc("renderer.render_experience_bar")
	public boolean renderExperienceBar = true;
	@LuaWhitelist
	@LuaFieldDoc("renderer.render_jump_meter")
	public boolean renderJumpMeter = true;
	@LuaWhitelist
	@LuaFieldDoc("renderer.render_Effects")
	public boolean renderEffects = true;
	@LuaWhitelist
	@LuaFieldDoc("renderer.render_gui")
	public boolean renderGUI = true;
	@LuaFieldDoc("renderer.render_first_person")
	public boolean renderFirstPerson;

	public boolean renderLeftItem, renderRightItem;

	@LuaWhitelist
	@LuaMethodDoc("renderer.should_render_first_person")
	public boolean shouldRenderFirstPerson() {
		return renderFirstPerson;
	}

	@LuaWhitelist
	@LuaMethodDoc(
			overloads = @LuaMethodOverload(
					argumentTypes = Boolean.class,
					argumentNames = "bool"
			),
			value = "renderer.set_render_first_person"
	)
	public RendererAPI setRenderFirstPerson(boolean bool) {
		this.renderFirstPerson = bool;
		return (RendererAPI)(Object)this;
	}

	@LuaMethodDoc("renderer.get_delta_time")
	public Float getDeltaTime() { return Minecraft.getInstance().getDeltaFrameTime(); }

	@LuaWhitelist
	@LuaMethodDoc(
			overloads = @LuaMethodOverload(
					argumentTypes = Boolean.class,
					argumentNames = "bool"
			),
			aliases = "renderLeftItem",
			value = "renderer.set_render_left_item"
	)
	public RendererAPI setRenderLeftItem(Boolean bool) {
		this.renderLeftItem = bool;
		return (RendererAPI)(Object)this;
	}

	@LuaWhitelist
	public RendererAPI renderLeftItem(Boolean bool) {
		return setRenderLeftItem(bool);
	}

	@LuaWhitelist
	@LuaMethodDoc(
			overloads = @LuaMethodOverload(
					argumentTypes = Boolean.class,
					argumentNames = "bool"
			),
			aliases = "renderRightItem",
			value = "renderer.set_render_right_item"
	)
	public RendererAPI setRenderRightItem(Boolean bool) {
		this.renderRightItem = bool;
		return (RendererAPI)(Object)this;
	}

	@LuaWhitelist
	public RendererAPI renderRightItem(Boolean bool) {
		return setRenderRightItem(bool);
	}

	@Inject(method="__index", at = @At("HEAD"), cancellable = true)
	public void addNewVars(String arg, CallbackInfoReturnable<Object> cir) {
		var returnr = (switch(arg) {
			case "renderPlayerHealth" -> renderPlayerHealth;
			case "renderSelectedItemName" -> renderSelectedItemName;
			case "renderHotbar" -> renderHotbar;
			case "renderExperienceBar" -> renderExperienceBar;
			case "renderJumpMeter" -> renderJumpMeter;
			case "renderEffects" -> renderEffects;
			case "renderGUI" -> renderGUI;
			case "renderFirstPerson" -> renderFirstPerson;
			default -> null;
		});
		if (returnr != null) {
			cir.setReturnValue(returnr);
		}
	}

	@Inject(method="__newindex", at = @At("HEAD"))
	public void addNewSetVars(String key, boolean value, CallbackInfo ci) {
		switch(key) {
			case "renderPlayerHealth" -> renderPlayerHealth = value;
			case "renderSelectedItemName" -> renderSelectedItemName = value;
			case "renderHotbar" -> renderHotbar = value;
			case "renderExperienceBar" -> renderExperienceBar = value;
			case "renderJumpMeter" -> renderJumpMeter = value;
			case "renderEffects" -> renderEffects = value;
			case "renderGUI" -> renderGUI = value;
			case "renderFirstPerson" -> renderFirstPerson = value;
			default -> throw new LuaError("Cannot assign value on key \"" + key + "\"");
		}
	}

}
