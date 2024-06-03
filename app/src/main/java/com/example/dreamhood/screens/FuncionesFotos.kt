package com.example.dreamhood.screens

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.dreamhood.R
import java.io.ByteArrayOutputStream
import java.io.InputStream

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

@Composable
fun ImagenDesdeBytesPerfil(bytes: ByteArray?){
    if (bytes == null) {
        // Si los bytes son nulos, no hay imagen para mostrar
        return
    }

    val bitmap = remember {
        BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .height(90.dp)
                    .width(90.dp)
                    .clip(CircleShape)
                    .border(2.dp, Color.Gray, CircleShape)

            )
        }


@Composable
fun PhotoPicker(perfil : Boolean) : ByteArray? {
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var imageBytes by remember { mutableStateOf<ByteArray?>(null) }
    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            selectedImageUri = uri
            imageBytes = uri?.let { uriToByteArray(context, it) }
        }
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box(
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
                .background(Color(0xFF196B52))
                .clickable { launcher.launch("image/*") },
            contentAlignment = Alignment.Center
        ) {

            Icon(
                imageVector = Icons.Filled.AddCircle,
                contentDescription = "Seleccionar Foto",
                modifier = Modifier.size(30.dp)
            )
        }

    }

    Spacer(modifier = Modifier.height(16.dp))

    if(!perfil){
        if (selectedImageUri != null) {
            val inputStream: InputStream? = context.contentResolver.openInputStream(selectedImageUri!!)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = null,
                contentScale = ContentScale.Inside,
                modifier = Modifier.height(200.dp)
                    .padding(top = 50.dp)

            )
        }
    }



    return imageBytes
}


fun uriToByteArray(context: Context, uri: Uri): ByteArray? {
    return context.contentResolver.openInputStream(uri)?.use { inputStream ->
        val buffer = ByteArrayOutputStream()
        val byteArray = ByteArray(1024)
        var len: Int
        while (inputStream.read(byteArray).also { len = it } != -1) {
            buffer.write(byteArray, 0, len)
        }
        buffer.toByteArray()
    }
}

@Composable
fun logoArriba(){
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.dreamhoodarriba),
            contentDescription = "Logo",
            modifier = Modifier
                .width(200.dp)
                .height(100.dp)
                .padding(top = 15.dp)
                .padding(bottom = 20.dp)
                .align(Alignment.TopCenter),
            contentScale = ContentScale.Fit
        )
    }
}
