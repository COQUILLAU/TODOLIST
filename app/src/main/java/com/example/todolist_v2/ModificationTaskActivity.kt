package com.example.todolist_v2

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ModificationTaskActivity: AppCompatActivity() {

    // variables pour les views
    lateinit var btn_mod : Button
    lateinit var task_name : EditText
    lateinit var task_dateLimite : EditText

    // variables qui vont récupérer les contenus des EditText (et autres)
    var task_nom: String? = null
    var task_date: String?  = null
    var task_pos: Int? = null
    var isUpdated: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_modification_task)
        // récupération des views des l'IHM
        btn_mod = findViewById(R.id.btn_mod)
        task_name = findViewById(R.id.task_name)
        task_dateLimite = findViewById(R.id.task_dateLimite)
        // récupération des valeurs transmises par MainActivity
        // les données du jeu à modifier
        task_nom = intent.getStringExtra(TASK_NOM)
        task_date = intent.getStringExtra(TASK_DATE)
        // pour j_pos, on prévoit une valeur par défaut pour traiter les erreurs
        task_pos = intent.getIntExtra(TASK_POS,-1)
        // afficher les données récupérées dans l'IHM
        task_name.setText(task_nom)
        task_dateLimite.setText(task_date)
        // traitement de l'action (via un listener)
        btn_mod.setOnClickListener {
            task_nom = task_name.text.toString()
            task_date = task_dateLimite.text.toString()
            isUpdated = true
            Toast.makeText(this, "Le jeu $task_nom a été modifié", Toast.LENGTH_LONG).show()
        }
        // Récupération de la référence à l'icône de flèche retour (backArrow)
        val backArrow = findViewById<ImageButton>(R.id.backArrow)
        // Ajout d'un OnClickListener à l'icône de flèche retour
        backArrow.setOnClickListener {
            // Appel de la méthode onBackPressed() pour revenir en arrière
            onBackPressed()
        }
    }
    // codage de l'action "retour en arrière"
    override fun onBackPressed() {
        // est-ce que l'utilisateur a cliqué sur enregistrer
        // pour pouvoir renvoyer les données modifiées à MainActivity
        if(isUpdated) {
            val intent = Intent()
            intent.putExtra(TASK_NOM,task_nom)
            intent.putExtra(TASK_DATE,task_date)
            intent.putExtra(TASK_POS,task_pos)
            setResult(Activity.RESULT_OK, intent)
        }
        super.onBackPressed()
    }
}