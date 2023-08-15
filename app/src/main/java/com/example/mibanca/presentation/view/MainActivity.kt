package com.example.mibanca.presentation.view

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.mibanca.databinding.ActivityMainBinding
import com.example.mibanca.presentation.viewmodel.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val loginViewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonSubmit.setOnClickListener {
            val user = binding.etUser.text.toString()
            val pass = binding.etPassword.text.toString()

            val validUser = loginViewModel.validateInput(user)
            val validPass = loginViewModel.validateInput(pass)

            binding.tilUser.error = if (validUser) null else "6 caracteres, alfanumérico"
            binding.tilPassword.error = if (validPass) null else "6 caracateres, alfanumérico"

            if (validUser && validPass) {
                loginViewModel.loginOrCreateUser(user, pass)
            }
        }

        loginViewModel.loginState.observe(this, Observer {
            when (it) {
                LoginViewModel.LoginState.LOGIN -> {
                    // navigate to home
                    Toast.makeText(this, "¡Bienvenid@ de nuevo!", Toast.LENGTH_LONG).show()
                    val intent = Intent(this, Home::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                }

                LoginViewModel.LoginState.REGISTERED -> {
                    // notify registered
                    // navigate to home
                    Toast.makeText(this, "¡Te has registrado con éxito!", Toast.LENGTH_LONG).show()
                    val intent = Intent(this, Home::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                }

                LoginViewModel.LoginState.ERROR -> Toast
                    .makeText(this, "Usuario y/o contraseña no válidos.", Toast.LENGTH_LONG)
                    .show()

                else -> {}
            }
        })
    }
}
