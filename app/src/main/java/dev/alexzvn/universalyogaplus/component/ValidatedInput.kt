package dev.alexzvn.universalyogaplus.component

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

class ValidateSignal {
    private val handlers = mutableListOf<() -> Boolean>()

    fun request(): Boolean {
        var success = true

        for (validate in handlers) {
            Log.d("validating", "Validate signal ${validate()}")
            success = validate() && success
        }

        return success
    }

    fun onRequest(handler: () -> Boolean) {
        handlers.add(handler)
    }

    fun removeListener(handler: () -> Boolean) {
        handlers.remove(handler)
    }
}

@Composable
fun ValidatedInput(
    modifier: Modifier = Modifier,
    onChange: (String) -> Unit = {},
    label: @Composable () -> Unit = {},
    error: @Composable () -> Unit = {},
    onValidate: (Boolean) -> Unit = {},
    placeholder: @Composable (() -> Unit)? = null,
    suffix: @Composable (() -> Unit)? = null,
    prefix: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    singleLine: Boolean = false,
    readOnly: Boolean = false,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    minLines: Int = 1,
    validate: (String) -> Boolean = { true },
    value: String = "",
    textStyle: TextStyle = LocalTextStyle.current,
    signal: ValidateSignal = ValidateSignal()
) {
    var initialized by remember { mutableStateOf(false) }
    var focused by remember { mutableStateOf(false) }
    var isValid by remember { mutableStateOf(false) }

    val validator = validator@{
        return@validator validate(value).also {
            isValid = it
            onValidate(it)
        }
    }

    val signalValidator = signal@{
        val status = validator()
        initialized = true
        focused = false

        return@signal status
    }

    LaunchedEffect(value) { validator() }
    LaunchedEffect(Unit) { initialized = false }

    signal.onRequest(signalValidator)

    OutlinedTextField(
        modifier = modifier.onFocusChanged {
            focused = it.isFocused
            if (!initialized) {
                initialized = true
            }
        },
        value = value,
        label = label,
        placeholder = placeholder,
        suffix = suffix,
        prefix = prefix,
        textStyle = textStyle,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        visualTransformation = visualTransformation,
        readOnly = readOnly,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        onValueChange = { onChange(it) },
        singleLine = singleLine,
        maxLines = maxLines,
        minLines = minLines,
        supportingText = support@{
            if (!initialized) {
                return@support
            }

            if (!focused && !isValid) {
                error()
            }
        }
    )
}

@Composable
@Preview(showBackground = true)
fun ValidatedInputPreview() {
    var inputA by remember { mutableStateOf("") }
    var inputB by remember { mutableStateOf("") }

    val signal = ValidateSignal()

    Column (
        modifier = Modifier.padding(16.dp)
    ) {
        ValidatedInput(
            value = inputA,
            label = { Text("mail") },
            onChange = { inputA = it },
            validate = { it.contains("@") },
            error = { Text("Invalid email", color = Color.Red) },
            signal = signal
        )

        ValidatedInput(
            value = inputB,
            label = { Text("mail 2") },
            onChange = { inputB = it },
            validate = { it.contains("@") },
            error = { Text("Invalid email", color = Color.Red) },
            signal = signal
        )

        Button(onClick = { signal.request() }) {
            Text("Validate")
        }
    }
}