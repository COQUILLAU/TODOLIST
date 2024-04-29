package com.example.todolist_v2.support

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.todolist_v2.R
import com.example.todolist_v2.model.Categorie

class CategorieAdapter(var lesCategories: List<Categorie>, var unListenerCategorie: CategorieListener): RecyclerView.Adapter<CategorieViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategorieViewHolder {
        val context = parent.context
        val view = LayoutInflater.from(context).inflate(R.layout.item_categorie, parent,false)
        return  CategorieViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategorieViewHolder, position: Int) {
        // on récupère grâce à la position, la tâche souhaité dans la liste des données métier
        val unCategorie = lesCategories[position]
        // le holder permet d'alimenter la cellule courante (CategorieViewHolder) du RecyclerView
        holder.categorie_nom.text = unCategorie.nom
        holder.itemView.setOnClickListener {
            unListenerCategorie.onItemClicked(position)
        }
        // ajouter un écouteur d'évènements sur la poubelle du viewHolder
        holder.btn_supp.setOnClickListener {
            unListenerCategorie.onSuppClicked(position)
        }
    }
    override fun getItemCount(): Int = lesCategories.size

} // fin de la classe CategorieAdapter

// CategorieViewHolder hérite de RecyclerView.ViewHolder en utilisant une view (item_categorie)
class CategorieViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    val categorie_nom: TextView = itemView.findViewById(R.id.categorie_nom)
    // je récupère le bouton de la poubelle
    val btn_supp : ImageButton = itemView.findViewById(R.id.btn_supp)
}// fin de la classe CategorieViewHolder