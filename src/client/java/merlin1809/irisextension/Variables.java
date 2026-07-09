package merlin1809.irisextension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.List;
import java.util.Map;
import java.util.Set;

import merlin1809.irisextension.mixin.client.GetShaderPropertiesMixin;
import net.minecraft.client.KeyMapping;

public class Variables {
    public static KeyMapping[] customKeybinds = new KeyMapping[10];

    public static Set<String> tessellationPrograms = new HashSet<>();
    public static Set<String> geometryPrograms = new HashSet<>();

    public static boolean dragonAlive = false;
    public static boolean firstdragonKilled = false;
    public static int crystalsAmount = 0;
    public static int playersCountDragon = 0;
    public static float dragonProgress = 0.0f;

    public static boolean isSereneSeasonsLoaded = false;
    public static boolean isBeltborneLanternsLoaded = false;
    public static boolean isCreateAeronauticsLoaded = false;

    public static boolean[] keyPressed = new boolean[10];

    public static int[] customKeyMethods = new int[10];

    public static int NONE = 0;
    public static int TOGGLE = 1;
    public static int HOLD = 2;

    public static int fromString(String s) {
        return switch (s.trim().toLowerCase(Locale.ROOT)) {
            case "toggle" -> TOGGLE;
            case "hold"  -> HOLD;
            default      -> NONE;
        };
    }

    public static int getKeyMethod(int index) {
        if (index < 0 || index > 9) throw new IndexOutOfBoundsException("Key index must be 0-9");
        return customKeyMethods[index];
    }
}
