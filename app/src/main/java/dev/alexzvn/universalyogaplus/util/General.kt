package dev.alexzvn.universalyogaplus.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import com.aventrix.jnanoid.jnanoid.NanoIdUtils
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.random.Random

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

fun <T> List<T>.pickRandom(): T? = when (isEmpty()) {
    true -> null
    false -> get(Random.nextInt(size))
}

fun <T> MutableList<T>.popRandom(): T? = when (isEmpty()) {
    true -> null
    false -> removeAt(Random.nextInt(size))
}

fun DocumentReference.observe() = callbackFlow {
    val listener = addSnapshotListener { snapshot, e ->
        if (e != null) {
            return@addSnapshotListener
        }

        if (snapshot == null) return@addSnapshotListener

        trySend(snapshot)
    }

    awaitClose { listener.remove() }
}

fun Query.observe() = callbackFlow {
    val listener = addSnapshotListener { snapshot, e ->
        if (e != null) {
            return@addSnapshotListener
        }

        if (snapshot == null) return@addSnapshotListener

        trySend(snapshot)
    }

    awaitClose { listener.remove() }
}