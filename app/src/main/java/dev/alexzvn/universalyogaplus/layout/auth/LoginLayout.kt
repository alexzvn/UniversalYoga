package dev.alexzvn.universalyogaplus.layout.auth

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.alexzvn.universalyogaplus.R
import dev.alexzvn.universalyogaplus.layout.AuthLayout
import dev.alexzvn.universalyogaplus.util.asPainter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class ValidatedInput(
    var email: Boolean?,
    var password: Boolean?
)

@Composable
fun LoginLayout() {
    val scope = rememberCoroutineScope()

    AuthLayout(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(
                modifier = Modifier
                    .defaultMinSize(minHeight = 100.dp)
                    .fillMaxHeight(.1f)
            )

            Text(
                style = TextStyle(fontSize = 32.sp, fontWeight = FontWeight.Bold),
                text ="Universal Yoga Plus"
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                style = TextStyle(fontSize = 24.sp),
                text ="Login"
            )

            Spacer(modifier = Modifier.height(40.dp))

            Column (
                modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp)
            ) {
                var email by remember { mutableStateOf("") }
                var password by remember { mutableStateOf("") }
                var visible by remember { mutableStateOf(false) }
                var validated by remember { mutableStateOf(ValidatedInput(null, null)) }
                var mounted by remember { mutableStateOf(false) }
                var signing by remember { mutableStateOf(false) }

                LaunchedEffect(Unit) { mounted = true }

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Your Email") },
                    maxLines = 1,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    textStyle = TextStyle(fontWeight = FontWeight.Bold),
                    modifier = Modifier.fillMaxWidth().onFocusChanged {
                        if (!mounted) return@onFocusChanged

                        validated = when (it.isFocused) {
                            true -> validated.copy(email = null)
                            false -> validated.copy(email = email.contains("@"))
                        }
                    },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Person, contentDescription = "Person")
                    },
                    supportingText = {
                        if (validated.email == false) {
                            Text(
                                text = "Your email address is invalid",
                                color = Color.Red
                            )
                        }
                    },
                )

                OutlinedTextField(
                    value = password,
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Lock, "Password")
                    },
                    onValueChange = { password = it },
                    label = { Text("Your Password") },
                    visualTransformation = when (visible) {
                        false -> PasswordVisualTransformation()
                        true -> VisualTransformation.None
                    },
                    maxLines = 1,
                    textStyle = TextStyle(fontWeight = FontWeight.Bold),
                    modifier = Modifier.fillMaxWidth().padding(top = 5.dp),
                    trailingIcon = {
                        val image = when (visible) {
                            true -> R.drawable.baseline_eye_24
                            false -> R.drawable.baseline_eye_off_24
                        }.asPainter()

                        IconButton(onClick = { visible = !visible }) {
                            Icon(painter = image, contentDescription = "Visibility")
                        }
                    }
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button (
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        validated = validated.copy(email = email.contains('@'))

                        if (validated.email != true || signing) return@Button

                        signing = true

                        scope.launch {
                            sleep(2000)
                            signing = false
                        }
                    },
                    enabled = !signing
                ) {
                    when (signing) {
                        true -> CircularProgressIndicator(modifier = Modifier.size(24.dp).aspectRatio(1f))
                        false -> Text("Sign In")
                    }
                }
            }
        }
    }
}

suspend fun sleep(time: Long) = run {
    withContext(Dispatchers.IO) {
        Thread.sleep(time)
    }
}

@Preview(showBackground = true)
@Composable
fun LoginLayoutPreview() {
    LoginLayout()
}