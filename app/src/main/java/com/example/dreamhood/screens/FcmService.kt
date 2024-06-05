package com.example.dreamhood.screens

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

//Función para poder obtener notificaciones desde FireBase
class FcmService: FirebaseMessagingService() {
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        println()
    }
}