package dev.alexzvn.universalyogaplus.util

import androidx.compose.runtime.Composable
import dev.alexzvn.universalyogaplus.ui.theme.UniversalYogaPlusTheme


fun theming(block: @Composable () -> Unit) = @Composable {
    UniversalYogaPlusTheme(content = block)
}