package com.example.todolist_v2.support

interface TaskListener {

    public fun onItemClicked(position: Int)

    public fun onSuppClicked(position: Int)

    fun onTaskCheckedChanged(position: Int, isChecked: Boolean)

}