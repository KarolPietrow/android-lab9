package pl.karolpietrow.kp9

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth

@Composable
fun LoginScreen(onLoginSuccess: () -> Unit) {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var register by remember { mutableStateOf(false) }
    var loading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            modifier = Modifier.padding(10.dp),
            text = if (register) "Zarejestruj się" else "Zaloguj się",
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
        )
        TextField(
            modifier = Modifier.padding(10.dp),
            value = email,
            onValueChange = { newText ->
                email = newText.replace(" ", "")
            },
            label = { Text("Adres e-mail") },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Email)
        )
        TextField(
            modifier = Modifier.padding(5.dp),
            value = password,
            onValueChange = { password = it },
            label = { Text("Hasło") },
            visualTransformation = PasswordVisualTransformation()
        )
        Button(
            modifier = Modifier.padding(10.dp),
            onClick = {
                if (email.isEmpty() && password.isEmpty()) {
                    Toast.makeText(context, "Podaj adres e-mail i hasło.", Toast.LENGTH_SHORT).show()
                } else if (email.isEmpty()) {
                    Toast.makeText(context, "Podaj adres e-mail.", Toast.LENGTH_SHORT).show()
                } else if (password.isEmpty()) {
                    Toast.makeText(context, "Podaj hasło.", Toast.LENGTH_SHORT).show()
                } else {
                    loading = true
                    if (register) {
                        auth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    loading = false
                                    onLoginSuccess()
                                } else {
                                    loading = false
                                }
                            }
                    } else {
                        auth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    loading = false
                                    onLoginSuccess()
                                } else {
                                    loading = false
                                }
                            }
                    }
                }
            },
        ) {
            Text(
                text = if (register) "Zarejestruj się" else "Zaloguj się",
            )
        }
        TextButton(
            modifier = Modifier.padding(10.dp),
            onClick = {
                register = !register
            },
        ) {
            Text(
                text = if (register) "Masz już konto? Zaloguj się" else "Nie masz konta? Zarejestruj się",
            )
        }
        if (loading) {
            Text(
                modifier = Modifier.padding(10.dp),
                text = "Ładowanie...",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
            )
            LinearProgressIndicator (
                color = MaterialTheme.colorScheme.secondary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
            )
        }
    }
}