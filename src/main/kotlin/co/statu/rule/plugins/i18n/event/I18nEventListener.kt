package co.statu.rule.plugins.i18n.event

import co.statu.parsek.api.PluginEvent
import co.statu.rule.plugins.i18n.I18nSystem

interface I18nEventListener : PluginEvent {
    fun onReady(i18nSystem: I18nSystem)
}