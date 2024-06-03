package com.example.dreamhood.screens

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.text.BoringLayout
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box

import androidx.compose.foundation.layout.Column

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.RadioButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.dreamhood.navegacion.AppScreens
import com.example.dreamhood.navegacion.SessionManager
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.RemoteMessage
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.sql.PreparedStatement
import java.sql.SQLException


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun SubirPublicacion(navController: NavController){
    Scaffold(
        bottomBar = {
            NavAbajo(navController = navController)
        }
    ){
        formulariopublicacion(navController)
    }

}

@Composable
fun formulariopublicacion(navController: NavController? = null) {
    logoArriba()
    var titulo by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var votacion by remember { mutableStateOf(false) }
    var foto by remember { mutableStateOf<ByteArray?>(null) }




    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 85.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
       
        Spacer(modifier = Modifier.height(15.dp))

        TextField(
            value = titulo,
            onValueChange = { titulo = it },
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Text
            ),
            label = { Text("Título") },
            leadingIcon = { Icon(Icons.Filled.Info, contentDescription = null) }
        )

        Spacer(modifier = Modifier.height(15.dp))


            TextField(
                value = descripcion,
                onValueChange = { descripcion = it },
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = androidx.compose.ui.text.input.KeyboardType.Text
                ),
                label = {
                    Box(
                        modifier = Modifier.height(150.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Descripción")
                    }
                },
                leadingIcon = {
                    Icon(Icons.Filled.Warning, contentDescription = null)
                },

                )

        Spacer(modifier = Modifier.height(15.dp))

        votacion = EsVotacion()

        Spacer(modifier = Modifier.height(15.dp))

        Row(){
            foto = PhotoPicker(false)
            Spacer(modifier = Modifier.width(20.dp))
            botonSubir(titulo,descripcion,votacion,foto,navController)
        }

        }
    }





@Composable
fun EsVotacion():Boolean{
    var OpcionSeleccionada by remember { mutableStateOf<String?>(null) }

           Column(
               Modifier.fillMaxWidth()
                   .padding(horizontal = 70.dp),
               horizontalAlignment = Alignment.Start
        ) {
            Text(text = "¿Es votación con Sí o No?")

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

@Composable
fun botonSubir(
    titulo: String,
    descripcion: String,
    votacion: Boolean,
    foto: ByteArray?,
    navController: NavController? = null
) {
    val context = LocalContext.current

    Column {
        Box(
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
                .background(Color(0xFF196B52))
                .clickable {
                    when {
                        titulo.isBlank() -> {
                            showToast(context, "Error: El título no puede estar vacío.")
                        }
                        descripcion.isBlank() -> {
                            showToast(context, "Error: La descripción no puede estar vacía.")
                        }
                        foto == null -> {
                            showToast(context, "Error: No ha subido ninguna foto.")
                        }
                        votacion == null -> {
                            showToast(context, "Error: Debe seleccionar la opción de votación.")
                        }
                        else -> {
                            insertPublicacion(titulo, descripcion, votacion, foto, context)
                            navController?.navigate(AppScreens.feed.route)
                            showToast(context, "Publicación Subida Correctamente.")
                        }
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.Done,
                contentDescription = "Subir",
                modifier = Modifier.size(30.dp)
            )
        }
    }
}


fun insertPublicacion(titulo: String, descripcion: String, es_votacion: Boolean, imagen: ByteArray?, context: Context) {
    var connectSql = ConnectSql()
    val (username, password, barrioId) = SessionManager.getSession(context)
    val usuarioid= obtenerIDUsuario(username)

    try {
        val addPublicacion: PreparedStatement = connectSql.dbConn()?.prepareStatement("INSERT INTO incidentes (titulo, descripcion, usuario_id, barrio_id, es_votacion, imagen) VALUES (?,?,?,?,?,?)")!!
        addPublicacion.setString(1, titulo)
        addPublicacion.setString(2, descripcion)
        addPublicacion.setInt(3, usuarioid)
        if (barrioId != null) {
            addPublicacion.setInt(4, barrioId)
        }
        addPublicacion.setBoolean(5, es_votacion)
        addPublicacion.setBytes(6, imagen)
        addPublicacion.executeUpdate()
    } catch (ex: Exception) {
        ex.printStackTrace()
    }finally {
        connectSql.close()
    }
}


fun obtenerIDUsuario(correo : String?): Int{
    var usuarioId: Int = 0
    val connectSql = ConnectSql()

    try {
        val statement: PreparedStatement = connectSql.dbConn()?.prepareStatement("SELECT id FROM usuarios WHERE correo = ?")!!
        statement.setString(1, correo)
        val resultSet = statement.executeQuery()
        if (resultSet.next()) {
            usuarioId = resultSet.getInt("id")
        }
    } catch (ex: SQLException) {
        ex.printStackTrace()
    }finally {
        connectSql.close()
    }
    return usuarioId

}



