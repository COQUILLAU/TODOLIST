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
    fun deleteTask(nom: String, i: Int) {
        open()
        val colonne = "id = ?"
        val args = arrayOf( "$i")
        maBase.delete("Task",colonne, args )
        close()
    }
    // requête select * from Task (table)
    fun getLesJeuxAvecId(): MutableMap<Int,Task> {
        // ouverture en lecture seule pour les getters
        maBase = monBDHelper.readableDatabase
        // le select sera réalisé via l'ORM
        val cursor = maBase.query("Task",
            arrayOf( "id", "nom",
                "datelimite"
            ),
            null, null, null, null,
            "nom"
        )
        var lesTasks: MutableList<Task> = mutableListOf<Task>()
        var lesId: MutableList<Int> = mutableListOf<Int>()
        var laTableTask: MutableMap<Int,Task> = mutableMapOf<Int, Task>()

        // il faut traiter le cursor qui récupère
        // le record set du select (résultat)
        // est-ce que le cursor contient des lignes ?
        if(cursor.count>0) {
            // s'il n'est pas vide, on le parcourt
            while (cursor.moveToNext()) {
                // on instancie un jeu avec les colonnes de la requête
                var unTask = Task(cursor.getString(1), cursor.getString(2))
                // on ajoute le jeu à la liste
                lesTasks.add(unTask)
                lesId.add(cursor.getInt(0))
                laTableTask.put(cursor.getInt(0),unTask)
            }
        }
        close()
        return laTableTask
    }
    // requête select * from jeux
    fun getLesTask(): MutableList<Task> {
        // ouverture en lecture seule pour les getters
        maBase = monBDHelper.readableDatabase
        // le select sera réalisé via l'ORM
        val cursor = maBase.query("Task",
            arrayOf( "nom",
                "datelimite"
            ),
            null, null, null, null,
            "nom"
        )
        var lesTasks: MutableList<Task> = mutableListOf<Task>()
        // il faut traiter le cursor qui récupère
        // le record set du select (résultat)
        // est-ce que le cursor contient des lignes ?
        if(cursor.count>0) {
            // s'il n'est pas vide, on le parcourt
            while (cursor.moveToNext()) {
                // on instancie un jeu avec les colonnes de la requête
                var unTask = Task(cursor.getString(0), cursor.getString(1))
                // on ajoute le jeu à la liste
                lesTasks.add(unTask)
            }
        }
        close()
        return lesTasks
    }

    // requête update
    fun updateTask(unTask: Task, i: Int) : Int {
        open()
        val values = ContentValues()
        values.put("nom", unTask.nom)
        values.put("datelimite",unTask.dateLimite)
        Log.i("updateTask","nom -> ${unTask.nom} date ->${unTask.dateLimite}")
        val colonne = "id = ?"
        val args = arrayOf( "$i")
        val result = maBase.update("Task", values, colonne, args).toInt()
        Log.i("updateTask","result ->  $result values -> ${values.toString()}")
        close()
        return result
    }

    // requête insert
    public  fun insertTask(unTask:Task) : Int {
        open()
        // écrire la requête insert into
        // ou utiliser les fonctions ORM
        // ORM = Object<->Relational Mapping
        // ContentValues est une classe android
        // utilisée pour le mapping des objets métier
        val values = ContentValues()
        values.put("nom", unTask.nom)
        values.put("datelimite",unTask.dateLimite)
        // on exécute la requête
        // et on retourne son résultat (échec/réussite)
        val result = maBase.insert("Task",null,values).toInt()
        close()
        return result
    }
    public fun init(context: Context) {
        monBDHelper = BDHelper(context)
    }

    // accès à la base de données
    public fun open() {
        maBase = monBDHelper.writableDatabase
    }

    public  fun close() {
        maBase.close()
    }
}