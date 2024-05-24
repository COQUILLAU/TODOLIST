package com.example.todolist_v2.crud_task

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
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
import com.example.todolist_v2.MainActivityDashboard
import com.example.todolist_v2.crud_categorie.MainActivityCategorie
import com.example.todolist_v2.R
import com.example.todolist_v2.model.DAOTask
import com.example.todolist_v2.model.Task
import com.example.todolist_v2.support.TaskAdapter
import com.example.todolist_v2.support.TaskListener
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.json.JSONArray
import java.io.IOException
import java.io.InputStream

class MainActivityTask : AppCompatActivity(), TaskListener {
    // RecyclerView pour afficher les tâches
    lateinit var taskRecyclerView: RecyclerView
    // Bouton flottant pour ajouter une nouvelle tâche
    lateinit var ajouterTask: LinearLayout
    // DAO pour gérer les opérations sur les tâches
    lateinit var sgbd: DAOTask
    // Liste de tâches
    var lesTasks: MutableList<Task> = mutableListOf()
    // Liste des IDs des tâches
    var lesId: MutableList<Int> = mutableListOf()
    // Table de correspondance entre ID et tâche
    var laTableTask: MutableMap<Int, Task> = mutableMapOf()
    var lesCategories: MutableMap<Int, String> = mutableMapOf()

    // Constants (meme s'il y a deja un fichier TaskConstantes
    companion object {
        const val TASK_NOM = "TASK_NOM"
        const val TASK_DATE = "TASK_DATE"
        const val TASK_CATEGORIE = "TASK_CATEGORIE"
        const val TASK_POS = "TASK_POS"
    }

    // Gestion du résultat de l'activité de modification d'une tâche
    private val modificationTaskActivityResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                val nomTask = data?.getStringExtra(TASK_NOM)
                val dateTask = data?.getStringExtra(TASK_DATE)
                val categorieTask = data?.getIntExtra(TASK_CATEGORIE, -1)
                val posTask = data?.getIntExtra(TASK_POS, -1)
                if (posTask != -1) {
                    lesTasks[posTask!!].nom = nomTask!!
                    lesTasks[posTask!!].dateLimite = dateTask!!
                    lesTasks[posTask!!].idCategorie = categorieTask!!
                    if (sgbd.updateTask(lesTasks[posTask!!], lesId[posTask!!]) != 0) {
                        runOnUiThread {
                            Toast.makeText(
                                this,
                                "La tâche ${lesTasks[posTask!!].nom} a été modifiée",
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
                val nomT = data?.getStringExtra(TASK_NOM)
                val dateT = data?.getStringExtra(TASK_DATE)
                val categorieT = data?.getIntExtra(TASK_CATEGORIE, -1)
                val nouveauTask = Task( nomT!!, dateT!!, categorieT!!, false,)
                sgbd.insertTask(nouveauTask)
                lesTasks.add(0, nouveauTask)
                Log.i("tâche", "tâche ajoutée ${lesTasks.toString()}")
                taskRecyclerView.adapter?.notifyDataSetChanged()
                runOnUiThread {
                    Toast.makeText(this, "La tâche a été ajoutée", Toast.LENGTH_LONG).show()
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Activer le mode Edge-to-Edge pour une apparence immersive
        enableEdgeToEdge()
        setContentView(R.layout.activity_main_task)

        // Initialiser les données en dur des tâches
        initDonneesEnDurTask()
        // Gérer le clic sur le bouton flottant pour ajouter une tâche
        ajouterTask = findViewById(R.id.tache_ajouter)
        ajouterTask.setOnClickListener {
            val intent = Intent(this, CreationTaskActivity::class.java)
            creationTaskActivityResult.launch(intent)
        }

        // Gestion de la navigation par le bas
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_dashboard -> {
                    // Redirection vers l'activité des catégories
                    val intent = Intent(this, MainActivityDashboard::class.java)
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

    // Initialisation des données en dur pour les tâches
    private fun initDonneesEnDurTask() {
        // Récupérer le RecyclerView
        taskRecyclerView = findViewById(R.id.recycler_task)
        // Initialiser le DAO
        sgbd = DAOTask()
        sgbd.init(this)
        lesCategories = sgbd.getLesCategories().associate { it.id to it.nom }.toMutableMap()

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
        val adapter = TaskAdapter(lesTasks, this, lesCategories) // Passer lesCategories ici
        // Instancier le gestionnaire de mise en page
        val layoutManager = LinearLayoutManager(this)
        taskRecyclerView.adapter = adapter
        taskRecyclerView.layoutManager = layoutManager
    }

    // Traitement d'un fichier JSON pour obtenir les données des tâches
    private fun traiteJson(jsonFic: String) {
        val jsonTab = JSONArray(jsonFic)
        for (i in 0 until jsonTab.length()) {
            val jsonObj = jsonTab.getJSONObject(i)
            val leNom: String = jsonObj.getString("nom")
            val laDate: String = jsonObj.getString("datelimite")
            val laCategorie: Int = jsonObj.getInt("idCategorie")
            val lafait: Boolean = jsonObj.getBoolean("fait")
            val unTask = Task(leNom, laDate, laCategorie, lafait)
            sgbd.insertTask(unTask)
            lesTasks.add(unTask)
            lesId.add((i + 1))
        }
    }

    // Lecture d'un fichier JSON local
    private fun lectureFichierLocal(): String {
        val inputStream: InputStream = assets.open("les_task.json")
        var fichierJson: String? = null
        try {
            fichierJson = inputStream.bufferedReader().readText()
            Log.i("json", "-> $fichierJson")
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return fichierJson ?: ""
    }

    // Gestion du clic sur un élément de la liste des tâches
    override fun onItemClicked(position: Int) {
        val intent = Intent(this, ModificationTaskActivity::class.java)
        intent.putExtra(TASK_NOM, lesTasks[position].nom)
        intent.putExtra(TASK_DATE, lesTasks[position].dateLimite)
        intent.putExtra(TASK_CATEGORIE, lesTasks[position].idCategorie)
        intent.putExtra(TASK_POS, position)
        modificationTaskActivityResult.launch(intent)
    }

    // Gestion du clic sur le bouton de suppression d'une tâche
    override fun onSuppClicked(position: Int) {
        val taskSup = lesTasks[position].nom
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Suppression de la tâche : $taskSup")
            .setMessage("Etes-vous sûr de vouloir supprimer cette tâche ?")
            .setIcon(android.R.drawable.ic_menu_delete)
            .setPositiveButton("Supprimer") { dialog, _ ->
                dialog.dismiss()
                sgbd.deleteTask(lesTasks[position].nom, lesId[position])
                lesTasks.removeAt(position)
                taskRecyclerView.adapter?.notifyItemRemoved(position)
                runOnUiThread {
                    Toast.makeText(this, "La tâche $taskSup a été supprimée", Toast.LENGTH_LONG).show()
                }
            }
            .setNegativeButton("Annuler", null)
        val alertDialog = builder.create()
        alertDialog.show()
    }

    override fun onTaskCheckedChanged(position: Int, isChecked: Boolean) {
        val task = lesTasks[position]
        task.fait = isChecked

        // Mettre à jour la tâche dans la base de données
        sgbd.updateFait(lesId[position], isChecked)

        // Notification à l'utilisateur
        val status = if (isChecked) "complétée" else "non complétée"
        Toast.makeText(this, "Tâche ${task.nom} marquée comme $status", Toast.LENGTH_SHORT).show()
    }
}
