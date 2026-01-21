package soup587.exturaddon.lua;


import org.luaj.vm2.LuaError;
import soup587.exturaddon.Exturaddon;

public class NotImplementedLuaError extends LuaError {
	private static final String version = /*$ minecraft */ "N/A";
	// This is marked as deprecated because we really should implement these functions
	@Deprecated
	public NotImplementedLuaError(){
		super("Method not implemented in " + version + " version of " + Exturaddon.MOD_FRIENDLY_NAME);
	}
}
