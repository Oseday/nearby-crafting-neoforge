package com.oseday.nearbycrafting;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class NearbyCraftingHelper {

    private NearbyCraftingHelper() {
    }

    /**
     * Pull missing ingredients for the given recipe from nearby containers into the
     * player's inventory, so vanilla RecipeBookMenu.handlePlacement can use them.
     *
     * This is intentionally conservative and only tries to satisfy one craft
     * worth of ingredients.
     */
    public static void topUpInventoryFromNearbyContainers(
            ServerLevel level,
            Player player,
            RecipeHolder<?> recipeHolder,
            Inventory playerInventory
    ) {
        if (level == null || player == null || recipeHolder == null || playerInventory == null) {
            return;
        }

        if (!Config.MOD_ENABLED.getAsBoolean()) {
            return;
        }

        Recipe<?> rawRecipe = recipeHolder.value();
        if (rawRecipe.getType() != RecipeType.CRAFTING || !(rawRecipe instanceof CraftingRecipe craftingRecipe)) {
            // Only handle normal crafting recipes
            return;
        }

        // Build a "needed" map: Item -> count for ONE craft
        Map<Item, Integer> needed = new HashMap<>();
        NonNullList<Ingredient> ingredients = craftingRecipe.getIngredients();
        for (Ingredient ingredient : ingredients) {
            if (ingredient.isEmpty()) continue;

            ItemStack[] matches = ingredient.getItems();
            if (matches.length == 0) continue;

            // Take the first match as representative
            Item item = matches[0].getItem();
            needed.merge(item, 1, Integer::sum);
        }

        if (needed.isEmpty()) {
            return;
        }

        // Subtract what the player already has in inventory
        subtractFromCounts(needed, playerInventory);
        if (needed.isEmpty()) {
            // Already have enough for one craft
            return;
        }

        BlockPos center = player.blockPosition();
        List<Container> nearbyContainers = findNearbyContainers(level, center, Config.CHEST_SEARCH_RANGE.getAsInt());
        if (nearbyContainers.isEmpty()) {
            return;
        }

        pullFromContainersIntoPlayer(nearbyContainers, needed, playerInventory);
    }

    private static void subtractFromCounts(Map<Item, Integer> needed, Inventory playerInventory) {
        for (int i = 0; i < playerInventory.getContainerSize(); i++) {
            ItemStack stack = playerInventory.getItem(i);
            if (stack.isEmpty()) continue;

            Item item = stack.getItem();
            if (!needed.containsKey(item)) continue;

            int left = needed.get(item) - stack.getCount();
            if (left <= 0) {
                needed.remove(item);
            } else {
                needed.put(item, left);
            }
        }
    }

    /**
     * Scan a cube radius around center and collect all BlockEntities that implement Container.
     */
    public static List<Container> findNearbyContainers(ServerLevel level, BlockPos center, int radius) {
        List<Container> result = new ArrayList<>();
        int r = Math.max(1, radius);

        for (int dx = -r; dx <= r; dx++) {
            for (int dy = -r; dy <= r; dy++) {
                for (int dz = -r; dz <= r; dz++) {
                    BlockPos pos = center.offset(dx, dy, dz);
                    if (!level.isLoaded(pos)) continue;

                    BlockEntity be = level.getBlockEntity(pos);
                    if (be instanceof Container container) {
                        result.add(container);
                    }
                }
            }
        }

        return result;
    }

    /**
     * Pull items from the given containers into the player's inventory until "needed" is satisfied or containers are empty.
     */
    private static void pullFromContainersIntoPlayer(
            List<Container> containers,
            Map<Item, Integer> needed,
            Inventory playerInventory
    ) {
        if (needed.isEmpty()) return;

        for (Container container : containers) {
            for (int slot = 0; slot < container.getContainerSize(); slot++) {
                ItemStack stackInContainer = container.getItem(slot);
                if (stackInContainer.isEmpty()) continue;

                Item item = stackInContainer.getItem();
                Integer need = needed.get(item);
                if (need == null || need <= 0) continue;

                int toMove = Math.min(need, stackInContainer.getCount());
                if (toMove <= 0) continue;

                ItemStack extracted = stackInContainer.split(toMove);
                if (!extracted.isEmpty()) {
                    ItemStack remainder = addToPlayerInventory(playerInventory, extracted);

                    // If we couldn't insert everything, put remainder back in the same slot
                    if (!remainder.isEmpty()) {
                        ItemStack existing = container.getItem(slot);
                        if (existing.isEmpty()) {
                            container.setItem(slot, remainder);
                        } else if (ItemStack.isSameItemSameComponents(existing, remainder)) {
                            existing.grow(remainder.getCount());
                        }
                    }

                    int newNeed = need - (toMove - remainder.getCount());
                    if (newNeed <= 0) {
                        needed.remove(item);
                        if (needed.isEmpty()) {
                            container.setChanged();
                            return;
                        }
                    } else {
                        needed.put(item, newNeed);
                    }
                }

                container.setChanged();
            }
        }
    }

    /**
     * Vanilla-like insert: merge into existing stacks, then into empty slots.
     * Returns leftover if the inventory is full.
     */
    private static ItemStack addToPlayerInventory(Inventory inventory, ItemStack stack) {
        ItemStack toInsert = stack.copy();

        // Merge into existing stacks
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack existing = inventory.getItem(i);
            if (existing.isEmpty()) continue;
            if (!ItemStack.isSameItemSameComponents(existing, toInsert)) continue;

            int maxStackSize = Math.min(existing.getMaxStackSize(), inventory.getMaxStackSize());
            int space = maxStackSize - existing.getCount();
            if (space <= 0) continue;

            int move = Math.min(space, toInsert.getCount());
            existing.grow(move);
            toInsert.shrink(move);
            if (toInsert.isEmpty()) {
                return ItemStack.EMPTY;
            }
        }

        // Put in empty slots
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack existing = inventory.getItem(i);
            if (!existing.isEmpty()) continue;

            inventory.setItem(i, toInsert);
            return ItemStack.EMPTY;
        }

        return toInsert; // inventory full
    }
}
