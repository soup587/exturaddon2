package soup587.exturaddon.ducks;

public interface RendererAPIAccessor {
	boolean shouldRenderPlayerHealth();
	boolean shouldRenderSelectedItemName();
	boolean shouldRenderHotbar();
	boolean shouldRenderExperienceBar();
	boolean shouldRenderJumpMeter();
	boolean shouldRenderEffects();
	boolean shouldRenderGUI();
	boolean shouldRenderFirstPerson();
}
