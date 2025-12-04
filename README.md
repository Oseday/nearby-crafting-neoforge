## Nearby Crafting (NeoForge)

Nearby Crafting is a lightweight NeoForge mod that lets the vanilla recipe book grab ingredients from chests, barrels, and other containers around you before it autofills a crafting grid. When you click a recipe in either the 2×2 or 3×3 recipe book, the mod scans the nearby area, tops up your inventory with what you are missing, and then lets vanilla crafting proceed as usual.

### What it does
- Hooks into `RecipeBookMenu.handlePlacement` on the server, so it works for both crafting table and inventory grids.
- Collects only the exact items needed for a single craft.
- Respects full player inventories; any leftovers are returned to the original container.
- Uses the same stacking logic as vanilla, so enchanted or component-tagged stacks remain intact.

### Tips
- Pair with storage mods or organized chest walls to craft without manual item shuffling.
- Keep essential ingredients within the configured radius; blocks outside the cube are ignored.
- Multiplayer-friendly: only the server needs the mod for the feature to work for all connected players.
