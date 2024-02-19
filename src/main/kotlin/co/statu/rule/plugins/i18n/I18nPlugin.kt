package co.statu.rule.plugins.i18n

import co.statu.parsek.api.ParsekPlugin
import co.statu.parsek.api.PluginContext
import co.statu.parsek.api.config.PluginConfigManager
import co.statu.rule.plugins.i18n.event.ParsekEventHandler
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class I18nPlugin(pluginContext: PluginContext) : ParsekPlugin(pluginContext) {
    companion object {
        internal val logger: Logger = LoggerFactory.getLogger(I18nPlugin::class.java)

        internal lateinit var pluginConfigManager: PluginConfigManager<I18nConfig>

        internal lateinit var INSTANCE: I18nPlugin

        internal lateinit var i18nSystem: I18nSystem
    }

    init {
        INSTANCE = this

        logger.info("Initialized instance")

        context.pluginEventManager.register(this, ParsekEventHandler())

        logger.info("Registered event")
    }
}