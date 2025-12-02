package soup587.exturaddon.overrides;

import net.minecraft.client.player.Input;
import net.minecraft.world.phys.Vec2;

public class NoInput extends Input {
	@Override
	public Vec2 getMoveVector() {
		return new Vec2(0, 0);
	}
}
