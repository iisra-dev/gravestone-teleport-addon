package dev.iisradev.gravestoneteleportaddon;

import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

@Mod(GravestoneTeleportAddon.MODID)
public class GravestoneTeleportAddon {

    public static final String MODID = "gravestone_teleport_addon";

    private static final DeferredRegister<DataComponentType<?>> DATA_COMPONENT_TYPES = DeferredRegister.create(BuiltInRegistries.DATA_COMPONENT_TYPE, MODID);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> CHARGED = DATA_COMPONENT_TYPES.register("charged",
            () -> DataComponentType.<Boolean>builder().persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL).build());

    private static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(BuiltInRegistries.RECIPE_SERIALIZER, MODID);
    public static final DeferredHolder<RecipeSerializer<?>, CustomRecipe.Serializer<ChargeObituaryRecipe>> CHARGE_OBITUARY_SERIALIZER = RECIPE_SERIALIZERS.register("charge_obituary",
            () -> new CustomRecipe.Serializer<>(ChargeObituaryRecipe::new));

    public GravestoneTeleportAddon(IEventBus modEventBus) {
        DATA_COMPONENT_TYPES.register(modEventBus);
        RECIPE_SERIALIZERS.register(modEventBus);

        NeoForge.EVENT_BUS.register(new ObituaryTeleportHandler());
    }
}
