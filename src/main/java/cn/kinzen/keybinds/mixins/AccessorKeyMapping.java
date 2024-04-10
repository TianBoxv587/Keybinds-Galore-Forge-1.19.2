package cn.kinzen.keybinds.mixins;


import net.minecraft.client.KeyMapping;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(KeyMapping.class)
public interface  AccessorKeyMapping {
    @Accessor("isDown")
    void setDown(boolean isDown);

    @Accessor("clickCount")
    void setClickCount(int clickCount);
}
