package co.statu.rule.plugins.i18n

import co.statu.parsek.api.config.PluginConfig

data class I18nConfig(
    val defaultLocale: String = "EN",
    val localeDir: String = "locales"
) : PluginConfig()