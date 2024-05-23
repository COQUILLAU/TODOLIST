// MainActivityCategorie.kt
package com.example.todolist_v2.crud_categorie

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.todolist_v2.CATEGORIE_NOM
import com.example.todolist_v2.CATEGORIE_POS
import com.example.todolist_v2.R
import com.example.todolist_v2.crud_task.MainActivityTask
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
    // Table de correspondance entre ID et categorie
    var laTableCategorie: MutableMap<Int, Categorie> = mutableMapOf<Int, Categorie>()

    lateinit var ajouterCategorie: LinearLayout

    // Gestion du résultat de l'activité de modification d'une catégorie
    private val modificationCategorieActivityResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                val nomCategorie = data?.getStringExtra(CATEGORIE_NOM)
                val posCategorie = data?.getIntExtra(CATEGORIE_POS, -1)
                if (posCategorie != -1) {
                    lesCategories[posCategorie!!].nom = nomCategorie!!
                    if (sgbd.updateCategorie(lesCategories[posCategorie!!], lesId[posCategorie!!]) != 0) {
                        runOnUiThread {
                            Toast.makeText(
                                this,
                                "La catégorie ${lesCategories[posCategorie!!].nom} a été modifié",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                    categorieRecyclerView.adapter?.notifyItemChanged(posCategorie)
                }
                Log.i("liste", "liste modifiée ${lesCategories.toString()}")
            }
        }

    // Gestion du résultat de l'activité de création d'une tâche
    private val creationCategorieActivityResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                val nomC = data?.getStringExtra(CATEGORIE_NOM)
                val nouvelleCategorie = Categorie(0, nomC.toString())  // L'id sera attribué automatiquement par la base de données
                val id = sgbd.insertCategorie(nouvelleCategorie)  // Insère la catégorie dans la base de données et récupère l'ID attribué
                nouvelleCategorie.id = id.toInt()  // Met à jour l'ID de la catégorie avec celui attribué par la base de données
                lesCategories.add(0, nouvelleCategorie)
                Log.i("categorie", "Categorie ajoutée $nouvelleCategorie")
                categorieRecyclerView.adapter?.notifyDataSetChanged()
                runOnUiThread {
                    Toast.makeText(this, "La catégorie a été ajoutée", Toast.LENGTH_LONG).show()
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Activer le mode Edge-to-Edge pour une apparence immersive
        enableEdgeToEdge()
        setContentView(R.layout.activity_main_categorie)

        // Initialiser les données en dur des catégories
        initDonneesEnDurCategorie()
        // Gérer le clic sur le bouton flottant pour ajouter une catégorie
        ajouterCategorie = findViewById(R.id.cat_ajouter)
        ajouterCategorie.setOnClickListener {
            val intent = Intent(this, CreationCategorieActivity::class.java)
            creationCategorieActivityResult.launch(intent)
        }

        // Gestion de la navigation par le bas
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_task -> {
                    // Redirection vers l'activité des categorie
                    val intent = Intent(this, MainActivityTask::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }

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
            var id: Int = jsonObj.getInt("id")
            var leNom: String = jsonObj.getString("nom")
            var uneCategorie: Categorie = Categorie(id, leNom)
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

    // Gestion du clic sur un élément de la liste des categorie
    override fun onItemClicked(position: Int) {
        val intent = Intent(this, ModificationCategorieActivity::class.java)
        intent.putExtra(CATEGORIE_NOM, lesCategories[position].nom)
        intent.putExtra(CATEGORIE_POS, position)
        modificationCategorieActivityResult.launch(intent)

    }

    // Gestion du clic sur le bouton de suppression d'une categorie
    override fun onSuppClicked(position: Int) {
        var categorie_sup: String = lesCategories[position].nom
        var builder = AlertDialog.Builder(this)
        builder.setTitle("Suppression de la categorie : ${lesCategories[position].nom}")
            .setMessage("Etes-vous sur de vouloir supprimer cette catégorie ?")
            .setIcon(android.R.drawable.ic_menu_delete)
            .setPositiveButton("Supprimer") { dialog, _ ->
                dialog.dismiss()
                sgbd.deleteCategorie(lesCategories[position].nom, lesId[position])
                lesCategories.removeAt(position)
                categorieRecyclerView.adapter?.notifyItemRemoved(position)
                runOnUiThread {
                    Toast.makeText(this, "La categorie ${categorie_sup} a été supprimé", Toast.LENGTH_LONG).show()
                }
            }
            .setNegativeButton("Annuler", null)
        var alertDialog = builder.create()
        alertDialog.show()
    }
}
