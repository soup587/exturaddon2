package soup587.exturaddon;

import org.figuramc.figura.lua.FiguraAPIManager;
import soup587.exturaddon.lua.KeyMappingAPI;
import soup587.exturaddon.platform.Platform;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//? fabric {
	import soup587.exturaddon.platform.fabric.FabricPlatform;
//?} forge {
	/*import soup587.exturaddon.platform.forge.ForgePlatform;
*///?} neoforge {
	/*import soup587.exturaddon.platform.neoforge.NeoforgePlatform;
*///?}


@SuppressWarnings("LoggingSimilarMessage")
public class Exturaddon {

	public static final String MOD_ID = /*$ mod_id*/ "exturaddon";
	public static final String MOD_VERSION = /*$ mod_version*/ "0.1.0";
	public static final String MOD_FRIENDLY_NAME = /*$ mod_name*/ "Exturaddon";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	private static final Platform PLATFORM = createPlatformInstance();

	public static void onInitialize() {
		FiguraAPIManager.WHITELISTED_CLASSES.add(KeyMappingAPI.class);
	}

	static Platform xplat() {
		return PLATFORM;
	}

	private static Platform createPlatformInstance() {
		//? fabric {
		return new FabricPlatform();
		//?} neoforge {
		/*return new NeoforgePlatform();
		 *///?} forge {
		/*return new ForgePlatform();
		*///?}
	}
}
