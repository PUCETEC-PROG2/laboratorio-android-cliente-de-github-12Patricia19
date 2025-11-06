package ec.edu.uisek.githubclient

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayout
import ec.edu.uisek.githubclient.fragment.ProjectFormFragment
import ec.edu.uisek.githubclient.fragment.RepositoryListFragment

class MainActivity : AppCompatActivity() {
    
    private lateinit var tabLayout: TabLayout
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        initViews()
        setupTabLayout()
        
        if (savedInstanceState == null) {
            loadFragment(RepositoryListFragment())
        }
    }
    
    private fun initViews() {
        tabLayout = findViewById(R.id.tabLayout)
    }
    
    private fun setupTabLayout() {
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                val fragment = when (tab.position) {
                    0 -> RepositoryListFragment()
                    1 -> ProjectFormFragment()
                    else -> RepositoryListFragment()
                }
                loadFragment(fragment)
            }
            
            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
    }
    
    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }
}