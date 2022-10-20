package com.rustyrobot.todo.fragments.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.rustyrobot.todo.R
import com.rustyrobot.todo.data.models.Priority
import com.rustyrobot.todo.data.models.ToDoData
import com.rustyrobot.todo.databinding.RowLayoutBinding

class ListAdapter : RecyclerView.Adapter<ListAdapter.MyViewHolder>() {
    private var dataList = emptyList<ToDoData>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = MyViewHolder(
        RowLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = dataList[position]

        holder.binding.titleText.text = currentItem.title
        holder.binding.descriptionText.text = currentItem.description
        holder.binding.rowBackground.setOnClickListener {
            val action = ListFragmentDirections.actionListFragmentToUpdateFragment(currentItem)
            holder.binding.root.findNavController().navigate(action)
        }

        when (currentItem.priority) {

            Priority.HIGH -> {
                holder.binding.priorityIndicator.setCardBackgroundColor(
                    ContextCompat.getColor(
                        holder.binding.root.context,
                        R.color.red
                    )
                )
            }
            Priority.MEDIUM -> holder.binding.priorityIndicator.setCardBackgroundColor(
                ContextCompat.getColor(
                    holder.binding.root.context,
                    R.color.yellow
                )
            )
            Priority.LOW -> holder.binding.priorityIndicator.setCardBackgroundColor(
                ContextCompat.getColor(
                    holder.binding.root.context,
                    R.color.green
                )
            )
        }
    }

    override fun getItemCount() = dataList.size

    fun setData(toDoData: List<ToDoData>) {
        this.dataList = toDoData
        notifyDataSetChanged()
    }

    class MyViewHolder(val binding: RowLayoutBinding) :
        RecyclerView.ViewHolder(binding.root)
}