package io.aoriani.recipe.ui.screens.landing

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.aoriani.recipe.ui.theme.RecipeTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Landing(
    landingUiState: LandingUiState,
    onIngredientsFound: (ingredients: List<String>, description: String) -> Unit = { _, _ -> }
) {
    Scaffold(topBar = {
        TopAppBar(title = {
            Text("Recipe")
        }
        )
    }
    ) { innerPadding ->
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            when (landingUiState) {
                is LandingUiState.Init -> LandingReadyState(landingUiState)
                is LandingUiState.Resolving -> LandingPageResolving()
                is LandingUiState.NoIngredients -> LandingPageNoIngredients(landingUiState)
                is LandingUiState.IngredientsFound -> {
                    onIngredientsFound(landingUiState.ingredients, landingUiState.description)
                    landingUiState.consume()
                }
            }

        }

    }
}

@Composable
fun LandingReadyState(uiState: LandingUiState.Init) {
    val cameraLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
            if (bitmap != null) uiState.setImageBitmap(bitmap)
        }

    val photoPicker =
        rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) uiState.setImageUri(uri)
        }
    TextField(
        value = uiState.text,
        onValueChange = uiState.onTextChanged,
        placeholder = { Text("Describe what you want about you recipe...") },
        minLines = 2,
        maxLines = 2,
        label = { Text("I want a recipe that") })
    Spacer(modifier = Modifier.height(64.dp))

    Row {
        Button(onClick = cameraLauncher::launch) {
            Text("Take Picture")
        }
        Spacer(modifier = Modifier.width(16.dp))
        Button(onClick = { photoPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) }) {
            Text("Pick Picture")
        }
    }

    Spacer(modifier = Modifier.height(64.dp))

    Button(onClick = uiState.send, enabled = uiState.sendEnabled) {
        Text("Send")
        Spacer(modifier = Modifier.width(8.dp))
        Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null)
    }
}

@Composable
fun LandingPageResolving() {
    CircularProgressIndicator(modifier = Modifier.size(100.dp))
    Text("Discovering Ingredients...", style = MaterialTheme.typography.displayMedium)
}


@Composable
fun LandingPageNoIngredients(uiState: LandingUiState.NoIngredients) {
    AlertDialog(
        onDismissRequest = uiState.onDismiss,
        confirmButton = { TextButton(onClick = uiState.onDismiss) { Text("Okay") } },
        text = { Text("No ingredients found.") })
}

@Preview
@Composable
fun LandingPreviewNormal() {
    RecipeTheme {
        Landing(LandingUiState.Init())
    }
}

@Preview
@Composable
fun LandingPreviewResolving() {
    RecipeTheme {
        Landing(LandingUiState.Resolving)
    }
}

@Preview
@Composable
fun LandingPreviewNoIngredients() {
    RecipeTheme {
        Landing(LandingUiState.NoIngredients())
    }
}