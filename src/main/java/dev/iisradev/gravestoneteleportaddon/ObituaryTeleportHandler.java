package dev.iisradev.gravestoneteleportaddon;

import de.maxhenkel.corelib.death.Death;
import de.maxhenkel.corelib.death.DeathManager;
import de.maxhenkel.gravestone.DeathInfo;
import de.maxhenkel.gravestone.items.ObituaryItem;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

import javax.annotation.Nullable;
import java.util.Set;

/**
 * Reads GraveStone's own public {@code gravestone:death} data component off the obituary
 * ItemStack and independently re-reads the matching death record from disk via corelib's
 * {@link DeathManager} - the same file GraveStone itself wrote. No GraveStone class is ever
 * modified; the original obituary GUI keeps working exactly as it does without this addon.
 */
public class ObituaryTeleportHandler {

    // Looked up by GraveStone's stable registered id instead of importing its internal
    // Main/GravestoneMod class, which has already been renamed once across their versions.
    private static final Identifier DEATH_COMPONENT_ID = Identifier.fromNamespaceAndPath("gravestone", "death");

    @Nullable
    @SuppressWarnings("unchecked")
    private static DeathInfo getDeathInfo(ItemStack stack) {
        DataComponentType<?> deathComponent = BuiltInRegistries.DATA_COMPONENT_TYPE.getValue(DEATH_COMPONENT_ID);
        if (deathComponent == null) {
            return null;
        }
        return (DeathInfo) stack.get(deathComponent);
    }

    @SubscribeEvent
    public void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
        ItemStack stack = event.getItemStack();
        if (!(stack.getItem() instanceof ObituaryItem)) {
            return;
        }
        if (!stack.getOrDefault(GravestoneTeleportAddon.CHARGED.get(), false)) {
            return;
        }

        Player player = event.getEntity();
        if (player.isShiftKeyDown()) {
            // Let GraveStone's own use() handle its GUI / admin restore menu.
            return;
        }

        event.setCanceled(true);
        event.setCancellationResult(InteractionResult.SUCCESS);

        if (!(player instanceof ServerPlayer serverPlayer)) {
            return;
        }

        DeathInfo deathInfo = getDeathInfo(stack);
        if (deathInfo == null) {
            serverPlayer.sendOverlayMessage(Component.translatable("message.gravestone_teleport_addon.death_not_found"));
            return;
        }

        Death death = DeathManager.getDeath(serverPlayer.level(), deathInfo.getPlayerId(), deathInfo.getDeathId());
        if (death == null) {
            serverPlayer.sendOverlayMessage(Component.translatable("message.gravestone_teleport_addon.death_not_found"));
            return;
        }

        teleportToGrave(serverPlayer, stack, death);
    }

    private void teleportToGrave(ServerPlayer player, ItemStack stack, Death death) {
        ResourceKey<Level> dimension = ResourceKey.create(Registries.DIMENSION, Identifier.parse(death.getDimension()));
        ServerLevel destination = player.level().getServer().getLevel(dimension);
        if (destination == null) {
            player.sendOverlayMessage(Component.translatable("message.gravestone_teleport_addon.dimension_not_found"));
            return;
        }

        double x = death.getPosX();
        double y = death.getPosY() + 1D;
        double z = death.getPosZ();

        player.level().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.CHORUS_FRUIT_TELEPORT, SoundSource.PLAYERS, 1F, 1F);

        player.teleportTo(destination, x, y, z, Set.of(), player.getYRot(), player.getXRot(), false);

        destination.playSound(null, x, y, z, SoundEvents.CHORUS_FRUIT_TELEPORT, SoundSource.PLAYERS, 1F, 1F);
        destination.sendParticles(ParticleTypes.PORTAL, x + 0.5D, y + 1D, z + 0.5D, 32, 0.5D, 1D, 0.5D, 0D);

        stack.remove(GravestoneTeleportAddon.CHARGED.get());
        stack.remove(DataComponents.ENCHANTMENT_GLINT_OVERRIDE);

        player.swing(InteractionHand.MAIN_HAND);
    }

    @SubscribeEvent
    public void onItemTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        if (!(stack.getItem() instanceof ObituaryItem)) {
            return;
        }

        if (stack.getOrDefault(GravestoneTeleportAddon.CHARGED.get(), false)) {
            event.getToolTip().add(Component.translatable("item.gravestone_teleport_addon.charged").withStyle(ChatFormatting.LIGHT_PURPLE));
        } else {
            event.getToolTip().add(Component.translatable("item.gravestone_teleport_addon.uncharged").withStyle(ChatFormatting.GRAY));
        }
    }
}
