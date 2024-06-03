package com.example.dreamhood.screens

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.dreamhood.navegacion.SessionManager
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun Comentarios(navController: NavController,idPublicacion: Int?) {
    Scaffold {
        ComentariosScreen(idPublicacion)
    }
}

data class Publicacion(
    val foto: ByteArray, // Puedes usar cualquier tipo adecuado para la foto
    val usuarioID: Int,
    val texto: String,
    val titulo: String,
)

@Composable
fun ComentariosScreen(idPublicacion: Int?) {
    var comentarios by remember { mutableStateOf(listOf<Publicacion>()) }
    var nombre by remember { mutableStateOf(TextFieldValue("")) }
    var texto by remember { mutableStateOf(TextFieldValue("")) }
    var foto by remember { mutableStateOf<ByteArray?>(null) }

    comentarios=DatosPublicacion(idPublicacion)

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(comentarios) { comentario ->
                ComentarioItem(comentario)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = texto,
            onValueChange = { texto = it },
            label = { Text("Comentario") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Añadir Comentario")
        }
    }
}

@Composable
fun ComentarioItem(comentario: Publicacion) {
    val nombreUsuario= obtenerNombreUsuario(comentario.usuarioID)
    Row(verticalAlignment = Alignment.CenterVertically) {
        if (comentario.foto.isNotEmpty()) {
            val bitmap = BitmapFactory.decodeByteArray(comentario.foto, 0, comentario.foto.size)
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = "Foto de ",
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.Gray)
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            androidx.compose.material.Text(
                text = comentario.titulo,
                modifier = Modifier.padding(top = 5.dp)
                    .padding(end = 5.dp)
                    .padding(start = 5.dp)
            )
        }
    }
}

fun DatosPublicacion(idPublicacion: Int?): MutableList<Publicacion> {
    val publicacion: MutableList<Publicacion> = mutableListOf()
    val connectSql = ConnectSql()
    try {


            val consulta: PreparedStatement = connectSql.dbConn()?.prepareStatement(
                "SELECT imagen, usuario_id, titulo, descripcion FROM [dbo].[incidentes] WHERE id = ?"
            )!!
        if (idPublicacion != null) {
            consulta.setInt(1, idPublicacion)
        }
            val resultado: ResultSet = consulta.executeQuery()

        while (resultado.next()) {
            val lista = Publicacion(
                    resultado.getBytes("imagen"),
                    resultado.getInt("usuario_id"),
                    resultado.getString("titulo"),
                    resultado.getString("descripcion"),
                )
            publicacion.add(lista)
            }

    } catch (ex: Exception) {
        Log.e("Error en sacarDatos: ", ex.message!!)
    } finally {
        connectSql.close()
    }

    return publicacion
}


fun obtenerNombreUsuario(idUsuario: Int): String{
    var nombreUsuario : String = ""
    val connectSql = ConnectSql()

    try {
        val statement: PreparedStatement = connectSql.dbConn()?.prepareStatement("SELECT nombre FROM usuarios WHERE id = ?")!!
        statement.setInt(1, idUsuario)
        val resultSet = statement.executeQuery()
        if (resultSet.next()) {
            nombreUsuario = resultSet.getString("nombre")
        }
    } catch (ex: SQLException) {
        ex.printStackTrace()
    }finally {
        connectSql.close()
    }
    return nombreUsuario

}
