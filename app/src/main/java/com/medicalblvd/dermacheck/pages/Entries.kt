package com.medicalblvd.dermacheck.pages

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.annotation.ExperimentalCoilApi
import com.medicalblvd.dermacheck.MainViewModel
import com.medicalblvd.dermacheck.Router
import com.medicalblvd.dermacheck.shared.BottomNavigation
import com.medicalblvd.dermacheck.shared.BottomNavigationItem
import com.medicalblvd.dermacheck.shared.navigateTo
import coil.compose.ImagePainter
import coil.compose.rememberImagePainter

import com.medicalblvd.dermacheck.R
import com.medicalblvd.dermacheck.shared.CommonImage
import com.medicalblvd.dermacheck.shared.ComposableInProgressSpinner
import com.medicalblvd.dermacheck.shared.UserImageCard


@Composable
fun Entries(navController: NavController, viewModel: MainViewModel) {
    val userInfo = viewModel.userInfo.value
    val newPostImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
    ) { uri ->
        uri?.let {
            val encoded = Uri.encode(it.toString())
            val route = Router.NewEntry.createRoute(encoded)
            navController.navigate(route)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.LightGray)
    ){
        Column(modifier = Modifier.weight(1f)) {
            Row {
                ProfileImage(userInfo?.imageUrl) {
                    newPostImageLauncher.launch("image/*")
                }

                Text(
                    text = "25", //"${posts.size}\nposts",
                    modifier = Modifier
                        .weight(1f)
                        .align(Alignment.CenterVertically),
                    textAlign = TextAlign.Center
                )
                Text(
                    text =  "16", //"$followers\nfollowers",
                    modifier = Modifier
                        .weight(1f)
                        .align(Alignment.CenterVertically),
                    textAlign = TextAlign.Center
                )
                Text(
                    text =  "12", //"${userData?.following?.size ?: 0}\nfollowing",
                    modifier = Modifier
                        .weight(1f)
                        .align(Alignment.CenterVertically),
                    textAlign = TextAlign.Center
                )
            }
            Column(modifier = Modifier.padding(8.dp)) {
                val usernameDisplay =
                    if (userInfo?.username == null) "" else "@${userInfo?.username}"
                Text(text = userInfo?.name ?: "", fontWeight = FontWeight.Bold)
                Text(text = usernameDisplay)
                Text(text = userInfo?.bio ?: "")
            }
            OutlinedButton(
                onClick = { navigateTo(navController, Router.EditProfile) },
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(backgroundColor = Color.Transparent),
                elevation = ButtonDefaults.elevation(
                    defaultElevation = 0.dp,
                    pressedElevation = 0.dp,
                    disabledElevation = 0.dp
                ),
                shape = RoundedCornerShape(10)
            ) {
                Text(text = "Edit Profile", color = Color.Black)
            }

        }
        BottomNavigation(
            selectedItem = BottomNavigationItem.ENTRIES,
            navController = navController
        )
    }
}

@Composable
fun ProfileImage(imageUrl: String?, onClick: () -> Unit) {
    Box(modifier = Modifier
        .padding(top = 16.dp)
        .clickable { onClick.invoke() }) {

        UserImageCard(
            userImage = imageUrl, modifier = Modifier
                .padding(8.dp)
                .size(80.dp)
        )

        Card(
            shape = CircleShape,
            border = BorderStroke(width = 2.dp, color = Color.White),
            modifier = Modifier
                .size(32.dp)
                .align(Alignment.BottomEnd)
                .padding(bottom = 8.dp, end = 8.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_add),
                contentDescription = null,
                modifier = Modifier
                    .background(Color.Blue)
            )
        }
    }
}


