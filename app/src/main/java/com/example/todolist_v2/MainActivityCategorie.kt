// MainActivityCategorie.kt
package com.example.todolist_v2

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.todolist_v2.model.DAOCategorie
import com.example.todolist_v2.model.Categorie
import com.example.todolist_v2.support.CategorieAdapter
import com.example.todolist_v2.support.CategorieListener
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.json.JSONArray
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream

class MainActivityCategorie : AppCompatActivity(), CategorieListener{
    // RecyclerView pour afficher les catégories
    lateinit var categorieRecyclerView: RecyclerView
    // DAO pour gérer les opérations sur les catégories
    lateinit var sgbd: DAOCategorie
    // Liste de catégories
    var lesCategories: MutableList<Categorie> = mutableListOf<Categorie>()
    var lesId: MutableList<Int> = mutableListOf<Int>()
    // Table de correspondance entre ID et tâche
    var laTableCategorie: MutableMap<Int, Categorie> = mutableMapOf<Int, Categorie>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Activer le mode Edge-to-Edge pour une apparence immersive
        enableEdgeToEdge()
        setContentView(R.layout.activity_main_categorie)

        // Gestion de la navigation par le bas
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_task -> {
                    // Redirection vers l'activité des tâches
                    val intent = Intent(this, MainActivityTask::class.java)
                    startActivity(intent)
                    true
                }
                R.id.navigation_categorie -> {
                    true
                }
                else -> false
            }
        }

        // Initialisation des données en dur des catégories
        initDonneesEnDurCategorie()
    }

    // Initialisation des données en dur pour les catégories
    private fun initDonneesEnDurCategorie() {
        // Récupérer le RecyclerView
        categorieRecyclerView = findViewById(R.id.recycler_categorie)
        // Initialiser le DAO
        sgbd = DAOCategorie()
        sgbd.init(this)
        // Vérifier si des données existent déjà dans la base de données
        if (sgbd.testBase() > 0) {
            // Si oui, les récupérer
            lesCategories = sgbd.getLesCategorie()
            laTableCategorie = sgbd.getLesCategoriesAvecId()
            lesId = laTableCategorie.keys.toMutableList()
        } else {
            // Sinon, les insérer à partir d'un fichier JSON
            val jsonFic: String = lectureFichierLocal()
            traiteJson(jsonFic)
        }
        // Instancier l'adaptateur et l'alimenter avec les données
        val adapter = CategorieAdapter(lesCategories, this)
        // Instancier le gestionnaire de mise en page
        val layoutManager = LinearLayoutManager(this)
        categorieRecyclerView.adapter = adapter
        categorieRecyclerView.layoutManager = layoutManager
    }

    // Traitement d'un fichier JSON pour obtenir les données des catégories
    private fun traiteJson(jsonFic: String) {
        var jsonTab = JSONArray(jsonFic)
        for (i in 0 until jsonTab.length()) {
            var jsonObj = jsonTab.getJSONObject(i)
            var leNom: String = jsonObj.getString("nom")
            var uneCategorie: Categorie = Categorie(leNom)
            sgbd.insertCategorie(uneCategorie)
            lesCategories.add(uneCategorie)
        }
    }

    // Lecture d'un fichier JSON local
    private fun lectureFichierLocal(): String {
        val inputStream: InputStream = assets.open("les_categories.json")
        var br: BufferedReader? = inputStream.bufferedReader()
        val builder = StringBuilder()
        var fichierJson: String? = null
        try {
            fichierJson = inputStream.bufferedReader().readText()
            Log.i("json", "-> $fichierJson")
        } catch (e: IOException) {
        }
        return fichierJson!!
    }

    // Gestion du clic sur un élément de la liste des tâches
    override fun onItemClicked(position: Int) {

    }

    // Gestion du clic sur le bouton de suppression d'une tâche
    override fun onSuppClicked(position: Int) {

    }
}
