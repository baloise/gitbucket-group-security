package ch.baloise.gitbucket.secext

import gitbucket.core.controller.ControllerBase
import gitbucket.core.service.{CommitStatusService, _}
import gitbucket.core.util.{OwnerAuthenticator, ReferrerAuthenticator, UsersAuthenticator}
import gitbucket.core.util.Implicits._
import ch.baloise.gitbucket.secext.html
import io.github.gitbucket.scalatra.forms._
import org.scalatra.i18n.Messages


class SecurityExtensionController extends SecurityExtensionControllerBase
  with RepositoryService with ReferrerAuthenticator with AccountService with WebHookService with ProtectedBranchService with CommitStatusService
  with OwnerAuthenticator with UsersAuthenticator

trait SecurityExtensionControllerBase extends ControllerBase {
  self: RepositoryService with ReferrerAuthenticator with AccountService with WebHookService with ProtectedBranchService with CommitStatusService
    with OwnerAuthenticator with UsersAuthenticator =>

  // for collaborator addition
  case class CollaboratorForm(userName: String)

  val collaboratorForm = mapping(
    "userName" -> trim(label("Username", text(required, collaborator)))
  )(CollaboratorForm.apply)

  get("/:owner/:repository/secextension")(referrersOnly { repository => {

    val ow = repository.owner
    val acc = getAccountByUserName(repository.owner)
    val loginUserName = context.loginAccount.get.userName
    var isManager = repository.managers.contains(loginUserName)


    getAccountByUserName(loginUserName, false).map { account =>
        if (account.isAdmin) {
          isManager = true
        }
      };

      html.collaborators(
        getCollaborators(repository.owner, repository.name),
        !isManager,
        repository)
  }})

  get("/:owner/:repository/secextension/collaborators/remove")(ownerOnly { repository => //    if(!getAccountByUserName(repository.owner).get.isGroupAccount){
    removeCollaborator(repository.owner, repository.name, params("name"))
    redirect(s"/${repository.owner}/${repository.name}/secextension")
  })

  post("/:owner/:repository/secextension/collaborators/add", collaboratorForm)(ownerOnly { (form, repository) =>
    addCollaborator(repository.owner, repository.name, form.userName)
    redirect(s"/${repository.owner}/${repository.name}/secextension")
  })

  private def collaborator: Constraint = new Constraint() {
    override def validate(name: String, value: String, messages: Messages): Option[String] =
      getAccountByUserName(value) match {
        case None => Some("User does not exist.")
        case Some(x) if (x.isGroupAccount)
        => Some("User does not exist.")
        case Some(x) if (x.userName == params("owner") || getCollaborators(params("owner"), params("repository")).contains(x.userName))
        => Some("User can access this repository already.")
        case _ => None
      }
  }


}
