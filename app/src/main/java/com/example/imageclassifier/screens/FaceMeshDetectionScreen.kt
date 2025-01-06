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
import com.example.imageclassifier.analyzer.FaceMeshDetectionAnalyzer
import com.example.imageclassifier.presentation.common.components.CameraView
import com.example.imageclassifier.presentation.common.utils.adjustPoint
import com.example.imageclassifier.presentation.common.utils.adjustSize
import com.example.imageclassifier.presentation.common.utils.drawBounds
import com.example.imageclassifier.presentation.common.utils.drawLandmark
import com.example.imageclassifier.presentation.common.utils.drawTriangle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.google.mlkit.vision.facemesh.FaceMesh

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun FaceMeshDetectionScreen(
    navController: NavHostController
){
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
private fun ScanSurface(navController: NavHostController) {
    val context = LocalContext.current
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    val faces = remember { mutableStateListOf<FaceMesh>() }

    val screenWidth = remember { mutableStateOf(context.resources.displayMetrics.widthPixels) }
    val screenHeight = remember { mutableStateOf(context.resources.displayMetrics.heightPixels) }

    val imageWidth = remember { mutableStateOf(0) }
    val imageHeight = remember { mutableStateOf(0) }

    Box(modifier = Modifier.fillMaxSize()) {
        CameraView(
            context = context,
            lifecycleOwner = lifecycleOwner,
            analyzer = FaceMeshDetectionAnalyzer { meshes, width, height ->
                faces.clear()
                faces.addAll(meshes)
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
                        painter = painterResource(R.drawable.arrow_back),
                        contentDescription = "back",
                    )
                }
                Text(
                    text = stringResource(id = R.string.face_mesh_detection_title),
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White
                )
            }
        }
        DrawFaces(faces = faces, imageHeight.value, imageWidth.value, screenWidth.value, screenHeight.value)
    }
}

@Composable
fun DrawFaces(faces: List<FaceMesh>, imageWidth: Int, imageHeight: Int, screenWidth: Int, screenHeight: Int) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        faces.forEach { face ->
            val boundingBox = face.boundingBox.toComposeRect()
            val topLeft = adjustPoint(PointF(boundingBox.topLeft.x, boundingBox.topLeft.y), imageWidth, imageHeight, screenWidth, screenHeight)
            val size = adjustSize(boundingBox.size, imageWidth, imageHeight, screenWidth, screenHeight)
            drawBounds(topLeft, size, Color.Yellow, 5f)

            face.allPoints.forEach {
                val landmark = adjustPoint(PointF(it.position.x, it.position.y), imageWidth, imageHeight, screenWidth, screenHeight)
                drawLandmark(landmark, Color.Cyan, 3f)
            }

            face.allTriangles.forEach { triangle ->
                val points = triangle.allPoints.map {
                    adjustPoint(PointF(it.position.x, it.position.y), imageWidth, imageHeight, screenWidth, screenHeight)
                }
                drawTriangle(points, Color.Cyan, 1f)
            }
        }
    }
}