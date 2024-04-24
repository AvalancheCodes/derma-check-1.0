package com.medicalblvd.dermacheck

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.NavHost
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.composable
import com.medicalblvd.dermacheck.ui.theme.DermaCheck10Theme
import dagger.hilt.android.AndroidEntryPoint

// DermaCheck
import com.medicalblvd.dermacheck.auth.Signup
import com.medicalblvd.dermacheck.auth.Login
import com.medicalblvd.dermacheck.auth.EditProfile
import com.medicalblvd.dermacheck.pages.Entries
import com.medicalblvd.dermacheck.pages.Feed
import com.medicalblvd.dermacheck.pages.Search
import com.medicalblvd.dermacheck.shared.ComposableNotificationToast

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DermaCheck10Theme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                     color = MaterialTheme.colorScheme.background
                ) {
                    DermaCheckMain("Androids")
                }
            }
        }
    }
}

@Composable
fun DermaCheckMain(name: String, modifier: Modifier = Modifier) {
    val viewModel = hiltViewModel<MainViewModel>();
    val navController = rememberNavController()
    
    // Notification 
    ComposableNotificationToast(viewModel = viewModel)

    NavHost(navController = navController, startDestination = Router.Signup.route ){
        composable(Router.Signup.route){
            Signup(navController = navController, viewModel = viewModel )
        }

        composable(Router.Login.route){
            Login(navController = navController, viewModel = viewModel )
        }

        composable(Router.Feed.route){
            Feed(navController = navController, viewModel = viewModel )
        }

        composable(Router.Search.route){
            Search(navController = navController, viewModel = viewModel )
        }

        composable(Router.Entries.route){
            Entries(navController = navController, viewModel = viewModel )
        }

        composable(Router.EditProfile.route){
            EditProfile(navController = navController, viewModel = viewModel )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    DermaCheck10Theme {
        DermaCheckMain("Androida")
    }
}