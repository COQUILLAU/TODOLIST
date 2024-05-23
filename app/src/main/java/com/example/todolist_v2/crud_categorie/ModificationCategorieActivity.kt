package com.example.todolist_v2.crud_categorie

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.todolist_v2.CATEGORIE_NOM
import com.example.todolist_v2.CATEGORIE_POS
import com.example.todolist_v2.R

class ModificationCategorieActivity: AppCompatActivity() {

    // variables pour les views
    lateinit var btn_mod : Button
    lateinit var categorie_name : EditText

    // variables qui vont récupérer les contenus des EditText (et autres)
    var categorie_nom: String? = null
    var categorie_pos: Int? = null
    var isUpdated: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_modification_categorie)
        // récupération des views des l'IHM
        btn_mod = findViewById(R.id.btn_mod)
        categorie_name = findViewById(R.id.categorie_name)
        // récupération des valeurs transmises par MainActivity
        // les données du jeu à modifier
        categorie_nom = intent.getStringExtra(CATEGORIE_NOM)
        // pour j_pos, on prévoit une valeur par défaut pour traiter les erreurs
        categorie_pos = intent.getIntExtra(CATEGORIE_POS,-1)
        // afficher les données récupérées dans l'IHM
        categorie_name.setText(categorie_nom)
        // traitement de l'action (via un listener)
        btn_mod.setOnClickListener {
            categorie_nom = categorie_name.text.toString()
            isUpdated = true
            Toast.makeText(this, "Le jeu $categorie_nom a été modifié", Toast.LENGTH_LONG).show()
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

    // codage de l'action "retour en arrière"
    override fun onBackPressed() {
        // est-ce que l'utilisateur a cliqué sur enregistrer
        // pour pouvoir renvoyer les données modifiées à MainActivity
        if(isUpdated) {
            val intent = Intent()
            intent.putExtra(CATEGORIE_NOM,categorie_nom)
            intent.putExtra(CATEGORIE_POS,categorie_pos)
            setResult(Activity.RESULT_OK, intent)
        }
        super.onBackPressed()
    }
}
