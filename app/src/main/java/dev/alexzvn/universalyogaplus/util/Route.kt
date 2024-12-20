package dev.alexzvn.universalyogaplus.util

data object Route {
    val Home = "home"
    val Cloud = "cloud"
    val Profile = "profile"

    data object Course {
        val List = "course/list"
        val Create = "course/create"
        val Edit = "course/edit/{id}"
        val View = "course/view/{id}"

        fun edit(id: String) = "course/edit/$id"
        fun view(id: String) = "course/view/$id"
    }

    data object cloud {
        val Home = "cloud/home"
        val View = "cloud/view/{id}"

        fun view(id: String) = "cloud/view/$id"
    }

    data object Auth {
        val SplashScreen = "splash"
        val Login = "auth/login"
    }
}
