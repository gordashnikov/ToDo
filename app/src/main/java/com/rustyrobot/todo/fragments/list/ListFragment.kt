package com.rustyrobot.todo.fragments.list

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.rustyrobot.todo.R
import com.rustyrobot.todo.data.viewmodel.ToDoViewModel
import com.rustyrobot.todo.databinding.FragmentListBinding
import com.rustyrobot.todo.fragments.SharedViewModel

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

        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(requireActivity())
        viewModel.getAllData.observe(viewLifecycleOwner) {
            sharedViewModel.checkIfDatabaseEmpty(it)
            adapter.setData(it)
        }

        sharedViewModel.emptyDatabase.observe(viewLifecycleOwner) {
            showEmptyDatabaseViews(it)
        }

        binding.floatingActionButton.setOnClickListener {
            findNavController().navigate(R.id.action_listFragment_to_addFragment)
        }

        setHasOptionsMenu(true)

        return binding.root
    }

    private fun showEmptyDatabaseViews(emptyDatabase: Boolean) {
        if (emptyDatabase) {
            binding.imageViewNoData.visibility = View.VISIBLE
            binding.textViewNoData.visibility = View.VISIBLE
        } else {
            binding.imageViewNoData.visibility = View.INVISIBLE
            binding.textViewNoData.visibility = View.INVISIBLE
        }
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
            Toast.makeText(requireContext(), "All items deleted", Toast.LENGTH_LONG)
                .show()
        }
        builder.setNegativeButton("No") { _, _ -> }
        builder.setTitle("Delete everything?")
        builder.setMessage("Are you sure you want to delete everything?")
        builder.create().show()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}