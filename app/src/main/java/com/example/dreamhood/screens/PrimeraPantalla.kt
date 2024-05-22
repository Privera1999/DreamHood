package com.example.dreamhood.screens

import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.dreamhood.R
import com.example.dreamhood.navegacion.AppScreens
import com.example.dreamhood.navegacion.SessionManager
import java.sql.DriverManager
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException
import java.sql.Types



@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun primeraPantlla(navController: NavController){
    val context = LocalContext.current
    //SessionManager.clearSession(context)
    Scaffold {
        val (username, password) = SessionManager.getSession(context)
        if (username != null && password != null) {
            navController.navigate(route = AppScreens.feed.route)
        }
        else{
            login(navController)
        }


    }
}

@Composable
fun login(navController: NavController){

    var email by remember { mutableStateOf("") }
    var pass by remember { mutableStateOf("") }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 125.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            painter = painterResource(id = R.drawable.logodm),
            contentDescription = "Logo Dream Hood",
            modifier = Modifier
                .requiredWidth(width = 280.dp)
                .requiredHeight(height = 200.dp))

        Spacer(modifier = Modifier.height(35.dp))

        TextField(
            value = email,
            onValueChange = { email = it },
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Email
            ),
            label = { Text("Correo Electrónico") },
            leadingIcon = { Icon(Icons.Filled.AccountCircle , contentDescription = null )}
        )

        Spacer(modifier = Modifier.height(35.dp))

        TextField(
            value = pass,
            onValueChange = { pass = it },
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Password
            ),
            visualTransformation = PasswordVisualTransformation(),
            label = { Text("Contraseña") },
            leadingIcon = { Icon(Icons.Filled.Lock , contentDescription = null )}
        )

        Botones(navController,email,pass)

    }
}

@Composable
fun Botones(navController: NavController, email:String,pass:String) {

    var showErrorDialog by remember { mutableStateOf(false) }
    var mensaje by remember { mutableStateOf("") }
    var TituloFlotante by remember { mutableStateOf("") }
    val context = LocalContext.current

    if (showErrorDialog) {
        AlertDialog(
            onDismissRequest = { showErrorDialog = false },
            confirmButton = {
                Button(onClick = { showErrorDialog = false }) {
                    Text("Aceptar")
                }
            },
            title = { Text(TituloFlotante) },
            text = { Text(mensaje) }
        )

    }


    Row(
        modifier = Modifier.padding(top = 35.dp),
        horizontalArrangement = Arrangement.spacedBy(35.dp) // Espacio entre los botones
    ) {
        Button(
            onClick = { navController.navigate(route = AppScreens.SegundaPantalla.route) },
            modifier = Modifier
                .width(120.dp)

        ) {
            Text("Registrarse")
        }
        Button(
            onClick = {
                if(iniciarSesion(email,pass)){
                    val barrioid = obtenerIDBarrio(email)
                    println("ID del barrio obtenido: $barrioid")
                    SessionManager.saveSession(context, email, pass,barrioid)
                    println("Datos de sesión obtenidos: Username=$email, Password=$pass, BarrioId=$barrioid")
                    navController.navigate(route = AppScreens.feed.route)
                }else{
                    TituloFlotante = "Error"
                    mensaje = "Usuario y/o Contraseña Incorrectos"
                    showErrorDialog = true
                }
                },
            modifier = Modifier
                .width(120.dp)

        ) {
            Text("Entrar")
        }
    }
}


fun iniciarSesion(email: String, pass: String): Boolean {
    var contrasennaBD: String? = null
    val connectSql = ConnectSql()

    try {
        val statement: PreparedStatement = connectSql.dbConn()?.prepareStatement("SELECT contrasena FROM usuarios WHERE correo = ?")!!
        statement.setString(1, email)
        val resultSet = statement.executeQuery()
        if (resultSet.next()) {
            contrasennaBD = resultSet.getString("contrasena")
        }
    } catch (ex: SQLException) {
        // Manejar la excepción adecuadamente, por ejemplo, registrando o devolviendo false
        ex.printStackTrace()
        return false
    }finally {
        connectSql.close()
    }

    return contrasennaBD == pass
}

fun obtenerIDBarrio(correo : String): Int{
    var barrio_id: Int = 0
    val connectSql = ConnectSql()

    try {
        val statement: PreparedStatement = connectSql.dbConn()?.prepareStatement("SELECT barrio_id FROM usuarios WHERE correo = ?")!!
        statement.setString(1, correo)
        val resultSet = statement.executeQuery()
        if (resultSet.next()) {
            barrio_id = resultSet.getInt("barrio_id")
        }
    } catch (ex: SQLException) {
        ex.printStackTrace()
    }finally {
        connectSql.close()
    }
    return barrio_id

}


























