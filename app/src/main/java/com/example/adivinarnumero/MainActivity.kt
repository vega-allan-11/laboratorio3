package com.example.adivinarnumero

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                AdivinarNumero()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdivinarNumero() {
    // Estados del juego
    var valorSecreto      by remember { mutableStateOf(Random.nextInt(0, 101)) }
    var numeroAdivinado   by remember { mutableStateOf("") }
    var intentosRestantes by remember { mutableStateOf(3) }
    var feedback          by remember { mutableStateOf("Te quedan $intentosRestantes intentos") }

    // Estados del temporizador
    var segundosRestantes by remember { mutableStateOf(60) }
    var reinicios         by remember { mutableStateOf(0) }

    // Reinicia juego y timer
    fun reiniciarJuego() {
        valorSecreto      = Random.nextInt(0, 101)
        intentosRestantes = 3
        numeroAdivinado   = ""
        feedback          = "Te quedan $intentosRestantes intentos"
        segundosRestantes = 60
        reinicios++
    }

    // Determina si el juego acabó
    val gameOver = intentosRestantes <= 0 ||
            feedback.startsWith("Correcto") ||
            segundosRestantes <= 0

    // Corutina que actualiza el temporizador cada segundo
    LaunchedEffect(reinicios) {
        while (segundosRestantes > 0 &&
            intentosRestantes > 0 &&
            !feedback.startsWith("Correcto")) {
            delay(1_000L)
            segundosRestantes--
        }
        if (segundosRestantes <= 0 &&
            intentosRestantes > 0 &&
            !feedback.startsWith("Correcto")) {
            feedback = "Tiempo agotado. El número era $valorSecreto."
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Adivina el número (0–100)") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Mostrar temporizador
            Text(
                text  = "Tiempo restante: ${segundosRestantes}s",
                style = MaterialTheme.typography.bodyLarge
            )

            OutlinedTextField(
                value         = numeroAdivinado,
                onValueChange = { if (!gameOver) numeroAdivinado = it },
                label         = { Text("Tu número") },
                singleLine    = true,
                enabled       = !gameOver,
                modifier      = Modifier.fillMaxWidth()
            )

            if (!gameOver) {
                Button(
                    onClick = {
                        val adivinanza = numeroAdivinado.toIntOrNull()
                        if (adivinanza == null || adivinanza !in 0..100) {
                            feedback = "Ingresa un número entre 0 y 100"
                            return@Button
                        }
                        intentosRestantes--
                        feedback = when {
                            adivinanza == valorSecreto ->
                                "Correcto, era $valorSecreto."
                            intentosRestantes > 0 && adivinanza < valorSecreto ->
                                "El número es mayor. Intentos restantes: $intentosRestantes"
                            intentosRestantes > 0 && adivinanza > valorSecreto ->
                                "El número es menor. Intentos restantes: $intentosRestantes"
                            else ->
                                "Juego terminado. El número era $valorSecreto."
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Comprobar")
                }
            }

            Text(
                text  = feedback,
                style = MaterialTheme.typography.bodyMedium
            )

            if (gameOver) {
                Button(
                    onClick  = { reiniciarJuego() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Jugar de nuevo")
                }
            }
        }
    }
}
