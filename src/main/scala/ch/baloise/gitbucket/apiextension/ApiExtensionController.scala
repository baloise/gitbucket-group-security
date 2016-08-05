package ch.baloise.gitbucket.apiextension

import gitbucket.core.api.{ApiRepository, ApiUser, JsonFormat}
import gitbucket.core.controller.ControllerBase
import gitbucket.core.model.Profile._
import gitbucket.core.service.RepositoryService.RepositoryInfo
import gitbucket.core.service._
import gitbucket.core.util.Implicits._
import profile.simple._
import gitbucket.core.util.{JGitUtil, OwnerAuthenticator, ReferrerAuthenticator, UsersAuthenticator}

class ApiExtensionController extends ApiExtensionControllerBase
  with RepositoryService with ReferrerAuthenticator with AccountService with WebHookService with ProtectedBranchService with CommitStatusService
  with OwnerAuthenticator with UsersAuthenticator

  trait ApiExtensionControllerBase extends ControllerBase {
    self: RepositoryService with ReferrerAuthenticator with AccountService with WebHookService with ProtectedBranchService with CommitStatusService
      with OwnerAuthenticator with UsersAuthenticator =>

    def getMyPublicRepos(): List[ApiRepository] = {
      Repositories.filter { t1 =>
        (t1.isPrivate === false.bind)
      }.sortBy(r => (r.userName, r.repositoryName)).list.map{ repository =>
        ApiRepository(repository, ApiUser(getAccountByUserName(repository.userName).get))
      }
    }

    get("/api/v3/repositories") {
      val allRepos = getMyPublicRepos()
      JsonFormat(allRepos)
    }
  }

