package ec.edu.uisek.githubclient.model

import com.google.gson.annotations.SerializedName

data class UpdateRepositoryRequest(
    @SerializedName("description")
    val description: String?
)
