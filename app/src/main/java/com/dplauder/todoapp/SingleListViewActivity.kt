package com.dplauder.todoapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.dplauder.todoapp.databinding.ActivitySingleListViewBinding
import com.dplauder.todoapp.databinding.DialogAddItemBinding


class SingleListViewActivity: AppCompatActivity() {
    // binding aus der onCreate genommen damit es für die deletes funktioniert
    private lateinit var binding: ActivitySingleListViewBinding
    private lateinit var todoList: TodoList
    private lateinit var itemAdapter: ItemAdapter

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)

        binding = ActivitySingleListViewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        todoList = intent.getParcelableExtraProvider("todoList") ?: return
        itemAdapter = ItemAdapter(todoList.items,
            {
                item: Item, position: Int ->
                changeIsDone(position)

            }
)
        binding.recyclerViewItemList.apply {
            layoutManager = LinearLayoutManager(this@SingleListViewActivity)
            adapter = itemAdapter
        }
        binding.listName.text = todoList.title

        binding.backButton.setOnClickListener{
            val intent = Intent().apply{
                putExtra("updatedTodoList", todoList)
            }
            setResult(RESULT_OK, intent)
            finish()
        }
        binding.addItemButton.setOnClickListener{
            addNewItem()
        }
        // Listener dazugefügt
        binding.deleteCompletedItemsButton.setOnClickListener(){
            showDeleteDoneItemsConfirmationDialog()
        }

    }

    /**
     * ersellt ein DialogFenster für neues Item
     * überprüft leere Eingabe
     * ruft addItem() im itemAdapter auf und übergibt Item
     */
    fun addNewItem(){
        val inflater = layoutInflater
        val dialogBinding = DialogAddItemBinding.inflate(inflater)
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Neues Item hinzufügen")
        builder.setView(dialogBinding.root)
        builder.setPositiveButton("Hinzufügen", null)
        builder.setNegativeButton("Abbrechen", null)
        val dialog = builder.create()
        dialog.setOnShowListener{
            val positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            val negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE)

            positiveButton.setOnClickListener{
                val itemName = dialogBinding.itemNameInput.text.toString().trim()
                if(itemName.isNotEmpty()){
                    val newItem = Item(itemName)
                    itemAdapter.addItem(newItem)
                    dialog.dismiss()
                } else{
                    dialogBinding.errorMessage.visibility = View.VISIBLE
                    dialogBinding.itemNameInput.requestFocus()
                }
            }
            negativeButton.setOnClickListener{
                dialog.dismiss()
            }
        }
        dialog.show()
    }

    /**
     * übergibt changeItemIsDone im itemAdapter position des Items
     */
    fun changeIsDone(position: Int){
        itemAdapter.changeItemIsDone(position)
    }

    /**
     * erstellt Dialog für erledigte Items löschen
     * ruft fun removeDoneItems in ItemAdapter
     */
    fun showDeleteDoneItemsConfirmationDialog(){
        val builder = AlertDialog.Builder(this)
        val startLength= todoList.items.size
        builder.setTitle("Erledigte Items löschen")
        builder.setTitle("Möchten Sie die erledigten Items löschen?")
        builder.setPositiveButton("Löschen"){_, _ ->
            itemAdapter.removeDoneItems()
            val endLength = todoList.items.size
            val newLength = startLength - endLength
            Toast.makeText(
                binding.root.context,
                "$newLength erledigte Items gelöscht",
                Toast.LENGTH_SHORT
            ).show()
        }
        builder.setNegativeButton("Abbrechen", null)
        builder.show()
    }
}