package dev.iisradev.gravestoneteleportaddon;

import de.maxhenkel.gravestone.items.ObituaryItem;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

/**
 * Charges a GraveStone obituary with an Ender Pearl. Only ever reads/copies the obituary
 * ItemStack via the public NeoForge item/component API - never touches GraveStone's classes
 * beyond the {@link ObituaryItem} type check needed to identify the item.
 */
public class ChargeObituaryRecipe extends CustomRecipe {

    public ChargeObituaryRecipe(CraftingBookCategory category) {
        super(category);
    }

    @Override
    public boolean matches(CraftingInput input, Level level) {
        boolean foundObituary = false;
        boolean foundPearl = false;

        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            if (stack.isEmpty()) {
                continue;
            }
            if (stack.getItem() instanceof ObituaryItem) {
                if (foundObituary || stack.getOrDefault(GravestoneTeleportAddon.CHARGED.get(), false)) {
                    return false;
                }
                foundObituary = true;
            } else if (stack.is(Items.ENDER_PEARL)) {
                if (foundPearl) {
                    return false;
                }
                foundPearl = true;
            } else {
                return false;
            }
        }

        return foundObituary && foundPearl;
    }

    @Override
    public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            if (!stack.isEmpty() && stack.getItem() instanceof ObituaryItem) {
                ItemStack result = stack.copy();
                result.set(GravestoneTeleportAddon.CHARGED.get(), true);
                result.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true);
                return result;
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public RecipeSerializer<ChargeObituaryRecipe> getSerializer() {
        return GravestoneTeleportAddon.CHARGE_OBITUARY_SERIALIZER.get();
    }
}
