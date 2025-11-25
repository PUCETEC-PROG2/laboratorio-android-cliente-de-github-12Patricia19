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
    
    @GET("user/repos")
    suspend fun getAuthenticatedUserRepositories(
        @Header("Authorization") token: String,
        @Query("per_page") perPage: Int = 30,
        @Query("page") page: Int = 1,
        @Query("sort") sort: String = "updated",
        @Query("affiliation") affiliation: String = "owner"
    ): Response<List<GitHubRepository>>
    
    @POST("user/repos")
    suspend fun createRepository(
        @Header("Authorization") token: String,
        @Body request: CreateRepositoryRequest
    ): Response<GitHubRepository>
    
    @PATCH("repos/{owner}/{repo}")
    suspend fun updateRepository(
        @Header("Authorization") token: String,
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Body request: CreateRepositoryRequest
    ): Response<GitHubRepository>
    
    @DELETE("repos/{owner}/{repo}")
    suspend fun deleteRepository(
        @Header("Authorization") token: String,
        @Path("owner") owner: String,
        @Path("repo") repo: String
    ): Response<Unit>
}
