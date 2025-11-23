package ec.edu.uisek.githubclient.model

import com.google.gson.annotations.SerializedName

data class CreateRepositoryRequest(
    @SerializedName("name")
    val name: String,
    
    @SerializedName("description")
    val description: String?,
    
    @SerializedName("private")
    val isPrivate: Boolean = false,
    
    @SerializedName("auto_init")
    val autoInit: Boolean = true
)
