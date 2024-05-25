package com.example.dreamhood.navegacion

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.dreamhood.screens.LaPrimera
import com.example.dreamhood.screens.Perfil
import com.example.dreamhood.screens.SegundaPantalla
import com.example.dreamhood.screens.SubirPublicacion
import com.example.dreamhood.screens.feed
import com.example.dreamhood.screens.primeraPantlla


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavigation() {

    val context = LocalContext.current

    val (username, password) = com.example.dreamhood.navegacion.SessionManager.getSession(context)
    if (username != null && password != null) {
        val navController = rememberNavController()
        NavHost(navController = navController, startDestination = AppScreens.feed.route) {
            composable(route = AppScreens.PrimeraPantalla.route) {
                primeraPantlla(navController)
            }
            composable(route = AppScreens.SegundaPantalla.route) {
                SegundaPantalla(navController)
            }
            composable(route = AppScreens.feed.route) {
                feed(navController)
            }
            composable(route = AppScreens.SubirPublicacion.route) {
                SubirPublicacion(navController)
            }
            composable(route = AppScreens.Perfil.route) {
                Perfil(navController)
            }
            composable(route = AppScreens.LaPrimera.route) {
                LaPrimera(navController)
            }


        }
    } else {
        val navController = rememberNavController()
        NavHost(
            navController = navController,
            startDestination = AppScreens.PrimeraPantalla.route
        ) {
            composable(route = AppScreens.PrimeraPantalla.route) {
                primeraPantlla(navController)
            }
            composable(route = AppScreens.SegundaPantalla.route) {
                SegundaPantalla(navController)
            }
            composable(route = AppScreens.feed.route) {
                feed(navController)
            }
            composable(route = AppScreens.SubirPublicacion.route) {
                SubirPublicacion(navController)
            }
            composable(route = AppScreens.Perfil.route) {
                Perfil(navController)
            }
            composable(route = AppScreens.LaPrimera.route) {
                LaPrimera(navController)
            }
        }

    }
}






