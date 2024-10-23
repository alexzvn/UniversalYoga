package dev.alexzvn.universalyogaplus.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import com.aventrix.jnanoid.jnanoid.NanoIdUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun Int.asPainter(): Painter = painterResource(this)

fun String.tryToInt(): Int? {
    return try {
        Integer.parseInt(this)
    } catch (e: Exception) {
        null
    }
}

fun String.padStart(length: Int, char: Char = ' '): String {
    if (this.length >= length) return this
    val padding = (1..length - this.length).joinToString("") { char.toString() }
    return padding + this
}

fun nanoid(): String = NanoIdUtils.randomNanoId()

suspend fun sleep(ms: Long) {
    withContext(Dispatchers.IO) {
        Thread.sleep(ms)
    }
}

object Scope {
    object IO {
        inline fun launch(crossinline block: suspend CoroutineScope.() -> Unit) {
            CoroutineScope(Dispatchers.IO).launch { block() }
        }
    }

    object Main {
        inline fun launch(crossinline block: suspend CoroutineScope.() -> Unit) {
            CoroutineScope(Dispatchers.Main).launch { block() }
        }
    }

    inline fun from(scope: CoroutineScope, crossinline block: suspend CoroutineScope.() -> Unit) {
        scope.launch { block() }
    }
}