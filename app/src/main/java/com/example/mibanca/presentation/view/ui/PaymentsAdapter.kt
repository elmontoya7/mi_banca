package com.example.mibanca.presentation.view.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mibanca.data.model.Payment
import com.example.mibanca.databinding.PaymentItemBinding
import java.text.SimpleDateFormat
import java.util.*

class PaymentsAdapter(private var itemList: List<Payment>) : RecyclerView.Adapter<PaymentsAdapter.ViewHolder>() {

    class ViewHolder(private val binding: PaymentItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind (item: Payment) {
            binding.tvDate.text = SimpleDateFormat("dd/MM/yyyy-HH:mm:ss", Locale.getDefault()).format(item.date)
            binding.tvName.text = item.toName
            binding.tvFrom.text = item.fromCard
            binding.tvTo.text = item.toCard
            binding.tvLocation.text = item.location
            binding.tvSubject.text = item.subject
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = PaymentItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(itemList[position])
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    fun updateItems (itemList: List<Payment>) {
        this.itemList = itemList
        notifyDataSetChanged()
    }
}