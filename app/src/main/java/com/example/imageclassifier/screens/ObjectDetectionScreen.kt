package com.example.imageclassifier.screens

import android.Manifest
import android.graphics.PointF
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toComposeRect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.imageclassifier.R
import com.example.imageclassifier.analyzer.ObjectDetectionAnalyzer
import com.example.imageclassifier.presentation.common.components.CameraView
import com.example.imageclassifier.presentation.common.utils.adjustPoint
import com.example.imageclassifier.presentation.common.utils.adjustSize
import com.example.imageclassifier.presentation.common.utils.drawBounds
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.google.mlkit.vision.objects.DetectedObject

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ObjectDetectionScreen(navController: NavHostController){
    val cameraPermissionState =
        rememberPermissionState(permission = Manifest.permission.CAMERA)
    if (cameraPermissionState.status.isGranted){
        ScanSurface(navController)
    } else {
        Column {
            val textToShow = if(cameraPermissionState.status.shouldShowRationale){
                stringResource(R.string.grant_permission)
            } else {
                stringResource(R.string.camera_not_available)
            }

            Text(
                text = textToShow,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(8.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = { cameraPermissionState.launchPermissionRequest() }) {
                Text(stringResource(R.string.request_permission))
            }
        }
    }
}

@Composable
private fun ScanSurface(navController: NavHostController){
    val context = LocalContext.current
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    val detectedObjects = remember { mutableStateListOf<DetectedObject>() }

    val screenWidth = remember { mutableIntStateOf(context.resources.displayMetrics.widthPixels) }
    val screenHeight = remember { mutableIntStateOf(context.resources.displayMetrics.heightPixels) }

    val imageWidth = remember { mutableIntStateOf(480) }
    val imageHeight = remember { mutableIntStateOf(640) }

    Box(modifier = Modifier.fillMaxSize()) {
        CameraView(
            context = context,
            lifecycleOwner = lifecycleOwner,
            analyzer = ObjectDetectionAnalyzer { objects, width, height ->
                detectedObjects.clear()
                detectedObjects.addAll(objects)
                imageWidth.intValue = width
                imageHeight.intValue = height
            }
        )
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(10.dp)
                .fillMaxHeight()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                IconButton(onClick = { navController.navigateUp() }) {
                    Icon(
                        painter = painterResource(R.drawable.arrow_back),
                        contentDescription = "back",
                    )
                }
                Text(
                    text = stringResource(id = R.string.object_detection_title),
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White
                )
            }

            DrawDetectedObjects(detectedObjects, imageHeight.value, imageWidth.value, screenWidth.value, screenHeight.value)
        }
    }
}

@Composable
fun DrawDetectedObjects(objects: List<DetectedObject>, imageWidth: Int, imageHeight: Int, screenWidth: Int, screenHeight: Int) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        objects.forEach {
            val boundingBox = it.boundingBox.toComposeRect()
            val topLeft = adjustPoint(PointF(boundingBox.topLeft.x, boundingBox.topLeft.y), imageWidth, imageHeight, screenWidth, screenHeight)
            val size = adjustSize(boundingBox.size, imageWidth, imageHeight, screenWidth, screenHeight)

            drawBounds(topLeft, size, Color.Yellow, 10f)
        }
    }
}