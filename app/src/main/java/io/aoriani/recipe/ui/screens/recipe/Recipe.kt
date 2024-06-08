package io.aoriani.recipe.ui.screens.recipe

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Recipe(onNavigateUp: () -> Unit = {}) {
    Scaffold(topBar = {
        TopAppBar(title = { Text("Recipe") }, navigationIcon = { IconButton(onClick = onNavigateUp ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
        } })
    }
    ) { innerPadding ->

    }
}



@Preview
@Composable
fun RecipePreview() {

}