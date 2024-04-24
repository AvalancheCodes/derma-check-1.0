package com.medicalblvd.dermacheck.shared

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.NavController
import com.medicalblvd.dermacheck.Router
import com.medicalblvd.dermacheck.data.Shared


fun navigateTo(navController: NavController, dest: Router, vararg params: Shared) {
    for (param in params) {
        navController.currentBackStackEntry?.arguments?.putParcelable(param.name, param.value)
    }
    navController.navigate(dest.route) {
        popUpTo(dest.route)
        launchSingleTop = true
    }
}

