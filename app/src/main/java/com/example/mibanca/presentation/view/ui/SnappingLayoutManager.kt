package com.example.mibanca.presentation.view.ui

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class SnappingLayoutManager(context: Context, orientation: Int, reverseLayout: Boolean = false) :
    LinearLayoutManager(context, orientation, reverseLayout) {

    override fun checkLayoutParams(lp: RecyclerView.LayoutParams?): Boolean {
        lp?.width = width // Set the item width to match parent width
        return true
    }
}