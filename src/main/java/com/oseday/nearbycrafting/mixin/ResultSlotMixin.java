package com.oseday.nearbycrafting.mixin;

import com.oseday.nearbycrafting.Config;
import com.oseday.nearbycrafting.bridge.RecipeBookMenuBridge;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.RecipeBookMenu;
import net.minecraft.world.inventory.ResultSlot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ResultSlot.class)
public abstract class ResultSlotMixin {

    @Inject(method = "onTake", at = @At("TAIL"))
    private void nearbycrafting$repeatLastRecipe(Player player, ItemStack stack, CallbackInfo ci) {
        if (!Config.MOD_ENABLED.getAsBoolean()) {
            return;
        }

        if (!(player instanceof ServerPlayer serverPlayer)) {
            return;
        }

        if (!(serverPlayer.containerMenu instanceof RecipeBookMenu recipeMenu)) {
            return;
        }

        if (recipeMenu instanceof RecipeBookMenuBridge bridge) {
            bridge.nearbycrafting$tryRepeatRecipe(serverPlayer);
        }
    }
}
