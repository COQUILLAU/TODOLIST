package com.example.todolist_v2.model

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.util.Log

class DAOCategorie() {
    private lateinit var monBDHelper: BDHelper
    private lateinit var maBase: SQLiteDatabase


    fun testBase(): Int {
        open()
        val req: String = "select count(nom) from Categorie";
        val cursor = maBase.rawQuery(req, null)
        cursor.moveToNext()
        Log.i("sgbd", " test Categorie : " + cursor.getInt(0))
        close()
        return cursor.getInt(0)
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
        var lesCategories: MutableList<Categorie> = mutableListOf<Categorie>()
        var laTableCategorie: MutableMap<Int, Categorie> = mutableMapOf<Int, Categorie>()

        if (cursor.count > 0) {
            while (cursor.moveToNext()) {
                var unCategorie = Categorie(cursor.getString(1))
                lesCategories.add(unCategorie)
                laTableCategorie.put(cursor.getInt(0), unCategorie)
            }
        }
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
            arrayOf("nom"),
            null, null, null, null,
            "nom"
        )
        var lesCategories: MutableList<Categorie> = mutableListOf<Categorie>()
        if (cursor.count > 0) {
            while (cursor.moveToNext()) {
                var unCategorie = Categorie(cursor.getString(0))
                lesCategories.add(unCategorie)
            }
        }
        close()
        return lesCategories
    }

}




