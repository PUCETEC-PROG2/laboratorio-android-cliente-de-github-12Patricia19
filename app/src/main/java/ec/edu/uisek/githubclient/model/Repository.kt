package ec.edu.uisek.githubclient.model

data class Repository(
    val name: String,
    val description: String,
    val language: String,
    val stars: Int,
    val isPublic: Boolean,
    val owner: String = "",
    val avatarUrl: String = ""
) {
    companion object {
        fun fromGitHubRepository(githubRepo: GitHubRepository): Repository {
            return Repository(
                name = githubRepo.name,
                description = githubRepo.description ?: "Sin descripción",
                language = githubRepo.language ?: "Unknown",
                stars = githubRepo.stars,
                isPublic = !githubRepo.isPrivate,
                owner = githubRepo.owner.login,
                avatarUrl = githubRepo.owner.avatarUrl
            )
        }
    }
}