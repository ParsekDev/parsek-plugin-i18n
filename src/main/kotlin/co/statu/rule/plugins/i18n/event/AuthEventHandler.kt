package co.statu.rule.plugins.i18n.event

import co.statu.parsek.api.annotation.EventListener
import co.statu.parsek.api.config.PluginConfigManager
import co.statu.rule.auth.AuthConfig
import co.statu.rule.auth.AuthFieldManager
import co.statu.rule.auth.event.AuthEventListener
import co.statu.rule.plugins.i18n.I18nConfig
import co.statu.rule.plugins.i18n.I18nPlugin
import co.statu.rule.plugins.i18n.I18nSystem
import co.statu.rule.plugins.i18n.error.InvalidLang

@EventListener
class AuthEventHandler(
    private val i18nPlugin: I18nPlugin
) : AuthEventListener {
    private val i18nSystem by lazy {
        i18nPlugin.pluginBeanContext.getBean(I18nSystem::class.java)
    }

    private val pluginConfigManager by lazy {
        i18nPlugin.pluginBeanContext.getBean(PluginConfigManager::class.java) as PluginConfigManager<I18nConfig>
    }

    private val config by lazy {
        pluginConfigManager.config
    }

    override suspend fun onAuthFieldsManagerReady(authFieldManager: AuthFieldManager) {
        if (!config.hookAuthPlugin) {
            return
        }

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
    }

    override suspend fun onValidatingRegisterField(
        field: Any?,
        registerField: AuthConfig.Companion.RegisterField,
        authFieldManager: AuthFieldManager
    ) {
        if (!config.hookAuthPlugin) {
            return
        }

        if (registerField.field == "lang") {
            if (i18nSystem.getSupportedLocales().none { it == field }) {
                throw InvalidLang()
            }
        }
    }
}