package com.benonardo.overworld_coordinates.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import me.cominixo.betterf3.modules.BaseModule;
import me.cominixo.betterf3.modules.CoordsModule;
import me.cominixo.betterf3.utils.DebugLine;
import me.cominixo.betterf3.utils.Utils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.TextColor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Arrays;

@Pseudo
@Mixin(value = CoordsModule.class, remap = false)
public abstract class CoordsModuleMixin extends BaseModule {

    @Shadow public TextColor colorX;
    @Shadow public TextColor colorY;
    @Shadow public TextColor colorZ;

    @Inject(method = "<init>", at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z", ordinal = 0, shift = At.Shift.AFTER))
    private void registerOverworldCoordinates(CallbackInfo ci) {
        this.lines.add(new DebugLine("overworld_coords", "format.betterf3.coords", true));
    }

    @Inject(method = "update", at = @At(value = "INVOKE", target = "Lme/cominixo/betterf3/utils/DebugLine;value(Ljava/lang/Object;)V", ordinal = 0, shift = At.Shift.AFTER))
    private void appendOverworldCoordinates(MinecraftClient client, CallbackInfo ci, @Local(ordinal = 1) String cameraY) {
        var coordinateScale = client.world.getDimension().coordinateScale();
        var overworldX = String.format("%.3f", client.getCameraEntity().getX() * coordinateScale);
        var overworldZ = String.format("%.3f", client.getCameraEntity().getZ() * coordinateScale);
        this.lines.get(1).value(Arrays.asList(Utils.styledText(overworldX, this.colorX), Utils.styledText(cameraY, this.colorY), Utils.styledText(overworldZ, this.colorZ)));
    }

    @ModifyArg(method = "update", at = @At(value = "INVOKE", target = "Ljava/util/List;get(I)Ljava/lang/Object;"), slice = @Slice(from = @At(value = "INVOKE", target = "Lme/cominixo/betterf3/utils/DebugLine;value(Ljava/lang/Object;)V", ordinal = 0)))
    private int shiftByOneForOverworldCoordinates(int i) {
        return i + 1;
    }

}
