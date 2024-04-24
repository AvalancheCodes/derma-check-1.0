package com.medicalblvd.dermacheck.shared

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.annotation.ExperimentalCoilApi
import coil.compose.ImagePainter
import coil.compose.rememberImagePainter

import com.medicalblvd.dermacheck.MainViewModel
import com.medicalblvd.dermacheck.R
import com.medicalblvd.dermacheck.Router


@Composable
fun ComposableNotificationToast(viewModel: MainViewModel) {
    val notificationState = viewModel.notification.value
    val notificationMessage = notificationState?.getContentOrNull()
    if (notificationMessage != null) {
        Toast.makeText(
            LocalContext.current,
            notificationMessage,
            Toast.LENGTH_LONG).show()
    }
}

@Composable
fun ComposableInProgressSpinner() {
    Row(
        modifier = Modifier
            .alpha(0.5f)
            .background(Color.LightGray)
            .clickable(enabled = false) { }
            .fillMaxSize(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun IsUserSignedIn(viewModel: MainViewModel, navController: NavController) {
    val alreadyLoggedIn = remember { mutableStateOf(false) }
    val signedIn by viewModel.signedIn.observeAsState(initial = false)

    // When 'signedIn' becomes true, navigate to the feed route
    if (signedIn) {
        LaunchedEffect(Unit) {
            navController.navigate(Router.Feed.route) {
                popUpTo(0)
            }
        }
    }
}

@OptIn(ExperimentalCoilApi::class)
@Composable
fun CommonImage(
    data: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop
) {
    val painter = rememberImagePainter(data = data)
    Image(
        painter = painter,
        contentDescription = null,
        modifier = modifier,
        contentScale = contentScale
    )
    if (painter.state is ImagePainter.State.Loading) {
        ComposableInProgressSpinner()
    }
}

@Composable
fun UserImageCard(
    userImage: String?,
    modifier: Modifier = Modifier
) {
    Card(shape = CircleShape, modifier = modifier) {
        if (userImage.isNullOrEmpty()) {
            Image(
                painter = painterResource(id = R.drawable.ic_user),
                contentDescription = null,
                colorFilter = ColorFilter.tint(Color.Gray)
            )
        } else {
            CommonImage(data = userImage)
        }
    }
}

@Composable
fun CommonDivider() {
    Divider(
        color = Color.LightGray,
        thickness = 1.dp,
        modifier = Modifier
            .alpha(0.3f)
            .padding(top = 8.dp, bottom = 8.dp)
    )
}
