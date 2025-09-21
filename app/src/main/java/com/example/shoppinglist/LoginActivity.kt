package com.example.shoppinglist

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val editEmail = findViewById<EditText>(R.id.editEmail)
        val editSenha = findViewById<EditText>(R.id.editSenha)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val btnCadastro = findViewById<Button>(R.id.btnCadastro)

        btnLogin.setOnClickListener {
            val email = editEmail.text.toString()
            val senha = editSenha.text.toString()

            // Exemplo bem simples para simular login
            if (email == "teste@teste.com" && senha == "1234") {
                // Login correto → vai para MainActivity
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish() // fecha a LoginActivity para não voltar nela ao apertar "voltar"
            } else {
                Toast.makeText(this, "E-mail ou senha inválidos", Toast.LENGTH_SHORT).show()
            }
        }

        btnCadastro.setOnClickListener {
            // Vai para tela de cadastro
            val intent = Intent(this, CadastroActivity::class.java)
            startActivity(intent)
        }
    }
}

