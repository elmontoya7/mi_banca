package com.example.mibanca.presentation.view.ui.payments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mibanca.databinding.FragmentNotificationsBinding
import com.example.mibanca.presentation.view.MainActivity
import com.example.mibanca.presentation.view.ui.PaymentsAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NotificationsFragment : Fragment() {

    private var _binding: FragmentNotificationsBinding? = null
    private val notificationsViewModel: NotificationsViewModel by viewModels()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val notificationsViewModel =
            ViewModelProvider(this).get(NotificationsViewModel::class.java)

        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val layoutManager = LinearLayoutManager(requireContext())
        val adapter = PaymentsAdapter(notificationsViewModel.user.value?.payments ?: emptyList())
        binding.recyclerView.apply {
            this.layoutManager = layoutManager
            this.adapter = adapter
            this.addItemDecoration(DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL))
        }

        notificationsViewModel.user.observe(viewLifecycleOwner, Observer {
            if (it == null) {
                val intent = Intent(requireContext(), MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            } else {
                // show payments
                if (it.payments.size == 0) {
                    binding.tvEmpty.visibility = View.VISIBLE
                } else {
                    adapter.updateItems(it.payments)
                    binding.tvEmpty.visibility = View.GONE
                }
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}