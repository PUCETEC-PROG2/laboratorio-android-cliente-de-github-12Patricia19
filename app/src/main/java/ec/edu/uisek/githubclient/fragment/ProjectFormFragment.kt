package ec.edu.uisek.githubclient.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import ec.edu.uisek.githubclient.MainActivity
import ec.edu.uisek.githubclient.R
import ec.edu.uisek.githubclient.model.CreateRepositoryRequest
import ec.edu.uisek.githubclient.network.RetrofitClient
import kotlinx.coroutines.launch

class ProjectFormFragment : Fragment() {

    private lateinit var toolbar: Toolbar
    private lateinit var etProjectName: EditText
    private lateinit var etProjectDescription: EditText
    private lateinit var cbPrivate: CheckBox
    private lateinit var btnCreate: AppCompatButton
    private lateinit var btnCancel: AppCompatButton
    private lateinit var progressBar: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_project_form, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        initViews(view)
        setupToolbar()
        setupButtonListeners()
    }

    private fun initViews(view: View) {
        toolbar = view.findViewById(R.id.toolbar)
        etProjectName = view.findViewById(R.id.etProjectName)
        etProjectDescription = view.findViewById(R.id.etProjectDescription)
        cbPrivate = view.findViewById(R.id.cbPrivate)
        btnCreate = view.findViewById(R.id.btnCreate)
        btnCancel = view.findViewById(R.id.btnCancel)
        progressBar = view.findViewById(R.id.progressBar)
    }
    
    private fun setupToolbar() {
        toolbar.setNavigationOnClickListener {
            (requireActivity() as? MainActivity)?.navigateToList()
        }
    }

    private fun setupButtonListeners() {
        btnCreate.setOnClickListener {
            val projectName = etProjectName.text.toString().trim()
            val projectDescription = etProjectDescription.text.toString().trim()
            val isPrivate = cbPrivate.isChecked
            
            if (projectName.isEmpty()) {
                Toast.makeText(requireContext(), "El nombre del proyecto es requerido", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            
            if (projectName.length > 100) {
                Toast.makeText(requireContext(), "El nombre no puede exceder 100 caracteres", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            
            createRepository(projectName, projectDescription, isPrivate)
        }

        btnCancel.setOnClickListener {
            (requireActivity() as? MainActivity)?.navigateToList()
        }
    }
    
    private fun createRepository(name: String, description: String, isPrivate: Boolean) {
        if (!RetrofitClient.hasToken()) {
            Toast.makeText(
                requireContext(),
                "Proyecto '$name' registrado localmente\n(Nota: Configura un token de GitHub en RetrofitClient para crear repositorios reales)",
                Toast.LENGTH_LONG
            ).show()
            clearForm()
            return
        }
        
        showLoading(true)
        
        lifecycleScope.launch {
            try {
                val request = CreateRepositoryRequest(
                    name = name,
                    description = description.ifEmpty { null },
                    isPrivate = isPrivate,
                    autoInit = true
                )
                
                val response = RetrofitClient.apiService.createRepository(
                    token = RetrofitClient.getAuthToken(),
                    request = request
                )
                
                showLoading(false)
                
                if (response.isSuccessful) {
                    val createdRepo = response.body()
                    Toast.makeText(
                        requireContext(),
                        "✓ Repositorio '${createdRepo?.name}' creado exitosamente en GitHub",
                        Toast.LENGTH_LONG
                    ).show()
                    clearForm()
                    (requireActivity() as? MainActivity)?.navigateToList()
                } else {
                    val errorBody = response.errorBody()?.string()
                    Toast.makeText(
                        requireContext(),
                        "Error al crear repositorio: ${response.code()}\n${errorBody ?: "Error desconocido"}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            } catch (e: Exception) {
                showLoading(false)
                Toast.makeText(
                    requireContext(),
                    "Error de conexión: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
    
    private fun showLoading(isLoading: Boolean) {
        progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        btnCreate.isEnabled = !isLoading
        btnCancel.isEnabled = !isLoading
        etProjectName.isEnabled = !isLoading
        etProjectDescription.isEnabled = !isLoading
        cbPrivate.isEnabled = !isLoading
    }

    private fun clearForm() {
        etProjectName.text.clear()
        etProjectDescription.text.clear()
        cbPrivate.isChecked = false
    }
}