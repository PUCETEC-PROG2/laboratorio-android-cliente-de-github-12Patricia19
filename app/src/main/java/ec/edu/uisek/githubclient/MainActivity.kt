package ec.edu.uisek.githubclient

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import ec.edu.uisek.githubclient.fragment.ProjectFormFragment
import ec.edu.uisek.githubclient.fragment.RepositoryEditFragment
import ec.edu.uisek.githubclient.fragment.RepositoryListFragment
import ec.edu.uisek.githubclient.model.Repository

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            loadFragment(RepositoryListFragment())
        }
    }

    fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .addToBackStack(null)
            .commit()
    }
    
    fun navigateToForm() {
        loadFragment(ProjectFormFragment())
    }
    
    fun navigateToEdit(repository: Repository) {
        loadFragment(RepositoryEditFragment.newInstance(repository))
    }
    
    fun navigateToList() {
        supportFragmentManager.popBackStack()
    }
}