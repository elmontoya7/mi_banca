package com.example.mibanca.presentation.view

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.mibanca.databinding.FragmentBottomSheetDialogBinding
import com.example.mibanca.domain.core.UserProvider
import com.example.mibanca.presentation.viewmodel.BottomSheetDialogViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.redmadrobot.inputmask.MaskedTextChangedListener
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class BottomSheetDialog @Inject constructor() : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentBottomSheetDialogBinding
    private val bottomSheetDialogViewModel: BottomSheetDialogViewModel by viewModels()

    interface BottomSheetDismissListener {
        fun onBottomSheetDismiss()
    }

    private var dismissListener: BottomSheetDismissListener? = null

    fun setBottomSheetDismissListener (listener: BottomSheetDismissListener) {
        dismissListener = listener
    }

    @Inject
    lateinit var userProvider: UserProvider

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBottomSheetDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val cardListener = MaskedTextChangedListener(
            "[0000] [0000] [0000] [0000]",
            true,
            binding.etCardNumber,
            null,
            null
        )
        binding.etCardNumber.addTextChangedListener(cardListener)

        val expListener = MaskedTextChangedListener(
            "[00]/[0000]",
            true,
            binding.etCardExp,
            null,
            null
        )
        binding.etCardExp.addTextChangedListener(expListener)

        binding.buttonSubmit.setOnClickListener {
            bottomSheetDialogViewModel.addCard(
                binding.etCardName.text.toString(),
                binding.etCardNumber.text.toString(),
                binding.etCardExp.text.toString()
            )
        }

        bottomSheetDialogViewModel.errorName.observe(viewLifecycleOwner, Observer {
            binding.etCardName.error = if (it) "Nombre no válido (min. 4 caracteres)" else null
        })

        bottomSheetDialogViewModel.errorCard.observe(viewLifecycleOwner, Observer {
            binding.etCardNumber.error = if (it) "Número no válido (16 dígitos)" else null
        })

        bottomSheetDialogViewModel.errorExp.observe(viewLifecycleOwner, Observer {
            binding.etCardExp.error = if (it) "Fecha no válida" else null
        })

        bottomSheetDialogViewModel.added.observe(viewLifecycleOwner, Observer {
            if (it) {
                dismiss()
            } else {
                Toast.makeText(requireContext(), "Verifica los datos", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)

        dismissListener?.onBottomSheetDismiss()
    }
}