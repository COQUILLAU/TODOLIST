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
    fun testBase(): Int {
        open()

        // Requête pour compter le nombre de tâches dans la table Task
        val reqCount: String = "SELECT COUNT(nom) FROM Task"
        val cursorCount = maBase.rawQuery(reqCount, null)
        cursorCount.moveToFirst()
        val count = cursorCount.getInt(0)
        Log.i("sgbd", "Nombre de tâches dans la base de données : $count")
        cursorCount.close()

        // Requête pour récupérer toutes les lignes de la table Task
        val reqTable: String = "SELECT * FROM Task"
        val cursorTable = maBase.rawQuery(reqTable, null)

        // Affichage des données de la table Task
        if (cursorTable.count > 0) {
            Log.i("sgbd", "Contenu de la table Task :")
            while (cursorTable.moveToNext()) {
                val id = cursorTable.getInt(cursorTable.getColumnIndex("id"))
                val nom = cursorTable.getString(cursorTable.getColumnIndex("nom"))
                val dateLimite = cursorTable.getString(cursorTable.getColumnIndex("datelimite"))
                val idCategorie = cursorTable.getInt(cursorTable.getColumnIndex("idCategorie"))
                val fait = cursorTable.getInt(cursorTable.getColumnIndex("fait"))

                Log.i("sgbd", "   id: $id, nom: $nom, date limite: $dateLimite, idCategorie: $idCategorie, fait: $fait")
            }
        } else {
            Log.i("sgbd", "La table Task est vide.")
        }

        cursorTable.close()
        close()

        return count
    }


    // requête delete
    fun deleteTask(nom: String, id: Int) {
        open()
        val colonne = "id = ?"
        val args = arrayOf( "$id")
        maBase.delete("Task",colonne, args )
        close()
    }
    // requête select * from Task avec id
    fun getLesTasksAvecId(): MutableMap<Int, Task> {
        open()
        maBase = monBDHelper.readableDatabase
        val cursor = maBase.query(
            "Task",
            arrayOf("id", "nom", "datelimite", "idCategorie", "fait"),
            null, null, null, null, "nom"
        )

        val laTableTask = mutableMapOf<Int, Task>()

        if (cursor.count > 0) {
            while (cursor.moveToNext()) {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                val nom = cursor.getString(cursor.getColumnIndexOrThrow("nom"))
                val datelimite = cursor.getString(cursor.getColumnIndexOrThrow("datelimite"))
                val idCategorie = cursor.getInt(cursor.getColumnIndexOrThrow("idCategorie"))
                val fait = cursor.getInt(cursor.getColumnIndexOrThrow("fait")) == 1
                val unTask = Task( nom, datelimite, idCategorie, fait)
                laTableTask[id] = unTask
            }
        }
        cursor.close()
        close()
        return laTableTask
    }

    // requête select * from Task
    fun getLesTask(): MutableList<Task> {
        open()
        maBase = monBDHelper.readableDatabase
        val cursor = maBase.query(
            "Task",
            arrayOf("nom", "datelimite", "idCategorie", "fait"),
            null, null, null, null, "nom"
        )
        val lesTasks = mutableListOf<Task>()

        if (cursor.count > 0) {
            while (cursor.moveToNext()) {
                val nom = cursor.getString(cursor.getColumnIndexOrThrow("nom"))
                val datelimite = cursor.getString(cursor.getColumnIndexOrThrow("datelimite"))
                val idCategorie = cursor.getInt(cursor.getColumnIndexOrThrow("idCategorie"))
                val fait = cursor.getInt(cursor.getColumnIndexOrThrow("fait")) == 1
                val unTask = Task( nom, datelimite, idCategorie, fait)
                lesTasks.add(unTask)
            }
        }
        cursor.close()
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
        values.put("fait", 0)
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

    // Mettre à jour le champ 'fait' d'une tâche dans la base de données
    fun updateFait(id: Int, fait: Boolean): Int {
        open()
        val values = ContentValues().apply {
            put("fait", fait) // Mettre à jour le champ 'fait' avec la nouvelle valeur
        }
        val result = maBase.update("Task", values, "id = ?", arrayOf(id.toString()))
        Log.i("updateFait", "Updated Task avec l'id=$id. fait = $fait")
        close()
        return result
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