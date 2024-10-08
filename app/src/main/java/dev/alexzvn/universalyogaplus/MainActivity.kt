package dev.alexzvn.universalyogaplus

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import dev.alexzvn.universalyogaplus.layout.MainLayout
import dev.alexzvn.universalyogaplus.layout.section.HomeSection
import dev.alexzvn.universalyogaplus.ui.theme.UniversalYogaPlusTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

class MainActivity : ComponentActivity(), CoroutineScope {
    override val coroutineContext: CoroutineContext
        get() =  Dispatchers.Main + job

    private lateinit var job: Job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        job = Job()
        enableEdgeToEdge()
        setContent {
            MainLayout(modifier = Modifier.fillMaxSize()) { padding ->
                HomeSection(padding)
            }
        }

    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    val state = remember { mutableIntStateOf(1) }

    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    UniversalYogaPlusTheme {
        Greeting("Android")
    }
}