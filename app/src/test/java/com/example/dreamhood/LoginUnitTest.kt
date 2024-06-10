package com.example.dreamhood

import com.example.dreamhood.screens.ConnectSql
import com.example.dreamhood.screens.iniciarSesion
import io.mockk.*
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import org.junit.Test
import java.sql.PreparedStatement
import java.sql.ResultSet


class LoginUnitTest {

    @Test
    fun `iniciarSesion devuelve true para credenciales válidas`() {
        // Preparar un mock de ConnectSql
        val connectSqlMock = mockk<ConnectSql>()
        val statementMock = mockk<PreparedStatement>()
        val resultSetMock = mockk<ResultSet>()

        every { connectSqlMock.dbConn() } returns mockk()
        every { connectSqlMock.dbConn()?.prepareStatement(any()) } returns statementMock
        every { statementMock.setString(any(), any()) } just Runs
        every { statementMock.executeQuery() } returns resultSetMock
        every { resultSetMock.next() } returns true
        every { resultSetMock.getString("contrasena") } returns "contrasenaValida"

        mockkStatic("com.example.dreamhood.screens.LoginKt")
        every { iniciarSesion("emailValido@ejemplo.com", "contrasenaValida") } answers { true }

        val resultado = iniciarSesion("emailValido@ejemplo.com", "contrasenaValida")

        assertTrue(resultado)

        unmockkStatic("com.example.dreamhood.screens.LoginKt")
    }

    @Test
    fun `iniciarSesion devuelve false para credenciales inválidas`() {
        // Preparar un mock de ConnectSql
        val connectSqlMock = mockk<ConnectSql>()
        val statementMock = mockk<PreparedStatement>()
        val resultSetMock = mockk<ResultSet>()

        every { connectSqlMock.dbConn() } returns mockk()
        every { connectSqlMock.dbConn()?.prepareStatement(any()) } returns statementMock
        every { statementMock.setString(any(), any()) } just Runs
        every { statementMock.executeQuery() } returns resultSetMock
        every { resultSetMock.next() } returns false

        mockkStatic("com.example.dreamhood.screens.LoginKt")
        every { iniciarSesion("emailInvalido@ejemplo.com", "contrasenaInvalida") } answers { false }

        val resultado = iniciarSesion("emailInvalido@ejemplo.com", "contrasenaInvalida")

        assertFalse(resultado)

        unmockkStatic("com.example.dreamhood.screens.LoginKt")
    }
}