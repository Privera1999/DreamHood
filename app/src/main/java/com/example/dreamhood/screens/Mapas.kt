import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.dreamhood.R
import com.example.dreamhood.navegacion.AppScreens
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
val Barrio = LatLng(37.883691, -4.762148)

data class MarcadoresLista(val id: Int,val latitud: Double,val longitud: Double,val descripcion: String, val imagen: ByteArray)

@Composable
fun MyMap(navController: NavController) {

    val defaultCameraPosition = CameraPosition.fromLatLngZoom(Cordoba, 11f)
    val cameraPositionState = rememberCameraPositionState {
        position = defaultCameraPosition
    }
    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMapView(
            cameraPositionState = cameraPositionState, navController =  navController)
    }
}

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
                icon = BitmapDescriptorFactory.defaultMarker()
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logodm),
                    contentDescription = "Logo Dream Hood",
                    modifier = Modifier
                        .requiredWidth(width = 280.dp)
                        .requiredHeight(height = 200.dp)


                )


            }
        }
    }
}



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



