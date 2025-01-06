package com.example.imageclassifier.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.imageclassifier.screens.BarcodeScanningScreen
import com.example.imageclassifier.screens.FaceMeshDetectionScreen
import com.example.imageclassifier.screens.HomeScreen
import com.example.imageclassifier.screens.ImageLabelingScreen
import com.example.imageclassifier.screens.ObjectDetectionScreen
import com.example.imageclassifier.screens.TextRecognitionScreen
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

internal sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object FaceMeshDetection : Screen("face_mesh_detection")
    data object TextRecognition : Screen("text_recognition")
    data object ObjectDetection : Screen("object_detection")
    data object BarcodeScanning : Screen("barcode_scanning")
    data object ImageLabeling : Screen("image_labeling")
}


@Composable
fun AppNavigation(navController: NavHostController = rememberNavController()) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ){
        composable(route = Screen.Home.route){
            HomeScreen(navController)
        }
        composable(route = Screen.TextRecognition.route) {
            TextRecognitionScreen(navController)
        }
        composable(route = Screen.ObjectDetection.route) {
            ObjectDetectionScreen(navController)
        }
        composable(route = Screen.FaceMeshDetection.route) {
            FaceMeshDetectionScreen(navController)
        }
        composable(route = Screen.BarcodeScanning.route) {
            BarcodeScanningScreen(navController)
        }
        composable(route = Screen.ImageLabeling.route) {
            ImageLabelingScreen(navController)
        }
    }
}
