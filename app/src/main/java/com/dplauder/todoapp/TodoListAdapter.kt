package com.dplauder.todoapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dplauder.todoapp.databinding.TodoListCardItemBinding
import com.dplauder.todoapp.TodoList
import com.dplauder.todoapp.Item

class TodoListAdapter(
    private  val todoLists: MutableList<TodoList>,
    private  val onItemClicked: (TodoList) -> Unit,
    private val onItemLongClicked: (TodoList, Int) -> Unit
) : RecyclerView.Adapter<TodoListAdapter.TodoListViewHolder>(){
    inner class TodoListViewHolder(val binding: TodoListCardItemBinding) :
        RecyclerView.ViewHolder(binding.root){

        fun bind(todoList: TodoList) {
            // Setzt den Titel und die Anzahl der Items
            binding.todoListTitle.text = todoList.title
            binding.todoListItemCount.text = binding.root.context.resources.getQuantityString(
                R.plurals.todo_list_item_count,
                todoList.items.size,
                todoList.items.size
            )

            //Click-Event, wenn ein TodoList-Element angeklickt wird
            binding.root.setOnClickListener {
                onItemClicked(todoList)
            }
            binding.root.setOnLongClickListener{
                val position = bindingAdapterPosition
                if(position != RecyclerView.NO_POSITION){
                    onItemLongClicked(todoList, position)
                }
                true
            }
        }
    }
    // Erstellt ein neues View für ein Listenelement (Viewholder)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoListViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = TodoListCardItemBinding.inflate(inflater, parent, false)
        return TodoListViewHolder(binding)
    }
    // Bindet die Daten an die Views des ViewHolders
    override fun onBindViewHolder(
        holder: TodoListViewHolder,
        position: Int,
    ) {
        val todoList = todoLists[position]
        holder.bind(todoList)
    }
    // Gibt die Anzahl der Todo-Listen zurück
    override fun getItemCount(): Int = todoLists.size

    fun addTodoList(todoList: TodoList){
        todoLists.add(todoList)
        notifyItemInserted(todoLists.size - 1)
    }
    // Methode, um eine Liste zu löschen
    fun removeTodoList(position: Int){
        todoLists.removeAt(position)
        notifyItemRemoved(position)

    }
}