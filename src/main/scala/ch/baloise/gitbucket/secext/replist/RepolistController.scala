package ch.baloise.gitbucket.secext.replist

import gitbucket.core.controller.ControllerBase
import gitbucket.core.service._
import gitbucket.core.util.Implicits._
import ch.baloise.gitbucket.repolist.html
import gitbucket.core.util.{OwnerAuthenticator, ReferrerAuthenticator, UsersAuthenticator}


class RepolistController extends RepolistControllerBase
  with RepositoryService with ReferrerAuthenticator with AccountService with WebHookService with ProtectedBranchService with CommitStatusService
  with OwnerAuthenticator with UsersAuthenticator



  trait RepolistControllerBase extends ControllerBase {
    self: RepositoryService with ReferrerAuthenticator with AccountService with WebHookService with ProtectedBranchService with CommitStatusService
      with OwnerAuthenticator with UsersAuthenticator =>



    get("/repolist") {
      val allRepos = getAllRepositories(null)

      html.repolist(allRepos)
    }
}
