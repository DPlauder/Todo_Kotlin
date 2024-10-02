package com.dplauder.todoapp

import android.app.LauncherActivity.ListItem
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dplauder.todoapp.databinding.ListItemBinding

class ItemAdapter(
    private var items: MutableList<Item>,
    private val onItemClicked: (Item, Int) -> Unit,
    ): RecyclerView.Adapter<ItemAdapter.ItemViewHolder>()
    {

    inner class ItemViewHolder(val binding: ListItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Item) {
            binding.itemName.text = item.name
            binding.root.setOnClickListener{
                val position = bindingAdapterPosition
                onItemClicked(item, position)
            }
            if (item.isDone) {
                binding.itemName.paintFlags =
                    binding.itemName.paintFlags or android.graphics.Paint.STRIKE_THRU_TEXT_FLAG
                binding.itemName.setTextColor(binding.root.context.getColor(R.color.gray))
            } else {
                binding.itemName.paintFlags =
                    binding.itemName.paintFlags and android.graphics.Paint.STRIKE_THRU_TEXT_FLAG.inv()
                binding.itemName.setTextColor(binding.root.context.getColor(android.R.color.black))
            }
            binding.deleteIcon.setOnClickListener{
                val position = bindingAdapterPosition
                removeItem(position)
            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding =
            ListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    fun addItem(item : Item){
        items.add(item)
        notifyItemInserted(items.size - 1)
    }
    fun removeItem(position: Int){
        items.removeAt(position)
        notifyItemRemoved(position)
    }
        fun changeItemIsDone(position: Int){
            val item = items[position]
            if(!item.isDone){
                item.isDone = true
            } else{
                item.isDone = false
            }
            notifyItemChanged(position)
        }

        fun removeDoneItems(){
            val undoneItems = items.filter{!it.isDone}
            items.clear()
            items.addAll(undoneItems)
            notifyDataSetChanged()
        }
}