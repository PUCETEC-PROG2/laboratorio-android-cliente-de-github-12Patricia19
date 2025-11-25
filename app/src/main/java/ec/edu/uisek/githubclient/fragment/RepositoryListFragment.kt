package ec.edu.uisek.githubclient.fragment

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import ec.edu.uisek.githubclient.R
import ec.edu.uisek.githubclient.adapter.RepositoryAdapter
import ec.edu.uisek.githubclient.model.CreateRepositoryRequest
import ec.edu.uisek.githubclient.model.Repository
import ec.edu.uisek.githubclient.model.UpdateRepositoryRequest
import ec.edu.uisek.githubclient.network.RetrofitClient
import kotlinx.coroutines.launch

class RepositoryListFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var tvError: TextView
    private lateinit var fabAddRepo: FloatingActionButton
    private lateinit var adapter: RepositoryAdapter
    
    private val repositories = mutableListOf<Repository>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_repository_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        initViews(view)
        setupRecyclerView()
        setupFab()
        fetchRepositories()
    }
    
    private fun initViews(view: View) {
        recyclerView = view.findViewById(R.id.recyclerViewRepositories)
        progressBar = view.findViewById(R.id.progressBar)
        tvError = view.findViewById(R.id.tvError)
        fabAddRepo = view.findViewById(R.id.fabAddRepo)
    }

    private fun setupRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
    }
    
    private fun setupFab() {
        fabAddRepo.setOnClickListener {
            (requireActivity() as? ec.edu.uisek.githubclient.MainActivity)?.navigateToForm()
        }
    }
    
    private fun fetchRepositories() {
        showLoading(true)
        
        lifecycleScope.launch {
            try {
                val response = if (RetrofitClient.hasToken()) {
                    RetrofitClient.apiService.getAuthenticatedUserRepositories(
                        token = RetrofitClient.getAuthToken(),
                        perPage = 100
                    )
                } else {
                    RetrofitClient.apiService.getUserRepositories(
                        username = "google",
                        perPage = 30
                    )
                }
                
                if (response.isSuccessful) {
                    val githubRepositories = response.body() ?: emptyList()
                    repositories.clear()
                    repositories.addAll(githubRepositories.map { 
                        Repository.fromGitHubRepository(it) 
                    })
                    
                    if (repositories.isEmpty()) {
                        showError("No se encontraron repositorios")
                    } else {
                        showRepositories(repositories)
                        val message = if (RetrofitClient.hasToken()) {
                            "Se cargaron ${repositories.size} repositorios tuyos"
                        } else {
                            "Se cargaron ${repositories.size} repositorios de @google"
                        }
                        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                    }
                } else {
                    showError("Error al cargar repositorios: ${response.code()}")
                }
            } catch (e: Exception) {
                showError("Error de conexión: ${e.message}")
            }
        }
    }
    
    private fun showRepositories(repositories: List<Repository>) {
        showLoading(false)
        adapter = RepositoryAdapter(
            repositories = repositories,
            onEditClick = { repository -> navigateToEdit(repository) },
            onDeleteClick = { repository -> showDeleteDialog(repository) }
        )
        recyclerView.adapter = adapter
        recyclerView.visibility = View.VISIBLE
        tvError.visibility = View.GONE
    }
    
    private fun navigateToEdit(repository: Repository) {
        (requireActivity() as? ec.edu.uisek.githubclient.MainActivity)?.navigateToEdit(repository)
    }
    
    private fun showEditDialog(repository: Repository) {
        if (!RetrofitClient.hasToken()) {
            Toast.makeText(requireContext(), "Necesitas configurar un token de GitHub para editar repositorios", Toast.LENGTH_LONG).show()
            return
        }
        
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_edit_repo, null)
        val etName = dialogView.findViewById<EditText>(R.id.etRepoName)
        val etDescription = dialogView.findViewById<EditText>(R.id.etRepoDescription)
        
        etName.setText(repository.name)
        etDescription.setText(repository.description)
        
        AlertDialog.Builder(requireContext())
            .setTitle("Editar Repositorio")
            .setView(dialogView)
            .setPositiveButton("Guardar") { _, _ ->
                val newName = etName.text.toString()
                val newDescription = etDescription.text.toString()
                updateRepository(repository, newName, newDescription)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
    
    private fun showDeleteDialog(repository: Repository) {
        if (!RetrofitClient.hasToken()) {
            Toast.makeText(requireContext(), "Necesitas configurar un token de GitHub para eliminar repositorios", Toast.LENGTH_LONG).show()
            return
        }
        
        AlertDialog.Builder(requireContext())
            .setTitle("Eliminar Repositorio")
            .setMessage("¿Estás seguro de que quieres eliminar '${repository.name}'?")
            .setPositiveButton("Eliminar") { _, _ ->
                deleteRepository(repository)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
    
    private fun updateRepository(repository: Repository, newName: String, newDescription: String) {
        showLoading(true)
        
        lifecycleScope.launch {
            try {
                val request = UpdateRepositoryRequest(
                    description = newDescription
                )
                
                val response = RetrofitClient.apiService.updateRepository(
                    token = RetrofitClient.getAuthToken(),
                    owner = repository.owner,
                    repo = repository.name,
                    request = request
                )
                
                if (response.isSuccessful) {
                    Toast.makeText(requireContext(), "Repositorio actualizado", Toast.LENGTH_SHORT).show()
                    fetchRepositories()
                } else {
                    showLoading(false)
                    Toast.makeText(requireContext(), "Error al actualizar: ${response.code()}", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                showLoading(false)
                Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
    
    private fun deleteRepository(repository: Repository) {
        showLoading(true)
        
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.deleteRepository(
                    token = RetrofitClient.getAuthToken(),
                    owner = repository.owner,
                    repo = repository.name
                )
                
                showLoading(false)
                
                when (response.code()) {
                    204 -> {
                        Toast.makeText(requireContext(), "Repositorio eliminado exitosamente", Toast.LENGTH_SHORT).show()
                        fetchRepositories()
                    }
                    404 -> {
                        Toast.makeText(
                            requireContext(),
                            "Repositorio no encontrado",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    403 -> {
                        Toast.makeText(
                            requireContext(),
                            "No tienes permisos para eliminar este repositorio. Verifica tu token.",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    401 -> {
                        Toast.makeText(
                            requireContext(),
                            "Token inválido o expirado",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    else -> {
                        Toast.makeText(
                            requireContext(),
                            "Error al eliminar (${response.code()})",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            } catch (e: Exception) {
                showLoading(false)
                Toast.makeText(requireContext(), "Error de red: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
    
    private fun showError(message: String) {
        showLoading(false)
        tvError.text = message
        tvError.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }
    
    private fun showLoading(isLoading: Boolean) {
        progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}