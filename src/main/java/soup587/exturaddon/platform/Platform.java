package soup587.exturaddon.platform;

public interface Platform {
	boolean isModLoaded(String modId);

	ModLoader loader();

	enum ModLoader {
		FABRIC, NEOFORGE, FORGE, QUILT
	}
}
