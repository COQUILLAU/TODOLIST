package com.example.todolist_v2.model
// définition de la classe métier jeu
class Task (
    var nom: String,
    var dateLimite: String,
)
{
    override fun toString(): String {
        return "Task(nom='$nom', dateSortie='$dateLimite')"
    }
}

