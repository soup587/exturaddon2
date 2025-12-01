package soup587.exturaddon.platform.fabric;

//? fabric {

import soup587.exturaddon.Exturaddon;
import net.fabricmc.api.ModInitializer;

public class FabricEntrypoint implements ModInitializer {

	@Override
	public void onInitialize() {
		Exturaddon.onInitialize();
	}
}
//?}
