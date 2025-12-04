package com.oseday.nearbycrafting.mixin.accessor;

import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.InventoryMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(InventoryMenu.class)
public interface InventoryMenuAccessor {
    @Accessor("craftSlots")
    CraftingContainer nearbycrafting$getCraftSlots();
}
