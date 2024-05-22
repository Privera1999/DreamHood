package com.example.dreamhood.screens

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.BitmapFactory
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import android.util.Log
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.compose.primaryLight
import com.example.dreamhood.navegacion.AppScreens
import com.example.dreamhood.navegacion.SessionManager
import java.sql.PreparedStatement
import java.sql.ResultSet


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun feed(navController: NavController){
    Scaffold {
        NavAbajo(navController)
        PhotoGrid()
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun NavAbajo(navController: NavController) {
    var selectedItem by remember { mutableStateOf(0) }
    val items = listOf("Home", "Añadir", "Buscar", "Perfil")
    val icons = listOf(Icons.Filled.Home, Icons.Filled.Add, Icons.Filled.Search, Icons.Filled.Person)

    Scaffold(
        bottomBar = {
            BottomNavigation(
                backgroundColor = primaryLight,
                contentColor = Color.White
            ) {
                items.forEachIndexed { index, item ->
                    BottomNavigationItem(
                        icon = { Icon(icons[index], contentDescription = item) },
                        label = { Text(item) },
                        selected = selectedItem == index,
                        onClick = { selectedItem = index
                            when (index) {
                                0 -> navController.navigate(route = AppScreens.feed.route)
                                1 -> navController.navigate(route = AppScreens.SubirPublicacion.route)
                                3 -> navController.navigate(route = AppScreens.Perfil.route)
                            }
                                  },
                        alwaysShowLabel = true
                    )
                }
            }
        }
    ) {
    }
}

@Composable
fun PhotoGrid() {

    val items = sacarfeed(context = LocalContext.current)

    if(items.isEmpty()){

    }else{



    LazyVerticalGrid(
        columns = GridCells.Fixed(1),
        contentPadding = PaddingValues(vertical = 10.dp),
        modifier = Modifier.padding(60.dp)


    ) {
        items(items) { item ->
            PhotoCard(item)
        }
    }
    }
}

@Composable
fun PhotoCard(lista: ListaFeed) {

    var isFavorite by remember { mutableStateOf(false) }
    var Afavor by remember { mutableStateOf(false) }

    val color by animateColorAsState(
        targetValue = if (isFavorite) Color.Red else Color.Black
    )

    val scale by animateFloatAsState(
        targetValue = if (isFavorite) 1f else 1f
    )

    val colora by animateColorAsState(
        targetValue = if (Afavor) Color.Blue else Color.Black
    )

    val scalea by animateFloatAsState(
        targetValue = if (Afavor) 1f else 1f
    )

    Column(
        modifier = Modifier
            .padding(top = 10.dp)
            .border(2.dp, Color.Gray, RectangleShape)
            .padding(15.dp)
            .fillMaxWidth()


    ) {
        Row(
        ){
            ImagenDesdeBytes(lista.avatar,true)
            Text(
                text = lista.nombreusuario,
                modifier = Modifier
                    .padding(top = 10.dp)
                    .padding(start = 3.dp)
            )
        }
        Spacer(modifier = Modifier.height(2.dp))
        ImagenDesdeBytes(lista.imagen,false)

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),

        ) {

            if(lista.esVotacion){
                Icon(
                    imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                    contentDescription = "Favoritos",
                    modifier = Modifier
                        .size(24.dp)
                        .clickable {
                            isFavorite = !isFavorite

                            if(isFavorite) {
                                megusta(lista.id, true)
                                lista.meGusta++
                            }
                            else {
                                megusta(lista.id, false)
                                lista.meGusta--
                            }
                        }
                        .scale(scale),
                    tint = color
                )

                Text(
                    text = lista.meGusta.toString() + " Me gustas ",
                    modifier = Modifier.padding(top = 5.dp)
                        .padding(end = 5.dp)
                        .padding(start = 5.dp)
                )
            }
            else{
                Icon(
                    imageVector =  if (Afavor) Icons.Filled.ThumbUp else Icons.Outlined.ThumbUp,
                    contentDescription = "A favor",
                    modifier = Modifier
                        .size(24.dp)
                        .clickable {
                            Afavor = !Afavor
                            if(Afavor) {
                                megusta(lista.id, true)
                                lista.meGusta++
                            }

                            else {
                                megusta(lista.id, false)
                                lista.meGusta--
                            }
                        }
                        .scale(scale),
                    tint = colora
                )

                Text(
                    text = lista.meGusta.toString() + " Votos A favor ",
                    modifier = Modifier.padding(top = 5.dp)
                        .padding(end = 5.dp)
                        .padding(start = 5.dp)
                )
            }

            Icon(
                imageVector = Icons.Outlined.Email,
                contentDescription = "Comentar",
                modifier = Modifier.size(24.dp),
                tint = Color.Black
            )
        }
        Text(
            text = lista.descripcion,
            modifier = Modifier.padding(top = 8.dp)
        )
    }

    Spacer(modifier = Modifier.height(50.dp))

}

data class ListaFeed(
    val id : Int,
    val titulo: String,
    val descripcion: String,
    val usuario: Int,
    var esVotacion: Boolean,
    var meGusta: Int,
    val imagen: ByteArray,
    val avatar: ByteArray,
    val nombreusuario: String
)

fun sacarfeed(context: Context): List<ListaFeed> {
    val usuarios: MutableList<ListaFeed> = mutableListOf()
    val connectSql = ConnectSql()

    try {
        val (username, password, barrioId) = SessionManager.getSession(context)


        if (barrioId != null) {
            val consulta: PreparedStatement = connectSql.dbConn()?.prepareStatement(
                """
                SELECT 
                    i.id,
                    i.titulo,
                    i.descripcion,
                    i.usuario_id,
                    i.es_votacion,
                    i.me_gusta,
                    i.imagen,
                    u.avatar,
                    u.nombre
                FROM 
                    incidentes i 
                JOIN 
                    usuarios u ON i.usuario_id = u.id
                WHERE i.barrio_id = ?
                """.trimIndent()
            )!!

            consulta.setInt(1, barrioId)
            val resultado: ResultSet = consulta.executeQuery()

            while (resultado.next()) {
                val lista = ListaFeed(
                    resultado.getInt("id"),
                    resultado.getString("titulo"),
                    resultado.getString("descripcion"),
                    resultado.getInt("usuario_id"),
                    resultado.getBoolean("es_votacion"),
                    resultado.getInt("me_gusta"),
                    resultado.getBytes("imagen"),
                    resultado.getBytes("avatar"),
                    resultado.getString("nombre")
                )
                usuarios.add(lista)
            }
        } else {

        }
    } catch (ex: Exception) {
        Log.e("Error en sacarfeed: ", ex.message!!)
    }finally {
        connectSql.close()
    }

    return usuarios
}







@Composable
fun ImagenDesdeBytes(bytes: ByteArray?,avatar: Boolean){
    if (bytes == null) {
        // Si los bytes son nulos, no hay imagen para mostrar
        return
    }

    val bitmap = remember {
        BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }

    if (bitmap != null) {
        if(!avatar){
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .height(200.dp)
                    .padding(top = 5.dp)
            )
        }
        else{
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .height(40.dp)
                    .width(40.dp)
                    .clip(CircleShape)
                    .border(2.dp, Color.Gray, CircleShape)

            )
        }

    }
}

fun megusta (id : Int, mas : Boolean){
    var connectSql = ConnectSql()

    if(mas){
        val annadirmegusta: PreparedStatement = connectSql.dbConn()?.prepareStatement("UPDATE incidentes SET me_gusta = me_gusta +1 WHERE id = ?")!!
        annadirmegusta.setInt(1,id)
        annadirmegusta.executeUpdate()

    }else{
            val annadirmegusta: PreparedStatement = connectSql.dbConn()?.prepareStatement("UPDATE incidentes SET me_gusta = me_gusta -1 WHERE id = ?")!!
            annadirmegusta.setInt(1,id)
            annadirmegusta.executeUpdate()

    }
        connectSql.close()
}
