package com.example.mibanca.domain.core

import com.example.mibanca.data.model.Card
import com.example.mibanca.data.model.Payment
import com.example.mibanca.data.model.User
import com.example.mibanca.domain.repositories.LocalRepository
import com.google.gson.Gson
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserProvider @Inject constructor(
    private val localRepository: LocalRepository,
    private val securityManager: SecurityManager
) {
    private val gson = Gson()
    private var _user: User? = null

    fun setUser (user: User) {
        _user = user
    }

    fun removeUser () {
        _user = null
    }

    fun getUser () : User? {
        return _user
    }

    fun addCard (card: Card) {
        _user?.cards?.add(card)
        updateInMemory()
    }

    fun removeCard (index: Int) {
        _user?.cards?.removeAt(index)
        updateInMemory()
    }

    fun savePayment (payment: Payment) {
        _user?.payments?.add(payment)
        updateInMemory()
    }

    private fun updateInMemory () {
        _user?.let {
            val encryptedData = securityManager.encryptData(gson.toJson(it), securityManager.decrypt(it.password))
            localRepository.saveData(it.user, encryptedData)
        }
    }
}