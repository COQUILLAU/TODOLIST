package com.example.todolist_v2

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.json.JSONArray
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream

class MainActivity : AppCompatActivity(), TaskListener {
    // on déclare une vue RecyclerView
    lateinit var taskRecyclerView: RecyclerView
    // on déclare un bouton flottant pour lancer la seconde activité
    lateinit var ajouterTask: FloatingActionButton

    // déclaration du DAO
    // pour qu'il soit accessible à toutes les méthodes de MainActvity
    lateinit var sgbd: DAOTask

    /*
    var lesJeux: MutableList<Jeu> = mutableListOf<Jeu>(  Jeu("Final Fantasy 7","2017-01-31"),
                                       Jeu("Call of Duty", "2018-10-26"),
                                       Jeu("donjons et Dragons","2003-05-14")
                                    )
    */
    var lesTasks: MutableList<Task> = mutableListOf<Task>()
    var lesId: MutableList<Int> = mutableListOf<Int>()
    var laTableTask: MutableMap<Int,Task> = mutableMapOf<Int, Task>()

    // la variable ci-dessous traite le retour des valerus modifiées par l'activité de modification
    private val modificationTaskActivityResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                result : ActivityResult ->
            if(result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                val nomTask = data?.getStringExtra(TASK_NOM)
                val dateTask = data?.getStringExtra(TASK_DATE)
                val posTask = data?.getIntExtra(TASK_POS, -1)
                if(posTask!=-1) {
                    // mise à jour du
                    lesTasks[posTask!!].nom=nomTask!!
                    lesTasks[posTask!!].dateLimite=dateTask!!
                    // update de la table Task
                    if(sgbd.updateTask(lesTasks[posTask!!],lesId[posTask!!])!= 0) {
                        runOnUiThread {
                            Toast.makeText(this, "Le jeu ${ lesTasks[posTask!!].nom} a été modifié", Toast.LENGTH_LONG).show()
                        }
                    }
                    //taskRecyclerView.adapter?.notifyDataSetChanged()
                    taskRecyclerView.adapter?.notifyItemChanged(posTask)
                }

                Log.i("liste","liste modifiée ${lesTasks.toString()}")
                //taskRecyclerView.adapter?.notifyItemChanged(0)

            }
        }

    private val creationTaskActivityResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                result : ActivityResult ->
            if(result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                val nomJ = data?.getStringExtra(TASK_NOM)
                val dateJ = data?.getStringExtra(TASK_DATE)
                val nouveauTask = Task(nomJ!!,dateJ!!)
                sgbd.insertTask(nouveauTask)
                lesTasks.add(0,nouveauTask)
                Log.i("tâche","tâche modifiée ${lesTasks.toString()}")
                //taskRecyclerView.adapter?.notifyItemChanged(0)
                taskRecyclerView.adapter?.notifyDataSetChanged()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        initDonneesEnDur()
        ajouterTask = findViewById(R.id.fab_ajouter)
        ajouterTask.setOnClickListener {
            // une Intent permet la communication entre activity
            // on demande à MainActivity de lancer CreationJeuActvity via une Intent
            val intent = Intent(this, CreationTaskActivity::class.java)
            // la méthode startActivty permet de basculer vesr l'activity cible
            //startActivity(intent)
            creationTaskActivityResult.launch(intent)
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun initDonneesEnDur() {
        // récupérer le RecyclerView
        taskRecyclerView = findViewById(R.id.rcv_jeu)
        // connexion à la base de données
        sgbd = DAOTask()
        sgbd.init(this)
        // récupérer les données dans la base de données ou dans le fichier json
        if(sgbd.testBase()>0) {// y-a-t 'il déjà des données ?
            // si oui, ils proviennent de la bdd
            lesTasks = sgbd.getLesTask()
            laTableTask = sgbd.getLesJeuxAvecId()
            lesId = laTableTask.keys.toMutableList()
        }
        else { // sinon, on les insère depuis le fichier json
            // lecture du fichier json dans le dossier assets
            val jsonFic: String = lectureFichierLocal()
            // transformation du json en List
            traiteJson(jsonFic)
        }
        // instancier l'Adapter et l'alimenter avec les données
        val adapter = TaskAdapter(lesTasks, this)
        // on instancie un layoutManager
        val layoutManager = LinearLayoutManager(this)
        taskRecyclerView.adapter = adapter
        taskRecyclerView.layoutManager = layoutManager
    }

    private fun traiteJson(jsonFic: String) {
        // on transforme la string en tableau d'objets json
        var jsonTab = JSONArray(jsonFic)
        for(i in 0..(jsonTab.length())-1) {
            // on récupère l'objet json courant pour créer un objet métier
            var jsonObj = jsonTab.getJSONObject(i)
            var leNom : String = jsonObj.getString("nom")
            var laDate : String = jsonObj.getString("datelimite")
            var unTask : Task = Task(leNom, laDate)
            // on insère le jeu dans la table et des les listes (jeux et indices)
            sgbd.insertTask(unTask)
            lesTasks.add(unTask)
            // décalage de 1 entre les indices du tableau et l'autoincrement
            lesId.add((i+1))
        }
    }

    private fun lectureFichierLocal(): String {
        // le fichier json est stocké dans le dossier "assets" dans main
        // NB : assets est reconnu par l'application s'il existe
        val inputStream: InputStream = assets.open("les_task.json")
        var br: BufferedReader? = inputStream.bufferedReader()
        val builder = StringBuilder()
        var fichierJson: String? = null
        try {
            fichierJson =  inputStream.bufferedReader().readText()
            Log.i("json","-> $fichierJson")
        }
        catch (e : IOException) { }
        return  fichierJson!!
    }

    override fun onItemClicked(position: Int) {
        Log.i("mainclic","On a cliqué sur le jeu numéro $position")
        Log.i("mainclic","Il s'agit du jeu ${lesTasks[position].nom}")
        // l'intent var relier MainActivity à ModificationJeuActivity
        val intent = Intent(this, ModificationTaskActivity::class.java)
        intent.putExtra(TASK_NOM,lesTasks[position].nom)
        intent.putExtra(TASK_DATE,lesTasks[position].dateLimite)
        // ne pas opublier de transmettre la position
        // NB : créer une nouvelle clé dans Constantes.kt
        intent.putExtra(TASK_POS,position)
        modificationTaskActivityResult.launch(intent)
    }

    override fun onSuppClicked(position: Int) {
        // sauvegarder le nom de la tâche à supprimer pour affichage Toast
        var task_sup: String = lesTasks[position].nom
        // on va configurer une boîte de dialogue
        // pour interagir avec l'utilisateur : "Etes-vous sur de ..."
        var builder = AlertDialog.Builder(this)
        builder.setTitle("Suppression de la tâche : ${lesTasks[position].nom}")
            .setMessage("Etes-vous sur de vouloir supprimer cette tâche ?")
            .setIcon(android.R.drawable.ic_menu_delete)
            .setPositiveButton("Supprimer") {
                    dialog, _ ->
                dialog.dismiss()
                // on exécute une requête delete
                sgbd.deleteTask(lesTasks[position].nom,lesId[position])
                // enlever le jeu de la liste
                lesTasks.removeAt(position)
                // mettre à jour l'affichage
                taskRecyclerView.adapter?.notifyItemRemoved(position)
                // runOnUiThread affiche le toast sur l'IHM qui a le focus
                // c-a-d (en cours d'exécution sur l'écran du smatphone)
                runOnUiThread {
                    Toast.makeText(this, "La tache ${task_sup} a été supprimé", Toast.LENGTH_LONG).show()
                }
            }
            .setNegativeButton("Annuler", null)
        var alertDialog = builder.create()
        alertDialog.show()
    }

}