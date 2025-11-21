package io.ban_spawn_ender_dragon.config;

import com.moandjiezana.toml.Toml;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ModConfig {

    private static final Path CONFIG_PATH = FabricLoader.getInstance()
            .getConfigDir()
            .resolve("ban_spawn_ender_dragon.toml");

    private static final Logger LOGGER = LoggerFactory.getLogger("BanSpawnEnderDragon/Config");

    public static final String DEFAULT_MESSAGE_ZH = "§c禁止在此放置末影水晶！末影龙复活已被阻止。";

    public static final String DEFAULT_MESSAGE_EN = "§cYou cannot place end crystals here! Ender dragon respawning is disabled.";

    public static final String DEFAULT_LANGUAGE = "en_us";

    public boolean enable_dragon_respawn_block = true;
    public String deny_message = DEFAULT_MESSAGE_ZH;
    public boolean show_deny_message = false;
    public String language = DEFAULT_LANGUAGE;

    private static ModConfig instance;

    public static ModConfig getInstance() {
        if (instance == null) {
            instance = new ModConfig();
        }
        return instance;
    }

    private ModConfig() {
        load();
    }

    public void load() {
        try {
            if (Files.exists(CONFIG_PATH)) {
                loadExistingConfig();
            } else {
                createDefaultConfig();
            }
        } catch (Exception e) {
            handleConfigError(e);
        }
    }

    public void reload() {
        LOGGER.info("重新加载配置文件 | Reloading configuration");
        load();
    }

    public void save() {
        try {
            LOGGER.info("保存配置文件 | Saving configuration: {}", CONFIG_PATH);
            createCommentedConfig();
            LOGGER.info("配置文件保存成功 | Configuration saved successfully");
        } catch (IOException e) {
            LOGGER.error("保存配置文件失败 | Failed to save configuration: {}", e.getMessage());
        }
    }

    public String getDenyMessage() {
        if (isCustomMessage()) {
            return deny_message;
        }
        return getDefaultMessage();
    }

    public void setLanguage(String lang) {
        this.language = lang;
        this.deny_message = getDefaultMessage();
        save();
    }

    private void loadExistingConfig() throws IOException {
        LOGGER.info("加载现有配置文件 | Loading existing config: {}", CONFIG_PATH);

        Toml toml = new Toml().read(CONFIG_PATH.toFile());

        this.enable_dragon_respawn_block = toml.getBoolean("enable_dragon_respawn_block", true);
        this.deny_message = toml.getString("deny_message", getDefaultMessage());
        this.show_deny_message = toml.getBoolean("show_deny_message", false);
        this.language = toml.getString("language", DEFAULT_LANGUAGE);

        LOGGER.info("配置文件加载成功 | Config loaded successfully");
    }

    private void createDefaultConfig() throws IOException {
        LOGGER.info("创建默认配置文件 | Creating default config: {}", CONFIG_PATH);
        createCommentedConfig();
        LOGGER.info("默认配置文件创建成功 | Default config created successfully");
    }

    private void handleConfigError(Exception e) {
        LOGGER.error("配置加载失败，使用默认值 | Config load failed, using defaults: {}", e.getMessage());
        try {
            createDefaultConfig();
        } catch (IOException ex) {
            LOGGER.error("创建默认配置失败 | Failed to create default config: {}", ex.getMessage());
        }
    }

    private void createCommentedConfig() throws IOException {
        String configContent = buildConfigContent();
        ensureConfigDirectory();
        Files.writeString(CONFIG_PATH, configContent);
    }

    private String buildConfigContent() {
        return """
            # 禁止复活末影龙模组配置文件
            # Ban Spawn Ender Dragon Configuration
            
            # 是否启用阻止末影龙复活功能
            # Enable/disable the ender dragon respawn blocking feature
            enable_dragon_respawn_block = %b
                        
            # 阻止放置时显示的消息（支持颜色代码 §）
            # Message displayed when placement is blocked (supports color codes §)
            deny_message = "%s"
                        
            # 是否在阻止放置时向玩家显示提示消息
            # Whether to show the deny message to players when placement is blocked
            show_deny_message = %b
                        
            # 语言设置
            # 可选值: zh_cn (简体中文), en_us (English)
            # Language setting
            # Available values: zh_cn (Simplified Chinese), en_us (English)
            language = "%s"
            """.formatted(
                enable_dragon_respawn_block,
                DEFAULT_MESSAGE_ZH, DEFAULT_MESSAGE_EN, deny_message,
                show_deny_message,
                language
        );
    }

    private void ensureConfigDirectory() throws IOException {
        Path configDir = CONFIG_PATH.getParent();
        if (!Files.exists(configDir)) {
            Files.createDirectories(configDir);
        }
    }

    private String getDefaultMessage() {
        return "en_us".equals(language) ? DEFAULT_MESSAGE_EN : DEFAULT_MESSAGE_ZH;
    }

    private boolean isCustomMessage() {
        return !deny_message.equals(DEFAULT_MESSAGE_ZH) &&
                !deny_message.equals(DEFAULT_MESSAGE_EN);
    }
}