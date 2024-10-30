package dev.alexzvn.universalyogaplus.layout.screen

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import dev.alexzvn.universalyogaplus.layout.MainLayout
import dev.alexzvn.universalyogaplus.layout.NavigateSection
import dev.alexzvn.universalyogaplus.service.AuthService
import com.google.firebase.auth.UserInfo


@Composable
fun ProfileScreen(
    navigation: NavController,
    user: UserInfo? = null
) {
    val user = user ?: AuthService.user

    val name = user?.displayName ?: user?.email?.run {
        when (val index = indexOf('@')) {
            -1 -> null
            lastIndex -> "A"
            else -> substring(0, index)
        }
    }

    val avatar = name?.let {
        when (it.length) {
            0 -> "A"
            else -> it[0].uppercase()
        }
    }


    MainLayout (
        section = NavigateSection.Profile,
        onNavigate = { NavigateSection.handle(it, navigation) },
    ) { padding ->
        Column (
            modifier = Modifier.padding(padding)
        ) {
            Row (
                modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Your Profile", fontSize = 24.sp, fontWeight = FontWeight.W500)

                TextButton(onClick = { AuthService.logout() }) {
                    Text("Sign Out")
                    Spacer(Modifier.width(5.dp))
                    Icon(Icons.AutoMirrored.Filled.ExitToApp, "Exit")
                }
            }

            Box (
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .padding(20.dp)
                    .size(150.dp)
                    .clip(CircleShape)
                    .border(2.dp, Color(0xff_ccc4cf), CircleShape)
                    .background(Color(0xff_f7f7f7))
                    .align(Alignment.CenterHorizontally)
                    .alpha(.6f),
            ) {
                Text("$avatar", fontSize = 72.sp, fontWeight = FontWeight.Light)
            }

            Column (
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Hello, $name", fontSize = 16.sp, fontWeight = FontWeight.W500)

                if (!user?.email.isNullOrEmpty()) {
                    Text(user?.email!!, modifier = Modifier.alpha(.6f))
                }
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
fun ProfileScreenPreview() {
    val user = object : UserInfo {
        override fun getPhotoUrl(): Uri? {
            return null
        }

        override fun getDisplayName(): String? {
            return null
        }

        override fun getEmail(): String {
            return "someone@gmail.com"
        }

        override fun getPhoneNumber(): String? {
            return null
        }

        override fun getProviderId(): String {
            return "email"
        }

        override fun getUid(): String {
            return "0123456"
        }

        override fun isEmailVerified(): Boolean {
            return true
        }

    }

    ProfileScreen(rememberNavController(), user)
}