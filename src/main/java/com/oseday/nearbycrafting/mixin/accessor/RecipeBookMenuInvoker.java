package com.oseday.nearbycrafting.mixin.accessor;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.RecipeBookMenu;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(RecipeBookMenu.class)
public interface RecipeBookMenuInvoker {
    @Invoker("handlePlacement")
    void nearbycrafting$invokeHandlePlacement(boolean placeAll, RecipeHolder<?> recipeHolder, ServerPlayer player);
}
