package ec.edu.uisek.githubclient.data

import ec.edu.uisek.githubclient.model.Repository

object RepositoryData {
    fun getRepositories(): List<Repository> {
        return listOf(
            Repository(
                name = "android-client",
                description = "Cliente Android para acceso a APIs REST",
                language = "Kotlin",
                stars = 156,
                isPublic = true
            ),
            Repository(
                name = "web-scraper",
                description = "Herramienta de scraping para sitios web dinámicos",
                language = "Python",
                stars = 89,
                isPublic = true
            ),
            Repository(
                name = "mobile-ui-kit",
                description = "Kit de componentes UI para aplicaciones móviles",
                language = "Java",
                stars = 234,
                isPublic = false
            ),
            Repository(
                name = "data-analyzer",
                description = "Análisis de datos y visualizaciones interactivas",
                language = "JavaScript",
                stars = 67,
                isPublic = true
            ),
            Repository(
                name = "ml-algorithms",
                description = "Implementación de algoritmos de machine learning",
                language = "Python",
                stars = 445,
                isPublic = true
            ),
            Repository(
                name = "react-components",
                description = "Biblioteca de componentes reutilizables para React",
                language = "TypeScript",
                stars = 178,
                isPublic = false
            ),
            Repository(
                name = "database-migrator",
                description = "Herramienta para migraciones de base de datos",
                language = "Go",
                stars = 92,
                isPublic = true
            ),
            Repository(
                name = "api-gateway",
                description = "Gateway de APIs con autenticación y rate limiting",
                language = "Node.js",
                stars = 301,
                isPublic = true
            )
        )
    }
}