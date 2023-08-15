package com.example.mibanca.domain.core

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.lang.Exception
import java.nio.charset.StandardCharsets
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec
import javax.inject.Inject

class SecurityManager @Inject constructor() {

    private val keyAlias = "mi_banca_keys"
    private val AES_ALGORITHM = "AES"

    init {
        createKeyIfNotExists()
    }

    private fun createKeyIfNotExists() {
        val keyStore = KeyStore.getInstance("AndroidKeyStore")
        keyStore.load(null)

        if (!keyStore.containsAlias(keyAlias)) {
            val keyGenParameterSpec = KeyGenParameterSpec.Builder(
                keyAlias,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            )
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setRandomizedEncryptionRequired(false) // Set to true for enhanced security
                .build()

            val keyGenerator = KeyGenerator.getInstance(
                KeyProperties.KEY_ALGORITHM_AES,
                "AndroidKeyStore"
            )
            keyGenerator.init(keyGenParameterSpec)
            keyGenerator.generateKey()
        }
    }

    fun encrypt(text: String): ByteArray {
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, getKey())
        val iv = cipher.iv
        return iv + cipher.doFinal(text.toByteArray(StandardCharsets.UTF_8))
    }

    fun decrypt(encryptedData: ByteArray): String {
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        val iv = encryptedData.sliceArray(0 until 12)
        val ciphertext = encryptedData.sliceArray(12 until encryptedData.size)
        cipher.init(Cipher.DECRYPT_MODE, getKey(), GCMParameterSpec(128, iv))

        return String(cipher.doFinal(ciphertext), StandardCharsets.UTF_8)
    }

    private fun getKey(): SecretKey {
        val keyStore = KeyStore.getInstance("AndroidKeyStore")
        keyStore.load(null)

        return keyStore.getKey(keyAlias, null) as SecretKey
    }

    /**
     * New implementations
     */

    fun encryptData(data: String, secret: String): String {
        try {
            val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
            val keyBytes = "secretkey-$secret".toByteArray(StandardCharsets.UTF_8)
            val secretKeySpec = SecretKeySpec(keyBytes, AES_ALGORITHM)
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec)
            val encryptedBytes = cipher.doFinal(data.toByteArray(StandardCharsets.UTF_8))
            return Base64.encodeToString(encryptedBytes, Base64.DEFAULT)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""
    }

    fun decryptData(encryptedData: String, secret: String): String {
        try {
            val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
            val keyBytes = "secretkey-$secret".toByteArray(StandardCharsets.UTF_8)
            val secretKeySpec = SecretKeySpec(keyBytes, AES_ALGORITHM)
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec)
            val decryptedBytes = cipher.doFinal(Base64.decode(encryptedData, Base64.DEFAULT))
            return String(decryptedBytes, StandardCharsets.UTF_8)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""
    }
}
