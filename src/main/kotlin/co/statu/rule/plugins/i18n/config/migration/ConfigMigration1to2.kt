package co.statu.rule.plugins.i18n.config.migration

import co.statu.parsek.annotation.Migration
import co.statu.parsek.api.config.PluginConfigMigration
import io.vertx.core.json.JsonObject

@Migration
class ConfigMigration1to2 : PluginConfigMigration(1, 2, "Add hookAuthPlugin config") {
    override fun migrate(config: JsonObject) {
        config.put("hookAuthPlugin", true)
    }
}