package soup587.exturaddon.mixin.lua;

import com.mojang.brigadier.StringReader;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.arguments.blocks.BlockStateArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.figuramc.figura.FiguraMod;
import org.figuramc.figura.avatar.Avatar;
import org.figuramc.figura.avatar.AvatarManager;
import org.figuramc.figura.avatar.local.LocalAvatarFetcher;
import org.figuramc.figura.avatar.local.LocalAvatarLoader;
import org.figuramc.figura.backend2.NetworkStuff;
import org.figuramc.figura.gui.widgets.lists.AvatarList;
import org.figuramc.figura.lua.LuaNotNil;
import org.figuramc.figura.lua.LuaWhitelist;
import org.figuramc.figura.lua.api.HostAPI;
import org.figuramc.figura.lua.api.entity.EntityAPI;
import org.figuramc.figura.lua.api.world.BlockStateAPI;
import org.figuramc.figura.lua.docs.LuaMethodDoc;
import org.figuramc.figura.lua.docs.LuaMethodOverload;
import org.figuramc.figura.math.vector.FiguraVec2;
import org.figuramc.figura.math.vector.FiguraVec3;
import org.figuramc.figura.mixin.input.KeyMappingAccessor;
import org.figuramc.figura.utils.LuaUtils;
import org.luaj.vm2.LuaError;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import soup587.exturaddon.ExturaPermissions;
import soup587.exturaddon.lua.KeyMappingAPI;
import soup587.exturaddon.lua.NotImplementedLuaError;
import soup587.exturaddon.overrides.ExturaInput;
import soup587.exturaddon.overrides.NoInput;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Mixin(HostAPI.class)
public abstract class HostAPIMixin {

	@Shadow @Final private Avatar owner;
	@Shadow @Final private boolean isHost;
	@Shadow @Final private Minecraft minecraft;

	@Shadow
	public abstract boolean isHost();

	@LuaWhitelist
	@LuaMethodDoc("host.allow_extura_cheats")
	public Boolean allowExturaCheats() {
		if(!this.isHost) return false;
		LocalPlayer player = this.minecraft.player;
		return player != null && ((player.hasPermissions(2)  ||
				this.minecraft.isLocalServer() ||
				//? if < 1.20.2 {
				(player.getScoreboard().hasObjective("extura_can_cheat"))
				//?} else
				//(player.getScoreboard().getObjective("extura_can_cheat") != null)
		));
	}
	public Boolean canExturaCheat() {
		if(!this.isHost) return false;
		LocalPlayer player = this.minecraft.player;
		if(player == null) return false;
		if(player.hasPermissions(2)  ||
				this.minecraft.isLocalServer() ||
				//? if < 1.20.2 {
				(player.getScoreboard().hasObjective("extura_can_cheat"))
				//?} else
				//(player.getScoreboard().getObjective("extura_can_cheat") != null)
		) return true;
		if(!owner.noPermissions.contains(ExturaPermissions.EXTURA_CHEATING)){
			owner.noPermissions.add(ExturaPermissions.EXTURA_CHEATING);
		}
		return false;
	}

	@LuaWhitelist
	@LuaMethodDoc(
			overloads = {
					@LuaMethodOverload,
					@LuaMethodOverload(
							argumentTypes = Boolean.class,
							argumentNames = "sprinting"
					)
			},
			value = "host.run_method"
	)
	public Object runMethod(String name, Object... args) {
		if (!this.isHost || !this.canExturaCheat()) return this;
		Method med;
		try {
			Class c = this.minecraft.player.getClass();
			if(args == null || args.length == 0){
				med = c.getMethod(name);
			}else{

				Class<?>[] argumentTypes = new Class[args.length];
				var len = args.length;
				for (int i = 0; i < len; i++) {
					argumentTypes[i] = args[i].getClass();
				}
				med = c.getMethod(name, argumentTypes);
			}
		} catch (NoSuchMethodException e) {
			throw new LuaError("No such method method "+name);
		}
		try{
			return med.invoke(name,args);
		}catch (InvocationTargetException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new LuaError("Unable to access method "+name);
		}
	}

	@LuaWhitelist
	@LuaMethodDoc("host.upload_avatar")
	public boolean uploadAvatar() {
		if(!this.isHost) return false;
		Avatar avatar = AvatarManager.getAvatarForPlayer(FiguraMod.getLocalPlayerUUID());
		if(avatar == null) throw new LuaError("Cannot upload a null avatar!");
		try {
			LocalAvatarLoader.loadAvatar(null, null);
		} catch (Exception ignored) {}
		NetworkStuff.uploadAvatar(avatar);
		AvatarList.selectedEntry = null;
		return true;
	}
	/*@LuaWhitelist
	@LuaMethodDoc("host.upload_avatar_to")
	public boolean uploadAvatarTo(boolean backend,boolean fsb) {
		if(!this.isHost) return false;
		Avatar avatar = AvatarManager.getAvatarForPlayer(FiguraMod.getLocalPlayerUUID());
		if(avatar == null) throw new LuaError("Cannot upload a null avatar!");
		try {
			LocalAvatarLoader.loadAvatar(null, null);
		} catch (Exception ignored) {}
		NetworkStuff.uploadAvatar(avatar,(!backend && !fsb) ? Destination.FSB_OR_BACKEND : Destination.fromBool(backend,fsb));
		AvatarList.selectedEntry = null;
		return true;
	}
	 */
	@LuaWhitelist
	@LuaMethodDoc(
			overloads = {
					@LuaMethodOverload(argumentTypes = String.class, argumentNames = "Avatar Owner"),
					@LuaMethodOverload(argumentTypes = EntityAPI.class, argumentNames = "Avatar Owner")
			},
			value = "host.reload_avatar")
	public void reloadAvatar(Object playerUUID) {
		if(!this.isHost) return;
		final UUID uuid;
		if(playerUUID instanceof EntityAPI){
			uuid = ((EntityAPI) playerUUID).getEntity().getUUID();
		}else if(playerUUID instanceof String){
			uuid = UUID.fromString((String) playerUUID);
		}else if(playerUUID != null){
			throw new LuaError("Expected String, EntityAPI or Nil");
		}else{
			uuid = FiguraMod.getLocalPlayerUUID();
		}
		// (UUID != null && !UUID.isEmpty() ? UUID.fromString(UUID) : FiguraMod.getLocalPlayerUUID() )
		AvatarManager.reloadAvatar(uuid);
	}

	@LuaWhitelist
	@LuaMethodDoc("host.load_local_avatar") // Did not steal this from GoofyPlugin, no proof
	public void loadLocalAvatar(String path) {
		if(!this.isHost) return;
		if(path == null || path.isEmpty()){
			AvatarManager.clearAvatars(FiguraMod.getLocalPlayerUUID());
			AvatarList.selectedEntry = null;
			return;
		}
		Path _path = LocalAvatarFetcher.getLocalAvatarDirectory().resolve(path);
		AvatarManager.loadLocalAvatar(_path);
		AvatarList.selectedEntry = _path;
	}

	@LuaWhitelist
	@LuaMethodDoc(
			overloads = {
					@LuaMethodOverload(
							argumentTypes = Boolean.class,
							argumentNames = "vec"
					),
			},
			value = "host.set_velocity"
	)
	public void setVelocity(Object x, Double y, Double z) {
		if(!canExturaCheat()) return;
		this.minecraft.player.setDeltaMovement(LuaUtils.parseVec3("player_setVelocity", x, y, z).asVec3());

	}
	@LuaWhitelist
	@LuaMethodDoc(
			overloads = {
					@LuaMethodOverload(
							argumentTypes = Boolean.class,
							argumentNames = "vec"
					),
			},
			value = "host.travel"
	)
	public void travel(Object x, Double y, Double z) {
		if(!canExturaCheat()) return;
		this.minecraft.player.travel(LuaUtils.parseVec3("player_travel", x, y, z).asVec3());

	}
	@LuaWhitelist
	@LuaMethodDoc("host.set_pose")
	public void setPose(String pose) {
		if(!canExturaCheat()) return;
		try{
			Pose _pose = Pose.valueOf(pose);
			this.minecraft.player.setPose(_pose);
		}catch(IllegalArgumentException ignored){
			throw new LuaError("Invalid pose " + pose);
		}
	}
	@LuaWhitelist
	@LuaMethodDoc("host.set_physics")
	public void setPhysics(Boolean physics) {
		if(!canExturaCheat()) return;
		this.minecraft.player.noPhysics = !physics;
	}
	@LuaWhitelist
	@LuaMethodDoc(
			overloads = {
					@LuaMethodOverload(
							argumentTypes = Boolean.class,
							argumentNames = "pos"
					),
			},
			value = "host.set_pos"
	)
	public void setPos(Object x, Double y, Double z) {
		if (!canExturaCheat() || x == null) return;
		LocalPlayer player = this.minecraft.player;
		player.setPos(LuaUtils.parseVec3("player_setPos", x, y, z).asVec3());
	}
	@LuaWhitelist
	@LuaMethodDoc("host.start_riding")
	public void startRiding(EntityAPI entity,boolean bool) {
		if (!canExturaCheat()) return;
		LocalPlayer player = this.minecraft.player;
		if(entity == null) {
			player.removeVehicle();
			return;
		}
//?		if > 1.21.11 {
//			throw new NotImplementedLuaError();
//?} else {
			Entity t = entity.getEntity();
			if(t == player) throw new LuaError("You cannot ride yourself!");
			player.startRiding(t,bool);
//?		}
	}

	@LuaWhitelist
	@LuaMethodDoc("host.drop_item")
	public void dropItem(boolean dropAll) {
		if(!this.isHost) return;
		LocalPlayer player = this.minecraft.player;
		player.drop(dropAll == true);
	}
	@LuaWhitelist
	@LuaMethodDoc("host.close_container")
	public void closeContainer() {
		LocalPlayer player = this.minecraft.player;
		player.closeContainer();
	}
	@LuaWhitelist
	@LuaMethodDoc("host.start_using_item")
	public void startUsingItem(boolean offHand) {
		if (!canExturaCheat()) return;
		LocalPlayer player = this.minecraft.player;
		player.startUsingItem(offHand ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND);
	}
	@LuaWhitelist
	@LuaMethodDoc("host.stop_using_item")
	public void stopUsingItem() {
		if (!canExturaCheat()) return;
		LocalPlayer player = this.minecraft.player;
		player.stopUsingItem();
	}
	@LuaWhitelist
	@LuaMethodDoc("host.send_open_inventory")
	public void sendOpenInventory() {
		if (!canExturaCheat()) return;
		LocalPlayer player = this.minecraft.player;
		player.sendOpenInventory();
	}

	@LuaWhitelist
	@LuaMethodDoc(
			overloads = {
					@LuaMethodOverload(
							argumentTypes = {Boolean.class},
							argumentNames = {"playerMovement"}
					)
			},
			value = "host.set_player_movement"
	)
	public void setPlayerMovement(Boolean playerMovement) {
//?		if < 1.21.2 {
		LocalPlayer player;
		if (!this.isHost || (player = this.minecraft.player) == null || !canExturaCheat()) return;
		player.input = (playerMovement ? new ExturaInput(this.minecraft.options) : new NoInput());
//?}else {
//		throw new NotImplementedLuaError();
//?}
	}
	@LuaWhitelist
	@LuaMethodDoc(
			overloads = {
					@LuaMethodOverload(
							argumentTypes = {String.class,Boolean.class},
							argumentNames = {"input","state"}
					),
					@LuaMethodOverload(
							argumentTypes = {String.class},
							argumentNames = {"input","state"}
					),
			},
			value = "host.override_player_movement"
	)
	public void overridePlayerMovement(@LuaNotNil String input,Boolean sta) {
		if(!canExturaCheat()) return;
		LocalPlayer player;
		if (!this.isHost || (player = this.minecraft.player) == null) return;
//?		if < 1.21.2 {
		if(!(player.input instanceof ExturaInput)){
			player.input = new ExturaInput(this.minecraft.options);
		}
		int state = sta == null ? 0 : sta ? 2 : 1;
		ExturaInput inputObj =(ExturaInput) player.input;
		switch(input.toLowerCase()){
			case "up": inputObj.upOverride = state; break;
			case "down": inputObj.downOverride = state; break;
			case "left": inputObj.leftOverride = state; break;
			case "right": inputObj.rightOverride = state; break;
			case "jump": inputObj.jumpOverride = state; break;
			case "shift": inputObj.shiftOverride = state; break;
			default: throw new LuaError("Invalid input");
		}
//?}else {
//		throw new NotImplementedLuaError();
//?}
	}
	@LuaWhitelist
	@LuaMethodDoc("host.get_player_movement")
	public Boolean getPlayerMovement() {
		LocalPlayer player;
		if (!this.isHost || (player = this.minecraft.player) == null) return true;
//?		if < 1.21.2 {
		return (player.input instanceof NoInput);
//?}else {
//		throw new NotImplementedLuaError();
//?}
	}

	@LuaWhitelist
	@LuaMethodDoc("host.get_last_death_pos")
	public FiguraVec3 getLastDeathPos() {
		if(!isHost) return null;
		LocalPlayer player = this.minecraft.player;
		if (player != null) {
			Optional<GlobalPos> deathLocation = player.getLastDeathLocation();
			if(deathLocation.isPresent()) return FiguraVec3.fromBlockPos(deathLocation.get().pos());
		}
		return null;
	}


	@LuaWhitelist
	@LuaMethodDoc(
			overloads = {
					@LuaMethodOverload(
							argumentTypes = FiguraVec2.class,
							argumentNames = "vec"
					),
					@LuaMethodOverload(
							argumentTypes = {Double.class, Double.class},
							argumentNames = {"x", "y"}
					)
			},
			value = "host.set_rot"
	)
	public void setRot(Object x, Double y) {
		if(!canExturaCheat()) return;
		FiguraVec2 vec = LuaUtils.parseVec2("player_setRot", x, y);
		LocalPlayer player = this.minecraft.player;
		player.setXRot((float) vec.x);
		player.setYRot((float) vec.y);

	}

	@LuaWhitelist
	@LuaMethodDoc(
			overloads = {
					@LuaMethodOverload(
							argumentTypes = {Double.class},
							argumentNames = {"angle"}
					)
			},
			value = "host.set_body_rot"
	)
	public void setBodyRot(Double angle) {
		if(!canExturaCheat()) return;
		LocalPlayer player = this.minecraft.player;
		player.setYBodyRot(angle.floatValue());

	}

	@LuaWhitelist
	@LuaMethodDoc(
			overloads = {
					@LuaMethodOverload(
							argumentTypes = {Double.class},
							argumentNames = {"angle"}
					)
			},
			value = "host.set_body_offset_rot"
	)
	public void setBodyOffsetRot(Double angle) {
		if(!canExturaCheat()) return;
		LocalPlayer player = this.minecraft.player;
		player.setYBodyRot( angle.floatValue() + player.getYRot() );
	}

	@LuaWhitelist
	@LuaMethodDoc(
			overloads = {
					@LuaMethodOverload(
							argumentTypes = {Boolean.class},
							argumentNames = {"hasForce"}
					)
			},
			value = "host.set_gravity"
	)
	public void setGravity(Boolean hasForce) {
		if(!canExturaCheat()) return;
		LocalPlayer player = this.minecraft.player;
		if (player == null) return;
		player.setNoGravity(!hasForce);

	}

	@LuaWhitelist
	@LuaMethodDoc(
			overloads = {
					@LuaMethodOverload(
							argumentTypes = {Boolean.class},
							argumentNames = {"hasForce"}
					)
			},
			value = "host.set_drag"
	)
	public void setDrag(Boolean hasForce) {
		if(!canExturaCheat()) return;
		LocalPlayer player = this.minecraft.player;
		if (player == null) return;
		player.setDiscardFriction(hasForce != true);
	}

	@LuaWhitelist
	@LuaMethodDoc(
			overloads = {
					@LuaMethodOverload(
							argumentTypes = {BlockStateAPI.class, FiguraVec3.class},
							argumentNames = {"block", "pos"}
					),
					@LuaMethodOverload(
							argumentTypes = {BlockStateAPI.class, Double.class, Double.class, Double.class},
							argumentNames = {"block", "x", "y", "z"}
					)
			},
			value = "host.set_block"
	)
	public Boolean setBlock(@LuaNotNil String string, Object x, Double y, Double z) {
		if(!this.isHost || !canExturaCheat()) return false;
		BlockPos pos = LuaUtils.parseVec3("setBlock", x, y, z).asBlockPos();
		try {
			Level level = this.minecraft.level;
			BlockState block = BlockStateArgument.block(CommandBuildContext.simple(level.registryAccess(), level.enabledFeatures())).parse(new StringReader(string)).getState();

			level.setBlockAndUpdate(pos,block);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	@LuaWhitelist
	@LuaMethodDoc("host.get_key_mappings")
	public Map<String, KeyMappingAPI<?>> getKeyMappings() {
		if (!this.isHost()) return new HashMap<>();
		HashMap<String, KeyMappingAPI<?>> mappingslist = new HashMap<>();

		Map<String, KeyMapping> mappings = KeyMappingAccessor.getAll();

		mappings.forEach((k,v) -> {
			mappingslist.put(k,KeyMappingAPI.wrap(v));
		});
		return mappingslist;
	}

	// borrowed this from vivecraft - jess

	@LuaWhitelist
	@LuaMethodDoc("host.set_bind_pressed")
	public void setBindPressed(@LuaNotNil String id, boolean state) {
		if (!isHost()) return;
		KeyMapping key = KeyMappingAccessor.getAll().get(id);
		if (key == null)
			throw new LuaError("Failed to find key: \"" + id + "\"");
		key.setDown(state);
		key.clickCount += 1;
	}
}
