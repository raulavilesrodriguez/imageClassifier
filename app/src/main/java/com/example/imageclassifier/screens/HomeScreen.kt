package com.example.imageclassifier.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.imageclassifier.navigation.Screen
import com.example.imageclassifier.presentation.common.components.ImageCard
import com.example.imageclassifier.R.string as AppText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavHostController){
    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                navigationIcon = {
                    IconButton(onClick = {}) {
                        Icon(imageVector = Icons.Default.Menu, contentDescription = "menu")
                    }
                },
                title = { Text(text = stringResource(id = AppText.app_name)) })
        },
        contentColor = MaterialTheme.colorScheme.primary,
        content = {
            LazyColumn(
                modifier = Modifier.padding(start = 12.dp, end = 12.dp, top = 12.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
                contentPadding = it
            ) {
                item {
                    ImageCard(
                        title = stringResource(id = AppText.barcode_detection_title),
                        description = stringResource(id = AppText.barcode_detection_description),
                        imageUrl = stringResource(id = AppText.barcode_detection_url),
                        onCardClick = { navController.navigate(Screen.BarcodeScanning.route) }
                    )
                }

                item {
                    ImageCard(
                        title = stringResource(id = AppText.face_mesh_detection_title),
                        description = stringResource(id = AppText.face_mesh_detection_description),
                        imageUrl = stringResource(id = AppText.face_mesh_detection_url),
                        onCardClick = { navController.navigate(Screen.FaceMeshDetection.route) }
                    )
                }

                item {
                    ImageCard(
                        title = stringResource(id = AppText.text_recognition_title),
                        description = stringResource(id = AppText.text_recognition_description),
                        imageUrl = stringResource(id = AppText.text_recognition_url),
                        onCardClick = { navController.navigate(Screen.TextRecognition.route) }
                    )
                }

                item {
                    ImageCard(
                        title = stringResource(id = AppText.image_labeling_detection_title),
                        description = stringResource(id = AppText.image_labeling_detection_description),
                        imageUrl = stringResource(id = AppText.image_labeling_detection_url),
                        onCardClick = { navController.navigate(Screen.ImageLabeling.route) }
                    )
                }

                item {
                    ImageCard(
                        title = stringResource(id = AppText.object_detection_title),
                        description = stringResource(id = AppText.object_detection_description),
                        imageUrl = stringResource(id = AppText.object_detection_url),
                        onCardClick = { navController.navigate(Screen.ObjectDetection.route) }
                    )
                }
            }
        }
    )
}