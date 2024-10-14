package dev.alexzvn.universalyogaplus.util

data object Route {
    val Home = "home"
    val Cloud = "cloud"
    val Profile = "profile"

    data object Course {
        val Edit = "course/edit"
        val View = "course/view"
    }

    data object Auth {
        val SplashScreen = "splash"
        val Login = "auth/login"
    }
}
