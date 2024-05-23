// MainActivityTask.kt
package com.example.todolist_v2

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
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
import com.example.todolist_v2.model.DAOTask
import com.example.todolist_v2.model.Task
import com.example.todolist_v2.support.TaskAdapter
import com.example.todolist_v2.support.TaskListener
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.json.JSONArray
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream

class MainActivityTask : AppCompatActivity(), TaskListener {
    // RecyclerView pour afficher les tâches
    lateinit var taskRecyclerView: RecyclerView
    // Bouton flottant pour ajouter une nouvelle tâche
    lateinit var ajouterTask: FloatingActionButton
    // DAO pour gérer les opérations sur les tâches
    lateinit var sgbd: DAOTask
    // Liste de tâches
    var lesTasks: MutableList<Task> = mutableListOf<Task>()
    // Liste des IDs des tâches
    var lesId: MutableList<Int> = mutableListOf<Int>()
    // Table de correspondance entre ID et tâche
    var laTableTask: MutableMap<Int, Task> = mutableMapOf<Int, Task>()

    // Gestion du résultat de l'activité de modification d'une tâche
    private val modificationTaskActivityResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                val nomTask = data?.getStringExtra(TASK_NOM)
                val dateTask = data?.getStringExtra(TASK_DATE)
                val posTask = data?.getIntExtra(TASK_POS, -1)
                if (posTask != -1) {
                    lesTasks[posTask!!].nom = nomTask!!
                    lesTasks[posTask!!].dateLimite = dateTask!!
                    if (sgbd.updateTask(lesTasks[posTask!!], lesId[posTask!!]) != 0) {
                        runOnUiThread {
                            Toast.makeText(
                                this,
                                "La tâche ${lesTasks[posTask!!].nom} a été modifié",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                    taskRecyclerView.adapter?.notifyItemChanged(posTask)
                }
                Log.i("liste", "liste modifiée ${lesTasks.toString()}")
            }
        }

    // Gestion du résultat de l'activité de création d'une tâche
    private val creationTaskActivityResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                val nomJ = data?.getStringExtra(TASK_NOM)
                val dateJ = data?.getStringExtra(TASK_DATE)
                val nouveauTask = Task(nomJ!!, dateJ!!)
                sgbd.insertTask(nouveauTask)
                lesTasks.add(0, nouveauTask)
                Log.i("tâche", "tâche modifiée ${lesTasks.toString()}")
                taskRecyclerView.adapter?.notifyDataSetChanged()
                runOnUiThread {
                    Toast.makeText(this, "La tâche a été ajouté", Toast.LENGTH_LONG).show()
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Activer le mode Edge-to-Edge pour une apparence immersive
        enableEdgeToEdge()
        setContentView(R.layout.activity_main_task)

        // Gestion de la navigation par le bas
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_task -> {
                    // Initialiser les données en dur des tâches
                    initDonneesEnDurTask()
                    // Gérer le clic sur le bouton flottant pour ajouter une tâche
                    ajouterTask = findViewById(R.id.tache_ajouter)
                    ajouterTask.setOnClickListener {
                        val intent = Intent(this, CreationTaskActivity::class.java)
                        creationTaskActivityResult.launch(intent)
                    }
                    // Ajuster les marges pour éviter le chevauchement avec la barre de navigation
                    ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
                        val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                        v.setPadding(
                            systemBars.left,
                            systemBars.top,
                            systemBars.right,
                            systemBars.bottom
                        )
                        insets
                    }
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

    // Initialisation des données en dur pour les tâches
    private fun initDonneesEnDurTask() {
        // Récupérer le RecyclerView
        taskRecyclerView = findViewById(R.id.recycler_task)
        // Initialiser le DAO
        sgbd = DAOTask()
        sgbd.init(this)
        // Vérifier si des données existent déjà dans la base de données
        if (sgbd.testBase() > 0) {
            // Si oui, les récupérer
            lesTasks = sgbd.getLesTask()
            laTableTask = sgbd.getLesTasksAvecId()
            lesId = laTableTask.keys.toMutableList()
        } else {
            // Sinon, les insérer à partir d'un fichier JSON
            val jsonFic: String = lectureFichierLocal()
            traiteJson(jsonFic)
        }
        // Instancier l'adaptateur et l'alimenter avec les données
        val adapter = TaskAdapter(lesTasks, this)
        // Instancier le gestionnaire de mise en page
        val layoutManager = LinearLayoutManager(this)
        taskRecyclerView.adapter = adapter
        taskRecyclerView.layoutManager = layoutManager
    }

    // Traitement d'un fichier JSON pour obtenir les données des tâches
    private fun traiteJson(jsonFic: String) {
        var jsonTab = JSONArray(jsonFic)
        for (i in 0 until jsonTab.length()) {
            var jsonObj = jsonTab.getJSONObject(i)
            var leNom: String = jsonObj.getString("nom")
            var laDate: String = jsonObj.getString("datelimite")
            var unTask: Task = Task(leNom, laDate)
            sgbd.insertTask(unTask)
            lesTasks.add(unTask)
            lesId.add((i + 1))
        }
    }

    // Lecture d'un fichier JSON local
    private fun lectureFichierLocal(): String {
        val inputStream: InputStream = assets.open("les_task.json")
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
        val intent = Intent(this, ModificationTaskActivity::class.java)
        intent.putExtra(TASK_NOM, lesTasks[position].nom)
        intent.putExtra(TASK_DATE, lesTasks[position].dateLimite)
        intent.putExtra(TASK_POS, position)
        modificationTaskActivityResult.launch(intent)
    }

    // Gestion du clic sur le bouton de suppression d'une tâche
    override fun onSuppClicked(position: Int) {
        var task_sup: String = lesTasks[position].nom
        var builder = AlertDialog.Builder(this)
        builder.setTitle("Suppression de la tâche : ${lesTasks[position].nom}")
            .setMessage("Etes-vous sur de vouloir supprimer cette tâche ?")
            .setIcon(android.R.drawable.ic_menu_delete)
            .setPositiveButton("Supprimer") { dialog, _ ->
                dialog.dismiss()
                sgbd.deleteTask(lesTasks[position].nom, lesId[position])
                lesTasks.removeAt(position)
                taskRecyclerView.adapter?.notifyItemRemoved(position)
                runOnUiThread {
                    Toast.makeText(this, "La tache ${task_sup} a été supprimé", Toast.LENGTH_LONG).show()
                }
            }
            .setNegativeButton("Annuler", null)
        var alertDialog = builder.create()
        alertDialog.show()
    }

}
