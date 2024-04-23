package com.example.todolist_v2.model

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class BDHelper(context: Context) :
    SQLiteOpenHelper(context, "todolist", null, 1) {
    override fun onCreate(db: SQLiteDatabase?) {
        var createTableTask = """
            CREATE TABLE Task (
                id integer primary key AUTOINCREMENT ,
                nom varchar(30) not null,
                datelimite varchar(30) not null
                )
        """.trimIndent()
        db?.execSQL(createTableTask)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE if EXISTS Task")
        onCreate(db)
    }

}