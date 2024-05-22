package com.example.dreamhood.navegacion

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.dreamhood.screens.Perfil
import com.example.dreamhood.screens.SegundaPantalla
import com.example.dreamhood.screens.SubirPublicacion
import com.example.dreamhood.screens.feed
import com.example.dreamhood.screens.primeraPantlla


@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = AppScreens.PrimeraPantalla.route) {
        composable(route=AppScreens.PrimeraPantalla.route){
            primeraPantlla(navController)
        }
        composable(route=AppScreens.SegundaPantalla.route){
            SegundaPantalla(navController)
        }
        composable(route=AppScreens.feed.route){
            feed(navController)
        }
        composable(route=AppScreens.SubirPublicacion.route){
            SubirPublicacion(navController)
        }
        composable(route=AppScreens.Perfil.route){
            Perfil(navController)
        }

    }
}
