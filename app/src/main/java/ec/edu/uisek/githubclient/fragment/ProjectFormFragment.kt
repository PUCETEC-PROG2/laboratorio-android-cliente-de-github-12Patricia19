package ec.edu.uisek.githubclient.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import ec.edu.uisek.githubclient.R

class ProjectFormFragment : Fragment() {

    private lateinit var etProjectName: EditText
    private lateinit var etProjectDescription: EditText
    private lateinit var btnCreate: AppCompatButton
    private lateinit var btnCancel: AppCompatButton

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
        setupButtonListeners()
    }

    private fun initViews(view: View) {
        etProjectName = view.findViewById(R.id.etProjectName)
        etProjectDescription = view.findViewById(R.id.etProjectDescription)
        btnCreate = view.findViewById(R.id.btnCreate)
        btnCancel = view.findViewById(R.id.btnCancel)
    }

    private fun setupButtonListeners() {
        btnCreate.setOnClickListener {
            val projectName = etProjectName.text.toString().trim()
            val projectDescription = etProjectDescription.text.toString().trim()
            
            if (projectName.isNotEmpty()) {
                Toast.makeText(requireContext(), "Proyecto '$projectName' creado exitosamente", Toast.LENGTH_SHORT).show()
                clearForm()
            } else {
                Toast.makeText(requireContext(), "El nombre del proyecto es requerido", Toast.LENGTH_SHORT).show()
            }
        }

        btnCancel.setOnClickListener {
            clearForm()
            Toast.makeText(requireContext(), "Formulario cancelado", Toast.LENGTH_SHORT).show()
        }
    }

    private fun clearForm() {
        etProjectName.text.clear()
        etProjectDescription.text.clear()
    }
}