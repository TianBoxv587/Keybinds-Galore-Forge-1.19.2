package cn.kinzen.keybinds.mixins;


import cn.kinzen.keybinds.KeybindsManager;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = KeyMapping.class, priority = -5000)
public abstract class MixinKeyMapping {
    @Shadow private InputConstants.Key key;
    private static final Logger LOGGER = LogManager.getLogger();
    @Inject(method = "set", at = @At("HEAD"), cancellable = true)
    private static void set(InputConstants.Key key, boolean down, CallbackInfo ci){
        if(down){
            boolean confilicting = KeybindsManager.handleConflict(key);
            if(confilicting){
                ci.cancel();
                KeybindsManager.openConflictMenu(key);
            }
        }
    }

    @Inject(method = "setDown", at = @At("HEAD"), cancellable = true)
    private void setDown(boolean down, CallbackInfo ci){
        if (KeybindsManager.isConflicting(this.key)){
            ci.cancel();
        }
    }

    @Inject(method = "click", at = @At("HEAD"), cancellable = true)
    private static void click(InputConstants.Key key, CallbackInfo ci){
        if(KeybindsManager.isConflicting(key)){
            ci.cancel();
            KeybindsManager.openConflictMenu(key);
        }
    }
}
