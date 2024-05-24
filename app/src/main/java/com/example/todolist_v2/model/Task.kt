package com.example.todolist_v2.model

class Task (
    var nom: String,
    var dateLimite: String,
    var idCategorie: Int,
    var fait: Boolean
    ) {
    override fun toString(): String {
        return "Task(nom='$nom', dateLimite='$dateLimite', idCategorie=$idCategorie, fait=$fait)"
    }
}
