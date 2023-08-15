package com.example.mibanca.domain.repositories

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class LocalRepository @Inject constructor(
    @ApplicationContext context: Context) {

    private var sharedPref: SharedPreferences = EncryptedSharedPreferences.create(
        context,
        "encrypted_file.txt",
        MasterKey.Builder(context).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build(),
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun saveData (key: String, value: String) {
        sharedPref.edit().apply {
            putString(key, value)
            apply()
        }
    }

    fun readData(key: String): String? {
        return sharedPref.getString(key, null)
    }
}