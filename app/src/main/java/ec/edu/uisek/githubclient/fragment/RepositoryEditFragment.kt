package ec.edu.uisek.githubclient.fragment

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import ec.edu.uisek.githubclient.MainActivity
import ec.edu.uisek.githubclient.R
import ec.edu.uisek.githubclient.model.Repository
import ec.edu.uisek.githubclient.model.UpdateRepositoryRequest
import ec.edu.uisek.githubclient.network.RetrofitClient
import kotlinx.coroutines.launch

class RepositoryEditFragment : Fragment() {

    private lateinit var tvRepositoryName: TextView
    private lateinit var etRepositoryDescription: EditText
    private lateinit var btnSaveChanges: Button
    private lateinit var btnCancel: Button
    private lateinit var btnDeleteRepository: Button
    private lateinit var progressBar: ProgressBar

    private var repository: Repository? = null

    companion object {
        private const val ARG_REPOSITORY_NAME = "repository_name"
        private const val ARG_REPOSITORY_DESCRIPTION = "repository_description"
        private const val ARG_REPOSITORY_OWNER = "repository_owner"
        private const val ARG_REPOSITORY_LANGUAGE = "repository_language"
        private const val ARG_REPOSITORY_STARS = "repository_stars"
        private const val ARG_REPOSITORY_IS_PUBLIC = "repository_is_public"
        private const val ARG_REPOSITORY_AVATAR_URL = "repository_avatar_url"

        fun newInstance(repository: Repository): RepositoryEditFragment {
            val fragment = RepositoryEditFragment()
            val args = Bundle().apply {
                putString(ARG_REPOSITORY_NAME, repository.name)
                putString(ARG_REPOSITORY_DESCRIPTION, repository.description)
                putString(ARG_REPOSITORY_OWNER, repository.owner)
                putString(ARG_REPOSITORY_LANGUAGE, repository.language)
                putInt(ARG_REPOSITORY_STARS, repository.stars)
                putBoolean(ARG_REPOSITORY_IS_PUBLIC, repository.isPublic)
                putString(ARG_REPOSITORY_AVATAR_URL, repository.avatarUrl)
            }
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            repository = Repository(
                name = it.getString(ARG_REPOSITORY_NAME, ""),
                description = it.getString(ARG_REPOSITORY_DESCRIPTION, ""),
                owner = it.getString(ARG_REPOSITORY_OWNER, ""),
                language = it.getString(ARG_REPOSITORY_LANGUAGE, ""),
                stars = it.getInt(ARG_REPOSITORY_STARS, 0),
                isPublic = it.getBoolean(ARG_REPOSITORY_IS_PUBLIC, true),
                avatarUrl = it.getString(ARG_REPOSITORY_AVATAR_URL, "")
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_repository_edit, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews(view)
        loadRepositoryData()
        setupListeners()

        // Verificar si tiene token configurado
        if (!RetrofitClient.hasToken()) {
            Toast.makeText(
                requireContext(),
                "Necesitas configurar un token de GitHub para editar repositorios",
                Toast.LENGTH_LONG
            ).show()
            btnSaveChanges.isEnabled = false
            btnDeleteRepository.isEnabled = false
        }
    }

    private fun initViews(view: View) {
        tvRepositoryName = view.findViewById(R.id.tvRepositoryName)
        etRepositoryDescription = view.findViewById(R.id.etRepositoryDescription)
        btnSaveChanges = view.findViewById(R.id.btnSaveChanges)
        btnCancel = view.findViewById(R.id.btnCancel)
        btnDeleteRepository = view.findViewById(R.id.btnDeleteRepository)
        progressBar = view.findViewById(R.id.progressBar)
    }

    private fun loadRepositoryData() {
        repository?.let {
            tvRepositoryName.text = it.name
            etRepositoryDescription.setText(it.description)
        }
    }

    private fun setupListeners() {
        btnSaveChanges.setOnClickListener {
            saveChanges()
        }

        btnCancel.setOnClickListener {
            navigateBack()
        }

        btnDeleteRepository.setOnClickListener {
            showDeleteConfirmation()
        }
    }

    private fun saveChanges() {
        val newDescription = etRepositoryDescription.text.toString()
        
        repository?.let { repo ->
            showLoading(true)

            lifecycleScope.launch {
                try {
                    val request = UpdateRepositoryRequest(
                        description = newDescription.ifEmpty { null }
                    )

                    val response = RetrofitClient.apiService.updateRepository(
                        token = RetrofitClient.getAuthToken(),
                        owner = repo.owner,
                        repo = repo.name,
                        request = request
                    )

                    showLoading(false)

                    if (response.isSuccessful) {
                        Toast.makeText(
                            requireContext(),
                            "Descripción actualizada exitosamente",
                            Toast.LENGTH_SHORT
                        ).show()
                        navigateBack()
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Error al actualizar: ${response.code()}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                } catch (e: Exception) {
                    showLoading(false)
                    Toast.makeText(
                        requireContext(),
                        "Error: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun showDeleteConfirmation() {
        repository?.let { repo ->
            AlertDialog.Builder(requireContext())
                .setTitle("Eliminar Repositorio")
                .setMessage("¿Estás seguro de que quieres eliminar '${repo.name}'?\n\nEsta acción no se puede deshacer.")
                .setPositiveButton("Eliminar") { _, _ ->
                    deleteRepository()
                }
                .setNegativeButton("Cancelar", null)
                .show()
        }
    }

    private fun deleteRepository() {
        repository?.let { repo ->
            showLoading(true)

            lifecycleScope.launch {
                try {
                    val response = RetrofitClient.apiService.deleteRepository(
                        token = RetrofitClient.getAuthToken(),
                        owner = repo.owner,
                        repo = repo.name
                    )

                    showLoading(false)

                    if (response.isSuccessful) {
                        Toast.makeText(
                            requireContext(),
                            "Repositorio eliminado exitosamente",
                            Toast.LENGTH_SHORT
                        ).show()
                        navigateBack()
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Error al eliminar: ${response.code()}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                } catch (e: Exception) {
                    showLoading(false)
                    Toast.makeText(
                        requireContext(),
                        "Error: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        btnSaveChanges.isEnabled = !isLoading
        btnCancel.isEnabled = !isLoading
        btnDeleteRepository.isEnabled = !isLoading
        etRepositoryDescription.isEnabled = !isLoading
    }

    private fun navigateBack() {
        (requireActivity() as? MainActivity)?.navigateToList()
    }
}
