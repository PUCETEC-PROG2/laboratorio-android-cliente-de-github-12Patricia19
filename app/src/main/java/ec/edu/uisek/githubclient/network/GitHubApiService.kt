package ec.edu.uisek.githubclient.network

import ec.edu.uisek.githubclient.model.CreateRepositoryRequest
import ec.edu.uisek.githubclient.model.GitHubRepository
import retrofit2.Response
import retrofit2.http.*

interface GitHubApiService {
    
    @GET("users/{username}/repos")
    suspend fun getUserRepositories(
        @Path("username") username: String,
        @Query("per_page") perPage: Int = 30,
        @Query("page") page: Int = 1,
        @Query("sort") sort: String = "updated"
    ): Response<List<GitHubRepository>>
    
    @POST("user/repos")
    suspend fun createRepository(
        @Header("Authorization") token: String,
        @Body request: CreateRepositoryRequest
    ): Response<GitHubRepository>
}
