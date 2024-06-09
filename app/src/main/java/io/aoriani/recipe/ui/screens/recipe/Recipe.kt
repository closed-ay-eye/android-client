package io.aoriani.recipe.ui.screens.recipe

import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import io.aoriani.recipe.ui.theme.RecipeTheme


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Recipe(uiState: RecipeUiState, onNavigateUp: () -> Unit = {}) {
    Scaffold(topBar = {
        TopAppBar(title = { Text("Recipe") }, navigationIcon = {
            IconButton(onClick = onNavigateUp) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
        })
    }
    ) { innerPadding ->

        when (uiState) {
            RecipeUiState.Finding -> RecipeFinding(modifier = Modifier.padding(innerPadding))
            is RecipeUiState.Error -> RecipeError(uiState, onNavigateUp)
            is RecipeUiState.Found -> ShowRecipe(uiState, Modifier.padding(innerPadding))
        }

    }
}


@Composable
fun ShowRecipe(uiState: RecipeUiState.Found, modifier: Modifier = Modifier) {
    val scrollableState = rememberScrollState()
    Column(
        modifier = modifier
            .verticalScroll(scrollableState)
            .fillMaxSize()
            .padding(horizontal = 24.dp)
    ) {
        Text(
            uiState.name,
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            uiState.description,
            style = MaterialTheme.typography.bodyMedium,
            fontStyle = FontStyle.Italic,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        val servingText = buildAnnotatedString {
            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                append("Servings")
            }
            append(" ")
            append(uiState.servings)
        }

        Text(
            servingText,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.fillMaxWidth()
        )

        val servingSizeText = buildAnnotatedString {
            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                append("Servings Size")
            }
            append(" ")
            append(uiState.servingsSize)
        }

        Text(
            servingSizeText,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))


        if (uiState.image != null) {
            AsyncImage(
                model = uiState.image,
                contentDescription = uiState.name,
                contentScale = ContentScale.FillWidth,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
        Card {
            Column(modifier = Modifier.padding(8.dp)) {
                Text(
                    "Ingredients",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.fillMaxWidth()
                )
                uiState.ingredients.forEach { ingredient ->
                    Text(
                        "◉ $ingredient",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        Card {
            Column(modifier = Modifier.padding(8.dp)) {
                Text(
                    "Steps",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.fillMaxWidth()
                )
                uiState.steps.forEach { step ->
                    Text(
                        "◉ $step",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }


    }
}

@Composable
fun RecipeError(uiState: RecipeUiState.Error, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = { TextButton(onClick = onDismiss) { Text("Okay") } },
        text = { Text(uiState.message) })
}


@Composable
fun RecipeFinding(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator(modifier = Modifier.size(100.dp))
        Text(
            "Finding Recipe...",
            style = MaterialTheme.typography.displayMedium,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
    }
}


@Preview
@Composable
fun RecipePreview() {
    RecipeTheme {
        Recipe(
            RecipeUiState.Found(
                name = "Premium Sushi",
                description = "A traditional Japanese dish",
                ingredients = listOf("3/4 cups of rice", "nori", "rice vinegar", "salmon"),
                steps = listOf("Make rice", "cut the salmon", "Make a roll"),
                servings = "4",
                servingsSize = "450g",
                image = null
            )
        )
    }
}