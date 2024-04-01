package co.statu.rule.plugins.i18n.event

import co.statu.parsek.api.event.PluginEventListener
import co.statu.rule.plugins.i18n.I18nSystem

interface I18nEventListener : PluginEventListener {
    fun onReady(i18nSystem: I18nSystem)
}