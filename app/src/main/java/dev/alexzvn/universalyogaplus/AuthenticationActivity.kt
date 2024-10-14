package dev.alexzvn.universalyogaplus

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dev.alexzvn.universalyogaplus.layout.screen.LoginScreen
import dev.alexzvn.universalyogaplus.layout.screen.SplashScreen
import dev.alexzvn.universalyogaplus.ui.theme.UniversalYogaPlusTheme
import dev.alexzvn.universalyogaplus.util.Route

class AuthenticationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val goMainActivity: () -> Unit = {
            Intent(this, MainActivity::class.java)
                .also { startActivity(it) }
                .also { finish() }
        }

        setContent {
            val navigation = rememberNavController()

            UniversalYogaPlusTheme {
                Scaffold { padding ->
                    Box (Modifier.fillMaxSize().padding(padding)) {
                        NavHost(
                            navController = navigation,
                            startDestination = Route.Auth.SplashScreen
                        ) {
                            composable(Route.Auth.SplashScreen) {
                                SplashScreen(navigation, onBypass = goMainActivity)
                            }
                            composable(Route.Auth.Login) {
                                LoginScreen(navigation, onBypass = goMainActivity)
                            }
                        }
                    }
                }
            }
        }
    }
}