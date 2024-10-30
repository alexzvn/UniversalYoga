package dev.alexzvn.universalyogaplus.layout.screen

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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import dev.alexzvn.universalyogaplus.R
import dev.alexzvn.universalyogaplus.component.ValidateSignal
import dev.alexzvn.universalyogaplus.component.ValidatedInput
import dev.alexzvn.universalyogaplus.service.AuthService
import dev.alexzvn.universalyogaplus.util.Rule
import dev.alexzvn.universalyogaplus.util.asPainter
import kotlinx.coroutines.launch

private data class Credential(
    val username: String = "",
    val password: String = ""
)

@Composable
fun LoginScreen (
    navigation: NavController,
    onBypass: () -> Unit = {},
) {
    val scope = rememberCoroutineScope()
    val snackbar = remember { SnackbarHostState() }

    Scaffold (
        snackbarHost = { SnackbarHost(snackbar) }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding),
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
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp)
            ) {
                var credential by remember { mutableStateOf(Credential()) }
                var visible by remember { mutableStateOf(false) }
                val signal = ValidateSignal()

                ValidatedInput(
                    signal = signal,
                    label = { Text("Email") },
                    value = credential.username,
                    placeholder = { Text("Your email address") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    textStyle = TextStyle(fontWeight = FontWeight.Bold),
                    onChange = { credential = credential.copy(username = it) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    validate = { Rule.isEmail(credential.username) },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Person, contentDescription = "Person")
                    },
                    error = { Text(text = "Your email address is invalid", color = Color.Red) }
                )

                ValidatedInput(
                    signal = signal,
                    value = credential.password,
                    leadingIcon = { Icon(imageVector = Icons.Default.Lock, "Password") },
                    onChange = { credential = credential.copy(password = it) },
                    label = { Text("Password") },
                    placeholder = { Text("Your password") },
                    singleLine = true,
                    textStyle = TextStyle(fontWeight = FontWeight.Bold),
                    visualTransformation = when (visible) {
                        false -> PasswordVisualTransformation()
                        true -> VisualTransformation.None
                    },
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

                var signing by remember { mutableStateOf(false) }
                val login = login@{
                    if (signal.request().not()) {
                        return@login
                    }

                    signing = true


                    scope.launch {
                        AuthService.login(credential.username, credential.password).apply {
                            signing = false
                            when (this) {
                                null -> snackbar.showSnackbar("Your credentials is not valid")
                                else -> onBypass()
                            }
                        }
                    }
                }

                Button (
                    modifier = Modifier.fillMaxWidth(),
                    enabled = signing.not(),
                    onClick = login,
                ) {
                    if (signing) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp).aspectRatio(1f))
                    } else {
                        Text("Login")
                    }
                }
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
fun LoginScreenPreview() {
    LoginScreen(rememberNavController())
}