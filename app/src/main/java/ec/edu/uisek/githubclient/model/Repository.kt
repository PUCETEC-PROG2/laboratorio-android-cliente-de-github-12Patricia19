package ec.edu.uisek.githubclient.model

data class Repository(
    val name: String,
    val description: String,
    val language: String,
    val stars: Int,
    val isPublic: Boolean
)