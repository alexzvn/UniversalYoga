package dev.alexzvn.universalyogaplus

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dev.alexzvn.universalyogaplus.layout.MainLayout
import dev.alexzvn.universalyogaplus.layout.screen.CloudScreen
import dev.alexzvn.universalyogaplus.layout.screen.HomeScreen
import dev.alexzvn.universalyogaplus.layout.screen.ProfileScreen
import dev.alexzvn.universalyogaplus.local.Database
import dev.alexzvn.universalyogaplus.service.AuthService
import dev.alexzvn.universalyogaplus.service.DatabaseService
import dev.alexzvn.universalyogaplus.ui.theme.UniversalYogaPlusTheme
import dev.alexzvn.universalyogaplus.util.Route
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class MainActivity : ComponentActivity(), CoroutineScope {
    override val coroutineContext: CoroutineContext
        get() =  Dispatchers.Main + job

    private lateinit var job: Job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        job = Job()

        DatabaseService.set(
            Database.create(this, "${AuthService.user?.uid}.sqlite")
        )

        enableEdgeToEdge()
        setContent {
            val navigation = rememberNavController()

            LaunchedEffect(Unit) {
                launch {
                    AuthService.flowUser.collect { user ->
                        if (user == null) {
                            backToAuthScreen()
                        }
                    }
                }
            }

            UniversalYogaPlusTheme {
                NavHost(
                    navController = navigation,
                    startDestination = Route.Home,
                ) {
                    composable(Route.Home) { HomeScreen(navigation) }
                    composable(Route.Cloud) { CloudScreen(navigation) }
                    composable(Route.Profile) { ProfileScreen(navigation) }
                }
            }
        }
    }

    private fun backToAuthScreen() {
        Intent(this, AuthenticationActivity::class.java).apply {
            startActivity(this)
            finish()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {

}