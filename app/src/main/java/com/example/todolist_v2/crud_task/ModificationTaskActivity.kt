package com.example.todolist_v2.crud_task

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.todolist_v2.R
import com.example.todolist_v2.model.Categorie
import com.example.todolist_v2.model.DAOTask

class ModificationTaskActivity : AppCompatActivity() {

    // variables pour les views
    lateinit var btn_mod: Button
    lateinit var task_name: EditText
    lateinit var task_dateLimite: EditText
    lateinit var spinnerCategories: Spinner
    var categoriesList: MutableList<Categorie> = mutableListOf() // Liste des catégories

    // variables qui vont récupérer les contenus des EditText (et autres)
    var task_nom: String? = null
    var task_date: String? = null
    var task_categorie: Int? = null
    var task_pos: Int? = null
    var isUpdated: Boolean = false

    lateinit var sgbd: DAOTask // DAO pour gérer les opérations sur les tâches

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_modification_task)

        // Initialisation du DAO pour les opérations sur les tâches
        sgbd = DAOTask()
        sgbd.init(this)

        // Récupération des références des vues
        btn_mod = findViewById(R.id.btn_mod)
        task_name = findViewById(R.id.task_name)
        task_dateLimite = findViewById(R.id.task_dateLimite)
        spinnerCategories = findViewById(R.id.spinner_categories)

        // Récupération des valeurs transmises par MainActivity
        task_nom = intent.getStringExtra(MainActivityTask.TASK_NOM)
        task_date = intent.getStringExtra(MainActivityTask.TASK_DATE)
        task_categorie = intent.getIntExtra(MainActivityTask.TASK_CATEGORIE, -1)
        task_pos = intent.getIntExtra(MainActivityTask.TASK_POS, -1)

        // Afficher les données récupérées dans l'IHM
        task_name.setText(task_nom)
        task_dateLimite.setText(task_date)

        // Récupérer les catégories depuis la base de données ou le DAO
        categoriesList.addAll(sgbd.getLesCategories())

        // Adapter le Spinner avec les noms de catégories récupérés
        val categorieNames = categoriesList.map { it.nom }
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categorieNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCategories.adapter = adapter

        // Sélectionner la catégorie actuelle de la tâche
        if (task_categorie != null && task_categorie != -1) {
            val selectedIndex = categoriesList.indexOfFirst { it.id == task_categorie }
            if (selectedIndex != -1) {
                spinnerCategories.setSelection(selectedIndex)
            }
        }

        // Traitement de l'action (via un listener)
        btn_mod.setOnClickListener {
            task_nom = task_name.text.toString()
            task_date = task_dateLimite.text.toString()
            task_categorie = categoriesList[spinnerCategories.selectedItemPosition].id
            isUpdated = true
            Toast.makeText(this, "La tâche $task_nom a été modifiée", Toast.LENGTH_LONG).show()
            // Appeler onBackPressed pour revenir en arrière
            onBackPressed()
        }

        // Récupération de la référence à l'icône de flèche retour (backArrow)
        val backArrow = findViewById<ImageButton>(R.id.backArrow)
        // Ajout d'un OnClickListener à l'icône de flèche retour
        backArrow.setOnClickListener {
            // Appel de la méthode onBackPressed() pour revenir en arrière
            onBackPressed()
        }
    }

    // Codage de l'action "retour en arrière"
    override fun onBackPressed() {
        // Est-ce que l'utilisateur a cliqué sur enregistrer
        // pour pouvoir renvoyer les données modifiées à MainActivity
        if (isUpdated) {
            val intent = Intent()
            intent.putExtra(MainActivityTask.TASK_NOM, task_nom)
            intent.putExtra(MainActivityTask.TASK_DATE, task_date)
            intent.putExtra(MainActivityTask.TASK_CATEGORIE, task_categorie)
            intent.putExtra(MainActivityTask.TASK_POS, task_pos)
            setResult(Activity.RESULT_OK, intent)
        }
        super.onBackPressed()
    }

    companion object {
        const val TASK_NOM = "task_nom"
        const val TASK_DATE = "task_date"
        const val TASK_CATEGORIE = "task_categorie"
        const val TASK_POS = "task_pos"
    }
}
