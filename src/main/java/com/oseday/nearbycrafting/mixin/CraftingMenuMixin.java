package com.oseday.nearbycrafting.mixin;

import com.oseday.nearbycrafting.NearbyCraftingHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.CraftingMenu;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CraftingMenu.class)
public abstract class CraftingMenuMixin {
	
	@Shadow @Final private CraftingContainer craftSlots;
	
	@Inject(
		method = "removed(Lnet/minecraft/world/entity/player/Player;)V",
		at = @At("HEAD")
	)
	private void nearbycrafting$returnLeftovers(Player player, CallbackInfo ci) {
		if (!(player instanceof ServerPlayer serverPlayer)) {
			return;
		}
		
		if (!(player.level() instanceof ServerLevel serverLevel)) {
			return;
		}
		
		NearbyCraftingHelper.returnCraftingGridToNearbyContainers(serverLevel, serverPlayer, this.craftSlots);
	}
}
