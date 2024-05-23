package com.example.todolist_v2.model

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.util.Log

class DAOTask() {
    lateinit var monBDHelper: BDHelper
    lateinit var maBase:SQLiteDatabase

    // coder les requêtes

    // test base (java)
    fun testBase(): Int
    {
        open()
        val req: String = "select count(nom) from Task";
        val cursor = maBase.rawQuery (req, null, null);
        cursor.moveToNext();
        Log.i("sgbd", " test base v2 : " + cursor.getInt(0));
        return cursor.getInt(0);
        close()

    }

    // requête delete
    fun deleteTask(nom: String, id: Int) {
        open()
        val colonne = "id = ?"
        val args = arrayOf( "$id")
        maBase.delete("Task",colonne, args )
        close()
    }
    // requête select * from Task (table)
    fun getLesTasksAvecId(): MutableMap<Int, Task> {
        maBase = monBDHelper.readableDatabase
        val cursor = maBase.query("Task",
            arrayOf("id", "nom", "datelimite", "idCategorie"),
            null, null, null, null, "nom"
        )

        val laTableTask = mutableMapOf<Int, Task>()

        if (cursor.count > 0) {
            while (cursor.moveToNext()) {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                val nom = cursor.getString(cursor.getColumnIndexOrThrow("nom"))
                val datelimite = cursor.getString(cursor.getColumnIndexOrThrow("datelimite"))
                val idCategorie = cursor.getInt(cursor.getColumnIndexOrThrow("idCategorie"))
                Log.i("getLesTasksAvecId", "Task: $nom, Date: $datelimite, Category: $idCategorie") // Debug Log
                val unTask = Task(nom, datelimite, idCategorie)
                laTableTask[id] = unTask
            }
        }
        cursor.close()
        close()
        return laTableTask
    }

    // requête select * from Task
    fun getLesTask(): MutableList<Task> {
        // ouverture en lecture seule pour les getters
        maBase = monBDHelper.readableDatabase
        // le select sera réalisé via l'ORM
        val cursor = maBase.query("Task",
            arrayOf("nom", "datelimite", "idCategorie"),
            null, null, null, null, "nom"
        )
        var lesTasks: MutableList<Task> = mutableListOf<Task>()
        // il faut traiter le cursor qui récupère
        // le record set du select (résultat)
        // est-ce que le cursor contient des lignes ?
        if(cursor.count>0) {
            // s'il n'est pas vide, on le parcourt
            while (cursor.moveToNext()) {
                // on instancie un jeu avec les colonnes de la requête
                val nom = cursor.getString(cursor.getColumnIndexOrThrow("nom"))
                val datelimite = cursor.getString(cursor.getColumnIndexOrThrow("datelimite"))
                val idCategorie = cursor.getInt(cursor.getColumnIndexOrThrow("idCategorie"))
                val unTask = Task(nom, datelimite, idCategorie)
                // on ajoute le jeu à la liste
                lesTasks.add(unTask)
            }
        }
        close()
        return lesTasks
    }

    // requête update
    fun updateTask(unTask: Task, id: Int): Int {
        open()
        val values = ContentValues().apply {
            put("nom", unTask.nom)
            put("datelimite", unTask.dateLimite)
            put("idCategorie", unTask.idCategorie)
        }
        val result = maBase.update("Task", values, "id = ?", arrayOf("$id")).toInt()
        Log.i("updateTask", "result ->  $result values -> ${values.toString()}")
        close()
        return result
    }

    // requête insert
    public  fun insertTask(unTask: Task) : Int {
        open()
        // écrire la requête insert into
        // ou utiliser les fonctions ORM
        // ORM = Object<->Relational Mapping
        // ContentValues est une classe android
        // utilisée pour le mapping des objets métier
        val values = ContentValues()
        values.put("nom", unTask.nom)
        values.put("datelimite",unTask.dateLimite)
        values.put("idCategorie", unTask.idCategorie)
        // on exécute la requête
        // et on retourne son résultat (échec/réussite)
        val result = maBase.insert("Task",null,values).toInt()
        close()
        return result
    }

    // Ajouter une méthode pour récupérer les catégories
    fun getLesCategories(): MutableList<Categorie> {
        open()
        val cursor = maBase.query(
            "Categorie",
            arrayOf("id", "nom"),
            null, null, null, null,
            "nom"
        )
        var lesCategories: MutableList<Categorie> = mutableListOf()
        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                val nom = cursor.getString(cursor.getColumnIndexOrThrow("nom"))
                val unCategorie = Categorie(id, nom)
                lesCategories.add(unCategorie)
            } while (cursor.moveToNext())
        }
        cursor.close()
        close()
        return lesCategories
    }


     fun init(context: Context) {
        monBDHelper = BDHelper(context)
    }

    // accès à la base de données
    fun open() {
        maBase = monBDHelper.writableDatabase
    }

    fun close() {
        maBase.close()
    }
}