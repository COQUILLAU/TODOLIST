package com.example.todolist_v2.model

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.util.Log

class DAOCategorie() {
    private lateinit var monBDHelper: BDHelper
    private lateinit var maBase: SQLiteDatabase


    fun testBase(): Int {
        open() // Ouvre la connexion à la base de données
        val reqCount: String = "select count(nom) from Categorie"; // Requête SQL pour compter les enregistrements
        val cursorCount = maBase.rawQuery(reqCount, null) // Exécute la requête de comptage
        cursorCount.moveToNext() // Déplace le curseur sur le premier résultat
        val count = cursorCount.getInt(0) // Récupère le nombre d'enregistrements
        Log.i("sgbd", "Nombre total de catégories : $count") // Affiche le nombre d'enregistrements dans les logs

        // Requête pour récupérer toutes les lignes de la table Categorie
        val reqTable: String = "select * from Categorie"
        val cursorTable = maBase.rawQuery(reqTable, null) // Exécute la requête pour récupérer toutes les lignes

        // Affichage des données de la table Categorie
        if (cursorTable.count > 0) {
            Log.i("sgbd", "Contenu de la table Categorie :")
            while (cursorTable.moveToNext()) {
                val id = cursorTable.getInt(cursorTable.getColumnIndex("id"))
                val nom = cursorTable.getString(cursorTable.getColumnIndex("nom"))
                Log.i("sgbd", "   id: $id, nom: $nom")
            }
        } else {
            Log.i("sgbd", "La table Categorie est vide.")
        }

        close() // Ferme la connexion à la base de données
        return count // Retourne le nombre d'enregistrements
    }

    fun deleteCategorie(nom: String, i: Int) {
        open()
        val colonne = "id = ?"
        val args = arrayOf("$i")
        maBase.delete("Categorie", colonne, args)
        close()
    }

    fun getLesCategoriesAvecId(): MutableMap<Int, Categorie> {
        maBase = monBDHelper.readableDatabase
        val cursor = maBase.query(
            "Categorie",
            arrayOf("id", "nom"),
            null, null, null, null,
            "nom"
        )
        var laTableCategorie: MutableMap<Int, Categorie> = mutableMapOf()

        if (cursor != null && cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndex("id"))
                val nom = cursor.getString(cursor.getColumnIndex("nom"))
                val uneCategorie = Categorie(id, nom)
                laTableCategorie[id] = uneCategorie
            } while (cursor.moveToNext())
        }

        cursor?.close()
        close()
        return laTableCategorie
    }


    fun updateCategorie(unCategorie: Categorie, i: Int): Int {
        open()
        val values = ContentValues()
        values.put("nom", unCategorie.nom)
        val colonne = "id = ?"
        val args = arrayOf("$i")
        val result = maBase.update("Categorie", values, colonne, args).toInt()
        close()
        return result
    }

    fun insertCategorie(unCategorie: Categorie): Int {
        open()
        val values = ContentValues()
        values.put("nom", unCategorie.nom)
        val result = maBase.insert("Categorie", null, values).toInt()
        Log.i("updateCategorie", "result ->  $result values -> ${values.toString()}")
        close()
        return result
    }

    fun init(context: Context) {
        monBDHelper = BDHelper(context)
    }

    fun open() {
        maBase = monBDHelper.readableDatabase
    }

    fun close() {
        maBase.close()
    }

    fun getLesCategorie(): MutableList<Categorie> {
        maBase = monBDHelper.readableDatabase
        val cursor = maBase.query(
            "Categorie",
            arrayOf("id", "nom"),
            null, null, null, null,
            "nom"
        )
        var lesCategories: MutableList<Categorie> = mutableListOf()

        if (cursor != null && cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndex("id"))
                val nom = cursor.getString(cursor.getColumnIndex("nom"))
                val uneCategorie = Categorie(id, nom)
                lesCategories.add(uneCategorie)
            } while (cursor.moveToNext())
        }

        cursor?.close()
        close()
        return lesCategories
    }


}




