package com.medicalblvd.dermacheck

sealed class Router (val route: String) {
    // Sighup
    object Signup : Router("signup")
    object Login : Router("login")

    // Bottom Nav Bar
    object Feed : Router("feed")
    object Search : Router("search")
    object Entries : Router("entries")

    object EditProfile : Router("editProfile")

    object NewEntry : Router("\"newentry/{imageUri}\""){
        fun createRoute(uri: String) = "newpost/$uri"
    }
}