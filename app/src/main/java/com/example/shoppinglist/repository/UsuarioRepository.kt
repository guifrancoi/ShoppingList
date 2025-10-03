package com.example.shoppinglist.repository

import com.example.shoppinglist.models.User
import java.util.UUID

object UsuarioRepository {
    private data class UserCredentials(
        val user: User,
        val senha: String
    )
    
    private val usuarios = mutableListOf<UserCredentials>()

    init {
        usuarios.add(
            UserCredentials(
                user = User(
                    id = UUID.randomUUID().toString(),
                    nome = "Usuário Teste",
                    email = "teste@teste.com"
                ),
                senha = "1234"
            )
        )
    }

    fun cadastrar(nome: String, email: String, senha: String): User? {
        if (usuarios.any { it.user.email == email }) {
            return null
        }
        val newUser = User(
            id = UUID.randomUUID().toString(),
            nome = nome,
            email = email
        )
        usuarios.add(UserCredentials(newUser, senha))
        return newUser
    }

    fun autenticar(email: String, senha: String): User? {
        return usuarios.find { it.user.email == email && it.senha == senha }?.user
    }
}