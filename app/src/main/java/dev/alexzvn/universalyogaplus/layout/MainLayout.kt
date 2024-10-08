package dev.alexzvn.universalyogaplus.layout

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import dev.alexzvn.universalyogaplus.R
import dev.alexzvn.universalyogaplus.ui.theme.UniversalYogaPlusTheme
import dev.alexzvn.universalyogaplus.util.asPainter

sealed class NavigateSection() {}
data object Home : NavigateSection()
data object Cloud : NavigateSection()
data object Profile : NavigateSection()

@Composable
fun MainLayout(
    modifier: Modifier,
    section: NavigateSection = Home,
    onNavigate: (NavigateSection) -> Unit = {},
    content: @Composable (padding: PaddingValues) -> Unit
) {
    UniversalYogaPlusTheme {
        Scaffold (
            modifier = modifier,

            bottomBar = {
                NavigationBar(
                    containerColor = Color.White
                ) {

                    val updateState: (NavigateSection) -> Unit = { value ->
                        if (section != value) {
                            section.apply(onNavigate)
                        }
                    }

                    NavigationBarItem(
                        icon = { Icon(Icons.Filled.Home, contentDescription = "Home") },
                        label = { Text("Home") },
                        selected = section is Home,
                        onClick = { updateState(Home) },
                    )

                    NavigationBarItem(
                        icon = { Icon(R.drawable.baseline_cloud_24.asPainter(), contentDescription = "Home") },
                        label = { Text("Cloud") },
                        selected = section is Cloud,
                        onClick = { updateState(Cloud) },
                    )

                    NavigationBarItem(
                        icon = { Icon(Icons.Filled.Person, contentDescription = "Home") },
                        label = { Text("Profile") },
                        selected = section is Profile,
                        onClick = { updateState(Profile) },
                    )
                }
            }
        ) { padding ->
            content(padding)
        }
    }
}


@Preview(showBackground = true)
@Composable
fun MainLayoutPreview() {
    MainLayout(modifier = Modifier.fillMaxSize()) { }
}