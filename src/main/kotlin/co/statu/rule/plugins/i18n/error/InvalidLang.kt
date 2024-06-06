package co.statu.rule.plugins.i18n.error

import co.statu.parsek.model.Error

class InvalidLang(
    statusMessage: String = "",
    extras: Map<String, Any> = mapOf()
) : Error(422, statusMessage, extras)