package io.ban_spawn_ender_dragon;

import io.ban_spawn_ender_dragon.config.ModConfig;
import io.ban_spawn_ender_dragon.command.ModCommands;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BanEnderDragonMod implements ModInitializer {

    public static final String MOD_ID = "ban_spawn_ender_dragon";

    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        initializeComponents();

        logStartupInfo();
    }

    private void initializeComponents() {
        ModConfig config = ModConfig.getInstance();

        EnderDragonRespawnHandler.register();

        registerCommands();
    }

    private void registerCommands() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            ModCommands.register(dispatcher);
        });
    }

    private void logStartupInfo() {

        LOGGER.info("=== Ban Spawn Ender Dragon Mod ===");
        LOGGER.info("✓ 模组初始化完成 | Mod initialized successfully");
    }
}