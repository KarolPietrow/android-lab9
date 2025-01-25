package pl.karolpietrow.kp9

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.google.firebase.auth.FirebaseAuth
import pl.karolpietrow.kp9.ui.theme.AppScreen
import pl.karolpietrow.kp9.ui.theme.KP9Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val auth = FirebaseAuth.getInstance()
        setContent {
            KP9Theme {
                var isLoggedIn by remember { mutableStateOf(auth.currentUser != null) }
                if (!isLoggedIn) {
                    LoginScreen(onLoginSuccess = { isLoggedIn = true } )
                } else {
                    AppScreen(onLogOut = { isLoggedIn = false } )
                }
            }
        }
    }
}