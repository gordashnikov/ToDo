package com.rustyrobot.todo.fragments.list

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.rustyrobot.todo.R
import com.rustyrobot.todo.data.models.ToDoData
import com.rustyrobot.todo.data.viewmodel.ToDoViewModel
import com.rustyrobot.todo.databinding.FragmentListBinding
import com.rustyrobot.todo.fragments.SharedViewModel
import com.rustyrobot.todo.fragments.list.adapter.ListAdapter
import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator
import jp.wasabeef.recyclerview.animators.SlideInRightAnimator
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator

class ListFragment : Fragment() {

    private var _binding: FragmentListBinding? = null
    private val binding get() = _binding!!

    private val adapter: ListAdapter by lazy { ListAdapter() }

    private val viewModel: ToDoViewModel by viewModels()
    private val sharedViewModel: SharedViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.sharedViewModel = sharedViewModel

        setupRecyclerView()

        viewModel.getAllData.observe(viewLifecycleOwner) {
            sharedViewModel.checkIfDatabaseEmpty(it)
            adapter.setData(it)
        }
        setHasOptionsMenu(true)

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.list_fragment_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_search -> {

            }
            R.id.menu_delete_all -> {
                confirmAllItemsRemoval()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun confirmAllItemsRemoval() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setPositiveButton("Yes") { _, _ ->
            viewModel.deleteAll()
            val snackBar = Snackbar.make(binding.root, "Items were deleted", Snackbar.LENGTH_LONG)
            snackBar.show()
        }
        builder.setNegativeButton("No") { _, _ -> }
        builder.setTitle("Delete everything?")
        builder.setMessage("Are you sure you want to delete everything?")
        builder.create().show()
    }

    private fun setupRecyclerView() {
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(requireActivity())
        binding.recyclerView.itemAnimator = SlideInUpAnimator().apply {
            addDuration = 300
        }

        swipeToDelete(binding.recyclerView)
    }

    private fun swipeToDelete(recyclerView: RecyclerView) {
        val swipeToDeleteCallBack = object : SwipeToDelete() {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val deletedItem = adapter.dataList[viewHolder.adapterPosition]
                viewModel.deleteItem(deletedItem)
                adapter.notifyItemRemoved(viewHolder.adapterPosition)
                restoreDeletedItem(viewHolder.itemView, deletedItem, viewHolder.adapterPosition)
            }
        }
        val itemTouchHelper = ItemTouchHelper(swipeToDeleteCallBack)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    private fun restoreDeletedItem(view: View, deletedItem: ToDoData, position: Int) {
        val snackBar = Snackbar.make(view, "${deletedItem.title} deleted", Snackbar.LENGTH_LONG)
        snackBar.setAction("Undo") {
            viewModel.insertData(deletedItem)
            adapter.notifyItemChanged(position)
        }
        snackBar.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}