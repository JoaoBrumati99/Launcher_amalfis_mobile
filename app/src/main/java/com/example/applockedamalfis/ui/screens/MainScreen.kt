package com.example.applockedamalfis.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.applockedamalfis.viewmodel.AmalfisViewModel

@Composable
fun MainScreen(viewModel: AmalfisViewModel) {
    Box(modifier = Modifier.fillMaxSize()) {
        HomeScreen(viewModel = viewModel)
    }
}
