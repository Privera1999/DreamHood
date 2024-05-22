package com.example.dreamhood.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.dreamhood.navegacion.AppScreens
import com.example.dreamhood.navegacion.SessionManager

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun Perfil(navController: NavController){
    Scaffold {
        val context = LocalContext.current
        Button(
            onClick = { SessionManager.clearSession(context)
                navController.navigate(route = AppScreens.PrimeraPantalla.route) },
            modifier = Modifier
                .width(120.dp)

        ) {
            Text("Registrarse")
        }

    }

}