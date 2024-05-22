package com.example.dreamhood.screens

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

@Composable
fun MensajeError(texto : String){

    var showErrorDialog by remember { mutableStateOf(false) }
    AlertDialog(
        onDismissRequest = { showErrorDialog = false },
        confirmButton = {
            Button(onClick = { showErrorDialog = false }) {
                Text("Aceptar")
            }
        },
        title = { Text("Error") },
        text = { Text(texto) }
    )
}