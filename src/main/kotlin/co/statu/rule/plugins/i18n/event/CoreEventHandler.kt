package co.statu.rule.plugins.i18n.event

import co.statu.parsek.PluginEventManager
import co.statu.parsek.api.annotation.EventListener
import co.statu.parsek.api.config.PluginConfigManager
import co.statu.parsek.api.event.CoreEventListener
import co.statu.parsek.config.ConfigManager
import co.statu.rule.auth.AuthConfig
import co.statu.rule.auth.AuthFieldManager
import co.statu.rule.plugins.i18n.I18nConfig
import co.statu.rule.plugins.i18n.I18nPlugin
import co.statu.rule.plugins.i18n.I18nSystem
import io.vertx.core.Vertx
import org.slf4j.Logger

@EventListener
class CoreEventHandler(private val i18nPlugin: I18nPlugin, private val vertx: Vertx, private val logger: Logger) :
    CoreEventListener {

    override suspend fun onConfigManagerReady(configManager: ConfigManager) {
        val pluginConfigManager = PluginConfigManager(
            i18nPlugin,
            I18nConfig::class.java
        )

        i18nPlugin.pluginBeanContext.beanFactory.registerSingleton(
            pluginConfigManager.javaClass.name,
            pluginConfigManager
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

        val config = pluginConfigManager.config

        if (!config.hookAuthPlugin) {
            return
        }

        val authFieldManager = i18nPlugin.pluginBeanContext.getBean(AuthFieldManager::class.java)

        authFieldManager.addRegisterField(
            AuthConfig.Companion.RegisterField(
                field = "lang",
                isBlankCheck = true,
                optional = false,
                min = 0,
                max = null,
                regex = null,
                unique = false,
                upperCaseFirstChar = false,
                hiddenToUI = false,
                type = AuthConfig.Companion.RegisterField.Companion.Type.STRING,
                onlyRegister = false
            )
        )

        logger.info("Hooked into parsek-plugin-auth plugin")
    }
}