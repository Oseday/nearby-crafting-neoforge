package com.oseday.nearbycrafting.bridge;

import net.minecraft.server.level.ServerPlayer;

public interface RecipeBookMenuBridge {
    void nearbycrafting$tryRepeatRecipe(ServerPlayer player);

    void nearbycrafting$clearRepeatRecipe();
}
