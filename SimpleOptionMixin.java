package dev.kurdo.client.mixin;

import dev.kurdo.client.module.ModuleManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.SimpleOption;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Overrides gamma (Fullbright) and FOV (Zoom) reads without touching saved settings. */
@Mixin(SimpleOption.class)
public class SimpleOptionMixin<T> {
    @SuppressWarnings("unchecked")
    @Inject(method = "getValue", at = @At("RETURN"), cancellable = true)
    private void kurdo$override(CallbackInfoReturnable<T> cir) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc == null || mc.options == null) return;
        Object self = this;
        if (self == mc.options.getGamma() && ModuleManager.fullbright.enabled) {
            cir.setReturnValue((T) Double.valueOf(15.0));
        } else if (self == mc.options.getFov() && ModuleManager.zoomActive) {
            cir.setReturnValue((T) Integer.valueOf(20));
        }
    }
}
