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

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        UserSession.init(this)
        
        if (UserSession.isLoggedIn()) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
            return
        }
        
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content)) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val inputLayoutEmail = findViewById<TextInputLayout>(R.id.inputLayoutEmail)
        val inputLayoutSenha = findViewById<TextInputLayout>(R.id.inputLayoutSenha)
        val editEmail = findViewById<EditText>(R.id.editEmail)
        val editSenha = findViewById<EditText>(R.id.editSenha)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val btnCadastro = findViewById<Button>(R.id.btnCadastro)

        btnLogin.setOnClickListener {
            inputLayoutEmail.error = null
            inputLayoutSenha.error = null

            val email = editEmail.text.toString()
            val senha = editSenha.text.toString()

            var hasError = false

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

            if (hasError) {
                return@setOnClickListener
            }

            val user = UsuarioRepository.autenticar(email, senha)
            if (user != null) {
                UserSession.login(this, user)
                
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "E-mail ou senha inválidos", Toast.LENGTH_SHORT).show()
            }
        }

        btnCadastro.setOnClickListener {
            val intent = Intent(this, CadastroActivity::class.java)
            startActivity(intent)
        }
    }

    private fun isValidEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}
