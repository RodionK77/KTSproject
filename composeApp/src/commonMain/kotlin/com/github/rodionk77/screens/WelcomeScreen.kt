package com.github.rodionk77.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import ktsproject.composeapp.generated.resources.Res
import ktsproject.composeapp.generated.resources.broken_image_icon
import ktsproject.composeapp.generated.resources.go_to_login
import ktsproject.composeapp.generated.resources.hide_image
import ktsproject.composeapp.generated.resources.hourglass_icon
import ktsproject.composeapp.generated.resources.ready_to_work
import ktsproject.composeapp.generated.resources.show_image
import ktsproject.composeapp.generated.resources.welcome
import ktsproject.composeapp.generated.resources.welcome_image
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun WelcomeScreen(onNavigateToLogin: () -> Unit) {

    var showImage by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        AnimatedVisibility(showImage) {
            AsyncImage(
                model = "https://cdni.iconscout.com/illustration/premium/thumb/login-illustration-svg-download-png-8333958.png",
                contentDescription = stringResource(Res.string.welcome_image),
                modifier = Modifier
                    .size(250.dp),
                error = painterResource(Res.drawable.broken_image_icon),
                placeholder = painterResource(Res.drawable.hourglass_icon)
            )
        }
        TextButton(onClick = {showImage = !showImage}){
            Text(
                text =
                    if(showImage) stringResource(Res.string.hide_image)
                    else stringResource(Res.string.show_image),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.secondary,
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = stringResource(Res.string.welcome),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground
        )

        Text(
            text = stringResource(Res.string.ready_to_work),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.padding(top = 8.dp)
        )

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onNavigateToLogin,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
                .height(50.dp),
            shape = MaterialTheme.shapes.medium,
        ) {
            Text(
                text = stringResource(Res.string.go_to_login),
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}