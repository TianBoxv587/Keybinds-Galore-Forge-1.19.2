package cn.kinzen.keybinds;



import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class KeybindsManager {

    private static Map<InputConstants.Key, List<KeyMapping>> conflictingKeys = Maps.newHashMap();
    public static Map<InputConstants.Key, KeyMapping> boundKeyMapping = Maps.newHashMap();

    public static boolean handleConflict(InputConstants.Key key) {
        List<KeyMapping> matches = new ArrayList<>();
        KeyMapping[] keysAll = Minecraft.getInstance().options.keyMappings;
        for (KeyMapping bind: keysAll) {
            if (bind.getKey().equals(key) && bind.getKeyConflictContext().conflicts(KeyConflictContext.IN_GAME) && bind.getKeyModifier() == KeyModifier.NONE) {
                matches.add(bind);
            }
        }
        if (matches.size()>1) {
            KeybindsManager.conflictingKeys.put(key, matches);
            return true;
        }else {
            KeybindsManager.conflictingKeys.remove(key);
            return false;
        }
    }

    public static boolean isConflicting(InputConstants.Key key) {
        return conflictingKeys.containsKey(key);
    }


    public static void openConflictMenu(InputConstants.Key key) {
        KeybindsScreen screen = new KeybindsScreen();
        screen.setConflictedKey(key);
        Minecraft.getInstance().setScreen(screen);
    }

    public static List<KeyMapping> getConflicting(InputConstants.Key key) {
        return conflictingKeys.get(key);
    }
}
