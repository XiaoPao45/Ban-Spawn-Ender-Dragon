package io.ban_spawn_ender_dragon.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import io.ban_spawn_ender_dragon.config.ModConfig;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class ModCommands {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("bsed")
                .requires(source -> source.hasPermissionLevel(2)) // 需要OP权限
                .executes(ModCommands::reloadConfig)
                .then(CommandManager.literal("reload")
                        .executes(ModCommands::reloadConfig))
                .then(CommandManager.literal("language")
                        .then(CommandManager.argument("lang", StringArgumentType.word())
                                .suggests((context, builder) -> {
                                    builder.suggest("zh_cn");
                                    builder.suggest("en_us");
                                    return builder.buildFuture();
                                })
                                .executes(ModCommands::setLanguage)))
        );
    }

    private static int reloadConfig(CommandContext<ServerCommandSource> context) {
        ModConfig config = ModConfig.getInstance();
        config.reload();

        ServerCommandSource source = context.getSource();
        source.sendFeedback(() -> Text.literal("§aBan Spawn Ender Dragon 配置已重载！").formatted(Formatting.GREEN), true);

        source.sendFeedback(() -> Text.literal("§7- 阻止末影龙复活: " +
                (config.enable_dragon_respawn_block ? "§a启用" : "§c禁用")), true);
        source.sendFeedback(() -> Text.literal("§7- 显示阻止消息: " +
                (config.show_deny_message ? "§a启用" : "§c禁用")), true);
        source.sendFeedback(() -> Text.literal("§7- 语言: " +
                ("zh_cn".equals(config.language) ? "§a简体中文" : "§bEnglish")), true);

        return 1;
    }

    private static int setLanguage(CommandContext<ServerCommandSource> context) {
        String lang = StringArgumentType.getString(context, "lang");
        ModConfig config = ModConfig.getInstance();

        if ("zh_cn".equals(lang) || "en_us".equals(lang)) {
            config.setLanguage(lang);

            ServerCommandSource source = context.getSource();
            if ("zh_cn".equals(lang)) {
                source.sendFeedback(() -> Text.literal("§a语言已切换为简体中文").formatted(Formatting.GREEN), true);
                source.sendFeedback(() -> Text.literal("§7提示消息: §c禁止在此放置末影水晶！末影龙复活已被阻止。"), true);
            } else {
                source.sendFeedback(() -> Text.literal("§aLanguage switched to English").formatted(Formatting.GREEN), true);
                source.sendFeedback(() -> Text.literal("§7Message: §cYou cannot place end crystals here! Ender dragon respawning is disabled."), true);
            }

            return 1;
        } else {
            ServerCommandSource source = context.getSource();
            source.sendFeedback(() -> Text.literal("§c无效的语言代码！可用选项: zh_cn, en_us").formatted(Formatting.RED), true);
            return 0;
        }
    }
}