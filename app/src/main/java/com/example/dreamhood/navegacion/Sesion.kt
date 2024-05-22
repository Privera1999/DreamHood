package com.example.dreamhood.navegacion

import android.content.Context
import com.example.dreamhood.screens.ConnectSql
import java.sql.PreparedStatement
import java.sql.SQLException

// Clase para manejar las preferencias compartidas
object SessionManager {
    private const val PREF_NAME = "MyAppPreferences"
    private const val KEY_USERNAME = "username"
    private const val KEY_PASSWORD = "password"
    private const val KEY_BARRIO = "barrioId"

    // Función para guardar los datos de sesión
    fun saveSession(context: Context, username: String, password: String,idbarrio:Int) {
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(KEY_USERNAME, username)
        editor.putString(KEY_PASSWORD, password)
        editor.putInt(KEY_BARRIO,idbarrio)
        editor.apply()
    }

    // Función para obtener los datos de sesión guardados
    fun getSession(context: Context): Triple<String?, String?, Int?> {
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val username = sharedPreferences.getString(KEY_USERNAME, null)
        val password = sharedPreferences.getString(KEY_PASSWORD, null)
        val barrioId = sharedPreferences.getInt(KEY_BARRIO, 0)
        return Triple(username, password,barrioId)
    }

    // Función para borrar los datos de sesión guardados
    fun clearSession(context: Context) {
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()
    }
    }