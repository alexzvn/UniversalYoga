package dev.alexzvn.universalyogaplus.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource

@Composable
fun Int.asPainter(): Painter = painterResource(this)