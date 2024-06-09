package io.aoriani.recipe.ui.screens.assistant

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Assistant(uiState: AssistantUiState, onNavigateUp: () -> Unit) {
    Scaffold(topBar = {
        TopAppBar(title = { Text("Assistant") }, navigationIcon = {
            IconButton(onClick = onNavigateUp) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
        })
    }
    ) { innerPadding ->

        when (uiState) {
            is AssistantUiState.Loading -> AssistantLoading(Modifier.padding(innerPadding))
            is AssistantUiState.Loaded -> AssistantReady(uiState)
        }
    }
}


@Composable
fun AssistantLoading(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator(modifier = Modifier.size(100.dp))
        Text(
            "Loading Assistant...",
            style = MaterialTheme.typography.displayMedium,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun AssistantReady(uiState: AssistantUiState.Loaded) {
    val scrollableState = rememberScrollState()
    Column(
        modifier = Modifier
            .verticalScroll(scrollableState)
            .fillMaxSize()
    ) {
        uiState.stepsUrls.forEach {
            AsyncImage(model = it, contentDescription = null, contentScale = ContentScale.FillWidth)
        }
    }
}