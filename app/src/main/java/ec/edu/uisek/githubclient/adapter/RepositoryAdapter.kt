package ec.edu.uisek.githubclient.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ec.edu.uisek.githubclient.R
import ec.edu.uisek.githubclient.model.Repository

class RepositoryAdapter(private val repositories: List<Repository>) : 
    RecyclerView.Adapter<RepositoryAdapter.RepositoryViewHolder>() {

    class RepositoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val repositoryIcon: ImageView = itemView.findViewById(R.id.ivRepositoryIcon)
        val repositoryName: TextView = itemView.findViewById(R.id.tvRepositoryName)
        val repositoryDescription: TextView = itemView.findViewById(R.id.tvRepositoryDescription)
        val language: TextView = itemView.findViewById(R.id.tvLanguage)
        val stars: TextView = itemView.findViewById(R.id.tvStars)
        val visibility: TextView = itemView.findViewById(R.id.tvVisibility)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RepositoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_repository, parent, false)
        return RepositoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: RepositoryViewHolder, position: Int) {
        val repository = repositories[position]
        
        holder.repositoryName.text = repository.name
        holder.repositoryDescription.text = repository.description
        holder.language.text = "Lenguaje: ${repository.language}"
        holder.stars.visibility = View.GONE
        holder.visibility.visibility = View.GONE
    }

    override fun getItemCount(): Int = repositories.size
}