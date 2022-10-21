package com.rustyrobot.todo.fragments.add

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.rustyrobot.todo.R
import com.rustyrobot.todo.data.models.ToDoData
import com.rustyrobot.todo.data.viewmodel.ToDoViewModel
import com.rustyrobot.todo.databinding.FragmentAddBinding
import com.rustyrobot.todo.fragments.SharedViewModel

class AddFragment : Fragment() {

    private var _binding: FragmentAddBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ToDoViewModel by viewModels()
    private val sharedViewModel: SharedViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddBinding.inflate(inflater, container, false)

        setHasOptionsMenu(true)
        binding.prioritiesSpinner.onItemSelectedListener = sharedViewModel.listener

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.add_fragment_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_add) {
            insertDataToDatabase()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun insertDataToDatabase() {
        val title = binding.titleEt.text.toString()
        val priority = binding.prioritiesSpinner.selectedItem.toString()
        val description = binding.descriptionEt.text.toString()

        val validation = sharedViewModel.verifyDataFromUser(title, description)
        if (validation) {
            val newItem = ToDoData(0, title, sharedViewModel.parsePriority(priority), description)
            viewModel.insertData(newItem)
            val snackBar = Snackbar.make(binding.root, "${newItem.title} saved", Snackbar.LENGTH_LONG)
            snackBar.show()
            findNavController().navigate(R.id.action_addFragment_to_listFragment)
        } else {
            val snackBar = Snackbar.make(binding.root, "Fields should not be blank", Snackbar.LENGTH_LONG)
            snackBar.show()
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}