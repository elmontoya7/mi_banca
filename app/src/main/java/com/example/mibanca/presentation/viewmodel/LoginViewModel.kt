package com.example.mibanca.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mibanca.data.model.User
import com.example.mibanca.domain.core.SecurityManager
import com.example.mibanca.domain.core.UserProvider
import com.example.mibanca.domain.repositories.LocalRepository
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val localRepository: LocalRepository,
    private val securityManager: SecurityManager,
    private val userProvider: UserProvider
    ) : ViewModel() {

    enum class LoginState {
        LOGIN,
        ERROR,
        REGISTERED
    }

    private val gson = Gson()
    val loginState: MutableLiveData<LoginState> = MutableLiveData<LoginState>()

    fun loginOrCreateUser (user: String, password: String) {
        viewModelScope.async {
            val encryptedData: String? = localRepository.readData(user)

            if (encryptedData != null) {
                try {
                    val decryptedData: String = securityManager.decryptData(encryptedData, password)
                    val data: User = gson.fromJson(decryptedData, User::class.java)

                    userProvider.setUser(data)
                    loginState.postValue(LoginState.LOGIN)
                } catch (e: Exception) {
                    loginState.postValue(LoginState.ERROR)
                }
            } else {
                // create a new user in shared preferences
                val encryptedPassword = securityManager.encrypt(password)
                val createdUser = User(user, encryptedPassword, mutableListOf(), mutableListOf())
                val encryptedData = securityManager.encryptData(gson.toJson(createdUser), password)

                localRepository.saveData(user, encryptedData)
                userProvider.setUser(createdUser)
                loginState.postValue(LoginState.REGISTERED)
            }
        }
    }

    fun validateInput (text: String) : Boolean {
        // validate user and password comply with the next rules:
        // - alphanumeric
        // - 6 characters length
        if (text.trim().isEmpty() || !text.matches(Regex("^[a-zA-Z0-9]*$")) || text.length != 6) {
            return false
        }

        return true
    }
}