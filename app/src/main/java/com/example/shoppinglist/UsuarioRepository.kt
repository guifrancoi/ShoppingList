package com.example.shoppinglist

object UsuarioRepository {
    private val usuarios = mutableListOf<Pair<String, String>>() // email, senha

    init {
        usuarios.add("teste@teste.com" to "1234")
    }

    fun cadastrar(email: String, senha: String): Boolean {
        if (usuarios.any { it.first == email }) {
            return false // já existe usuário com esse email
        }
        usuarios.add(email to senha)
        return true
    }

    fun autenticar(email: String, senha: String): Boolean {
        return usuarios.any { it.first == email && it.second == senha }
    }
}
