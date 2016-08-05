import javax.servlet.ServletContext

import ch.baloise.gitbucket.secext.SecurityExtensionController
import gitbucket.core.controller.Context
import gitbucket.core.plugin.{Link, PluginRegistry}
import gitbucket.core.service.RepositoryService.RepositoryInfo
import gitbucket.core.service.SystemSettingsService.SystemSettings
import io.github.gitbucket.solidbase.model.Version
/**
  * Created by carina on 06.07.16.
  */
class Plugin extends gitbucket.core.plugin.Plugin {
  override val pluginId: String = "secextension"
  override val pluginName: String = "Security Extension"
  override val versions: Seq[Version] = List(new Version("1.0.0"))
  override val description: String = "Ability to add committer by Org managers"

  override def initialize(registry: PluginRegistry, context: ServletContext, settings: SystemSettings): Unit = {
    super.initialize(registry, context, settings)

  }

  override val controllers = Seq(
    "/*" -> new SecurityExtensionController
  )

  override val repositoryMenus = Seq(
    (repositoryInfo: RepositoryInfo, context: Context) => Some(Link("secextension", "Collaboration", "/secextension", Some("circuit-board")))
  )

}
