package com.example.shoppinglist.session

import android.content.Context
import android.content.SharedPreferences
import com.example.shoppinglist.models.User

object UserSession {
    private const val PREFS_NAME = "shopping_list_prefs"
    private const val KEY_USER_ID = "user_id"
    private const val KEY_USER_NAME = "user_name"
    private const val KEY_USER_EMAIL = "user_email"
    
    private var currentUser: User? = null
    
    fun init(context: Context) {
        val prefs = getPrefs(context)
        val userId = prefs.getString(KEY_USER_ID, null)
        val userName = prefs.getString(KEY_USER_NAME, null)
        val userEmail = prefs.getString(KEY_USER_EMAIL, null)
        
        if (userId != null && userName != null && userEmail != null) {
            currentUser = User(userId, userName, userEmail)
        }
    }
    
    fun login(context: Context, user: User) {
        currentUser = user
        val prefs = getPrefs(context)
        prefs.edit().apply {
            putString(KEY_USER_ID, user.id)
            putString(KEY_USER_NAME, user.nome)
            putString(KEY_USER_EMAIL, user.email)
            apply()
        }
    }
    
    fun logout(context: Context) {
        currentUser = null
        val prefs = getPrefs(context)
        prefs.edit().clear().apply()
    }
    
    fun getCurrentUser(): User? = currentUser
    
    fun isLoggedIn(): Boolean = currentUser != null
    
    fun getCurrentUserId(): String? = currentUser?.id
    
    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }
}
