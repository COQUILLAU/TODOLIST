package com.example.todolist_v2.support

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.todolist_v2.R
import com.example.todolist_v2.model.Task

class TaskAdapter(
    private val lesTasks: List<Task>,
    private val unListener: TaskListener,
    private val lesCategories: Map<Int, String> // Ajout des catégories ici
) : RecyclerView.Adapter<TaskViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val context = parent.context
        val view = LayoutInflater.from(context).inflate(R.layout.item_task, parent,false)
        return  TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val unTask = lesTasks[position]
        holder.task_nom.text = unTask.nom
        holder.task_dateLimite.text = unTask.dateLimite
        val categorieNom = lesCategories[unTask.idCategorie] // Récupérer le nom de la catégorie
        holder.task_idCategorie.text = categorieNom // Afficher le nom de la catégorie
        holder.itemView.setOnClickListener {
            unListener.onItemClicked(position)
        }
        holder.btn_supp.setOnClickListener {
            unListener.onSuppClicked(position)
        }
    }

    override fun getItemCount(): Int = lesTasks.size

} // fin de la classe TaskAdapter

// TaskViewHolder hérite de RecyclerView.ViewHolder en utilisant une view (item_task)
class TaskViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    val task_nom: TextView = itemView.findViewById(R.id.task_nom)
    val task_dateLimite: TextView = itemView.findViewById(R.id.task_dateLimite)
    val task_idCategorie: TextView = itemView.findViewById(R.id.task_idCategorie)
    // je récupère le bouton de la poubelle
    val btn_supp : ImageButton = itemView.findViewById(R.id.btn_supp)
}// fin de la classe TaskViewHolder