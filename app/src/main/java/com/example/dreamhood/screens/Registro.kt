package com.example.dreamhood.screens

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.width

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle

import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home

import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button

import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon

import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext

import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import java.sql.Types

import androidx.navigation.NavController

import com.example.dreamhood.R
import com.example.dreamhood.navegacion.AppScreens
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.sql.PreparedStatement
import java.sql.ResultSet


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun SegundaPantalla(navController: NavController) {
    Scaffold {
        registrarse(navController = navController, context = LocalContext.current)
    }
}
//Función que contiene todo el diseño de la pagina.
@Composable
fun registrarse(navController: NavController? = null, context: Context) {
    var email by remember { mutableStateOf("") }
    var pass by remember { mutableStateOf("") }
    var nombre by remember { mutableStateOf("") }
    var confirmarcon by remember { mutableStateOf("") }
    var pdfFileUri by remember { mutableStateOf<Uri?>(null) }
    var showErrorDialog by remember { mutableStateOf(false) }
    var mensaje by remember { mutableStateOf("") }
    var TituloFlotante by remember { mutableStateOf("") }
    var idBarrio by remember { mutableStateOf(3) }
    var BarrioSeleccionado by remember { mutableStateOf("") }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        pdfFileUri = uri
    }

    if (showErrorDialog) {
            AlertDialog(
                onDismissRequest = { showErrorDialog = false },
                confirmButton = {
                    Button(onClick = { showErrorDialog = false }) {
                        Text("Aceptar")
                    }
                },
                title = { Text(TituloFlotante) },
                text = { Text(mensaje) }
            )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 85.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            painter = painterResource(id = R.drawable.logodm),
            contentDescription = "Logo Dream Hood",
            modifier = Modifier
                .requiredWidth(width = 280.dp)
                .requiredHeight(height = 200.dp)
        )

        Spacer(modifier = Modifier.height(15.dp))

        TextField(
            value = email,
            onValueChange = { email = it },
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Email
            ),
            label = { Text("Correo Electrónico") },
            leadingIcon = { Icon(Icons.Filled.Email, contentDescription = null) }
        )

        Spacer(modifier = Modifier.height(15.dp))

        TextField(
            value = nombre,
            onValueChange = { nombre = it },
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Text
            ),
            label = { Text("Nombre y Apellidos") },
            leadingIcon = { Icon(Icons.Filled.AccountCircle, contentDescription = null) }
        )

        Spacer(modifier = Modifier.height(15.dp))

        TextField(
            value = pass,
            onValueChange = { pass = it },
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Password
            ),
            label = { Text("Contraseña") },
            leadingIcon = { Icon(Icons.Filled.Lock, contentDescription = null) },
            visualTransformation = PasswordVisualTransformation()
        )

        Spacer(modifier = Modifier.height(15.dp))

        TextField(
            value = confirmarcon,
            onValueChange = { confirmarcon = it },
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Password
            ),
            label = { Text("Confirmar Contraseña") },
            leadingIcon = { Icon(Icons.Filled.Lock, contentDescription = null) },
            visualTransformation = PasswordVisualTransformation()
        )

        Spacer(modifier = Modifier.height(15.dp))

        BarrioSeleccionado = SeleccionaBarrio()

        Row(
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Button(
                onClick = { launcher.launch("application/pdf") },
            ) {
                Text("Adjuntar Cert.", Modifier.padding(start = 8.dp))
            }

            Spacer(modifier = Modifier.width(12.dp))

            Button(
                onClick = {
                    // Validaciones de los campos
                    when {
                        nombre.isEmpty() -> {
                            TituloFlotante = "Error"
                            mensaje = "El nombre y apellidos no pueden estar vacíos."
                            showErrorDialog = true
                        }
                        !isValidEmail(email) -> {
                            TituloFlotante = "Error"
                            mensaje = "El correo electrónico no tiene una estructura válida."
                            showErrorDialog = true
                        }
                        pass.length < 8 -> {
                            TituloFlotante = "Error"
                            mensaje = "La contraseña debe tener al menos 8 caracteres."
                            showErrorDialog = true
                        }
                        pass != confirmarcon -> {
                            TituloFlotante = "Error"
                            mensaje = "Las contraseñas no coinciden."
                            showErrorDialog = true
                        }
                        pdfFileUri == null -> {
                            TituloFlotante = "Error"
                            mensaje = "Debe adjuntar un certificado PDF."
                            showErrorDialog = true
                        }

                        VerificarCorreo(email, obtenerCorreos()) == false -> {
                            TituloFlotante = "Error"
                            mensaje = "Correo Electronico ya registrado."
                            showErrorDialog = true
                        }

                        else -> {
                            idBarrio = ObtenerBarrioId(BarrioSeleccionado)
                            // Registrar el usuario si todas las validaciones pasan
                            registrarUsuario(context, email, pass, nombre, idBarrio, pdfFileUri)

                            TituloFlotante = "Confirmación"
                            mensaje = "Usuario Registrado Correctamente."
                            showErrorDialog = true

                            if (showErrorDialog) {
                                navController?.navigate(AppScreens.PrimeraPantalla.route)
                            }
                        }
                    }
                },
                enabled = pdfFileUri != null
            ) {
                Text("Registrarse")
            }

        }
    }
}

// Función para validar la estructura del correo electrónico
fun isValidEmail(email: String): Boolean {
    val emailPattern = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"
    return email.matches(emailPattern.toRegex())
}
//Función para obtener los datos del pdf seleccionado por el usuario.
fun obtenerDatosArchivoPDF(context: Context, uri: Uri): ByteArray? {
    val contentResolver: ContentResolver = context.contentResolver
    val inputStream: InputStream? = contentResolver.openInputStream(uri)
    return inputStream?.use { inputStream ->
        val buffer = ByteArrayOutputStream()
        val data = ByteArray(16384) // Tamaño del buffer, puedes ajustarlo según tus necesidades
        var nRead: Int
        while (inputStream.read(data, 0, data.size).also { nRead = it } != -1) {
            buffer.write(data, 0, nRead)
        }
        buffer.flush()
        buffer.toByteArray()
    }
}

//Función para registrar al usuario en la BBDD
fun registrarUsuario(context: Context, email: String, pass: String, nombre: String, barrio: Int, pdfFileUri: Uri?) {
    var connectSql = ConnectSql()

    var fotoDefault =  getImageByteArray(context,R.drawable.logodm)

    try {
        val addUsuario: PreparedStatement = connectSql.dbConn()?.prepareStatement("INSERT INTO usuarios (nombre, correo, contrasena, barrio_id, archivo_pdf, avatar, verificado) VALUES (?,?,?,?,?,?,?)")!!
        addUsuario.setString(1, nombre)
        addUsuario.setString(2, email)
        addUsuario.setString(3, pass)
        addUsuario.setInt(4, barrio)

        // Convertir pdfFileUri de Uri? a ByteArray?
        val pdfData = pdfFileUri?.let { uri ->
            obtenerDatosArchivoPDF(context, uri)
        }

        // Insertamos los datos binarios en la base de datos
        pdfData?.let {
            addUsuario.setBytes(5, it)
        } ?: addUsuario.setNull(5, Types.VARBINARY)

        addUsuario.setBytes(6, fotoDefault)
        addUsuario.setBoolean(7, true)
        addUsuario.executeUpdate()

    } catch (ex: Exception) {
        ex.printStackTrace()
    }finally {
        connectSql.close()
    }
}

//Función que contiene el diseño y la logica de seleccionar un barrio u otro
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeleccionaBarrio(): String {
    val barrios = obtenerBarriosDesdeBD()
    var expanded by remember { mutableStateOf(false) }
    var selectedBarrio by remember { mutableStateOf(barrios[0]) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = {
            expanded = !expanded
        }
    ) {
        TextField(
            value = selectedBarrio,
            onValueChange = {},
            readOnly = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor(),
            leadingIcon = { Icon(Icons.Filled.Home, contentDescription = null) }
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            barrios.forEach { item ->
                DropdownMenuItem(
                    text = { Text(text = item) },
                    onClick = {
                        selectedBarrio = item
                        expanded = false
                    }
                )
            }
        }
    }
    return selectedBarrio
}

//Función para obtener los barrios desde la BBDD
fun obtenerBarriosDesdeBD(): List<String> {
    val barrios = mutableListOf<String>()
    val connectSql = ConnectSql()

    try {
        val statement: PreparedStatement = connectSql.dbConn()?.prepareStatement("SELECT nombre FROM barrios")!!
        val resultSet = statement.executeQuery()
        while (resultSet.next()) {
            barrios.add(resultSet.getString("nombre"))
        }
    } catch (ex: Exception) {
        ex.printStackTrace()
    }finally {
        connectSql.close()
    }

    return barrios
}

//Función para obtener el bitmap del pdf
fun getBitmapFromResource(context: Context, resId: Int): Bitmap {
    return BitmapFactory.decodeResource(context.resources, R.drawable.logodm)
}

//Función para pasar el bitmap a array
fun bitmapToByteArray(bitmap: Bitmap): ByteArray {
    val stream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
    return stream.toByteArray()
}

//Función para obtener una iamgen desde un array de bytes
fun getImageByteArray(context: Context, resId: Int): ByteArray {
    val bitmap = getBitmapFromResource(context, resId)
    return bitmapToByteArray(bitmap)
}

//Función para obtener los correos electronicos ya registrados.
fun obtenerCorreos(): List<String>{
    val Correos = mutableListOf<String>()
    val connectSql = ConnectSql()

    try {
        val statement: PreparedStatement = connectSql.dbConn()?.prepareStatement("SELECT correo FROM usuarios")!!
        val resultSet = statement.executeQuery()
        while (resultSet.next()) {
            Correos.add(resultSet.getString("correo"))
        }
    } catch (ex: Exception) {
        ex.printStackTrace()
    }finally {
        connectSql.close()
    }

    return Correos
}
//Función para verificar si el correo ya existe.
fun VerificarCorreo(correo: String,Correos : List<String>):Boolean{
return !Correos.contains(correo)
}

//Función para para obtener el id del barrio.
fun ObtenerBarrioId(barrio: String): Int {
    var idBarrio: Int = 0
    val connectSql = ConnectSql()
    println("Datos de sesión obtenidos: Barrio=$barrio")

    try {
        val statement: PreparedStatement = connectSql.dbConn()?.prepareStatement("SELECT id FROM barrios WHERE nombre = ?")!!
        statement.setString(1, barrio)
        val resultSet: ResultSet = statement.executeQuery()

        if (resultSet.next()) {
            idBarrio = resultSet.getInt("id")
        }
    } catch (ex: Exception) {
        ex.printStackTrace()
    } finally {
        connectSql.close()
    }

    println("Datos de sesión obtenidos: idBarrio=$idBarrio")
    return idBarrio
}
