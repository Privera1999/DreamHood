package com.example.dreamhood.screens

import android.os.StrictMode
import android.util.Log
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

class ConnectSql {
    private val ip = "192.168.1.5:60078"
    private val db = "mejora_tu_barrio"
    private val username = "pablo"
    private val password = "1234567890q"

    private var conn: Connection? = null

    fun dbConn(): Connection? {
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        val connString: String
        try {
            Class.forName("net.sourceforge.jtds.jdbc.Driver").newInstance()
            connString = "jdbc:jtds:sqlserver://$ip;databaseName=$db;user=$username;password=$password"
            conn = DriverManager.getConnection(connString)
        } catch (ex: SQLException) {
            Log.e("Error: ", ex.message!!)
        } catch (ex1: ClassNotFoundException) {
            Log.e("Error: ", ex1.message!!)
        } catch (ex2: Exception) {
            Log.e("Error: ", ex2.message!!)
        }
        return conn
    }

    fun close() {
        try {
            conn?.close()
        } catch (ex: SQLException) {
            Log.e("Error al cerrar la conexión: ", ex.message!!)
        }
    }
}