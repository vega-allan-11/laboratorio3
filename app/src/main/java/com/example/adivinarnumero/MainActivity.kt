//Allan Vega 8-1001-2089 1SF242
package com.example.adivinarnumero

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {          // Tema de Material3
                AdivinarNumero()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdivinarNumero() {
    // Estados mutables
    var valorSecreto      by remember { mutableStateOf(Random.nextInt(0, 101)) }
    var numeroAdivinado   by remember { mutableStateOf("") }
    var intentosRestantes by remember { mutableStateOf(3) }
    var feedback    by remember { mutableStateOf("Te queda $intentosRestantes intentos") }

    // Función para reiniciar la partida
    fun reiniciarJuego() {
        valorSecreto       = Random.nextInt(0, 101)
        intentosRestantes = 3
        numeroAdivinado    = ""
        feedback     = "Te queda $intentosRestantes intentos"
    }

    val gameOver = intentosRestantes <= 0 || feedback.startsWith("Acertaste, felicidades!")

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Adivina el número entre 0 a 100") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value         = numeroAdivinado,
                onValueChange = { if (!gameOver) numeroAdivinado = it },
                label         = { Text("Tu número") },
                singleLine    = true,
                enabled       = !gameOver,
                modifier      = Modifier.fillMaxWidth()
            )

            // Botón de adivinar, solo si el juego no ha terminado
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

            // Botón de reinicio, solo si el juego terminó
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
