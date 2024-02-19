package co.statu.rule.plugins.i18n

import co.statu.parsek.PluginEventManager
import co.statu.parsek.api.config.PluginConfigManager
import co.statu.parsek.util.TextUtil.compileInline
import co.statu.rule.plugins.i18n.event.I18nEventListener
import com.github.jknack.handlebars.Template
import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import org.slf4j.Logger
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.collections.set
import kotlin.system.exitProcess

class I18nSystem private constructor(
    private val vertx: Vertx,
    private val pluginConfigManager: PluginConfigManager<I18nConfig>,
    private val logger: Logger
) {
    private val i18nConfig by lazy {
        pluginConfigManager.config
    }

    private val folder by lazy {
        File(i18nConfig.localeDir)
    }

    private val locales = mutableMapOf<String, Map<String, Template>>()

    companion object {
        internal fun create(
            vertx: Vertx,
            pluginConfigManager: PluginConfigManager<I18nConfig>,
            pluginEventManager: PluginEventManager,
            logger: Logger
        ): I18nSystem {
            val i18nSystem = I18nSystem(vertx, pluginConfigManager, logger)

            val i18nEventHandlers = pluginEventManager.getEventHandlers<I18nEventListener>()

            i18nEventHandlers.forEach { it.onReady(i18nSystem) }

            return i18nSystem
        }
    }

    init {
        checkFolder()

        loadLocales()

        logger.info("Supported languages: {}", getSupportedLocales())
    }

    private fun checkFolder() {
        logger.info("Checking locale Dir: ${folder.absolutePath}")

        if (folder.parentFile == null || !folder.parentFile.exists() || !folder.exists() || !folder.isDirectory) {
            logger.error("\"${folder.absolutePath}\" is an invalid i18n directory!")

            exitProcess(1)
        }

        if (folder.listFiles()!!.none { it.name == i18nConfig.defaultLocale + ".json" }) {
            logger.error("I18n dir missing default \"${i18nConfig.defaultLocale}\" locale!")

            exitProcess(1)
        }
    }

    private fun loadLocales() {
        logger.info("Loading locales...")

        folder.listFiles()!!.forEach { localeFile ->
            val localeFileAsString = String(Files.readAllBytes(Paths.get(localeFile.absolutePath)))

            val localeMap = JsonObject(localeFileAsString).map
            val compiledLocaleMap = localeMap.map { Pair(it.key, it.value.toString().compileInline()) }.toMap()

            locales[localeFile.nameWithoutExtension] = compiledLocaleMap
        }

        logger.info("Locales are loaded.")
    }

    fun getLocales() = locales.toMap()

    fun getLocale(locale: String) = locales[locale]

    fun getTranslation(locale: String, translation: String, variables: Map<String, Any> = mapOf()): String {
        val foundLocale = getLocale(locale)!!
        val foundTranslation = foundLocale[translation]!!

        if (variables.isEmpty()) {
            return foundTranslation.text()
        }

        return foundTranslation.apply(variables)
    }

    fun getSupportedLocales() = locales.keys
}