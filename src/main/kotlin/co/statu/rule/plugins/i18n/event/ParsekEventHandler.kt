package co.statu.rule.plugins.i18n.event

import co.statu.parsek.PluginEventManager
import co.statu.parsek.api.annotation.EventListener
import co.statu.parsek.api.config.PluginConfigManager
import co.statu.parsek.api.event.CoreEventListener
import co.statu.parsek.config.ConfigManager
import co.statu.rule.plugins.i18n.I18nConfig
import co.statu.rule.plugins.i18n.I18nPlugin
import co.statu.rule.plugins.i18n.I18nSystem
import io.vertx.core.Vertx
import org.slf4j.Logger

@EventListener
class ParsekEventHandler(private val i18nPlugin: I18nPlugin, private val vertx: Vertx, private val logger: Logger) :
    CoreEventListener {
    override suspend fun onConfigManagerReady(configManager: ConfigManager) {
        val pluginConfigManager = PluginConfigManager(
            configManager,
            i18nPlugin,
            I18nConfig::class.java,
            listOf(),
            listOf("i18n")
        )

        logger.info("Initialized plugin config")

        val i18nSystem = I18nSystem(
            vertx,
            pluginConfigManager,
            logger
        )

        i18nPlugin.registerSingletonGlobal(i18nSystem)

        val i18nEventHandlers = PluginEventManager.getEventListeners<I18nEventListener>()

        i18nEventHandlers.forEach { it.onReady(i18nSystem) }
    }
}