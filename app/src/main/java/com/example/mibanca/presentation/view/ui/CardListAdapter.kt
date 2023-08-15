package com.example.mibanca.presentation.view.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mibanca.R
import com.example.mibanca.data.model.Card
import com.example.mibanca.databinding.CardLayoutBinding

class CardListAdapter(private var itemList: List<Card>) : RecyclerView.Adapter<CardListAdapter.ViewHolder>() {

    class ViewHolder(private val binding: CardLayoutBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind (card: Card) {
            binding.tvName.text = card.name
            binding.tvExp.text = card.expDate
            binding.tvNumber.text = card.cardNumber
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = CardLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(itemList[position])
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    fun updateItems (itemList: List<Card>) {
        this.itemList = itemList
        notifyDataSetChanged()
    }
}
