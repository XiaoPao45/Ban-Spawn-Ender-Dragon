package io.ban_spawn_ender_dragon;

import io.ban_spawn_ender_dragon.config.ModConfig;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Arrays;
import java.util.List;

public class EnderDragonRespawnHandler {

    private static final List<BlockPos> CRYSTAL_KEY_POSITIONS = Arrays.asList(
            new BlockPos(-3, 64, 0),
            new BlockPos(0, 64, -3),
            new BlockPos(3, 64, 0),
            new BlockPos(0, 64, 3)
    );

    private static final double DETECTION_RANGE = 1.5;

    public static void register() {
        UseBlockCallback.EVENT.register(EnderDragonRespawnHandler::handleBlockUse);
    }

    private static ActionResult handleBlockUse(net.minecraft.entity.player.PlayerEntity player,
                                               World world,
                                               net.minecraft.util.Hand hand,
                                               net.minecraft.util.hit.BlockHitResult hitResult) {

        ModConfig config = ModConfig.getInstance();

        if (!shouldProcessEvent(config, world, player, hand)) {
            return ActionResult.PASS;
        }

        BlockPos placementPosition = calculateCrystalPosition(hitResult);
        if (!isCriticalCrystalPosition(placementPosition)) {
            return ActionResult.PASS;
        }

        return preventCrystalPlacement(config, player);
    }

    private static boolean shouldProcessEvent(ModConfig config, World world,
                                              net.minecraft.entity.player.PlayerEntity player,
                                              net.minecraft.util.Hand hand) {

        if (!config.enable_dragon_respawn_block) {
            return false;
        }

        if (world.isClient) {
            return false;
        }

        if (world.getRegistryKey() != World.END) {
            return false;
        }

        ItemStack handItem = player.getStackInHand(hand);
        if (handItem.getItem() != Items.END_CRYSTAL) {
            return false;
        }

        return true;
    }

    private static BlockPos calculateCrystalPosition(net.minecraft.util.hit.BlockHitResult hitResult) {
        return hitResult.getBlockPos().up();
    }

    private static boolean isCriticalCrystalPosition(BlockPos position) {
        for (BlockPos keyPosition : CRYSTAL_KEY_POSITIONS) {
            if (isPositionWithinRange(position, keyPosition, DETECTION_RANGE)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isPositionWithinRange(BlockPos position1, BlockPos position2, double range) {
        return position1.isWithinDistance(position2, range);
    }

    private static ActionResult preventCrystalPlacement(ModConfig config,
                                                        net.minecraft.entity.player.PlayerEntity player) {

        if (config.show_deny_message) {
            sendDenyMessage(player, config.getDenyMessage());
        }

        return ActionResult.FAIL;
    }

    private static void sendDenyMessage(net.minecraft.entity.player.PlayerEntity player, String message) {
        player.sendMessage(Text.literal(message), false);
    }
}