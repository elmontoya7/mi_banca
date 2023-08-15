package com.example.mibanca.presentation.view.ui.pay

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mibanca.data.model.Payment
import com.example.mibanca.data.model.User
import com.example.mibanca.domain.core.LocationProvider
import com.example.mibanca.domain.core.UserProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.*
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val userProvider: UserProvider,
    private val locationProvider: LocationProvider
) : ViewModel() {

    var user: MutableLiveData<User?> = MutableLiveData<User?>()
    var created = MutableLiveData<Boolean>()

    init {
        getUser()
    }

    fun getUser () {
        user.postValue(userProvider.getUser())
    }

    var errorName = MutableLiveData<Boolean>()
    var errorCard = MutableLiveData<Boolean>()
    var errorSubject = MutableLiveData<Boolean>()

    fun createPayment (cardName: String, cardNumber: String, subject: String, fromCard: String) {
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

        if (!isValidSubject(subject)) {
            errorSubject.postValue(true)
            hasError = true
        } else {
            errorSubject.postValue(false)
        }

        if (!hasError){
            userProvider.savePayment(
                Payment(
                    Calendar.getInstance().time,
                     "${locationProvider.latitude},${locationProvider.longitude}",
                    "****" + fromCard.removeRange(0, fromCard.length - 4),
                    "****" + cardNumber.removeRange(0, cardNumber.length - 4),
                    cardName,
                    subject
                )
            )

            created.postValue(true)
        }
    }

    fun isValidName (cardName: String) : Boolean {
        return cardName.isNotBlank() && cardName.isNotEmpty() && cardName.length > 3
    }

    fun isValidCard (card: String) : Boolean {
        return card.isNotEmpty() && card.isNotBlank() && card.replace(" ", "").length == 16
    }

    fun isValidSubject (subject: String) : Boolean {
        return subject.isNotBlank() && subject.isNotEmpty()
    }

    fun resetCreated () {
        created.postValue(false)
    }
}