package com.example.shoppinglist

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.shoppinglist.repository.UsuarioRepository
import com.example.shoppinglist.session.UserSession
import com.google.android.material.textfield.TextInputLayout

class CadastroActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_cadastro)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content)) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val inputLayoutNome = findViewById<TextInputLayout>(R.id.inputLayoutNome)
        val inputLayoutEmail = findViewById<TextInputLayout>(R.id.inputLayoutEmail)
        val inputLayoutSenha = findViewById<TextInputLayout>(R.id.inputLayoutSenha)
        val inputLayoutConfirmarSenha = findViewById<TextInputLayout>(R.id.inputLayoutConfirmarSenha)
        val editNome = findViewById<EditText>(R.id.editNome)
        val editEmail = findViewById<EditText>(R.id.editEmailCadastro)
        val editSenha = findViewById<EditText>(R.id.editSenhaCadastro)
        val editConfirmar = findViewById<EditText>(R.id.editConfirmarSenha)
        val btnFinalizar = findViewById<Button>(R.id.btnFinalizarCadastro)

        btnFinalizar.setOnClickListener {
            inputLayoutNome.error = null
            inputLayoutEmail.error = null
            inputLayoutSenha.error = null
            inputLayoutConfirmarSenha.error = null

            val nome = editNome.text.toString()
            val email = editEmail.text.toString()
            val senha = editSenha.text.toString()
            val confirmar = editConfirmar.text.toString()

            var hasError = false

            if (nome.isEmpty()) {
                inputLayoutNome.error = "Campo obrigatório"
                hasError = true
            }

            if (email.isEmpty()) {
                inputLayoutEmail.error = "Campo obrigatório"
                hasError = true
            } else if (!isValidEmail(email)) {
                inputLayoutEmail.error = "E-mail inválido"
                hasError = true
            }

            if (senha.isEmpty()) {
                inputLayoutSenha.error = "Campo obrigatório"
                hasError = true
            }

            if (confirmar.isEmpty()) {
                inputLayoutConfirmarSenha.error = "Campo obrigatório"
                hasError = true
            } else if (senha != confirmar) {
                inputLayoutConfirmarSenha.error = "As senhas não coincidem"
                hasError = true
            }

            if (hasError) {
                return@setOnClickListener
            }

            val newUser = UsuarioRepository.cadastrar(nome, email, senha)

            if (newUser != null) {
                UserSession.login(this, newUser)
                
                Toast.makeText(this, "Usuário cadastrado com sucesso!", Toast.LENGTH_SHORT).show()
                
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "E-mail já cadastrado!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun isValidEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}
