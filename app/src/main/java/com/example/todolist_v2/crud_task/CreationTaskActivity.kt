package com.example.todolist_v2.crud_task

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import android.content.Intent
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.TimePicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.todolist_v2.R
import com.example.todolist_v2.model.Categorie
import com.example.todolist_v2.model.DAOTask
import java.util.*

class CreationTaskActivity : AppCompatActivity() {
    lateinit var btn_crea: Button
    lateinit var task_name: EditText
    lateinit var task_dateLimite: EditText
    lateinit var spinnerCategories: Spinner
    var categoriesList: MutableList<Categorie> = mutableListOf() // Liste des catégories

    lateinit var sgbd: DAOTask // DAO pour gérer les opérations sur les tâches

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_creation_task)

        // Initialisation du DAO pour les opérations sur les tâches
        sgbd = DAOTask()
        sgbd.init(this)

        // Récupération des références des vues
        task_name = findViewById(R.id.task_name)
        task_dateLimite = findViewById(R.id.task_dateLimite)
        btn_crea = findViewById(R.id.btn_crea)

        // Récupération de la référence à l'icône de flèche retour (backArrow)
        val backArrow = findViewById<ImageButton>(R.id.backArrow)
        // Ajout d'un OnClickListener à l'icône de flèche retour
        backArrow.setOnClickListener {
            // Appel de la méthode onBackPressed() pour revenir en arrière
            onBackPressed()
        }

        // Configuration du bouton pour afficher le sélecteur de date et d'heure
        task_dateLimite.setOnClickListener {
            showDateTimePicker()
        }

        // Configuration du Spinner pour afficher les catégories
        spinnerCategories = findViewById(R.id.spinner_categories)
        // Récupérer les catégories depuis la base de données ou le DAO
        categoriesList.addAll(sgbd.getLesCategories())

        // Adapter le Spinner avec les noms de catégories récupérés
        val categorieNames = categoriesList.map { it.nom }
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categorieNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCategories.adapter = adapter

        // Configuration du bouton pour ajouter une tâche
        btn_crea.setOnClickListener {
            val nomT = task_name.text.toString()
            val dateT = task_dateLimite.text.toString()
            val categorieT = spinnerCategories.selectedItemPosition // Récupérer l'index de la catégorie sélectionnée

            Log.i("creaTask", "-> $nomT -> $dateT -> Catégorie: $categorieT")

            // Si le nom de la tâche est vide, on ne fait rien
            if (nomT.isEmpty()) {
                Toast.makeText(this, "Veuillez saisir un nom de tâche", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Si la date limite est vide, on ne fait rien
            if (dateT.isEmpty()) {
                Toast.makeText(this, "Veuillez choisir une date limite", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Création de l'intent avec les données de la tâche
            val intent = Intent()
            intent.putExtra(MainActivityTask.TASK_NOM, nomT)
            intent.putExtra(MainActivityTask.TASK_DATE, dateT)
            intent.putExtra(MainActivityTask.TASK_CATEGORIE, categorieT)

            // Retour à l'activité précédente avec les données de la tâche
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }

    // Fonction pour afficher le sélecteur de date et d'heure
    private fun showDateTimePicker() {
        // Obtention de l'instance du calendrier avec la date et l'heure actuelles
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
        val hourOfDay = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        // Affichage du sélecteur de date
        val datePickerDialog = DatePickerDialog(
            this,
            DatePickerDialog.OnDateSetListener { view: DatePicker?, year: Int, monthOfYear: Int, dayOfMonth: Int ->
                // Après la sélection de la date, affichage du sélecteur de l'heure
                TimePickerDialog(
                    this,
                    TimePickerDialog.OnTimeSetListener { view: TimePicker?, hourOfDay: Int, minute: Int ->
                        // Construction de la date complète avec l'heure sélectionnée
                        val dateLimite = "$dayOfMonth/${monthOfYear + 1}/$year $hourOfDay:$minute"
                        // Affichage de la date dans le champ de texte
                        task_dateLimite.setText(dateLimite)
                    },
                    hourOfDay,
                    minute,
                    true
                ).show()
            },
            year,
            month,
            dayOfMonth
        )
        // Affichage du sélecteur de date
        datePickerDialog.show()
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }
}
