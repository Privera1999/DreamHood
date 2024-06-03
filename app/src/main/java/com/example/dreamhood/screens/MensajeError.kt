package com.example.dreamhood.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

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

fun showToast(context: Context, message: String) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}