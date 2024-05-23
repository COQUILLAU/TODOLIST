package com.example.todolist_v2

import android.app.Activity
import android.os.Bundle
import android.content.Intent
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class CreationCategorieActivity : AppCompatActivity() {
    lateinit var btn_crea: Button
    lateinit var categorie_name: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_creation_categorie)

        // Récupération des références des vues
        categorie_name = findViewById(R.id.categorie_name)
        btn_crea = findViewById(R.id.btn_crea)


        // Configuration du bouton pour ajouter une categorie
        btn_crea.setOnClickListener {
            val nomC = categorie_name.text.toString()

            // Si le nom de la categorie est vide, on ne fait rien
            if (nomC.isEmpty()) {
                Toast.makeText(this, "Veuillez saisir un nom de categorie", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Création de l'intent avec les données de la categorie
            val intent = Intent()
            intent.putExtra(CATEGORIE_NOM, nomC)

            // Retour à l'activité précédente avec les données de la categorie
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
        // Récupération de la référence à l'icône de flèche retour (backArrow)
        val backArrow = findViewById<ImageButton>(R.id.backArrow)
        // Ajout d'un OnClickListener à l'icône de flèche retour
        backArrow.setOnClickListener {
            // Appel de la méthode onBackPressed() pour revenir en arrière
            onBackPressed()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

}

