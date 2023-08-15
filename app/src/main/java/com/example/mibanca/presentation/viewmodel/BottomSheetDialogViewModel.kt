package com.example.mibanca.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mibanca.data.model.Card
import com.example.mibanca.domain.core.UserProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import java.lang.Exception
import java.util.*
import javax.inject.Inject

@HiltViewModel
class BottomSheetDialogViewModel @Inject constructor(
    private val userProvider: UserProvider
) : ViewModel() {

    var errorName = MutableLiveData<Boolean>()
    var errorCard = MutableLiveData<Boolean>()
    var errorExp = MutableLiveData<Boolean>()
    var added = MutableLiveData<Boolean>()

    fun addCard (cardName: String, cardNumber: String, cardExp: String) {
        var hasError = false

        if (!isValidName(cardName)) {
            errorName.postValue(true)
            hasError = true
        } else {
            errorName.postValue(false)
        }

        if (!isValidCard(cardNumber)) {
            errorCard.postValue(true)
            hasError = true
        } else {
            errorCard.postValue(false)
        }

        if (!isValidExp(cardExp)) {
            errorExp.postValue(true)
            hasError = true
        } else {
            errorExp.postValue(false)
        }

        if (!hasError){
            userProvider.addCard(Card(
                cardName,
                cardNumber,
                cardExp
            ))

            added.postValue(true)
        } else {
            added.postValue(false)
        }
    }

    fun isValidName (cardName: String) : Boolean {
        return cardName.isNotBlank() && cardName.isNotEmpty() && cardName.length > 3
    }

    fun isValidCard (card: String) : Boolean {
        return card.isNotEmpty() && card.isNotBlank() && card.replace(" ", "").length == 16
    }

    fun isValidExp (exp: String) : Boolean {
        try {
            val values = exp.split("/")
            var month = values[0].toInt()
            var year = values[1].toInt()

            val currentYear = Calendar.getInstance().get(Calendar.YEAR)
            val currentMonth = Calendar.getInstance().get(Calendar.MONTH)
            if (year < currentYear) {
                return false
            } else if (year == currentYear && month < currentMonth) {
                return false
            }

            return true
        } catch (e: Exception) {
            Log.d("session", e.toString())
            return false
        }
    }
}