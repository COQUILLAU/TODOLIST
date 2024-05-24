package com.example.todolist_v2.support

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.todolist_v2.R
import com.example.todolist_v2.model.Task

// Adapter pour la liste des tâches
class TaskAdapter(
    private val lesTasks: List<Task>,
    private val unListener: TaskListener,
    private val lesCategories: Map<Int, String> // Ajout des catégories ici
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    // Création de la vue pour chaque élément de la liste
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val context = parent.context
        val view = LayoutInflater.from(context).inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(view)
    }

    // Liaison des données de la tâche avec la vue
    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val unTask = lesTasks[position]
        holder.task_nom.text = unTask.nom
        holder.task_dateLimite.text = unTask.dateLimite
        val categorieNom = lesCategories[unTask.idCategorie] // Récupérer le nom de la catégorie
        holder.task_idCategorie.text = categorieNom // Afficher le nom de la catégorie

        // Gérer l'état du CheckBox en fonction de la tâche
        holder.checkbox.isChecked = unTask.fait

        // Gestion du clic sur l'élément de la liste
        holder.itemView.setOnClickListener {
            unListener.onItemClicked(position)
        }

        // Gestion du clic sur le bouton de suppression
        holder.btn_supp.setOnClickListener {
            unListener.onSuppClicked(position)
        }

        // Gestion du clic sur le CheckBox pour marquer la tâche comme complétée
        holder.checkbox.setOnCheckedChangeListener { _, isChecked ->
            unTask.fait = isChecked
            unListener.onTaskCheckedChanged(position, isChecked)
        }
    }

    // Nombre total d'éléments dans la liste
    override fun getItemCount(): Int = lesTasks.size

    // ViewHolder pour chaque élément de la liste des tâches
    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val task_nom: TextView = itemView.findViewById(R.id.task_nom)
        val task_dateLimite: TextView = itemView.findViewById(R.id.task_dateLimite)
        val task_idCategorie: TextView = itemView.findViewById(R.id.task_idCategorie)
        val btn_supp: ImageButton = itemView.findViewById(R.id.btn_supp) // Récupération du bouton de suppression
        val checkbox: CheckBox = itemView.findViewById(R.id.fait) // Récupération du CheckBox
    }
}
