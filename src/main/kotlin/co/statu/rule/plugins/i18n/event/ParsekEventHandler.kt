package co.statu.rule.plugins.i18n.event

import co.statu.parsek.api.config.PluginConfigManager
import co.statu.parsek.api.event.ParsekEventListener
import co.statu.parsek.config.ConfigManager
import co.statu.rule.plugins.i18n.I18nConfig
import co.statu.rule.plugins.i18n.I18nPlugin
import co.statu.rule.plugins.i18n.I18nPlugin.Companion.logger
import co.statu.rule.plugins.i18n.I18nSystem

class ParsekEventHandler : ParsekEventListener {
    override suspend fun onConfigManagerReady(configManager: ConfigManager) {
        I18nPlugin.pluginConfigManager = PluginConfigManager(
            configManager,
            I18nPlugin.INSTANCE,
            I18nConfig::class.java,
            logger,
            listOf(),
            listOf("i18n")
        )

        logger.info("Initialized plugin config")

        val context = I18nPlugin.INSTANCE.context

        I18nPlugin.i18nSystem = I18nSystem.create(
            context.vertx,
            I18nPlugin.pluginConfigManager,
            context.pluginEventManager,
            logger
        )
    }
}