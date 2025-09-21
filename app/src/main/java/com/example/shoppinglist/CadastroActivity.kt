package com.example.shoppinglist

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class CadastroActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cadastro)

        val editNome = findViewById<EditText>(R.id.editNome)
        val editEmail = findViewById<EditText>(R.id.editEmailCadastro)
        val editSenha = findViewById<EditText>(R.id.editSenhaCadastro)
        val editConfirmar = findViewById<EditText>(R.id.editConfirmarSenha)
        val btnFinalizar = findViewById<Button>(R.id.btnFinalizarCadastro)

        btnFinalizar.setOnClickListener {
            val nome = editNome.text.toString()
            val email = editEmail.text.toString()
            val senha = editSenha.text.toString()
            val confirmar = editConfirmar.text.toString()

            if (senha == confirmar && email.isNotEmpty() && nome.isNotEmpty()) {
                // Aqui salva no banco/disco
                Toast.makeText(this, "Usuário cadastrado com sucesso!", Toast.LENGTH_SHORT).show()
                finish() // volta para LoginActivity
            } else {
                Toast.makeText(this, "Preencha corretamente os campos", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

