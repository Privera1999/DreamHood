package com.example.dreamhood.screens

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.compose.primaryLight
import com.example.dreamhood.navegacion.AppScreens
import com.example.dreamhood.navegacion.SessionManager
import java.sql.PreparedStatement
import java.sql.ResultSet

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun Perfil(navController: NavController){
    Scaffold {
        formularioPerfil(navController)

    }
}

data class ListaUsuario(
    val id : Int,
    val nombre: String,
    val correo: String,
    val contrasenna: String,
    var barrio_id: Int,
    var archivo_pdf: ByteArray,
    val avatar: ByteArray,
)




@Composable
fun formularioPerfil(navController: NavController) {
    val context = LocalContext.current
    var nuevapass by remember { mutableStateOf("") }
    var confirmarpass by remember { mutableStateOf("") }
    var pdfFileUri by remember { mutableStateOf<Uri?>(null) }
    var foto by remember { mutableStateOf<ByteArray?>(null) }
    var datosUsuario by remember { mutableStateOf<ListaUsuario?>(null) }

    // Obtener los datos del usuario
    datosUsuario = sacarDatos(context)


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 85.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {

        datosUsuario?.let { usuario ->
            // Mostrar la imagen del avatar
            ImagenDesdeBytes(usuario.avatar, true)

        }

        Spacer(modifier = Modifier.height(15.dp))

        TextField(
            value = nuevapass,
            onValueChange = { nuevapass = it },
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Password
            ),
            label = { Text("Nueva Contraseña") },
            leadingIcon = { Icon(Icons.Filled.Lock, contentDescription = null) }
        )

        Spacer(modifier = Modifier.height(15.dp))

        TextField(
            value = confirmarpass,
            onValueChange = { confirmarpass = it },
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Password
            ),
            label = { Text("Confirmar Nueva Contraseña") },
            leadingIcon = { Icon(Icons.Filled.Lock, contentDescription = null) }
        )


        Spacer(modifier = Modifier.height(15.dp))

        SeleccionaBarrio()

    }
}


fun sacarDatos(context: Context): ListaUsuario? {
    val connectSql = ConnectSql()
    try {
        val (username, password, barrioId) = SessionManager.getSession(context)

        if (barrioId != null) {
            val consulta: PreparedStatement = connectSql.dbConn()?.prepareStatement(
                "SELECT id, nombre, correo, contrasena, barrio_id, archivo_pdf, avatar FROM usuarios WHERE correo = ?"
            )!!
            consulta.setString(1, username)
            val resultado: ResultSet = consulta.executeQuery()

            if (resultado.next()) {
                return ListaUsuario(
                    resultado.getInt("id"),
                    resultado.getString("nombre"),
                    resultado.getString("correo"),
                    resultado.getString("contrasena"),
                    resultado.getInt("barrio_id"),
                    resultado.getBytes("archivo_pdf"),
                    resultado.getBytes("avatar")
                )
            }
        }
    } catch (ex: Exception) {
        Log.e("Error en sacarDatos: ", ex.message!!)
    } finally {
        connectSql.close()
    }

    return null
}
