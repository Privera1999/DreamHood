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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.dreamhood.navegacion.SessionManager
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun Comentarios(navController: NavController, idPublicacion: Int?) {
    var context = LocalContext.current
    Scaffold(
        bottomBar = {
            NavAbajo(navController = navController)
        }
    ) {
        ComentariosScreen(navController, idPublicacion,context)
    }
}


data class Publicacion(
    val foto: ByteArray, // Puedes usar cualquier tipo adecuado para la foto
    val usuarioID: Int,
    val texto: String,
    val titulo: String,
)

@Composable
fun ComentariosScreen(navController: NavController, idPublicacion: Int?, context : Context) {
    var publicacion by remember { mutableStateOf<Publicacion?>(null) }
    var comentarios by remember { mutableStateOf(listOf<Comentario>()) }
    var nuevoComentario by remember { mutableStateOf(TextFieldValue("")) }

    LaunchedEffect(idPublicacion) {
        if (idPublicacion != null) {
            publicacion = obtenerPublicacion(idPublicacion)
            comentarios = obtenerComentarios(idPublicacion)
        }
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {

        publicacion?.let { pub ->
            val bitmap = BitmapFactory.decodeByteArray(pub.foto, 0, pub.foto.size)
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = "Imagen de la Publicación",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(Color.Gray)
            )

            Spacer(modifier = Modifier.height(16.dp))
        }

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(comentarios) { comentario ->
                ComentarioItem(comentario)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = nuevoComentario,
            onValueChange = { nuevoComentario = it },
            label = { Text("Comentario") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (idPublicacion != null) {
                    val (username, password, barrioId) = SessionManager.getSession(context)
                    val usuarioID = username?.let { obtenerIdUsuario(it) }
                    if (usuarioID != null) {
                        añadirComentario(idPublicacion, usuarioID, nuevoComentario.text)
                    }
                    nuevoComentario = TextFieldValue("") // Clear the text field
                    comentarios = obtenerComentarios(idPublicacion) // Refresh comments
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Añadir Comentario")
        }

        Spacer(modifier = Modifier.height(56.dp)) // Deja espacio para la barra de navegación
    }
}

data class Comentario(
    val usuarioID: Int,
    val texto: String,
    val avatar: ByteArray?,
)

@Composable
fun ComentarioItem(comentario: Comentario) {
    val nombreUsuario = obtenerNombreUsuario(comentario.usuarioID)
    Row(verticalAlignment = Alignment.CenterVertically) {
        if (comentario.avatar != null) {
            val bitmap = BitmapFactory.decodeByteArray(comentario.avatar, 0, comentario.avatar.size)
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = "Avatar de $nombreUsuario",
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.Gray)
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(
                text = nombreUsuario,
                fontSize = 14.sp,
                modifier = Modifier.padding(top = 5.dp)
            )
            Text(
                text = comentario.texto,
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}

fun obtenerPublicacion(idPublicacion: Int): Publicacion? {
    val connectSql = ConnectSql()
    var publicacion: Publicacion? = null
    try {
        val consulta: PreparedStatement = connectSql.dbConn()?.prepareStatement(
            "SELECT imagen, usuario_id, titulo, descripcion FROM [dbo].[incidentes] WHERE id = ?"
        )!!
        consulta.setInt(1, idPublicacion)
        val resultado: ResultSet = consulta.executeQuery()

        if (resultado.next()) {
            publicacion = Publicacion(
                resultado.getBytes("imagen"),
                resultado.getInt("usuario_id"),
                resultado.getString("titulo"),
                resultado.getString("descripcion")
            )
        }
    } catch (ex: Exception) {
        Log.e("Error en obtenerPublicacion: ", ex.message!!)
    } finally {
        connectSql.close()
    }
    return publicacion
}

fun obtenerComentarios(idPublicacion: Int): List<Comentario> {
    val comentarios: MutableList<Comentario> = mutableListOf()
    val connectSql = ConnectSql()
    try {
        val consulta: PreparedStatement = connectSql.dbConn()?.prepareStatement(
            "SELECT c.usuario_id, c.comentario, u.avatar \n" +
                    "FROM [dbo].[comentarios] c\n" +
                    "INNER JOIN [dbo].[usuarios] u ON c.usuario_id = u.id\n" +
                    "WHERE c.incidente_id = ?\n"
        )!!
        consulta.setInt(1, idPublicacion)
        val resultado: ResultSet = consulta.executeQuery()

        while (resultado.next()) {
            val comentario = Comentario(
                resultado.getInt("usuario_id"),
                resultado.getString("comentario"),
                resultado.getBytes("avatar")
            )
            comentarios.add(comentario)
        }
    } catch (ex: Exception) {
        Log.e("Error en obtenerComentarios: ", ex.message!!)
    } finally {
        connectSql.close()
    }
    return comentarios
}

fun añadirComentario(idPublicacion: Int, usuarioID: Int, texto: String) {
    val connectSql = ConnectSql()
    try {
        val consulta: PreparedStatement = connectSql.dbConn()?.prepareStatement(
            "INSERT INTO [dbo].[comentarios] (incidente_id, usuario_id, comentario) VALUES (?, ?, ?)"
        )!!
        consulta.setInt(1, idPublicacion)
        consulta.setInt(2, usuarioID)
        consulta.setString(3, texto)
        consulta.executeUpdate()
    } catch (ex: Exception) {
        Log.e("Error en añadirComentario: ", ex.message!!)
    } finally {
        connectSql.close()
    }
}

fun obtenerNombreUsuario(idUsuario: Int): String {
    var nombreUsuario: String = ""
    val connectSql = ConnectSql()
    try {
        val statement: PreparedStatement = connectSql.dbConn()?.prepareStatement(
            "SELECT nombre FROM usuarios WHERE id = ?"
        )!!
        statement.setInt(1, idUsuario)
        val resultSet = statement.executeQuery()
        if (resultSet.next()) {
            nombreUsuario = resultSet.getString("nombre")
        }
    } catch (ex: SQLException) {
        ex.printStackTrace()
    } finally {
        connectSql.close()
    }
    return nombreUsuario
}

fun obtenerIdUsuario(correo: String): Int {
    var idUsuario = 0
    val connectSql = ConnectSql()
    try {
        val statement: PreparedStatement = connectSql.dbConn()?.prepareStatement(
            "SELECT id FROM usuarios WHERE correo = ?"
        )!!
        statement.setString(1, correo)
        val resultSet = statement.executeQuery()
        if (resultSet.next()) {
            idUsuario = resultSet.getInt("id")
        }
    } catch (ex: SQLException) {
        ex.printStackTrace()
    } finally {
        connectSql.close()
    }
    return idUsuario
}
