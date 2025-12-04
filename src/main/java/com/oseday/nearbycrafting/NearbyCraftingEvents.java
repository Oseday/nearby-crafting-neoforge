package com.oseday.nearbycrafting;

import com.oseday.nearbycrafting.bridge.RecipeBookMenuBridge;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.RecipeBookMenu;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

@EventBusSubscriber(modid = NearbyCrafting.MOD_ID)
public final class NearbyCraftingEvents {
    private NearbyCraftingEvents() {
    }

    @SubscribeEvent
    public static void onItemCrafted(PlayerEvent.ItemCraftedEvent event) {
        if (!Config.MOD_ENABLED.getAsBoolean()) {
            return;
        }

        if (!(event.getEntity() instanceof ServerPlayer serverPlayer)) {
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
