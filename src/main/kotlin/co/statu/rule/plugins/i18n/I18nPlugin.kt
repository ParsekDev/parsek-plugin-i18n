package co.statu.rule.plugins.i18n

import co.statu.parsek.PluginEventManager
import co.statu.parsek.api.ParsekPlugin
import co.statu.parsek.api.config.PluginConfigManager
import co.statu.rule.auth.AuthConfig
import co.statu.rule.auth.AuthFieldManager
import co.statu.rule.plugins.i18n.event.I18nEventListener

class I18nPlugin : ParsekPlugin() {
    override suspend fun onStart() {
        val pluginConfigManager = PluginConfigManager(
            this,
            I18nConfig::class.java
        )

        pluginBeanContext.beanFactory.registerSingleton(
            pluginConfigManager.javaClass.name,
            pluginConfigManager
        )

        logger.info("Initialized plugin config")

        val i18nSystem = I18nSystem(
            vertx,
            pluginConfigManager,
            logger
        )

        registerSingletonGlobal(i18nSystem)

        val i18nEventHandlers = PluginEventManager.getEventListeners<I18nEventListener>()

        i18nEventHandlers.forEach { it.onReady(i18nSystem) }

        val config = pluginConfigManager.config

        if (!config.hookAuthPlugin) {
            return
        }

        val authFieldManager = pluginBeanContext.getBean(AuthFieldManager::class.java)

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