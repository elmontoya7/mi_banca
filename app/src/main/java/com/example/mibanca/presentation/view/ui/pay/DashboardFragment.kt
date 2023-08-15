package com.example.mibanca.presentation.view.ui.pay

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.SnapHelper
import com.example.mibanca.R
import com.example.mibanca.databinding.FragmentDashboardBinding
import com.example.mibanca.presentation.view.Home
import com.example.mibanca.presentation.view.MainActivity
import com.example.mibanca.presentation.view.ui.CardListAdapter
import com.example.mibanca.presentation.view.ui.SnappingLayoutManager
import com.redmadrobot.inputmask.MaskedTextChangedListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val dashboardViewModel: DashboardViewModel by viewModels()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val dashboardViewModel =
            ViewModelProvider(this).get(DashboardViewModel::class.java)

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val layoutManager = SnappingLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        layoutManager.orientation = LinearLayoutManager.HORIZONTAL
        val adapter = CardListAdapter(dashboardViewModel.user.value?.cards ?: emptyList())
        binding.recyclerView.apply {
            this.layoutManager = layoutManager
            this.adapter = adapter
        }

        // Attach LinearSnapHelper for snapping behavior
        val snapHelper: SnapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(binding.recyclerView)

        dashboardViewModel.user.observe(viewLifecycleOwner, Observer {
            if (it == null) {
                val intent = Intent(requireContext(), MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            } else {
                // show cards
                if (it.cards.size == 0) {
                    binding.cvEmpty.visibility = View.VISIBLE
                    binding.buttonSubmit.isEnabled = false
                } else {
                    adapter.updateItems(it.cards)
                }
            }
        })

        val cardListener = MaskedTextChangedListener(
            "[0000] [0000] [0000] [0000]",
            true,
            binding.etCardNumber,
            null,
            null
        )
        binding.etCardNumber.addTextChangedListener(cardListener)

        dashboardViewModel.errorCard.observe(viewLifecycleOwner, Observer {
            binding.tilCardNumber.error = if (it) "Tarjeta no válida (16 dígitos)" else null
        })

        dashboardViewModel.errorName.observe(viewLifecycleOwner, Observer {
            binding.tilCardName.error = if (it) "Nombre no válido (min. 4 caracteres)" else null
        })

        dashboardViewModel.errorSubject.observe(viewLifecycleOwner, Observer {
            binding.tilSubject.error =  if (it) "Ingresa un motivo" else null
        })

        binding.buttonSubmit.setOnClickListener {
            dashboardViewModel.user.value?.let {
                if (it.cards.size == 0) {
                    Toast.makeText(requireContext(), "Necesitas agregar una tarjeta a tu cuenta.", Toast.LENGTH_LONG).show()
                } else {
                    dashboardViewModel.createPayment(
                        binding.etCardName.text.toString(),
                        binding.etCardNumber.text.toString(),
                        binding.etSubject.text.toString(),
                        it.cards[layoutManager.findFirstVisibleItemPosition()].cardNumber
                    )
                }
            }
        }

        dashboardViewModel.created.observe(viewLifecycleOwner, Observer {
            if (it) {
                dashboardViewModel.resetCreated()

                binding.etCardName.text = null
                binding.etCardNumber.text = null
                binding.etSubject.text = null

                Toast.makeText(requireContext(), "Se creo el pago con éxito.", Toast.LENGTH_LONG).show()

                val activity = requireActivity() as Home
                activity.navigateTo(R.id.navigation_notifications)
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}