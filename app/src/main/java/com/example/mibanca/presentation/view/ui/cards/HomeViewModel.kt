package com.example.mibanca.presentation.view.ui.cards

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mibanca.data.model.User
import com.example.mibanca.domain.core.UserProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val userProvider: UserProvider
) : ViewModel() {

    var user: MutableLiveData<User?> = MutableLiveData<User?>()

    init {
        getUser()
    }

    fun getUser () {
        user.postValue(userProvider.getUser())
    }


}