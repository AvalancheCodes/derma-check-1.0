package com.medicalblvd.dermacheck.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import com.medicalblvd.dermacheck.MainViewModel
import com.medicalblvd.dermacheck.shared.BottomNavigation
import com.medicalblvd.dermacheck.shared.BottomNavigationItem

@Composable
fun Feed(navController: NavController, viewModel: MainViewModel) {
    val userDataLoading = viewModel.inProgress.value
    val userData = viewModel.userInfo.value

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.LightGray)
    ) {
        // Push the menu down
        Column (
            modifier = Modifier.weight(1f)
        ){
            Text(text = "Feed Screen")
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .background(Color.White)
        ) {
//            todo: add user card
//            UserImageCard(userImage = userData?.imageUrl)
        }

        BottomNavigation(
            selectedItem = BottomNavigationItem.FEED,
            navController = navController
        )
    }

}