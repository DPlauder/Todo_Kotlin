package com.dplauder.todoapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher

import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.dplauder.todoapp.databinding.ActivityMainBinding
import com.dplauder.todoapp.databinding.DialogAddListBinding



class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var todoListAdapter: TodoListAdapter
    private val todoLists = mutableListOf<TodoList>()
    private lateinit var listActivityLauncher: ActivityResultLauncher<Intent>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //View Binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialsiere den Adapter und setze ihn af den RecyclerView
        todoListAdapter = TodoListAdapter(todoLists,
            { todoList: TodoList ->
                val intent = Intent(this,SingleListViewActivity::class.java).apply{
                    putExtra("todoList", todoList) // übertrage die TodoList über den Intent
                }
                listActivityLauncher.launch(intent)

            },
            { todoList: TodoList, position ->
                showDeleteConfirmationDialog(todoList, position)
            }
        )
        binding.addListButton.setOnClickListener{
            addNewList()
        }


        binding.recyclerViewList.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = todoListAdapter
        }
        //Beispiel-Daten
        val exampleList = TodoList("Einkaufsliste",
            mutableListOf(
                Item("Bananen", false),
                Item("Mehl", true),
                Item("Milch", false)
            ))
        val exampleList2 = TodoList("Garten",
            mutableListOf(
                Item("Rindenmulch", false)
            ))

        todoListAdapter.addTodoList(exampleList)
        todoListAdapter.addTodoList(exampleList2)

        listActivityLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()

        ){ result ->
            if(result.resultCode == RESULT_OK){
                val updatedTodoList = result.data?.getParcelableExtraProvider<TodoList>("updatedTodoList")
                updatedTodoList?.let{ updatedList ->
                    val index = todoLists.indexOfFirst { it.title == updatedList.title }
                    if(index != -1){
                        todoLists[index] = updatedList
                        todoListAdapter.notifyItemChanged(index)
                    }
                }
            }
        }
    }
    fun addNewList(){
        val inflater = layoutInflater
        val dialogBinding = DialogAddListBinding.inflate(inflater)
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Neue Liste hinzufügen")
        builder.setView(dialogBinding.root)
        builder.setPositiveButton("Hinzufügen", null)
        builder.setNegativeButton("Abbrechen", null)

        val dialog = builder.create()
        dialog.setOnShowListener{
            val positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            val negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE)

            positiveButton.setOnClickListener{
                val listName = dialogBinding.listNameInput.text.toString().trim()
                if(listName.isNotEmpty()){
                    val newList = TodoList(listName, mutableListOf())
                    todoListAdapter.addTodoList(newList)
                    dialog.dismiss()
                } else{
                    dialogBinding.errorMessage.visibility = View.VISIBLE
                    dialogBinding.listNameInput.requestFocus()
                }
            }
            negativeButton.setOnClickListener{
                dialog.dismiss()
            }
        }
        dialog.show()
    }
    fun showDeleteConfirmationDialog(todoList: TodoList, position: Int){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Listen löschen")
        builder.setTitle("Möchten Sie die Liste \"${todoList.title}\" wirklich löschen?")
        builder.setPositiveButton("Löschen"){_, _->
            val listName = todoLists[position].title
            todoListAdapter.removeTodoList(position)
            Toast.makeText(
                binding.root.context,
                "$listName gelöscht",
                Toast.LENGTH_SHORT
            ).show()
        }
        builder.setNegativeButton("Abbrechen", null)
        builder.show()
    }

}
