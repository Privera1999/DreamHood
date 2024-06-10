package com.example.dreamhood

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.NavController
import com.example.dreamhood.screens.primeraPantlla
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class SistemaPrueba {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Mock
    lateinit var navController: NavController

    @Test
    fun pruebaInicioSesionExitoso() {
        // Configurar el comportamiento esperado del NavController
        `when`(navController.navigate(route = "segundaPantalla")).thenReturn(Unit)

        // Simular la navegación a la primera pantalla
        composeTestRule.setContent {
            primeraPantlla(navController = navController)
        }

        // Realizar acciones de usuario: ingresar correo y contraseña y hacer clic en el botón de entrada
        composeTestRule.onNodeWithContentDescription("Correo Electrónico").performTextInput("sandra@a.com")
        composeTestRule.onNodeWithContentDescription("Contraseña").performTextInput("1234567890q")
        composeTestRule.onNodeWithText("Entrar").performClick()

        // Verificar que se navegue a la segunda pantalla
        composeTestRule.onNodeWithText("segundaPantalla").assertExists()
    }

    @Test
    fun pruebaInicioSesionFallido() {
        // Configurar el comportamiento esperado del NavController
        `when`(navController.navigate(route = "segundaPantalla")).thenReturn(Unit)

        // Simular la navegación a la primera pantalla
        composeTestRule.setContent {
            primeraPantlla(navController = navController)
        }

        // Realizar acciones de usuario: ingresar correo y contraseña incorrectos y hacer clic en el botón de entrada
        composeTestRule.onNodeWithContentDescription("Correo Electrónico").performTextInput("usuarioincorrecto@example.com")
        composeTestRule.onNodeWithContentDescription("Contraseña").performTextInput("contraseñaincorrecta")
        composeTestRule.onNodeWithText("Entrar").performClick()

        // Verificar que se muestre un diálogo de error
        composeTestRule.onNodeWithText("Error").assertExists()
    }
}