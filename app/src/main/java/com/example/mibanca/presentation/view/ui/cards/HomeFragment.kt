package com.example.mibanca.presentation.view.ui.cards

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mibanca.R
import com.example.mibanca.databinding.FragmentHomeBinding
import com.example.mibanca.presentation.view.BottomSheetDialog
import com.example.mibanca.presentation.view.MainActivity
import com.example.mibanca.presentation.view.ui.CardListAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment(), BottomSheetDialog.BottomSheetDismissListener {

    private lateinit var binding: FragmentHomeBinding

    private val homeViewModel: HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val layoutManager = LinearLayoutManager(requireContext())
        val adapter = CardListAdapter(homeViewModel.user.value?.cards ?: emptyList())
        binding.recyclerView.apply {
            this.layoutManager = layoutManager
            this.adapter = adapter
        }

        homeViewModel.user.observe(viewLifecycleOwner, Observer {
            if (it == null) {
                val intent = Intent(requireContext(), MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            } else {
                // show cards
                if (it.cards.size == 0) {
                    binding.tvEmpty.visibility = View.VISIBLE
                } else {
                    binding.tvEmpty.visibility = View.GONE
                    adapter.updateItems(it.cards)
                }
            }
        })

        binding.floatingActionButton.setOnClickListener {
            val navController = findNavController()
            val currentDestination = navController.currentDestination?.id
            if (currentDestination == R.id.navigation_home) {
                val bottomSheetDialog = BottomSheetDialog()
                bottomSheetDialog.setBottomSheetDismissListener(this)
                bottomSheetDialog.show(parentFragmentManager, "ModalBottomSheet")
            }
        }

    }

    override fun onBottomSheetDismiss() {
        homeViewModel.getUser()
    }
}