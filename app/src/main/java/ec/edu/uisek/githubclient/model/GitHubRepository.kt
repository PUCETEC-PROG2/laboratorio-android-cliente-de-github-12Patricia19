package ec.edu.uisek.githubclient.model

import com.google.gson.annotations.SerializedName

data class GitHubRepository(
    @SerializedName("id")
    val id: Long,
    
    @SerializedName("name")
    val name: String,
    
    @SerializedName("description")
    val description: String?,
    
    @SerializedName("language")
    val language: String?,
    
    @SerializedName("stargazers_count")
    val stars: Int,
    
    @SerializedName("private")
    val isPrivate: Boolean,
    
    @SerializedName("owner")
    val owner: Owner,
    
    @SerializedName("html_url")
    val htmlUrl: String,
    
    @SerializedName("created_at")
    val createdAt: String,
    
    @SerializedName("updated_at")
    val updatedAt: String
)

data class Owner(
    @SerializedName("login")
    val login: String,
    
    @SerializedName("avatar_url")
    val avatarUrl: String,
    
    @SerializedName("html_url")
    val htmlUrl: String
)
