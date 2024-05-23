package com.example.todolist_v2

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.enableEdgeToEdge
import com.example.todolist_v2.crud_categorie.MainActivityCategorie
import com.example.todolist_v2.crud_task.MainActivityTask
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivityDashboard : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Activer le mode Edge-to-Edge pour une apparence immersive
        enableEdgeToEdge()

        // Définir le contenu de l'activité à partir du layout XML
        setContentView(R.layout.activity_main_dashboard)

        // Gestion de la navigation par le bas
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_task -> {
                    // Redirection vers l'activité des catégories
                    val intent = Intent(this, MainActivityTask::class.java)
                    startActivity(intent)
                    true
                }
                R.id.navigation_categorie -> {
                    // Redirection vers l'activité des catégories
                    val intent = Intent(this, MainActivityCategorie::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }

    }
}
