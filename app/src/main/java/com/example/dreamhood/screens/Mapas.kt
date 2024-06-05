import android.annotation.SuppressLint
import android.content.Context
import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.dreamhood.navegacion.SessionManager
import com.example.dreamhood.screens.ConnectSql
import com.example.dreamhood.screens.NavAbajo
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MarkerInfoWindowContent
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import java.sql.PreparedStatement
import java.sql.ResultSet


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun mapas(navController: NavController){
    Scaffold(
        bottomBar = {
            NavAbajo(navController = navController)
        }
    ){
        MyMap(navController)
    }
}

val Cordoba = LatLng(37.887620, -4.779756)
//Data Class para los marcadores del mapa
data class MarcadoresLista(val id: Int,val latitud: Double,val longitud: Double,val descripcion: String, val imagen: ByteArray)

//Función para crear un mapa usando la api de google maps
@Composable
fun MyMap(navController: NavController) {

    val defaultCameraPosition = CameraPosition.fromLatLngZoom(Cordoba, 11f)
    val cameraPositionState = rememberCameraPositionState {
        position = defaultCameraPosition
    }
    Box(modifier = Modifier.size(790.dp)) {
        GoogleMapView(
            cameraPositionState = cameraPositionState, navController =  navController)
    }
}
//Función para modificar los marcadores del mapa
@Composable
fun GoogleMapView(
    modifier: Modifier = Modifier,
    cameraPositionState: CameraPositionState = rememberCameraPositionState(),
    navController : NavController


) {
    val context = LocalContext.current
    val markers = remember { sacarmapa(context) }

    GoogleMap(
        modifier = modifier,
        cameraPositionState = cameraPositionState,
    ) {

           markers.forEach { MarcadoresLista ->

               val posicion = LatLng(MarcadoresLista.latitud,MarcadoresLista.longitud)
               val posicionbuena= rememberMarkerState(position = posicion)
               MarkerInfoWindowContent(
                   state = posicionbuena,
                   icon = BitmapDescriptorFactory.defaultMarker(),
                   onInfoWindowClick = {irAcomentarios(navController,MarcadoresLista.id)}
               ) {
                   Column {
                       val bitmap = BitmapFactory.decodeByteArray(MarcadoresLista.imagen, 0, MarcadoresLista.imagen.size)
                       Image(
                           bitmap = bitmap.asImageBitmap(),
                           contentDescription = "",
                           modifier = Modifier
                               .requiredWidth(width = 200.dp)
                               .requiredHeight(height = 200.dp)
                       )
                       Spacer(modifier = Modifier.height(5.dp))
                       Text(text = MarcadoresLista.descripcion)
                   }
               }
           }
       }
    }

//Función para obtener los marcadores del mapa y su respectiva información
fun sacarmapa(context: Context): List<MarcadoresLista> {
    val marcadores: MutableList<MarcadoresLista> = mutableListOf()
    val connectSql = ConnectSql()

    try {
        val (username, password, barrioId) = SessionManager.getSession(context)
        if (barrioId != null) {
            val consulta: PreparedStatement = connectSql.dbConn()?.prepareStatement(
                """
                SELECT 
                    id,
                    latitud,
                    longitud,
                    descripcion,
                    imagen
                FROM incidentes
                WHERE barrio_id = ?
                ORDER BY id desc
                """.trimIndent()
            )!!

            consulta.setInt(1, barrioId)
            val resultado: ResultSet = consulta.executeQuery()

            while (resultado.next()) {
                val lista = MarcadoresLista(
                    resultado.getInt("id"),
                    resultado.getString("latitud").toDouble(),
                    resultado.getString("longitud").toDouble(),
                    resultado.getString("descripcion"),
                    resultado.getBytes("imagen")

                )
                marcadores.add(lista)
            }
        } else {

        }
    } catch (ex: Exception) {
        Log.e("Error en sacarMarcadores: ", ex.message!!)
    }finally {
        connectSql.close()
    }

    return marcadores
}

//Función para redirigir a los comentarios de la publicación seleccionada.
fun irAcomentarios(navController: NavController,id : Int){
    navController.navigate("comentarios/${id}")
}

