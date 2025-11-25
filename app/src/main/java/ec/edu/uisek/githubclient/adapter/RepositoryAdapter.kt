package ec.edu.uisek.githubclient.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.CircleCropTransformation
import ec.edu.uisek.githubclient.R
import ec.edu.uisek.githubclient.model.Repository

class RepositoryAdapter(
    private val repositories: List<Repository>,
    private val onItemClick: (Repository) -> Unit
) : RecyclerView.Adapter<RepositoryAdapter.RepositoryViewHolder>() {

    class RepositoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val repositoryAvatar: ImageView = itemView.findViewById(R.id.ivRepositoryAvatar)
        val repositoryName: TextView = itemView.findViewById(R.id.tvRepositoryName)
        val repositoryDescription: TextView = itemView.findViewById(R.id.tvRepositoryDescription)
        val language: TextView = itemView.findViewById(R.id.tvLanguage)
        val btnEdit: ImageButton = itemView.findViewById(R.id.btnEdit)
        val btnDelete: ImageButton = itemView.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RepositoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_repository, parent, false)
        return RepositoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: RepositoryViewHolder, position: Int) {
        val repository = repositories[position]
        
        holder.repositoryAvatar.load(repository.avatarUrl) {
            crossfade(true)
            placeholder(R.drawable.repo_placeholder)
            error(R.drawable.repo_placeholder)
            transformations(CircleCropTransformation())
        }
        
        holder.repositoryName.text = repository.name
        holder.repositoryDescription.text = repository.description
        holder.language.text = "Lenguaje: ${repository.language}"
        
        // Click en el botón de editar lleva a la pantalla de edición
        holder.btnEdit.setOnClickListener {
            onItemClick(repository)
        }
        
        // Ocultar el botón de eliminar ya que ahora se elimina desde la pantalla de edición
        holder.btnDelete.visibility = View.GONE
    }

    override fun getItemCount(): Int = repositories.size
}