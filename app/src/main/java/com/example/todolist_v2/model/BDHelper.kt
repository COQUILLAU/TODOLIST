package com.example.todolist_v2.model

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class BDHelper(context: Context) :
    SQLiteOpenHelper(context, "todolists", null, 1) {

    override fun onCreate(db: SQLiteDatabase?) {
        createTableTask(db)
        createTableCategorie(db)
    }

    private fun createTableCategorie(db: SQLiteDatabase?) {
        // Table Categorie
        var createTableCategorie = """
            CREATE TABLE Categorie (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nom VARCHAR(30) NOT NULL
            )
        """.trimIndent()
        db?.execSQL(createTableCategorie)
        Log.i("BDHelper", "Table 'Categorie' created successfully.")
    }

    private fun createTableTask(db: SQLiteDatabase?) {
        // Table Task
        var createTableTask = """
            CREATE TABLE Task (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                fait Boolean,
                nom VARCHAR(30) NOT NULL,
                datelimite VARCHAR(30) NOT NULL,
                idCategorie INTEGER NOT NULL,
                FOREIGN KEY (idCategorie) REFERENCES Categorie(id)
            )
        """.trimIndent()
        db?.execSQL(createTableTask)
        Log.i("BDHelper", "Table 'Task' created successfully.")
    }


    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS Task")
        db?.execSQL("DROP TABLE IF EXISTS Categorie")
        onCreate(db)
    }
}

