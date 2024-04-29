package com.example.todolist_v2.support

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.todolist_v2.R
import com.example.todolist_v2.model.Task

class TaskAdapter(var lesTasks: List<Task>, var unListener: TaskListener): RecyclerView.Adapter<TaskViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val context = parent.context
        val view = LayoutInflater.from(context).inflate(R.layout.item_task, parent,false)
        return  TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        // on récupère grâce à la position, la tâche souhaité dans la liste des données métier
        val unTask = lesTasks[position]
        // le holder permet d'alimenter la cellule courante (TaskViewHolder) du RecyclerView
        holder.task_nom.text = unTask.nom
        holder.task_dateLimite.text = unTask.dateLimite
        holder.itemView.setOnClickListener {
            unListener.onItemClicked(position)
        }
        // ajouter un écouteur d'évènements sur la poubelle du viewHolder
        holder.btn_supp.setOnClickListener {
            unListener.onSuppClicked(position)
        }
    }
    // ci-dessous, le = remplace le return lesJeux.size
    override fun getItemCount(): Int = lesTasks.size

} // fin de la classe TaskAdapter

// TaskViewHolder hérite de RecyclerView.ViewHolder en utilisant une view (item_task)
class TaskViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    val task_nom: TextView = itemView.findViewById(R.id.task_nom)
    val task_dateLimite: TextView = itemView.findViewById(R.id.task_dateLimite)
    // je récupère le bouton de la poubelle
    val btn_supp : ImageButton = itemView.findViewById(R.id.btn_supp)
}// fin de la classe TaskViewHolder