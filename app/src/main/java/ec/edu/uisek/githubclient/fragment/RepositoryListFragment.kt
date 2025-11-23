package ec.edu.uisek.githubclient.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import ec.edu.uisek.githubclient.model.Repository
import ec.edu.uisek.githubclient.network.RetrofitClient
import kotlinx.coroutines.launch

class RepositoryListFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var tvError: TextView
    private lateinit var fabAddRepo: FloatingActionButton
    private lateinit var adapter: RepositoryAdapter
    
    private val githubUsername = "google"

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
                val response = RetrofitClient.apiService.getUserRepositories(
                    username = githubUsername,
                    perPage = 30
                )
                
                if (response.isSuccessful) {
                    val githubRepositories = response.body() ?: emptyList()
                    val repositories = githubRepositories.map { 
                        Repository.fromGitHubRepository(it) 
                    }
                    
                    if (repositories.isEmpty()) {
                        showError("No se encontraron repositorios para @$githubUsername")
                    } else {
                        showRepositories(repositories)
                        Toast.makeText(
                            requireContext(),
                            "Se cargaron ${repositories.size} repositorios de @$githubUsername",
                            Toast.LENGTH_SHORT
                        ).show()
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
        adapter = RepositoryAdapter(repositories)
        recyclerView.adapter = adapter
        recyclerView.visibility = View.VISIBLE
        tvError.visibility = View.GONE
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