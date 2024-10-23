package dev.alexzvn.universalyogaplus.layout.screen


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import dev.alexzvn.universalyogaplus.service.AuthService
import dev.alexzvn.universalyogaplus.util.Route

@Composable
fun SplashScreen(
    navigation: NavController,
    onBypass: () -> Unit = {},
) {
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        when (AuthService.user) {
            null -> navigation.navigate(Route.Auth.Login)
            else -> onBypass()
        }
    }

    Column (
        Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Universal Yoga", style = TextStyle(fontSize = 45.sp, fontWeight = FontWeight.SemiBold))
        Text(text = "Greenwich University 2024", Modifier.alpha(.7f))

        Spacer(Modifier.height(200.dp))
    }
}

@Composable
@Preview(showBackground = true)
fun SplashScreenPreview() {
    SplashScreen(rememberNavController())
}