package com.example.dreamhood.navegacion

sealed class AppScreens(val route: String) {
    object PrimeraPantalla: AppScreens("primera_pantalla")
    object SegundaPantalla: AppScreens("segunda_pantalla")
    object feed: AppScreens("feed")
    object SubirPublicacion: AppScreens("SubirPublicacion")
    object Perfil: AppScreens("Perfil")
}