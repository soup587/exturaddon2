package soup587.exturaddon.mixin.font;


import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import com.moulberry.mixinconstraints.annotations.IfModAbsent;
import com.google.gson.JsonObject;
import org.figuramc.figura.font.EmojiMetadata;
import org.figuramc.figura.utils.JsonUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;


@IfModLoaded(value = "figura", minVersion = "0.1.0", maxVersion = "0.1.5")
@IfModAbsent(value = "extura")
@Mixin(EmojiMetadata.class)
public class EmojiMetadataMixin {
	private static final String JSON_KEY_FRAMES = "frames";
	private static final String JSON_KEY_FRAME_TIME = "frametime";
	private static final String JSON_KEY_WIDTH = "width";
	private static final String JSON_KEY_NAMES = "names";
	private static final String JSON_KEY_SHORTCUTS = "shortcuts";
	private static final String JSON_KEY_DEFAULT_COLOR = "color";
	public static EmojiMetadata fromJson(JsonObject entry) {
        int frameCount = JsonUtils.getIntOrDefault(entry, JSON_KEY_FRAMES, 1);
        int frameTime = JsonUtils.getIntOrDefault(entry, JSON_KEY_FRAME_TIME, 1);
        int width = JsonUtils.getIntOrDefault(entry, JSON_KEY_WIDTH, 8);

//        if (entry.has("color")) {
//            String hexColor = entry.get(JSON_KEY_DEFAULT_COLOR).getAsString();
//            return new EmojiMetadata(frameCount, frameTime, width, Integer.parseInt(hexColor, 16));
//        }

        return new EmojiMetadata(frameCount, frameTime, width);
    }


}
