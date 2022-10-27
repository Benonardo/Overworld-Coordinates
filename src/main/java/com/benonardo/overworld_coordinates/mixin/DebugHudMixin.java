package com.benonardo.overworld_coordinates.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.DebugHud;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Locale;

@Mixin(DebugHud.class)
public class DebugHudMixin {

    @Shadow @Final private MinecraftClient client;

    @Unique
    private List<String> overworld_coordinates$textList;

    @ModifyVariable(method = "getLeftText", at = @At("STORE"), ordinal = 0)
    private List<String> grabTextList(List<String> list) {
        overworld_coordinates$textList = list;
        return list;
    }

    @Inject(method = "getLeftText", at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z", ordinal = 0, shift = At.Shift.AFTER), slice = @Slice(from = @At(value = "CONSTANT", args = "stringValue=XYZ: %.3f / %.5f / %.3f")))
    private void appendOverworldCoordinates(CallbackInfoReturnable<List<String>> cir) {
        var coordinateScale = client.world.getDimension().coordinateScale();
        overworld_coordinates$textList.add(String.format(Locale.ROOT, "Overworld: %.3f / %.3f", this.client.getCameraEntity().getX() * coordinateScale, this.client.getCameraEntity().getZ() * coordinateScale));
    }

}
