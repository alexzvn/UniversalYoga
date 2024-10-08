package dev.alexzvn.universalyogaplus.layout

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import dev.alexzvn.universalyogaplus.ui.theme.UniversalYogaPlusTheme

@Composable
fun AuthLayout(modifier: Modifier, content: @Composable (padding: PaddingValues) -> Unit) {
    UniversalYogaPlusTheme {
        Scaffold (modifier = modifier) { padding ->
            content(padding)
        }
    }
}

@Composable
@Preview(showBackground = true)
fun AuthLayoutPreview() {
    AuthLayout(modifier = Modifier.fillMaxSize()) {}
}
