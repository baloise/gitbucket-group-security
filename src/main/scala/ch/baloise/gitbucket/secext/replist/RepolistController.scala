package ch.baloise.gitbucket.secext.replist

import gitbucket.core.controller.ControllerBase
import gitbucket.core.service._
import gitbucket.core.util.Implicits._
import ch.baloise.gitbucket.repolist.html
import gitbucket.core.model.Profile._
import gitbucket.core.model.Session
import gitbucket.core.service.RepositoryService.RepositoryInfo
import profile.simple._
import gitbucket.core.util.{JGitUtil, OwnerAuthenticator, ReferrerAuthenticator, UsersAuthenticator}


class RepolistController extends RepolistControllerBase
  with RepositoryService with ReferrerAuthenticator with AccountService with WebHookService with ProtectedBranchService with CommitStatusService
  with OwnerAuthenticator with UsersAuthenticator



  trait RepolistControllerBase extends ControllerBase {
    self: RepositoryService with ReferrerAuthenticator with AccountService with WebHookService with ProtectedBranchService with CommitStatusService
      with OwnerAuthenticator with UsersAuthenticator =>


    def getMyPublicRepos(userName: String): List[RepositoryInfo] = {
      Repositories.filter { t1 =>
        (t1.isPrivate === false.bind) ||
        (t1.userName === userName.bind) ||
          (Collaborators.filter { t2 => t2.byRepository(t1.userName, t1.repositoryName) && (t2.collaboratorName === userName.bind)} exists)
      }.sortBy(r => (r.userName, r.repositoryName)).list.map{ repository =>
        new RepositoryInfo(

            new JGitUtil.RepositoryInfo(repository.userName, repository.repositoryName)
          ,
          repository,
          0,
          null)
      }
    }

    get("/repolist") {
      val userName  = context.loginAccount.get.userName
      val allRepos = getMyPublicRepos(userName)
      val visibleRepos = getVisibleRepositories(context.loginAccount, withoutPhysicalInfo = true)
      val userRepos = getUserRepositories(userName, withoutPhysicalInfo = true)
      html.repolist(allRepos, visibleRepos, userRepos)
    }
}
