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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
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
import com.example.imageclassifier.analyzer.BarcodeScanningAnalyzer
import com.example.imageclassifier.presentation.common.components.CameraView
import com.example.imageclassifier.presentation.common.utils.adjustPoint
import com.example.imageclassifier.presentation.common.utils.adjustSize
import com.example.imageclassifier.presentation.common.utils.drawBounds
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.google.mlkit.vision.barcode.common.Barcode
import com.example.imageclassifier.R.string as AppText
import com.example.imageclassifier.R.drawable as AppIcon

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun BarcodeScanningScreen(navController: NavHostController) {
    val cameraPermissionState =
        rememberPermissionState(permission = Manifest.permission.CAMERA)
    if (cameraPermissionState.status.isGranted){
        ScanSurface(navController)
    } else {
        Column {
            val textToShow = if(cameraPermissionState.status.shouldShowRationale){
                stringResource(AppText.grant_permission)
            } else {
                stringResource(AppText.camera_not_available)
            }

            Text(
                text = textToShow,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(8.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = { cameraPermissionState.launchPermissionRequest() }) {
                Text(stringResource(AppText.request_permission))
            }
        }
    }
}

@Composable
private fun ScanSurface(navController: NavHostController) {
    val context = LocalContext.current
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    val detectedBarcode = remember { mutableStateListOf<Barcode>() }

    val screenWidth = remember { mutableStateOf(context.resources.displayMetrics.widthPixels) }
    val screenHeight = remember { mutableStateOf(context.resources.displayMetrics.heightPixels) }

    val imageWidth = remember { mutableStateOf(0) }
    val imageHeight = remember { mutableStateOf(0) }

    Box(modifier = Modifier.fillMaxSize()) {
        CameraView(
            context = context,
            lifecycleOwner = lifecycleOwner,
            analyzer = BarcodeScanningAnalyzer { barcodes, width, height ->
                detectedBarcode.clear()
                detectedBarcode.addAll(barcodes)
                imageWidth.value = width
                imageHeight.value = height
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
                        painter = painterResource(AppIcon.arrow_back),
                        contentDescription = "back",
                    )
                }
                Text(
                    text = stringResource(id = R.string.barcode_detection_title),
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White
                )
            }

            Card(
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .padding(vertical = 10.dp),
            ) {
                Text(
                    text = detectedBarcode.joinToString(separator = "\n") { it.displayValue.toString() },
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier
                        .padding(horizontal = 10.dp)
                        .align(Alignment.CenterHorizontally)
                )
            }
        }
        DrawBarcode(
            barcodes = detectedBarcode,
            imageWidth = imageWidth.value,
            imageHeight = imageHeight.value,
            screenWidth = screenWidth.value,
            screenHeight = screenHeight.value
        )
    }
}

@Composable
fun DrawBarcode(barcodes : List<Barcode>, imageWidth: Int, imageHeight: Int, screenWidth: Int, screenHeight: Int) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        barcodes.forEach { barcode ->
            barcode.boundingBox?.toComposeRect()?.let {
                val topLeft = adjustPoint(PointF(it.topLeft.x, it.topLeft.y), imageWidth, imageHeight, screenWidth, screenHeight)
                val size = adjustSize(it.size, imageWidth, imageHeight, screenWidth, screenHeight)
                drawBounds(topLeft, size, Color.Yellow, 10f)
            }
        }
    }
}