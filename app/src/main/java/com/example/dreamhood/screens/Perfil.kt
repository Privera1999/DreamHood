package com.example.dreamhood.screens

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.RadioButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.compose.primaryLight
import com.example.dreamhood.navegacion.AppScreens
import com.example.dreamhood.navegacion.SessionManager
import kotlinx.coroutines.launch
import java.sql.PreparedStatement
import java.sql.ResultSet

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun Perfil(navController: NavController){
    Scaffold(
        bottomBar = {
            NavAbajo(navController = navController)
        }
    ){
        formularioPerfil(navController)
    }
}

//Data class para los usuarios
data class ListaUsuario(
    val id: Int,
    val nombre: String,
    val correo: String,
    val contrasena: String,
    var barrio_id: Int,
    var archivo_pdf: ByteArray,
    var avatar: ByteArray?,
)



//Función que contiene todo lo visual de la pagina de perfil
@Composable
fun formularioPerfil(navController: NavController) {
    logoArriba()
    val context = LocalContext.current
    var nuevapass by remember { mutableStateOf("") }
    var confirmarpass by remember { mutableStateOf("") }
    var pdfFileUri by remember { mutableStateOf<Uri?>(null) }
    var foto by remember { mutableStateOf<ByteArray?>(null) }
    var datosUsuario by remember { mutableStateOf<ListaUsuario?>(null) }
    var originalDatosUsuario by remember { mutableStateOf<ListaUsuario?>(null) }
    var TraspasoBarrio by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        pdfFileUri = uri
    }

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    // Obtener los datos del usuario
    datosUsuario = sacarDatos(context)
    originalDatosUsuario = datosUsuario?.copy() // Guardar una copia de los datos originales

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 90.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        datosUsuario?.let { usuario ->
            // Mostrar la imagen del avatar
            ImagenDesdeBytesPerfil(usuario.avatar)
            Spacer(modifier = Modifier.height(15.dp))
            foto = PhotoPicker(true)
        }

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

        TraspasoBarrio = TraspasoDeBarrio()

        if(TraspasoBarrio){
            SeleccionaBarrio()
            Spacer(modifier = Modifier.height(15.dp))

            Button(
                onClick = { launcher.launch("application/pdf") },
            ) {
                Text("Adjuntar Cert.", Modifier.padding(start = 8.dp))
            }
        }
        Spacer(modifier = Modifier.height(15.dp))
        Row {
            Button(
                onClick = {
                    coroutineScope.launch {
                        guardarCambios(context, datosUsuario, originalDatosUsuario, nuevapass, confirmarpass, pdfFileUri, foto)
                        showToast(context,"Cambios Guardados Correctamente.")
                    }
                },
            ) {
                Text("Guardar Cambios", Modifier.padding(start = 8.dp))
            }
            Button(
                onClick = {
                    SessionManager.clearSession(context)
                    navController?.navigate(AppScreens.PrimeraPantalla.route)
                },
                modifier = Modifier.padding(start = 10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
            ) {
                Text("Cerrar Sesión", Modifier.padding(start = 8.dp))
            }
        }
    }
}

//Función para guardar los cambios obtenidos del usuario
suspend fun guardarCambios(
    context: Context,
    datosUsuario: ListaUsuario?,
    originalDatosUsuario: ListaUsuario?,
    nuevapass: String,
    confirmarpass: String,
    pdfFileUri: Uri?,
    foto: ByteArray?
) {
    if (datosUsuario == null || originalDatosUsuario == null) return

    val connectSql = ConnectSql()
    try {
        val consulta: PreparedStatement = connectSql.dbConn()?.prepareStatement(
            "UPDATE usuarios SET nombre = ?, contrasena = ?, barrio_id = ?, archivo_pdf = ?, avatar = ? WHERE id = ?"
        )!!

        var cambios = false

        consulta.setString(1, datosUsuario.nombre) // Seteamos el nombre del usuario

        if (nuevapass.isNotEmpty() && nuevapass == confirmarpass && nuevapass != originalDatosUsuario.contrasena) {
            consulta.setString(2, nuevapass)
            cambios = true
        } else {
            consulta.setString(2, originalDatosUsuario.contrasena)
        }

        if (datosUsuario.barrio_id != originalDatosUsuario.barrio_id) {
            consulta.setInt(3, datosUsuario.barrio_id)
            cambios = true
        } else {
            consulta.setInt(3, originalDatosUsuario.barrio_id)
        }

        if (pdfFileUri != null) {
            val inputStream = context.contentResolver.openInputStream(pdfFileUri)
            val pdfBytes = inputStream?.readBytes()
            consulta.setBytes(4, pdfBytes)
            cambios = true
        } else {
            consulta.setBytes(4, originalDatosUsuario.archivo_pdf)
        }

        if (foto != null && !foto.contentEquals(originalDatosUsuario.avatar)) {
            consulta.setBytes(5, foto)
            cambios = true
        } else {
            consulta.setBytes(5, originalDatosUsuario.avatar)
        }

        consulta.setInt(6, datosUsuario.id)

        if (cambios) {
            consulta.executeUpdate()
        }

    } catch (ex: Exception) {
        Log.e("Error en guardarCambios: ", ex.message!!)
    } finally {
        connectSql.close()
    }
}

//Función para sacar los datos del usuario
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

//Función para traspasar de barrio al usuario
@Composable
fun TraspasoDeBarrio():Boolean{
    var OpcionSeleccionada by remember { mutableStateOf<String?>(null) }

    Column(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 70.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text(text = "¿Te has mudado de barrio Sí o No?")

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = 4.dp)
        ) {
            RadioButton(
                selected = OpcionSeleccionada == "Sí",
                onClick = { OpcionSeleccionada = "Sí" }
            )
            Text(text = "Sí")
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = 4.dp)
        ) {
            RadioButton(
                selected = OpcionSeleccionada == "No",
                onClick = { OpcionSeleccionada = "No" }
            )
            Text(text = "No")
        }
        Spacer(modifier = Modifier.height(16.dp))
    }

    if(OpcionSeleccionada=="Sí"){
        return true
    }else{
        return false
    }


}