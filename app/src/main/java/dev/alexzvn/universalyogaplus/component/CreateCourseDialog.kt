package dev.alexzvn.universalyogaplus.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@Composable
fun CreateCourseDialog(
    onDismiss: () -> Unit = {}
) {
    Dialog(
        properties = DialogProperties(
            dismissOnBackPress = true,
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        ),
        onDismissRequest = onDismiss
    ) {
        Box (
            modifier = Modifier.fillMaxSize().background(Color.White),
        ) {
            Row (
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row (verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onDismiss
                    ) { Icon(Icons.Default.Close, "close") }

                    Text(text = "Create new Course", style = TextStyle(fontSize = 18.sp))
                }

                TextButton (onClick = {}) { Text("Save") }
            }
        }
    }

}

@Preview(showBackground = true)
@Composable
fun CreateCourseDialogPreview() {
    CreateCourseDialog()
}