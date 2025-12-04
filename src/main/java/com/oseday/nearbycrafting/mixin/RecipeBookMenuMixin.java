package com.oseday.nearbycrafting.mixin;

import com.oseday.nearbycrafting.NearbyCraftingHelper;
import com.oseday.nearbycrafting.mixin.accessor.CraftingMenuAccessor;
import com.oseday.nearbycrafting.mixin.accessor.InventoryMenuAccessor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.RecipeBookMenu;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RecipeBookMenu.class)
public abstract class RecipeBookMenuMixin {

    /**
     * Inject at the HEAD of RecipeBookMenu.handlePlacement.
     *
     * For both CraftingMenu (3x3) and InventoryMenu (2x2), this is what the recipe book calls
     * when you click a recipe. We pull missing materials from nearby containers into the
     * player's inventory before vanilla autofill runs.
     */
    @Inject(
            method = "handlePlacement(ZLnet/minecraft/world/item/crafting/RecipeHolder;Lnet/minecraft/server/level/ServerPlayer;)V",
            at = @At("HEAD")
    )
    private void nearbycrafting$beforeHandlePlacement(
            boolean placeAll,
            RecipeHolder<?> recipeHolder,
            ServerPlayer player,
            CallbackInfo ci
    ) {
        if (player == null) return;
        if (!(player.level() instanceof ServerLevel serverLevel)) return;
        if (serverLevel.isClientSide()) return;

                CraftingContainer bridge = nearbycrafting$getCraftingGrid();
                if (bridge != null && !bridge.isEmpty()) {
                        NearbyCraftingHelper.returnCraftingGridToNearbyContainers(serverLevel, player, bridge);
        }

        Inventory inv = player.getInventory();

        NearbyCraftingHelper.topUpInventoryFromNearbyContainers(
                serverLevel,
                player,
                recipeHolder,
                inv
        );
    }

        private CraftingContainer nearbycrafting$getCraftingGrid() {
                Object self = this;
                if (self instanceof CraftingMenuAccessor craftingMenuAccessor) {
                        return craftingMenuAccessor.nearbycrafting$getCraftSlots();
                }

                if (self instanceof InventoryMenuAccessor inventoryMenuAccessor) {
                        return inventoryMenuAccessor.nearbycrafting$getCraftSlots();
                }

                return null;
        }
}
