package com.example.todolist_v2.model

class Categorie(
    var id: Int,
    var nom: String
) {
    override fun toString(): String {
        return "Categorie(id=$id, nom='$nom')"
    }
}
